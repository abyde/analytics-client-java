package com.acunu.performance.randomness;

import com.acunu.performance.Randomness;

import cern.jet.random.AbstractDistribution;
import cern.jet.random.Distributions;
import cern.jet.random.engine.RandomEngine;

public class PowerLawRandomness implements Randomness {

	public static final double CUT = 1.0;
	public static final double ALPHA = 2.0;
	private final RandomEngine engine = AbstractDistribution.makeDefaultGenerator();

	@Override
	public double nextDouble() {
		return Distributions.nextPowLaw(ALPHA, CUT, engine);
	}

}
