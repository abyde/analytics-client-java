package com.acunu.client.example;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import com.acunu.client.AqlClient;
import com.acunu.client.Event;
import com.acunu.client.EventClient;
import com.acunu.client.EventReceiver;
import com.acunu.client.QueryClient;
import com.acunu.client.Result;
import com.acunu.client.Row;
import com.acunu.client.http.HttpAnalyticsClient;
import com.beust.jcommander.JCommander;
import com.beust.jcommander.ParameterException;

/**
 * Example for client usage. Pass the target hostname and port as parameters.
 * 
 * @author rallison
 * 
 */
public class HttpExample {

	public static void main(String[] args) throws Exception {

		Arguments arguments = new Arguments();
		JCommander cmd = new JCommander(arguments);
		try {
			cmd.parse(args);
			new HttpExample().run(arguments);
		} catch (ParameterException ex) {
			System.out.println(ex.getMessage());
			cmd.usage();
		}
	}

	private void run(Arguments args) throws IOException {

		System.setProperty("org.slf4j.simpleLogger.defaultLogLevel", args.loglevel);

		PrintStream out = System.out;

		ThreadPoolExecutor executor = new ThreadPoolExecutor(10, 20, 1, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>());
		try {
			HttpAnalyticsClient client = HttpAnalyticsClient.create().withHost(args.hostname, args.port)
					.withAuthentication(args.username, args.password).build();

			out.println();
			out.println("AQL");
			out.println("---");

			AqlClient aqlclient = client.getAqlClient();

			BufferedReader reader = new BufferedReader(aqlclient.execute("truncate table client_example"));
			for (String line = reader.readLine(); line != null; line = reader.readLine()) {
				out.println(line);
			}

			out.println();
			out.println("EVENTS");
			out.println("------");

			EventClient eventclient = client.getEventClient(executor, 100, 300);
			EventReceiver receiver = eventclient.getEventReceiver("client_example");

			for (int i = 0; i < 10; i++) {

				{
					Event event = new Event();
					event.put("name", "rob");
					event.put("dayofweek", "mon");
					event.put("spend", i % 7);
					receiver.submitEvent(event);
					System.out.println(event);
				}

				{
					Event event = new Event();
					event.put("name", "rob");
					event.put("dayofweek", "tue");
					event.put("spend", i % 8);
					receiver.submitEvent(event);
					System.out.println(event);
				}

				{
					Event event = new Event();
					event.put("name", "rob");
					event.put("dayofweek", "wed");
					event.put("spend", i % 9);
					receiver.submitEvent(event);
					System.out.println(event);
				}

				{
					Event event = new Event();
					event.put("name", "rob");
					event.put("dayofweek", "thu");
					event.put("spend", i % 10);
					receiver.submitEvent(event);
					System.out.println(event);
				}
			}

			receiver.flush();

			out.println();
			out.println("QUERY");
			out.println("-----");

			QueryClient queryclient = client.getQueryClient(100);

			Result result = queryclient.query("select sum(spend) from client_example where name=rob group by dayofweek");
			for (Row row : result) {
				out.println(row);
			}

			out.println();
			out.println("PREPARED");
			out.println("--------");

			Map<String, Object> params = new HashMap<String, Object>();
			params.put("name", "rob");

			Result presult = queryclient.queryProcedure("sum_by_name", params);
			for (Row row : presult) {
				out.println(row);
			}

		} finally {
			executor.shutdownNow();
		}

	}

}
