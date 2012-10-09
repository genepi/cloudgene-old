package cloudgene.mapred.apps;

import java.util.Map;

import cloudgene.mapred.jobs.MapReduceJob;

public class Step {

	private String jar;

	private String mapper;

	private String reducer;

	private String params;

	private String name;

	private String exec;

	private String job;

	private Map<String, String> jobInputs;

	private Map<String, String> jobOutputs;
	
	private MapReduceJob mapReduceJob;

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

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setExec(String exec) {
		this.exec = exec;
	}

	public String getExec() {
		return exec;
	}

	public void setJob(String job) {
		this.job = job;
	}

	public String getJob() {
		return job;
	}

	public Map<String, String> getJobInputs() {
		return jobInputs;
	}

	public void setJobInputs(Map<String, String> jobInputs) {
		this.jobInputs = jobInputs;
	}

	public Map<String, String> getJobOutputs() {
		return jobOutputs;
	}

	public void setJobOutputs(Map<String, String> jobOutputs) {
		this.jobOutputs = jobOutputs;
	}

	public MapReduceJob getMapReduceJob() {
		return mapReduceJob;
	}

	public void setMapReduceJob(MapReduceJob mapReduceJob) {
		this.mapReduceJob = mapReduceJob;
	}

}
