package com.acunu.performance.generators;

import com.acunu.performance.ValueGenerator;

public class Timestamp implements ValueGenerator {

	@Override
	public Object generate() {
		return System.currentTimeMillis();
	}

}
