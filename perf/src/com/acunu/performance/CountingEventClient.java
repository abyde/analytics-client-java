package com.acunu.performance;

import java.util.concurrent.atomic.AtomicLong;

import com.acunu.client.Event;
import com.acunu.client.EventClient;
import com.acunu.client.EventReceiver;

public class CountingEventClient implements EventClient {

	private final EventClient client;
	private final AtomicLong count = new AtomicLong();

	public CountingEventClient(EventClient client) {
		this.client = client;
	}
	
	public long getCount( ) {
		return count.getAndSet(0);
	}

	@Override
	public EventReceiver getEventReceiver(String name) {

		final EventReceiver receiver = client.getEventReceiver(name);

		return new EventReceiver() {
			
			@Override
			public String getName() {
				return receiver.getName();
			}

			@Override
			public void submitEvent(Event event) {
				receiver.submitEvent(event);
				count.incrementAndGet();
			}

			@Override
			public void flush() {
				receiver.flush();
			}
		};
	}

}
