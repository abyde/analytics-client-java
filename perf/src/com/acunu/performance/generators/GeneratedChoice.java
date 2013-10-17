package com.acunu.performance.generators;

import java.util.LinkedList;
import java.util.List;

import com.acunu.performance.ValueGenerator;

public class GeneratedChoice extends AbstractRandom {

	private final List<Object> choices = new LinkedList<Object>();

	private Object generator;
	private int size;

	private void initialise() {
		if (generator != null && size != 0) {
			for (int i = 0; i < size; i++) {
				Object value = generator;
				if (value instanceof ValueGenerator) {
					value = ((ValueGenerator) value).generate();
				}
				choices.add(value);
			}
		}
	}

	@Override
	public Object generate() {
		return choices.get((int) nextLong(choices.size()));
	}

	public Object getGenerator() {
		return generator;
	}

	public void setGenerator(Object generator) {
		this.generator = generator;
		initialise();
	}

	public int getSize() {
		return size;
	}

	public void setSize(int size) {
		this.size = size;
		initialise();
	}

}
