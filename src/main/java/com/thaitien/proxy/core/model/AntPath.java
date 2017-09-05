package com.thaitien.proxy.core.model;

/**
 * This class is used to simplify a URL. Instead of using regular expression
 * format, it uses wildcard "*" to represent zero or many characters
 * 
 * @author tien
 *
 */
public class AntPath {
	private String antPath;

	public AntPath(String antPath) {
		this.antPath = antPath;
	}

	/**
	 * convert ant path to regular expression
	 * 
	 * @return String regular expression
	 */
	public String convertToRegexPattern() {
		char[] chars = antPath.toCharArray();
		StringBuilder builder = new StringBuilder();

		for (char c : chars) {
			if (c == '*')
				builder.append(".*");
			else
				builder.append(c);
		}
		return builder.toString();
	}

	public String getAntPath() {
		return antPath;
	}

	public void setAntPath(String antPath) {
		this.antPath = antPath;
	}

	@Override
	public String toString() {
		return "AntPath [antPath=" + antPath + "]";
	}

	public static void main(String[] args) throws Exception {
		System.out.println(new AntPath("tien").convertToRegexPattern());
		System.out.println(new AntPath("tien/").convertToRegexPattern());
		System.out.println(new AntPath("/tien**").convertToRegexPattern());
		System.out.println(new AntPath("tien_132").convertToRegexPattern());
		System.out.println(new AntPath("tien_132/*/*").convertToRegexPattern());
		System.out.println(new AntPath("/tien_132/*/**").convertToRegexPattern());
	}

}
