package cloudgene.mapred.apps;

import cloudgene.mapred.util.ExtJsTreeItem;

public class Category extends ExtJsTreeItem {

	private AppMetaData[] children;

	public boolean isExpanded() {
		return false;
	}

	public boolean isLeaf() {
		return false;
	}

	public void setChildren(AppMetaData[] children) {
		this.children = children;
	}

	public AppMetaData[] getChildren() {
		return children;
	}

}
