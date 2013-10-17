package com.acunu.performance.generators;

import java.util.concurrent.atomic.AtomicLong;

import com.acunu.performance.ValueGenerator;

public class SequentialLong implements ValueGenerator {

	private final AtomicLong counter = new AtomicLong();
	private long start;
	private long delta;

	@Override
	public Object generate() {
		return counter.getAndAdd(delta);
	}

	public long getStart() {
		return start;
	}

	public void setStart(long start) {
		this.start = start;
	}

	public long getDelta() {
		return delta;
	}

	public void setDelta(long delta) {
		this.delta = delta;
	}

}
