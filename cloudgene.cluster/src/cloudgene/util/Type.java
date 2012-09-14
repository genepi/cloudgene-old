package cloudgene.util;


public class Type implements Comparable<Type>{
	private String value;

	public void setValue(String value) {
		this.value = value;
	}

	public String getValue() {
		return value;
	}

	@Override
	public int compareTo(Type arg0) {
		return arg0.getValue().compareTo(value);
	}

}
