package cloudgene.mapred.jobs;

import java.io.BufferedOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Vector;

import org.apache.commons.io.FileUtils;
import org.apache.commons.logging.LogFactory;

import cloudgene.mapred.apps.Parameter;
import cloudgene.mapred.apps.Step;
import cloudgene.mapred.core.User;
import cloudgene.mapred.util.FileUtil;
import cloudgene.mapred.util.S3Util;
import cloudgene.mapred.util.Settings;

abstract public class Job implements Runnable {

	private static final org.apache.commons.logging.Log log = LogFactory
			.getLog(Job.class);

	private DateFormat formatter = new SimpleDateFormat("yy/MM/dd HH:mm:ss");

	public static final int MAPREDUCE = 1;

	public static final int TASK = 2;

	public static final int LOCAL = 3;

	public static final int WAITING = 1;

	public static final int RUNNING = 2;

	public static final int EXPORTING_DATA = 3;

	public static final int FINISHED = 4;

	public static final int ERROR = 5;

	public static final int CANCELED = 6;

	private String id;

	private int state = WAITING;

	private long startTime = 0;

	private long endTime = 0;

	private String name;

	private String currentStep;

	private User user;

	private String error = "";

	private String s3Url = "";

	private int map = -1;

	private int reduce = -1;

	protected List<Parameter> inputParams = new Vector<Parameter>();

	protected List<Parameter> outputParams = new Vector<Parameter>();

	protected List<Step> steps = new Vector<Step>();

	private BufferedOutputStream stdOutStream;

	private BufferedOutputStream logStream;

	protected Job parent;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public int getState() {
		return state;
	}

	public void setState(int state) {
		this.state = state;
	}

	public long getStartTime() {
		return startTime;
	}

	public void setStartTime(long startTime) {
		this.startTime = startTime;
	}

	public long getEndTime() {
		return endTime;
	}

	public void setEndTime(long endTime) {
		this.endTime = endTime;
	}

