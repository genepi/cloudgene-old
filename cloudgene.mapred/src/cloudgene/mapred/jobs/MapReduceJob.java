package cloudgene.mapred.jobs;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.List;
import java.util.Vector;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import cloudgene.mapred.apps.MapReduceConfig;
import cloudgene.mapred.apps.Parameter;
import cloudgene.mapred.apps.Step;
import cloudgene.mapred.database.JobDao;
import cloudgene.mapred.jobs.export.ExportJob;
import cloudgene.mapred.util.FileUtil;
import cloudgene.mapred.util.HdfsUtil;
import cloudgene.mapred.util.RMarkdown;
import cloudgene.mapred.util.S3Util;
import cloudgene.mapred.util.Settings;

public class MapReduceJob extends Job {

	private List<String> inputValues = new Vector<String>();

	private List<String> outputValues = new Vector<String>();

	private MapReduceConfig config;

	private BufferedOutputStream reportStream;

	private static final Log log = LogFactory.getLog(MapReduceJob.class);

	private CloudgeneStep instance;

	public MapReduceJob() {
		super();
	}

	public MapReduceJob(MapReduceConfig config) throws Exception {

		this.config = config;

		for (Step step : config.getSteps()) {

			step.setMyJob(this);

			if (step.getPig() != null) {

				// pig script
				step.setClassname("cloudgene.mapred.steps.PigHadoop");

			} else if (step.getRmd() != null) {

				// rscript
				step.setClassname("cloudgene.mapred.steps.RMarkdown");

			} else if (step.getClassname() != null) {
				
				//custom class

			} else if (step.getExec() != null) {

				// command
				step.setClassname("cloudgene.mapred.steps.Command");

			} else {

				// mapreduce
				step.setClassname("cloudgene.mapred.steps.MapReduce");

			}
		}

		// init values

		for (int i = 0; i < config.getInputs().size(); i++) {
			inputValues.add("");
		}

		for (int i = 0; i < config.getOutputs().size(); i++) {
			outputValues.add("");
		}

		setInputParams(config.getInputs());
		setOutputParams(config.getOutputs());
	}

	public void setInputParam(String id, String param) {

		for (int i = 0; i < inputParams.size(); i++) {
			Parameter inputParam = inputParams.get(i);

			if (inputParam.getId().equalsIgnoreCase(id)) {

				if (isHdfsInput(i)) {
					String workspace = Settings.getInstance().getHdfsWorkspace(
							getUser().getUsername());
					if (param != null && !param.isEmpty()) {
						if (HdfsUtil.isAbsolute(param)) {
							inputValues.set(i, param);
						} else {
							String path = HdfsUtil.path(workspace, param);
							if (inputParam.isMakeAbsolute()) {
								inputValues.set(i, HdfsUtil.makeAbsolute(path));
							} else {
								inputValues.set(i, path);
							}
						}
					} else {

						inputValues.set(i, "");

					}

				} else {
					inputValues.set(i, param);
				}

				// TODO: remove!!!
				config.getInputs().get(i).setValue(param);
				return;
			}
		}

	}

