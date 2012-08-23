package core.programs;

import java.util.Map;

//extracted from YAML file. Only GenericProgram cluster is important here
public class CloudgeneYaml extends UserProgramMetadata {

	private ClusterYaml cluster;

	private Map<String, String> mapred;

	public ClusterYaml getCluster() {
		return cluster;
	}

	public void setCluster(ClusterYaml cluster) {
		this.cluster = cluster;
	}

	public void setMapred(Map<String, String> mapred) {
		this.mapred = mapred;
	}

	public Map<String, String> getMapred() {
		return mapred;
	}


	

}