	public int getExecutionTime() {
		if (endTime == 0) {
			return (int) (System.currentTimeMillis() - startTime);
		} else {
			return (int) (endTime - startTime);
		}
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public String getError() {
		return error;
	}

	public void setError(String error) {
		this.error = error;
	}

	public String getCurrentStep() {
		return currentStep;
	}

	public void setCurrentStep(String currentStep) {
		this.currentStep = currentStep;
	}

	public void setS3Url(String s3Url) {
		this.s3Url = s3Url;
	}

	public String getS3Url() {
		return s3Url;
	}

	public void setMap(int map) {
		this.map = map;
	}

	public int getMap() {
		return map;
	}

	public void setReduce(int reduce) {
		this.reduce = reduce;
	}

	public int getReduce() {
		return reduce;
	}

	public List<Parameter> getInputParams() {
		return inputParams;
	}

	public void setInputParams(List<Parameter> inputParams) {
		this.inputParams = inputParams;
	}

	public List<Parameter> getOutputParams() {
		return outputParams;
	}

	public void setOutputParams(List<Parameter> outputParams) {
		this.outputParams = outputParams;
	}

	@Override
	public void run() {

		try {
			initLocalDirectories();
			initStdOutFiles();

		} catch (FileNotFoundException e1) {

			setEndTime(System.currentTimeMillis());

			setState(Job.ERROR);
			log.error("Job " + getId() + ": initialization failed.", e1);
			writeLog("Initialization failed: " + e1.getLocalizedMessage());
			return;

		}

		setState(Job.RUNNING);
		setStartTime(System.currentTimeMillis());
		writeLog("Details:");
		writeLog("  Name: " + getName());
		writeLog("  Job-Id: " + getId());
		writeLog("  Started At: " + getStartTime());
		writeLog("  Finished At: " + getExecutionTime());
		writeLog("  Execution Time: " + getExecutionTime());

		writeLog("  Inputs:");
		for (Parameter parameter : inputParams) {
			writeLog("    " + parameter.getDescription() + ": "
					+ parameter.getValue());
		}

		writeLog("  Outputs:");
		for (Parameter parameter : outputParams) {
			writeLog("    " + parameter.getDescription() + ": "
					+ parameter.getValue());
		}

		writeLog("Preparing Job....");
		boolean successfulBefore = before();

		if (!successfulBefore) {

			setState(Job.ERROR);
			log.error("Job " + getId() + ": job preparation failed.");
			writeLog("Job preparation failed.");

		} else {

			log.info("Job " + getId() + ": executing.");
			writeLog("Executing Job....");

			boolean succesfull = execute();

			if (succesfull) {

				log.info("Job " + getId() + ":  executed successful.");

				writeLog("Job executed successful.");
				writeLog("Exporting Data...");

				setState(Job.EXPORTING_DATA);

				try {

					boolean successfulAfter = after();

					if (successfulAfter) {

						setEndTime(System.currentTimeMillis());

						setState(Job.FINISHED);
						log.info("Job " + getId() + ": data export successful.");
						writeLog("Data export successful.");

					} else {

						setEndTime(System.currentTimeMillis());

						setState(Job.ERROR);
						log.error("Job " + getId() + ": data export failed.");
						writeLog("Data export failed.");

					}

				} catch (Exception e) {

					setEndTime(System.currentTimeMillis());

					setState(Job.ERROR);
					log.error("Job " + getId() + ": data export failed.", e);
					writeLog("Data export failed: " + e.getLocalizedMessage());

				}

			} else {

				setEndTime(System.currentTimeMillis());

				setState(Job.ERROR);
				log.error("Job " + getId() + ": execution failed. "
						+ getError());
				writeLog("Job execution failed: " + getError());

			}
		}

		closeStdOutFiles();

		exportStdOutToS3();

	}

	public void cancle() {

		setEndTime(System.currentTimeMillis());

		writeLog("Canceled by user.");

		if (state == RUNNING) {
			closeStdOutFiles();
		}

		setState(Job.CANCELED);

	}

	private void initStdOutFiles() throws FileNotFoundException {

		stdOutStream = new BufferedOutputStream(new FileOutputStream(
				getStdOutFile()));

		logStream = new BufferedOutputStream(new FileOutputStream(
				getLogOutFile()));

	}

	private void initLocalDirectories() {

		if (getUser() != null) {

			String localWorkspace = Settings.getInstance().getLocalWorkspace(
					getUser().getUsername());

			String directory = FileUtil.path(localWorkspace, "output", getId());
			FileUtil.createDirectory(directory);

		}

	}

	public String getStdOutFile() {

		if (getUser() != null) {

			String localWorkspace = Settings.getInstance().getLocalWorkspace(
					getUser().getUsername());

			return FileUtil.path(localWorkspace, "output", getId(), "std.out");

		} else {

			return "";
		}
	}

	public String getLogOutFile() {

		if (getUser() != null) {

			String localWorkspace = Settings.getInstance().getLocalWorkspace(
					getUser().getUsername());

			return FileUtil.path(localWorkspace, "output", getId(), "job.txt");

		} else {

			return "";
		}
	}

	private void closeStdOutFiles() {

		try {

			stdOutStream.close();
			logStream.close();

		} catch (IOException e) {

			e.printStackTrace();
		}

	}

	public void writeOutput(String line) {

		if (parent != null) {

			parent.writeOutput("    " + line);

		} else {

			try {
				stdOutStream.write(line.getBytes());
				stdOutStream.flush();
			} catch (IOException e) {
				e.printStackTrace();
			}

		}

	}

	public void writeOutputln(String line) {

		if (parent != null) {

			parent.writeOutputln("    " + line);

		} else {

			try {
				stdOutStream.write((formatter.format(new Date()) + " ")
						.getBytes());
				stdOutStream.write(line.getBytes());
				stdOutStream.write("\n".getBytes());
				stdOutStream.flush();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public void writeLog(String line) {

		if (parent != null) {

			// parent.writeLog("    " + line);

		} else {

			try {
				logStream
						.write((formatter.format(new Date()) + " ").getBytes());
				logStream.write(line.getBytes());
				logStream.write("\n".getBytes());
				logStream.flush();
			} catch (IOException e) {
				e.printStackTrace();
			}

		}
	}

	private void exportStdOutToS3() {

		// export to s3
		if (getUser().isExportToS3()) {

			S3Util.copyFile(getUser().getAwsKey(), getUser().getAwsSecretKey(),
					getUser().getS3Bucket(), getId(), getLogOutFile());

			S3Util.copyFile(getUser().getAwsKey(), getUser().getAwsSecretKey(),
					getUser().getS3Bucket(), getId(), getStdOutFile());
		}

	}

	public List<Step> getSteps() {
		return steps;
	}

	public void setSteps(List<Step> steps) {
		this.steps = steps;
	}

	abstract public boolean execute();

	abstract public boolean before();

	abstract public boolean after();

	abstract public int getType();
}
