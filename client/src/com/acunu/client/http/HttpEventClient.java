package com.acunu.client.http;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executor;

import net.minidev.json.JSONObject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.helpers.MessageFormatter;

import com.acunu.client.AcunuClientException;
import com.acunu.client.Event;
import com.acunu.client.EventClient;
import com.acunu.client.EventReceiver;
import com.acunu.client.util.concurrent.BatchingQueue;
import com.acunu.client.util.concurrent.LinkedBatchingQueue;

class HttpEventClient implements EventClient {

	public static final String DEFAULT_EVENT_PATH = "/analytics/api/data/%s";

	private static final Logger log = LoggerFactory.getLogger(HttpEventClient.class);

	private final HttpClientWrapper client;
	private final Executor executor;
	private final int batchsize;
	private final int queuesize;
	private final String eventpath;

	HttpEventClient(HttpClientWrapper client, Executor executor, int batchsize, int queuesize) {
		this.client = client;
		this.executor = executor;
		this.batchsize = batchsize;
		this.queuesize = queuesize;
		this.eventpath = DEFAULT_EVENT_PATH;
	}

	@Override
	public EventReceiver getEventReceiver(final String name) {

		final String rpath = String.format(eventpath, name);
		log.debug("created event receiver with path: {}", rpath);

		return new EventReceiver() {

			BatchingQueue<Event> events = new LinkedBatchingQueue<Event>(queuesize, batchsize);

			{
				executor.execute(new Runnable() {
					@Override
					public void run() {
						try {
							// Sending over http:
							// 1. Takes a batch of events (fast)
							// 2. Executes this runnable with a new thread ready
							// for more events
							// 3. Sends over http (slow) - the new thread
							// started at 2 may be sending the next batch
							// concurrently (and in turn starting its own new
							// thread).
							// Thread initiation goes through the provided
							// thread pool,
							// so will depend on thread pool setup (num threads
							// etc)

							List<Event> batch = events.takeBatch();
							executor.execute(this);
							sendOverHttp(batch);

						} catch (InterruptedException e) {
							Thread.currentThread().interrupt();
						}
					}
				});
			}

			@Override
			public String getName() {
				return name;
			}

			@Override
			public void submitEvent(Event event) {
				try {
					log.debug("event received: {}", event);
					events.put(event);
				} catch (InterruptedException e) {
					Thread.currentThread().interrupt();
				}
			}

			@Override
			public void flush() {
				List<Event> batch = events.takeAll();
				sendOverHttp(batch);
			}

			private void sendOverHttp(List<Event> batch) {

				log.debug("sending batch, path: {}, batchsize: {}", rpath, batch.size());

				StringWriter stringwriter = new StringWriter();
				try {
					BufferedWriter writer = new BufferedWriter(stringwriter);
					for (Event event : batch) {
						JSONObject obj = new JSONObject(event);
						writer.write(obj.toJSONString());
						writer.write("\n");
					}
					writer.flush();
					writer.close();
				} catch (IOException e) {
					String msg = MessageFormatter.format("exception executing post to url: {}", rpath).getMessage();
					log.error(msg, e);
					throw new AcunuClientException(msg, e);
				}

				Map<String, String> headers = new HashMap<String, String>();
				headers.put("Accept", "application/json, text/javascript");
				headers.put("Content-Type", "text/plain");
				client.post(rpath, headers, stringwriter.toString());
			}
		};
	}
}
