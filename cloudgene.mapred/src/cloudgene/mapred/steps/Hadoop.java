package cloudgene.mapred.steps;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.mapred.RunningJob;

import cloudgene.mapred.jobs.CloudgeneContext;
import cloudgene.mapred.jobs.CloudgeneStep;
import cloudgene.mapred.jobs.LogMessage;
import cloudgene.mapred.util.HadoopUtil;

public abstract class Hadoop extends CloudgeneStep {

	protected String jobId;

	protected static final Log log = LogFactory.getLog(Hadoop.class);

	protected int map = 0;

	protected int reduce = 0;

	protected boolean executeCommand(List<String> command,
			CloudgeneContext context) throws IOException, InterruptedException {
		// set global variables
		for (int j = 0; j < command.size(); j++) {

			String cmd = command.get(j).replaceAll("\\$job_id",
					context.getJobId());
			command.set(j, cmd);
		}

		log.info(command);

		context.beginTask("Running...");

		context.println("Command: " + command);
		context.println("Working Directory: "
				+ new File(context.getConfig().getPath()).getAbsolutePath());

		ProcessBuilder builder = new ProcessBuilder(command);
		builder.directory(new File(context.getConfig().getPath()));
		builder.redirectErrorStream(true);
		Process process = builder.start();
		InputStream is = process.getInputStream();
		InputStreamReader isr = new InputStreamReader(is);
		BufferedReader br = new BufferedReader(isr);
		String line = null;

		// Find job id and write output into file
		Pattern pattern = Pattern.compile("Running job: (.*)");
		Pattern pattern2 = Pattern.compile("HadoopJobId: (.*)");

		context.println("Output: ");
		while ((line = br.readLine()) != null) {

			Matcher matcher = pattern.matcher(line);
			if (matcher.find()) {
				// write statistics from old job
				jobId = matcher.group(1).trim();
				log.info("Job " + context.getJobId() + " -> HadoopJob " + jobId);
			} else {
				Matcher matcher2 = pattern2.matcher(line);
				if (matcher2.find()) {
					jobId = matcher2.group(1).trim();
					log.info("Job " + context.getJobId() + " -> HadoopJob "
							+ jobId);
				}
			}

			context.println("  " + line);
		}

		br.close();
		isr.close();
		is.close();

		process.waitFor();
		context.println("Exit Code: " + process.exitValue());
		if (process.exitValue() != 0) {
			context.endTask(
					"Execution failed. Please have a look at the logfile for details.",
					LogMessage.ERROR);
			return false;
		} else {
			process.destroy();
		}
		context.endTask("Execution successful.", LogMessage.OK);
		return true;
	}

	@Override
	public void updateProgress() {

		RunningJob job = HadoopUtil.getInstance().getJob(jobId);
		if (job != null) {

			try {

				if (job.setupProgress() >= 1) {
					map = ((int) (job.mapProgress() * 100));
					reduce = ((int) (job.reduceProgress() * 100));
				} else {
					map = 0;
					reduce = 0;
				}

			} catch (Exception e) {
				map = 0;
				reduce = 0;
			}

		} else {
			map = 0;
			reduce = 0;
		}

	}

	public int getMapProgress() {
		return map;
	}

	public int getReduceProgress() {
		return reduce;
	}

	@Override
	public void kill() {

		try {

			if (jobId != null) {

				log.info(" Cancel Job " + jobId);

				HadoopUtil.getInstance().kill(jobId);

			}

		} catch (IOException e) {

			log.error(" Cancel Job failed: ", e);

		}

	}

}