	@Override
	public boolean execute() {

		JobDao dao = new JobDao();

		// normal job

		// set output directory, temp directory & jobname
		String workspace = Settings.getInstance().getHdfsWorkspace(
				getUser().getUsername());

		String tempDirectory = HdfsUtil.makeAbsolute(HdfsUtil.path(workspace,
				"output", getId(), "temp"));

		String outputDirectory = HdfsUtil.makeAbsolute(HdfsUtil.path(workspace,
				"output", getId()));

		String localWorkspace = new File(Settings.getInstance()
				.getLocalWorkspace(getUser().getUsername())).getAbsolutePath();

		String localOutputDirectory = new File(FileUtil.path(localWorkspace,
				"output", getId())).getAbsolutePath();

		FileUtil.createDirectory(localOutputDirectory);

		String localTempDirectory = new File(FileUtil.path(localWorkspace,
				"output", getId(), "temp")).getAbsolutePath();

		FileUtil.createDirectory(localTempDirectory);

		// create output directories
		for (int i = 0; i < config.getOutputs().size(); i++) {

			Parameter param = config.getOutputs().get(i);
			if (param.getType().equals(Parameter.HDFS_FILE)
					| param.getType().equals(Parameter.HDFS_FOLDER)) {
				if (param.isTemp()) {
					param.setValue(HdfsUtil.path(tempDirectory, param.getId()));
				} else {
					param.setValue(HdfsUtil.path(outputDirectory, param.getId()));
				}
			}

			if (param.getType().equals(Parameter.LOCAL_FILE)) {
				FileUtil.createDirectory(FileUtil.path(localOutputDirectory,
						param.getId()));
				param.setValue(FileUtil.path(localOutputDirectory,
						param.getId(), param.getId()));
			}

			if (param.getType().equals(Parameter.LOCAL_FOLDER)) {
				param.setValue(FileUtil.path(localOutputDirectory,
						param.getId()));
				FileUtil.createDirectory(FileUtil.path(localOutputDirectory,
						param.getId()));
			}

		}

		try {

			initJobStats();

			for (int k = 0; k < config.getSteps().size(); k++) {

				String stepName = config.getSteps().get(k).getName();
				int step = k + 1;
				int steps = config.getSteps().size();

				if (stepName != null) {
					setCurrentStep(stepName + " (" + step + "/"
							+ config.getSteps().size() + ")");
				} else {
					setCurrentStep("Step " + step + " (" + step + "/" + steps
							+ ")");
				}
				// config.getSteps().get(k).setMapReduceJob(this);
				getSteps().add(config.getSteps().get(k));
				dao.update(this);

				log.info("job " + getId() + " submitted...");

				writeOutputln("------------------------------------------------------");
				writeOutputln(getCurrentStep());
				writeOutputln("------------------------------------------------------");

				// normal command

				// set output directory, temp directory & jobname

				CloudgeneContext context = new CloudgeneContext(config,
						inputValues, config.getSteps().get(k), stdOutStream);
				context.setHdfsTemp(tempDirectory);
				context.setHdfsOutput(outputDirectory);
				context.setLocalTemp(localTempDirectory);
				context.setLocalOutput(localOutputDirectory);
				context.setJobId(getId());

				try {
					String jar = FileUtil.path(
							new File(getConfig().getPath()).getAbsolutePath(),
							config.getSteps().get(k).getJar());

					URL url = new File(jar).toURL();
					URLClassLoader urlCl = new URLClassLoader(
							new URL[] { url },
							MapReduceJob.class.getClassLoader());
					Class myClass = urlCl.loadClass(config.getSteps().get(k)
							.getClassname());
					instance = (CloudgeneStep) myClass.newInstance();
					boolean successful = instance.run(context);

					if (!successful) {
						return false;
					}
				} catch (Exception e) {
					log.error("Running extern job failed!", e);
					return false;
				}

			}

			closeJobState();

			setError(null);
			return true;

		} catch (Exception e) {
			e.printStackTrace();
			setError(e.getMessage());
			return false;
		}

	}

	@Override
	public boolean before() {

		return true;

	}

