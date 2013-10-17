package com.acunu.performance.generators;

import java.util.LinkedList;
import java.util.List;

public class RandomIp extends AbstractRandom {

	private final List<String> address = new LinkedList<String>();
	private int size;

	@Override
	public Object generate() {
		return address.get((int) nextLong(address.size()));
	}

	public int getSize() {
		return size;
	}

	public void setSize(int n) {
		this.size = n;
		
		initialise(n);
	}

	private void initialise(int n) {
		
		for (int i = 0; i < n; i++) {
			address.add(String.format("%d.%d.%d.%d", nextLong(255), nextLong(255), nextLong(255), nextLong(255)));
		}
	}
	
	

}
