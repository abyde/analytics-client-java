package com.acunu.performance.generators;

import com.acunu.performance.ValueGenerator;

public class IntervalTimestamp implements ValueGenerator {

	private long interval;

	@Override
	public Object generate() {
		return interval * Math.floor(System.currentTimeMillis() / interval);
	}

	public long getInterval() {
		return interval;
	}

	public void setInterval(long interval) {
		this.interval = interval;
	}

}
