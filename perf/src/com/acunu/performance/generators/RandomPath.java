package com.acunu.performance.generators;

import java.util.List;

public class RandomPath extends AbstractRandom {

	private String separator;
	private List<List<String>> paths;

	@Override
	public Object generate() {
		String result = "";
		for (List<String> p : paths) {
			int i = (int) nextLong(p.size());
			result += separator;
			result += p.get(i);
		}
		return result.substring(1);
	}

	public String getSeparator() {
		return separator;
	}

	public void setSeparator(String separator) {
		this.separator = separator;
	}

	public List<List<String>> getPaths() {
		return paths;
	}

	public void setPaths(List<List<String>> paths) {
		this.paths = paths;
	}
}
