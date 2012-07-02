package cloudgene.mapred.apps;

public class MyFile implements Comparable<MyFile> {

	private String name;

	private String path;

	private String size;

	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public String getPath() {
		return path;
	}

	public void setSize(String size) {
		this.size = size;
	}

	public String getSize() {
		return size;
	}

	@Override
	public int compareTo(MyFile o) {
		return name.compareTo(o.getName());

	}

}
