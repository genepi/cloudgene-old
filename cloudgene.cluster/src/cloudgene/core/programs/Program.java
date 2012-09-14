package cloudgene.core.programs;

import java.util.Map;

//extracted from YAML file. Only GenericProgram cluster is important here
public class Program extends ProgramMetadata {

	private ClusterSetup cluster;

	private Map<String, String> mapred;

	public ClusterSetup getCluster() {
		return cluster;
	}

	public void setCluster(ClusterSetup cluster) {
		this.cluster = cluster;
	}

	public void setMapred(Map<String, String> mapred) {
		this.mapred = mapred;
	}

	public Map<String, String> getMapred() {
		return mapred;
	}


	

}
