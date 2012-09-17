package cloudgene.mapred.apps;

import java.util.Map;

public class App extends AppMetaData implements Comparable<App>{

	private MapReduceConfig mapred;

	private Map<String, String> cluster;

	public MapReduceConfig getMapred() {
		return mapred;
	}

	public void setMapred(MapReduceConfig mapred) {
		this.mapred = mapred;
	}

	public Map<String, String> getCluster() {
		return cluster;
	}

	public void setCluster(Map<String, String> cluster) {
		this.cluster = cluster;
	}

	@Override
	public int compareTo(App o) {
		return getName().compareTo(o.getName());
	}

}
