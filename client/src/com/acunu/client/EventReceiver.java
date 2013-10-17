package com.acunu.client;

/**
 * Receiver interface for accepting events. This may be backed by an analytics
 * table, a preprocessor, or remote representations of either.
 * 
 * Implementations may choose to buffer events for efficient transport to the
 * underlying table or preprocessor.
 * 
 * @author rallison
 * 
 */
public interface EventReceiver {

	/**
	 * Unique name within the set of event receivers.
	 *
	 * @return name
	 */
	public String getName( );
	
	/**
	 * Sends an event to the receiver.
	 * 
	 * @param event
	 *            analytics event, usually a key value map corresponding to the
	 *            fields in an analytics table, or the parameters to a
	 *            preprocessor.
	 */
	public void submitEvent(Event event);

	/**
	 * Flush all events to the underlying entity, usually a table or
	 * preprocessor.
	 */
	public void flush();
}
