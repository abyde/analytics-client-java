package com.acunu.performance.generators;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class RandomBag extends AbstractRandom {

	private int min;
	private int max;
	private List<String> strings;

	@Override
	public Object generate() {

		Set<String> bag = new HashSet<String>();
		int size = (int) nextLong(max - min) + min;

		for (int i = 0; i < size; i++) {
			bag.add(strings.get((int) nextLong(strings.size())));
		}

		return bag;
	}

	public int getMin() {
		return min;
	}

	public void setMin(int min) {
		this.min = min;
	}

	public int getMax() {
		return max;
	}

	public void setMax(int max) {
		this.max = max;
	}

	public List<String> getStrings() {
		return strings;
	}

	public void setStrings(List<String> strings) {
		this.strings = strings;
	}

}
