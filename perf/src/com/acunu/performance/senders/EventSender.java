package com.acunu.performance.senders;

import java.util.Map;

import com.acunu.client.Event;
import com.acunu.client.EventReceiver;
import com.acunu.performance.ValueGenerator;

public class EventSender {

	private int rate;
	private String endpoint;
	private Map<String, Object> fields;

	public void send(EventReceiver receiver) {
		
		Event event = new Event();

		for (Map.Entry<String, Object> entry : fields.entrySet()) {
			String key = entry.getKey();
			Object value = entry.getValue();
			if (value instanceof ValueGenerator) {
				value = ((ValueGenerator) value).generate();
			}
			event.put(key, value);
		}

		receiver.submitEvent(event);
	}

	public int getRate() {
		return rate;
	}

	public void setRate(int rate) {
		this.rate = rate;
	}

	public String getEndpoint() {
		return endpoint;
	}

	public void setEndpoint(String endpoint) {
		this.endpoint = endpoint;
	}

	public Map<String, Object> getFields() {
		return fields;
	}

	public void setFields(Map<String, Object> fields) {
		this.fields = fields;
	}
}
