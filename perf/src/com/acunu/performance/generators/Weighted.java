package com.acunu.performance.generators;

public class Weighted {

	private final Object object;
	private final int weight;

	public Weighted(Object object, int weight) {
		this.object = object;
		this.weight = weight;
	}

	public Object getObject() {
		return object;
	}

	public int getWeight() {
		return weight;
	}

}
