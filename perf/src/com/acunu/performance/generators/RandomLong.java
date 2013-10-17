package com.acunu.performance.generators;

public class RandomLong extends AbstractRandom {

	private long low;
	private long high;

	@Override
	public Object generate() {
		return low + nextLong(high - low);
	}

	public long getLow() {
		return low;
	}

	public void setLow(long low) {
		this.low = low;
	}

	public long getHigh() {
		return high;
	}

	public void setHigh(long high) {
		this.high = high;
	}

}
