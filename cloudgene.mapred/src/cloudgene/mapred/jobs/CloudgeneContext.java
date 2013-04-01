package cloudgene.mapred.jobs;

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

	public CloudgeneContext(MapReduceConfig config, List<String> inputValues,
			Step step) {

		this.step = step;

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

	public void done(int type) {
		LogMessage status = step.getLogMessages().get(
				step.getLogMessages().size() - 1);
		status.setType(type);
	}

	public void done(String message, int type) {
		LogMessage status = step.getLogMessages().get(
				step.getLogMessages().size() - 1);
		status.setType(type);
		status.setMessage(message);
	}

	public void start(String message) {

		LogMessage status = new LogMessage(step, LogMessage.RUNNING, message);

		List<LogMessage> logs = step.getLogMessages();
		if (logs == null) {
			logs = new Vector<LogMessage>();
			step.setLogMessages(logs);
		}
		logs.add(status);

	}

}
