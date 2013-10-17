package com.acunu.performance.generators;


public class RandomDouble extends AbstractRandom {

	private double low;
	private double high;

	@Override
	public Object generate() {
		return low + nextDouble() * (high - low);
	}

	public double getLow() {
		return low;
	}

	public void setLow(double low) {
		this.low = low;
	}

	public double getHigh() {
		return high;
	}

	public void setHigh(double high) {
		this.high = high;
	}

	
}
