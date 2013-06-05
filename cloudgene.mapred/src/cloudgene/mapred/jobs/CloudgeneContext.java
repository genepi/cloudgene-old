package cloudgene.mapred.jobs;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import cloudgene.mapred.apps.MapReduceConfig;
import cloudgene.mapred.apps.Parameter;
import cloudgene.mapred.apps.Step;

public class CloudgeneContext {

	private Map<String, String> params;
	private Step step;
	private String hdfsTemp;
	private String localTemp;
	private String hdfsOutput;
	private String localOutput;

	private String jobId;

	private MapReduceConfig config;

	private DateFormat formatter = new SimpleDateFormat("yy/MM/dd HH:mm:ss");

	private BufferedOutputStream stdOutStream;

	public CloudgeneContext(MapReduceConfig config, List<String> inputValues,
			Step step, BufferedOutputStream stdOutStream) {

		this.step = step;
		this.config = config;
		this.stdOutStream = stdOutStream;

		params = new HashMap<String, String>();

		for (int i = 0; i < config.getInputs().size(); i++) {
			Parameter param = config.getInputs().get(i);
			params.put(param.getId(), inputValues.get(i));
		}

		for (int i = 0; i < config.getOutputs().size(); i++) {
			Parameter param = config.getOutputs().get(i);
			params.put(param.getId(), param.getValue());
		}
	}

	public String get(String param) {
		return params.get(param);
	}

	public String getHdfsTemp() {
		return hdfsTemp;
	}

	public void setHdfsTemp(String hdfsTemp) {
		this.hdfsTemp = hdfsTemp;
	}

	public String getLocalTemp() {
		return localTemp;
	}

	public void setLocalTemp(String localTemp) {
		this.localTemp = localTemp;
	}

	public String getHdfsOutput() {
		return hdfsOutput;
	}

	public void setHdfsOutput(String hdfsOutput) {
		this.hdfsOutput = hdfsOutput;
	}

	public String getLocalOutput() {
		return localOutput;
	}

	public void setLocalOutput(String localOutput) {
		this.localOutput = localOutput;
	}

	public void endTask(int type) {
		LogMessage status = step.getLogMessages().get(
				step.getLogMessages().size() - 1);
		status.setType(type);
	}

	public MapReduceConfig getConfig() {
		return config;
	}

	public String getJobId() {
		return jobId;
	}

	public void setJobId(String jobId) {
		this.jobId = jobId;
	}

	public Step getStep() {
		return step;
	}

	public void setConfig(MapReduceConfig config) {
		this.config = config;
	}

	public void endTask(String message, int type) {
		LogMessage status = step.getLogMessages().get(
				step.getLogMessages().size() - 1);
		status.setType(type);
		status.setMessage(message);
	}

	public void error() {
		LogMessage status = step.getLogMessages().get(
				step.getLogMessages().size() - 1);
		status.setType(LogMessage.ERROR);
	}

	public void message(String message, int type) {
		LogMessage status = new LogMessage(step, type, message);

		List<LogMessage> logs = step.getLogMessages();
		if (logs == null) {
			logs = new Vector<LogMessage>();
			step.setLogMessages(logs);
		}
		logs.add(status);
	}

	public void ok(String message) {
		message(message, LogMessage.OK);
	}

	public void error(String message) {
		message(message, LogMessage.ERROR);
	}

	public void setTaskName(String name) {
		LogMessage status = new LogMessage(step, LogMessage.RUNNING, name);

		List<LogMessage> logs = step.getLogMessages();
		if (logs == null) {
			logs = new Vector<LogMessage>();
			step.setLogMessages(logs);
		}
		logs.add(status);
	}

	public void subTask(String name) {
		setTaskName(name);
	}

	public void beginTask(String name, int totalWork) {
		setTaskName(name);
	}

	public void beginTask(String name) {
		beginTask(name, -1);
	}

	public void worked(int work) {

	}

	public String[] resolveParams(String[] params) {

		String[] result = new String[params.length];

		for (int i = 0; i < params.length; i++) {
			result[i] = resolveParams(params[i]);
		}

		return result;
	}

	public String resolveParams(String param) {
		String result = param;
		for (String paramKey : this.params.keySet()) {
			String paramValue = this.params.get(paramKey);
			result = result.replaceAll("\\$" + paramKey, paramValue);
		}
		return result;
	}

	public void println(String line) {

		try {

			if (stdOutStream != null) {

				stdOutStream.write((formatter.format(new Date()) + " ")
						.getBytes());
				stdOutStream.write(line.getBytes());
				stdOutStream.write("\n".getBytes());
				stdOutStream.flush();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

}
