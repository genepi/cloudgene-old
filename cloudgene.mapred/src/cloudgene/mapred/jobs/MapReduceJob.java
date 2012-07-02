package cloudgene.mapred.jobs;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.mapred.RunningJob;

import cloudgene.mapred.apps.MapReduceConfig;
import cloudgene.mapred.apps.Parameter;
import cloudgene.mapred.apps.Step;
import cloudgene.mapred.database.JobDao;
import cloudgene.mapred.jobs.export.ExportJob;
import cloudgene.mapred.util.FileUtil;
import cloudgene.mapred.util.HadoopUtil;
import cloudgene.mapred.util.HdfsUtil;
import cloudgene.mapred.util.S3Util;
import cloudgene.mapred.util.Settings;

public class MapReduceJob extends Job {

	private List<String> inputValues = new Vector<String>();

	private List<String> outputValues = new Vector<String>();

	private List<List<String>> commands = new Vector<List<String>>();

	private MapReduceConfig config;

	private String jobId = null;

	private static final Log log = LogFactory.getLog(MapReduceJob.class);

	public MapReduceJob() {
		super();
	}

	public MapReduceJob(MapReduceConfig config) throws Exception {

		this.config = config;

		String hadoopPath = Settings.getInstance().getHadoopPath();
		String hadoop = FileUtil.path(hadoopPath, "bin", "hadoop");
		String streamingJar = Settings.getInstance().getStreamingJar();

		for (Step step : config.getSteps()) {

			// command
			if (step.getExec() != null) {

				String tiles[] = step.getExec().split(" ");

				List<String> command = new Vector<String>();
				for (String tile : tiles) {
					command.add(tile);
				}
				commands.add(command);

			} else {

				// hadoop jar or streaming
				List<String> command = new Vector<String>();

				command.add(hadoop);
				command.add("jar");

				if (step.getJar() != null) {

					// classical
					command.add(step.getJar());

				} else {

					// streaming

					if (Settings.getInstance().isStreaming()) {

						command.add(streamingJar);

					} else {

						throw new Exception(
								"Streaming mode is disabled.\nPlease specify the streaming-jar file in config/settings.yaml to run this job.");

					}

				}

				// params
				String[] tiles1 = step.getParams().split(" ");
				for (String tile : tiles1) {
					command.add(tile.trim());
				}

				// mapper and reducer

				if (step.getJar() == null) {

					if (step.getMapper() != null) {

						String tiles[] = step.getMapper().split(" ", 2);
						String filename = tiles[0];

						command.add("-mapper");

						if (tiles.length > 1) {
							String params = tiles[1];
							command.add(filename + " " + params);
						} else {
							command.add(filename);
						}

					}

					if (step.getReducer() != null) {

						String tiles[] = step.getReducer().split(" ", 2);
						String filename = tiles[0];

						command.add("-reducer");

						if (tiles.length > 1) {
							String params = tiles[1];
							command.add(filename + " " + params);
						} else {
							command.add(filename);
						}

					}

				}

				commands.add(command);
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
						String path = HdfsUtil.path(workspace, param);
						if (inputParam.isMakeAbsolute()) {
							inputValues.set(i, HdfsUtil.makeAbsolute(path));
						} else {
							inputValues.set(i, path);
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

	public String getHadoopJobId() {
		return jobId;
	}

	@Override
	public boolean execute() {

		JobDao dao = new JobDao();

		try {
			for (int k = 0; k < config.getSteps().size(); k++) {

				List<String> command = commands.get(k);

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
				dao.update(this);

				// set input values
				for (int i = 0; i < config.getInputs().size(); i++) {
					for (int j = 0; j < command.size(); j++) {
						String value = inputValues.get(i);
						String name = config.getInputs().get(i).getId();
						String cmd = command.get(j).replaceAll("\\$" + name,
								value);
						command.set(j, cmd);
					}
				}

				// set output directory, temp directory & jobname
				String workspace = Settings.getInstance().getHdfsWorkspace(
						getUser().getUsername());

				String tempDirectory = HdfsUtil.makeAbsolute(HdfsUtil.path(
						workspace, "output", getId(), "temp"));

				String outputDirectory = HdfsUtil.makeAbsolute(HdfsUtil.path(
						workspace, "output", getId()));

				String localWorkspace = new File(Settings.getInstance()
						.getLocalWorkspace(getUser().getUsername()))
						.getAbsolutePath();

				String localOutputDirectory = new File(FileUtil.path(
						localWorkspace, "output", getId())).getAbsolutePath();

				FileUtil.createDirectory(localOutputDirectory);

				// set global variables
				for (int j = 0; j < command.size(); j++) {

					String cmd = command.get(j)
							.replaceAll("\\$job_id", getId());
					command.set(j, cmd);
				}

				// set output values
				for (int i = 0; i < config.getOutputs().size(); i++) {

					Parameter param = config.getOutputs().get(i);
					if (param.getType().equals(Parameter.HDFS_FILE)
							| param.getType().equals(Parameter.HDFS_FOLDER)) {
						if (param.isTemp()) {
							param.setValue(HdfsUtil.path(tempDirectory,
									param.getId()));
						} else {
							param.setValue(HdfsUtil.path(outputDirectory,
									param.getId()));
						}
					}

					if (param.getType().equals(Parameter.LOCAL_FILE)) {
						FileUtil.createDirectory(FileUtil.path(
								localOutputDirectory, param.getId()));
						param.setValue(FileUtil.path(localOutputDirectory,
								param.getId(), param.getId()));
					}

					if (param.getType().equals(Parameter.LOCAL_FOLDER)) {
						param.setValue(FileUtil.path(localOutputDirectory,
								param.getId()));
						FileUtil.createDirectory(FileUtil.path(
								localOutputDirectory, param.getId()));
					}

					for (int j = 0; j < command.size(); j++) {
						String value = param.getValue();
						String name = param.getId();
						String cmd = command.get(j).replaceAll("\\$" + name,
								value);
						command.set(j, cmd);
					}
				}

				log.info("job " + getId() + " submitted...");

				writeOutputln("------------------------------------------------------");
				writeOutputln(getId() + " (" + step + "/"
						+ config.getSteps().size() + ")");
				writeOutputln("------------------------------------------------------");
				writeOutputln("Command: " + command);
				writeOutputln("Working Directory: "
						+ new File(getConfig().getPath()).getAbsolutePath());

				ProcessBuilder builder = new ProcessBuilder(command);
				builder.directory(new File(getConfig().getPath()));
				builder.redirectErrorStream(true);
				Process process = builder.start();
				InputStream is = process.getInputStream();
				InputStreamReader isr = new InputStreamReader(is);
				BufferedReader br = new BufferedReader(isr);
				String line = null;

				// Find job id and write output into file
				Pattern pattern = Pattern.compile("Running job: (.*)");
				jobId = null;
				writeOutputln("Output: ");
				while ((line = br.readLine()) != null) {
					if (jobId == null) {
						Matcher matcher = pattern.matcher(line);
						if (matcher.find()) {
							jobId = matcher.group(1).trim();
							log.info("Job " + getId() + " -> HadoopJob "
									+ jobId);
						}
					}
					writeOutputln("  " + line);
				}

				br.close();
				isr.close();
				is.close();

				process.waitFor();
				writeOutputln("Exit Code: " + process.exitValue());
				if (process.exitValue() != 0) {
					setError("Abnormal Termination: " + process.exitValue());
					return false;
				} else {
					process.destroy();
				}
			}

			setError(null);
			return true;

		} catch (Exception e) {
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

	public void updateProgress() {

		RunningJob job = HadoopUtil.getInstance().getJob(jobId);
		if (job != null) {

			try {

				if (job.setupProgress() >= 1) {
					setMap((int) (job.mapProgress() * 100));
					setReduce((int) (job.reduceProgress() * 100));
				} else {
					setMap(0);
					setReduce(0);
				}

			} catch (Exception e) {
				setMap(0);
				setReduce(0);
			}

		} else {

			setMap(0);
			setReduce(0);

		}

		if (getState() == EXPORTING_DATA) {

		}

	}

}
