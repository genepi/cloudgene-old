package cloudgene.mapred.apps;

import java.util.Arrays;
import java.util.Collections;

import cloudgene.mapred.util.ExtJsTreeItem;

public class Category extends ExtJsTreeItem implements Comparable<Category> {

	private AppMetaData[] children;

	public boolean isExpanded() {
		return false;
	}

	public boolean isLeaf() {
		return false;
	}

	public void setChildren(AppMetaData[] children) {
		this.children = children;
		sort();
	}

	public AppMetaData[] getChildren() {
		return children;
	}

	public void sort(){
		Arrays.sort(children);
	}
	
	@Override
	public int compareTo(Category o) {
		return getText().compareTo(o.getText());
	}

}