	@Override
	public boolean after() {

		String workspace = Settings.getInstance().getHdfsWorkspace(
				getUser().getUsername());

		String localWorkspace = Settings.getInstance().getLocalWorkspace(
				getUser().getUsername());

		String localOutput = FileUtil.path(localWorkspace, "output", getId());

		if (!getUser().isExportToS3()) {
			// create output zip file for hdfs folders
			for (Parameter out : config.getOutputs()) {

				if (out.isDownload()) {

					// export to local folder for faster download
					if (out.getType().equals(Parameter.HDFS_FOLDER)) {

						String localOutputDirectory = FileUtil.path(
								localOutput, out.getId());

						FileUtil.createDirectory(localOutputDirectory);

						String filename = out.getValue();
						String hdfsPath = null;
						if (filename.startsWith("hdfs://")
								|| filename.startsWith("file:/")) {
							hdfsPath = filename;

						} else {

							hdfsPath = HdfsUtil.makeAbsolute(HdfsUtil.path(
									workspace, filename));
						}

						System.out.println("Export Path: " + hdfsPath);

						if (out.isZip()) {

							String zipName = FileUtil.path(
									localOutputDirectory, out.getId() + ".zip");

							if (out.isMergeOutput()) {

								HdfsUtil.compressAndMerge(zipName, hdfsPath,
										out.isRemoveHeader());

							} else {

								HdfsUtil.compress(zipName, hdfsPath);

							}

						} else {

							if (out.isMergeOutput()) {

								HdfsUtil.exportDirectoryAndMerge(
										localOutputDirectory, out.getId(),
										hdfsPath, out.isRemoveHeader());

							} else {

								HdfsUtil.exportDirectory(localOutputDirectory,
										out.getId(), hdfsPath);

							}

						}

					}

					if (out.getType().equals(Parameter.HDFS_FILE)) {

						String localOutputDirectory = FileUtil.path(
								localOutput, out.getId());

						FileUtil.createDirectory(localOutputDirectory);

						String filename = out.getValue();
						String hdfsPath = null;
						if (filename.startsWith("hdfs://")
								|| filename.startsWith("file:/")) {

							hdfsPath = filename;

						} else {

							hdfsPath = HdfsUtil.makeAbsolute(HdfsUtil.path(
									workspace, filename));
						}

						if (out.isZip()) {

							HdfsUtil.exportFile(localOutputDirectory, hdfsPath);

						} else {

							HdfsUtil.compressFile(localOutputDirectory,
									hdfsPath);

						}

					}

				}
			}
		} else {

			// export to s3

			setS3Url("s3n://" + getUser().getS3Bucket() + "/" + getId());

			for (Parameter out : config.getOutputs()) {

				if (out.isDownload()) {

					String filename = out.getValue();

					if (out.getType().equals(Parameter.HDFS_FOLDER)
							|| out.getType().equals(Parameter.HDFS_FILE)) {

						String hdfsPath = null;
						if (filename.startsWith("hdfs://")) {
							hdfsPath = filename;
						} else {
							hdfsPath = HdfsUtil.path(workspace, filename);
						}

						/** set job specific attributes */
						try {

							ExportJob copyJob = new ExportJob("Copy data to s3");
							copyJob.setInput(hdfsPath);
							copyJob.setAwsKey(getUser().getAwsKey());
							copyJob.setAwsSecretKey(getUser().getAwsSecretKey());
							copyJob.setS3Bucket(getUser().getS3Bucket());
							copyJob.setDirectory(FileUtil.path(getId(),
									out.getId()));
							copyJob.setOutput(hdfsPath + "_temp");
							writeOutputln("Copy data from " + hdfsPath
									+ " to s3n://" + getUser().getS3Bucket()
									+ "/" + FileUtil.path(getId(), out.getId()));

							boolean success = copyJob.execute();

							if (!success) {
								log.error("Exporting data failed.");
								writeOutputln("Exporting data failed.");
							}

						} catch (Exception e) {

							log.error("Exporting data failed.", e);
							writeOutputln("Exporting data failed. "
									+ e.getMessage());
						}
					}

					if (out.getType().equals(Parameter.LOCAL_FILE)) {

						S3Util.copyFile(getUser().getAwsKey(), getUser()
								.getAwsSecretKey(), getUser().getS3Bucket(),
								getId(), filename);
					}

					if (out.getType().equals(Parameter.LOCAL_FOLDER)) {

						S3Util.copyDirectory(getUser().getAwsKey(), getUser()
								.getAwsSecretKey(), getUser().getS3Bucket(),
								getId(), filename);
					}

				}

			}

			if (getUser().isExportInputToS3()) {

				for (Parameter in : config.getInputs()) {

					if (in.isDownload()) {

						String filename = in.getValue();

						if (in.getType().equals(Parameter.HDFS_FOLDER)
								|| in.getType().equals(Parameter.HDFS_FILE)) {

							String hdfsPath = null;
							if (filename.startsWith("hdfs://")) {
								hdfsPath = filename;
							} else {
								hdfsPath = HdfsUtil.path(workspace, filename);
							}

							/** set job specific attributes */

							try {

								ExportJob copyJob = new ExportJob(
										"Copy data to s3");
								copyJob.setInput(hdfsPath);
								copyJob.setAwsKey(getUser().getAwsKey());
								copyJob.setAwsSecretKey(getUser()
										.getAwsSecretKey());
								copyJob.setS3Bucket(getUser().getS3Bucket());
								copyJob.setDirectory(FileUtil.path(getId(),
										in.getId()));
								copyJob.setOutput(hdfsPath + "_temp");
								writeOutputln("Copy data from " + hdfsPath
										+ " to s3n://"
										+ getUser().getS3Bucket() + "/"
										+ FileUtil.path(getId(), in.getId()));

								boolean success = copyJob.execute();

								if (!success) {
									log.error("Exporting data failed.");
									writeOutputln("Exporting data failed.");
								}

							} catch (Exception e) {

								log.error("Exporting data failed.", e);
								writeOutputln("Exporting data failed. "
										+ e.getMessage());
							}
						}

						if (in.getType().equals(Parameter.LOCAL_FILE)) {

							S3Util.copyFile(getUser().getAwsKey(), getUser()
									.getAwsSecretKey(),
									getUser().getS3Bucket(), getId(), filename);
						}

						if (in.getType().equals(Parameter.LOCAL_FOLDER)) {

							S3Util.copyDirectory(getUser().getAwsKey(),
									getUser().getAwsSecretKey(), getUser()
											.getS3Bucket(), getId(), filename);
						}

					}

				}

			}

			writeOutputln("Exporting data successful.");
		}

		// Write report
		if (new File(getStatFile()).exists()) {
			RMarkdown.convert("job-report.Rmd", getReportFile(),
					new String[] { getStatFile() });
		}

		// Delete temporary directory
		String tempDirectory = HdfsUtil.makeAbsolute(HdfsUtil.path(workspace,
				"output", getId(), "temp"));

		writeOutputln("Cleaning up...");
		HdfsUtil.deleteDirectory(tempDirectory);
		writeOutputln("Clean up successful.");

		return true;
	}

