package com.acunu.performance.generators;

import java.util.HashMap;
import java.util.Map;

import com.acunu.performance.Randomness;
import com.acunu.performance.ValueGenerator;
import com.acunu.performance.randomness.PowerLawRandomness;
import com.acunu.performance.randomness.UniformRandomness;

public abstract class AbstractRandom implements ValueGenerator {

	private static final Map<String,Randomness> randoms = new HashMap<String, Randomness>();
	
	static {
		randoms.put("uniform", new UniformRandomness());
		randoms.put("powerlaw", new PowerLawRandomness());
	}
	
	private String random;

	public String getRandom() {
		return random;
	}

	public void setRandom(String random) {
		this.random = random;
	}
	
	protected double nextDouble( ) {
		return randoms.get(random).nextDouble();
	}
	
	protected long nextLong( long high ) {
		return Math.round(nextDouble() * (high - 1));
	}

}
