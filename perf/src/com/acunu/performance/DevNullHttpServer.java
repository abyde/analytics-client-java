package com.acunu.performance;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

public class DevNullHttpServer {

	public static void main(String[] args) throws IOException {

		int port = args.length > 0 ? Integer.parseInt(args[0]) : 8080;

		HttpServer server = HttpServer.create(new InetSocketAddress("localhost", port), 10000);
		server.setExecutor(Executors.newCachedThreadPool());
		server.createContext("/", new HttpHandler() {

			long start = System.currentTimeMillis();
			int count = 0;

			@Override
			public void handle(HttpExchange ex) throws IOException {

				int lines = 0;
				BufferedReader reader = new BufferedReader(new InputStreamReader(ex.getRequestBody()));
				while (reader.readLine() != null) {
					lines++;
				}
				reader.close();

				ex.sendResponseHeaders(200, 0);
				ex.close();

				synchronized (this) {
					count += lines;
					long now = System.currentTimeMillis();
					if (now - start > 1000) {
						System.out.printf("lines/s: %d\n", count);
						start = now;
						count = 0;
					}
				}
			}
		});

		server.start();

		System.out.println("dev null http server started");
	}
}