	public MapReduceConfig getConfig() {
		return config;
	}

	private boolean isHdfsInput(int index) {
		return config.getInputs().get(index).getType()
				.equals(Parameter.HDFS_FILE)
				|| config.getInputs().get(index).getType()
						.equals(Parameter.HDFS_FOLDER);
	}

	@Override
	public int getType() {

		return Job.MAPREDUCE;

	}

	protected void initJobStats() {

		String header = "job\tid\ttype\tstart\tend\tseconds";

		try {
			reportStream = new BufferedOutputStream(new FileOutputStream(
					getStatFile()));
			reportStream.write(header.getBytes());

		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	protected void closeJobState() {

		try {
			reportStream.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public String getStatFile() {

		if (getUser() != null) {

			String localWorkspace = Settings.getInstance().getLocalWorkspace(
					getUser().getUsername());

			return FileUtil.path(localWorkspace, "output", getId(),
					"statistics.txt");

		} else {

			return "";
		}
	}

	public String getReportFile() {

		if (getUser() != null) {

			String localWorkspace = Settings.getInstance().getLocalWorkspace(
					getUser().getUsername());

			return FileUtil.path(localWorkspace, "output", getId(),
					"statistics.html");

		} else {

			return "";
		}
	}

	@Override
	public void kill() {
		if (instance != null) {
			instance.kill();
		}
	}

	public void updateProgress() {

		if (instance != null) {

			instance.updateProgress();
			setMap(instance.getMapProgress());
			setReduce(instance.getReduceProgress());

		} else {
			setMap(0);
			setReduce(0);
		}

	}

}
