package com.acunu.performance.generators;

import com.acunu.performance.ValueGenerator;

public class Timespan implements ValueGenerator {

	private long span;

	@Override
	public Object generate() {
		long now = System.currentTimeMillis();
		return String.format("[%d,%d]", now - span, now);
	}

	public long getSpan() {
		return span;
	}

	public void setSpan(long span) {
		this.span = span;
	}
}
