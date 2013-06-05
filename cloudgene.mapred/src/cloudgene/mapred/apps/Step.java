package cloudgene.mapred.apps;

import java.util.List;
import java.util.Map;

import cloudgene.mapred.jobs.Job;
import cloudgene.mapred.jobs.LogMessage;
import cloudgene.mapred.jobs.MapReduceJob;

public class Step {

	private String jar;

	private String mapper;

	private String reducer;

	private String params;

	private String name;

	private String exec;

	private String pig;

	private String rmd;

	private String output;

	private String job;

	private String classname;

	private Map<String, String> jobInputs;

	private Map<String, String> jobOutputs;

	private MapReduceJob mapReduceJob;

	private Job myJob;

	private int id;

	private List<LogMessage> logMessages;

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

	public void setPig(String pig) {
		this.pig = pig;
	}

	public String getPig() {
		return pig;
	}

	public String getRmd() {
		return rmd;
	}

	public void setRmd(String rmd) {
		this.rmd = rmd;
	}

	public String getOutput() {
		return output;
	}

	public void setOutput(String output) {
		this.output = output;
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

	public String getClassname() {
		return classname;
	}

	public Job getMyJob() {
		return myJob;
	}

	public void setMyJob(Job myJob) {
		this.myJob = myJob;
	}

	public void setClassname(String classname) {
		this.classname = classname;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public List<LogMessage> getLogMessages() {
		return logMessages;
	}

	public void setLogMessages(List<LogMessage> logMessages) {
		this.logMessages = logMessages;
	}

}
