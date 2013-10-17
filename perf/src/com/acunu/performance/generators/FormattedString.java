package com.acunu.performance.generators;

import java.util.List;

import com.acunu.performance.ValueGenerator;

public class FormattedString implements ValueGenerator {

	private String format;
	private List<Object> generators;

	@Override
	public Object generate() {

		Object[] values = new Object[generators.size()];
		for (int i = 0; i < generators.size(); i++) {

			Object value = generators.get(i);
			if (value instanceof ValueGenerator) {
				value = ((ValueGenerator) value).generate();
			}
			values[i] = value;
		}

		return String.format(format, values);
	}

	public String getFormat() {
		return format;
	}

	public void setFormat(String format) {
		this.format = format;
	}

	public List<Object> getGenerators() {
		return generators;
	}

	public void setGenerators(List<Object> generators) {
		this.generators = generators;
	}

}
