package com.acunu.performance.generators;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import com.acunu.performance.ValueGenerator;

public class WeightedChoice implements ValueGenerator {

	private final Random random = new Random();
	private final List<Weighted> weighted = new LinkedList<Weighted>();
	private List<Object> choices;
	private int total;

	@Override
	public Object generate() {
		int sum = 0;
		int result = random.nextInt(total);
		for (Weighted w : weighted) {
			sum += w.getWeight();
			if (result < sum)
				return w.getObject();
		}
		throw new RuntimeException("weights not summed correctly");
	}

	public List<Object> getChoices() {
		return choices;
	}

	public void setChoices(List<Object> choices) {
		this.choices = choices;
		initialise();
	}
	
	private void initialise( ) {
		
		for ( int i = 0; i < choices.size(); i=i+2 )
		{
			Object obj = choices.get(i);
			int weight = (Integer)choices.get(i+1);
			weighted.add(new Weighted( obj, weight));
			total += weight;
		}
	}

}
