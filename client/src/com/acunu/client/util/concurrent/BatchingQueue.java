package com.acunu.client.util.concurrent;

import java.util.List;

/**
 * A queue that allows elements to be retrieved in batches, waiting until enough elements are present.
 * 
 * @author rallison
 *
 * @param <T> the type of elements held in this collection
 */
public interface BatchingQueue<T> {

	public void put(T val) throws InterruptedException;

	public List<T> takeBatch() throws InterruptedException;

	public List<T> takeAll();

}