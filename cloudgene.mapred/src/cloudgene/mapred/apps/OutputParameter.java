package cloudgene.mapred.apps;

import java.util.List;

public class OutputParameter extends Parameter {

	private List<MyFile> files;

	@Override
	public boolean isInput() {
		return false;
	}

	public List<MyFile> getFiles() {
		return files;
	}

	public void setFiles(List<MyFile> files) {
		this.files = files;
	}

}
