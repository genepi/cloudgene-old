package cloudgene.mapred.apps;

public class Step {

	private String jar;

	private String mapper;

	private String reducer;

	private String params;

	private String name;

	private String exec;

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

}
