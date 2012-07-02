package cloudgene.mapred.apps;

import java.util.List;
import java.util.Vector;

public class MapReduceConfig {

	private String jar;

	private String mapper;

	private String reducer;

	private String exec;

	private String params;

	private List<Step> steps = new Vector<Step>();

	private List<Parameter> inputs = new Vector<Parameter>();

	private List<Parameter> outputs = new Vector<Parameter>();

	private String path;

	public String getJar() {
		return jar;
	}

	public void setJar(String jar) {
		this.jar = jar;
	}

	public String getMapper() {
		return mapper;
	}

	public void setMapper(String mapper) {
		this.mapper = mapper;
	}

	public String getReducer() {
		return reducer;
	}

	public void setReducer(String reducer) {
		this.reducer = reducer;
	}

	public String getParams() {
		return params;
	}

	public void setParams(String params) {
		this.params = params;
	}

	public List<Parameter> getInputs() {
		return inputs;
	}

	public void setInputs(List<Parameter> inputs) {
		this.inputs = inputs;
	}

	public List<Parameter> getOutputs() {
		return outputs;
	}

	public void setOutputs(List<Parameter> outputs) {
		this.outputs = outputs;
	}

	public List<Step> getSteps() {
		return steps;
	}

	public void setSteps(List<Step> steps) {
		this.steps = steps;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public void setExec(String exec) {
		this.exec = exec;
	}

	public String getExec() {
		return exec;
	}

}
