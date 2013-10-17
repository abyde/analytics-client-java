package com.acunu.client;

/**
 * Client interface for receiving event data. Receivers for events are looked up
 * by name.
 * 
 * @author rallison
 * 
 */
public interface EventClient {

	/**
	 * Get an event receiver by name. The receiver might be a table on the
	 * analytics node, or it may be the name of a preprocessor.
	 * 
	 * @param name
	 *            name of event receiver
	 * @return an EventRecceiver interface corresponding to the named event
	 *         receiver.
	 */
	public EventReceiver getEventReceiver(String name);

}
