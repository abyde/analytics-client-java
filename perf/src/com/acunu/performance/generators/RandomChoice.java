package com.acunu.performance.generators;

import java.util.List;


public class RandomChoice extends AbstractRandom {

	private List<Object> choices;

	@Override
	public Object generate() {
		return choices.get((int)nextLong(choices.size()));
	}

	public List<Object> getChoices() {
		return choices;
	}

	public void setChoices(List<Object> choices) {
		this.choices = choices;
	}
}
