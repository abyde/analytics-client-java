package com.acunu.client;

import java.util.Arrays;
import java.util.List;

public class EventFactory {

	private final List<String> columns;

	public EventFactory(String[] columns) {
		this(Arrays.asList(columns));
	}

	public EventFactory(List<String> columns) {
		this.columns = columns;
	}

	public Event build(Object[] values) {
		return build(Arrays.asList(values));
	}

	public Event build(List<Object> values) {
		if (values.size() != columns.size()) {
			throw new RuntimeException("values list of wrong length");
		}

		Event event = new Event();
		for (int i = 0; i < values.size(); i++) {
			event.put(columns.get(i), values.get(i));
		}

		return event;
	}
}
