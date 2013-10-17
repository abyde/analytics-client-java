package com.acunu.client.util.concurrent;

import java.util.LinkedList;
import java.util.List;

/**
 * Bounded BatchingQueue backed by a LinkedList.
 *  
 * @author rallison
 *
 * @param <T> the type of elements held in this collection
 */
public class LinkedBatchingQueue<T> implements BatchingQueue<T> {

	private final int queuesize;
	private final int batchsize;
	private final List<T> list = new LinkedList<T>();

	public LinkedBatchingQueue(int queuesize, int batchsize) {

		if (batchsize > queuesize) {
			throw new IllegalArgumentException("batch size must be less than or equal to queue size");
		}
		this.queuesize = queuesize;
		this.batchsize = batchsize;
	}

	@Override
	public void put(T val) throws InterruptedException {

		synchronized (list) {

			while (list.size() >= queuesize) {
				list.wait();
			}

			list.add(val);
			list.notifyAll();
		}
	}

	@Override
	public List<T> takeBatch() throws InterruptedException {

		synchronized (list) {

			while (list.size() < batchsize) {
				list.wait();
			}

			List<T> result = new LinkedList<T>();
			for (int i = 0; i < batchsize; i++) {
				result.add(list.remove(0));
			}

			list.notifyAll();
			return result;
		}
	}

	@Override
	public List<T> takeAll() {

		synchronized (list) {

			List<T> result = new LinkedList<T>();
			while (!list.isEmpty()) {
				result.add(list.remove(0));
			}

			list.notifyAll();
			return result;
		}
	}

}
