package com.acunu.performance.randomness;

import java.util.Random;

import com.acunu.performance.Randomness;

public class UniformRandomness implements Randomness {

	private final Random random = new Random();

	@Override
	public double nextDouble() {
		return random.nextDouble();
	}

}
