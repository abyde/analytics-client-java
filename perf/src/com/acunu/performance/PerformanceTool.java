package com.acunu.performance;

import java.io.File;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import com.acunu.client.EventReceiver;
import com.acunu.client.http.HttpAnalyticsClient;
import com.acunu.performance.generators.RandomChoice;
import com.acunu.performance.randomness.UniformRandomness;
import com.acunu.performance.senders.EventSender;
import com.acunu.performance.senders.ParameterisedQuerySender;
import com.acunu.performance.senders.PlainQuerySender;
import com.acunu.performance.senders.ProcedureQuerySender;
import com.acunu.performance.senders.QuerySender;
import com.beust.jcommander.JCommander;
import com.beust.jcommander.ParameterException;

public class PerformanceTool {

	public static final PrintWriter writer = new PrintWriter(System.out);

	public static void main(String[] args) throws Exception {

		Arguments arguments = new Arguments();
		JCommander cmd = new JCommander(arguments);
		try {
			cmd.parse(args);
			new PerformanceTool().run(arguments);
		} catch (ParameterException ex) {
			System.out.println(ex.getMessage());
			cmd.usage();
		}
	}

	public void run(final Arguments args) throws Exception {

		System.setProperty("org.slf4j.simpleLogger.defaultLogLevel", args.loglevel);

		final PrintStream reporting = args.verbose ? System.out : new PrintStream(new NullOutputStream());

		ExecutorService clientexec = Executors.newFixedThreadPool(args.threads);

		HttpAnalyticsClient client = buildClient(args);
		final CountingEventClient eventclient = new CountingEventClient(client.getEventClient(clientexec, args.eventbatchsize, args.eventqueuesize));
		final CountingQueryClient queryclient = new CountingQueryClient(client.getQueryClient(args.querypagesize));

		reporting.println("performance tool starting...");
		reporting.println();
		reporting.printf("targeting %s for %d seconds with %d threads using %s at event rate %d and query rate %d\n", args.hostname, args.duration,
				args.threads, args.file, args.eventmultiplier, args.querymultiplier);
		reporting.println();

		reporting.println("parsing yaml definition file:");

		List<String> packs = new LinkedList<String>();
		packs.add(RandomChoice.class.getPackage().getName());
		packs.add(UniformRandomness.class.getPackage().getName());

		@SuppressWarnings("rawtypes")
		Map<String, Class> tags = new HashMap<String, Class>();
		tags.put("!Query", PlainQuerySender.class);
		tags.put("!ProcedureQuery", ProcedureQuerySender.class);
		tags.put("!ParameterisedQuery", ParameterisedQuerySender.class);
		tags.put("!Event", EventSender.class);

		@SuppressWarnings("unchecked")
		Map<String, Object> context = (Map<String, Object>) new YamlLoader(packs, tags).load(new File(args.file));

		reporting.println();
		reporting.println("scheduling events:");

		@SuppressWarnings("unchecked")
		List<EventSender> events = (List<EventSender>) context.get("events");

		List<EventReceiver> receivers = new LinkedList<EventReceiver>();
		List<ExecutorService> executors = new LinkedList<ExecutorService>();

		for (final EventSender sender : events) {

			int rate = sender.getRate();
			if (rate > 0) {

				rate = rate * args.eventmultiplier;
				reporting.printf("scheduled:%8d /sec: %s\n", rate, sender.toString());

				final EventReceiver receiver = eventclient.getEventReceiver(sender.getEndpoint());
				receivers.add(receiver);

				ScheduledThreadPoolExecutor exec = new ScheduledThreadPoolExecutor(1);
				exec.scheduleAtFixedRate(new Runnable() {
					@Override
					public void run() {

						try {
							sender.send(receiver);
						} catch (RuntimeException e) {
							System.out.println("EXCEPTION ON SENDING - SENDING TERMINATED");
							e.printStackTrace();
							throw e;
						}
					}
				}, 1000000, 1000000 / rate, TimeUnit.MICROSECONDS);
				executors.add(exec);
			}
		}

		reporting.println();
		reporting.println("scheduling queries:");

		@SuppressWarnings("unchecked")
		List<QuerySender> queries = (List<QuerySender>) context.get("queries");

		for (final QuerySender sender : queries) {

			int rate = sender.getRate();
			if (rate > 0) {

				rate = rate * args.querymultiplier;
				reporting.printf("scheduled:%8d /sec: %s\n", rate, sender.toString());
				ScheduledThreadPoolExecutor exec = new ScheduledThreadPoolExecutor(1);
				exec.scheduleAtFixedRate(new Runnable() {
					@Override
					public void run() {

						try {
							sender.send(queryclient);
						} catch (RuntimeException e) {
							System.out.println("EXCEPTION ON SENDING - SENDING TERMINATED");
							e.printStackTrace();
							throw e;
						}
					}
				}, 1000000, 1000000 / rate, TimeUnit.MICROSECONDS);
				executors.add(exec);
			}
		}

		reporting.println();

		ScheduledThreadPoolExecutor reportingexec = new ScheduledThreadPoolExecutor(1);
		reportingexec.scheduleAtFixedRate(new Runnable() {

			@Override
			public void run() {

				long events = eventclient.getCount();
				long queries = queryclient.getCount();
				reporting.printf("event rate: %8d   query rate: %8d\n", events, queries);
				reporting.flush();
				if (!args.verbose) {
					System.out.printf("%d %d\n", events, queries);
				}
			}
		}, 2, 1, TimeUnit.SECONDS);
		executors.add(reportingexec);

		Thread.sleep(args.duration * 1000);

		for (ExecutorService ex : executors) {
			ex.shutdown();
		}

		for (EventReceiver receiver : receivers) {
			receiver.flush();
		}

		clientexec.shutdownNow(); // terminates threads blocking on queue

		reporting.flush();
	}

	private HttpAnalyticsClient buildClient(Arguments args) {

		if (args.ssl) {
			return HttpAnalyticsClient.create().withHost(args.hostname, args.port).withAuthentication(args.username, args.password)
					.withConnections(args.connections)
					.withSSL(new File(args.keystore), new File(args.truststore), args.kspassword, args.tspassword, args.verifyhostname).build();
		} else {
			return HttpAnalyticsClient.create().withHost(args.hostname, args.port).withAuthentication(args.username, args.password)
					.withConnections(args.connections).withHttp().build();
		}
	}
}
