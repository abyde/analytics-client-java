package com.acunu.client.http;

import java.io.File;
import java.io.FileInputStream;
import java.security.KeyStore;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executor;

import javax.net.ssl.SSLContext;

import net.minidev.json.JSONValue;

import org.apache.http.client.HttpClient;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLContexts;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;

import com.acunu.client.AqlClient;
import com.acunu.client.EventClient;
import com.acunu.client.QueryClient;

public class HttpAnalyticsClient {

	private static final String defaultAuthPath = "/analytics/api/auth/session";

	private final HttpClientWrapper client;

	/**
	 * Fluent style builder
	 * 
	 * @author rallison
	 * 
	 */
	public static class Builder {

		private HttpClient client;
		private String protocol;
		private String hostname;
		private int port;
		private String username = null;
		private String password = null;
		private int connections = 20;

		/**
		 * Set hostname and port
		 * 
		 * @param hostname
		 *            analytics host name
		 * @param port
		 *            analytics port
		 * @return Builder
		 */
		public Builder withHost(String hostname, int port) {

			this.hostname = hostname;
			this.port = port;
			return this;
		}

		/**
		 * Set authentication credentials
		 * 
		 * @param username
		 * @param password
		 * @return Builder
		 */
		public Builder withAuthentication(String username, String password) {

			this.username = username;
			this.password = password;
			return this;
		}

		/**
		 * Set a custom configured Apache HttpClient
		 * 
		 * @param client
		 * @return Builder
		 */
		public Builder withClient(HttpClient client) {
			this.client = client;
			return this;
		}

		/**
		 * Set protocol. Use when setting a custom configured HttpClient
		 * 
		 * @param protocol
		 * @return Builder
		 */
		public Builder withProtocol(String protocol) {
			this.protocol = protocol;
			return this;
		}

		/**
		 * Set the max number of http connections to use. Will default to 20 if
		 * not set here.
		 * 
		 * @param connections
		 * @return Builder
		 */
		public Builder withConnections(int connections) {
			this.connections = connections;
			return this;
		}

		/**
		 * Configures transport over HTTP.
		 * 
		 * @return Builder
		 */
		public Builder withHttp() {
			PlainConnectionSocketFactory plainsf = PlainConnectionSocketFactory.getSocketFactory();
			RegistryBuilder<ConnectionSocketFactory> registryBuilder = RegistryBuilder.<ConnectionSocketFactory> create();
			registryBuilder.register("http", plainsf);
			PoolingHttpClientConnectionManager cm = new PoolingHttpClientConnectionManager(registryBuilder.build());
			cm.setDefaultMaxPerRoute(connections);
			cm.setMaxTotal(connections);
			client = HttpClients.custom().setConnectionManager(cm).build();
			protocol = "http";
			return this;
		}

		/**
		 * Configures transport of SSL
		 * 
		 * @param keystore
		 *            keystore for client certificate
		 * @param truststore
		 *            truststore for trusted server certificate
		 * @param kspassword
		 *            password for keystore
		 * @param tspassword
		 *            password for truststore
		 * @param verifyhostname
		 *            verify server hostname when connecting
		 * @return Builder
		 */
		public Builder withSSL(File keystore, File truststore, String kspassword, String tspassword, boolean verifyhostname) {

			SSLContext sslContext;
			try {
				KeyStore ks = KeyStore.getInstance("jks");
				ks.load(new FileInputStream(keystore), kspassword.toCharArray());
				KeyStore ts = KeyStore.getInstance("jks");
				ts.load(new FileInputStream(truststore), tspassword.toCharArray());
				sslContext = SSLContexts.custom().loadKeyMaterial(ks, kspassword.toCharArray()).useTLS().loadTrustMaterial(ts).build();
			} catch (Exception e) {
				throw new RuntimeException(e);
			}

			SSLConnectionSocketFactory sslsf = verifyhostname ? new SSLConnectionSocketFactory(sslContext) : new SSLConnectionSocketFactory(
					sslContext, SSLConnectionSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);

			RegistryBuilder<ConnectionSocketFactory> registryBuilder = RegistryBuilder.<ConnectionSocketFactory> create();
			registryBuilder.register("https", sslsf);
			PoolingHttpClientConnectionManager cm = new PoolingHttpClientConnectionManager(registryBuilder.build());
			cm.setDefaultMaxPerRoute(connections);
			cm.setMaxTotal(connections);
			client = HttpClients.custom().setConnectionManager(cm).build();
			protocol = "https";
			return this;
		}

		/**
		 * Builds a client
		 * 
		 * @return HttpAnalyticsClient
		 */
		public HttpAnalyticsClient build() {
			return new HttpAnalyticsClient(client, protocol, hostname, port, username, password);
		}
	}

	/**
	 * open a client builder
	 * 
	 * @return Builder
	 */
	public static Builder create() {
		return new Builder();
	}

	/**
	 * Construct with a custom configured instance of Apache HttpClient.
	 * 
	 * @param client
	 *            Apache client instance
	 * @param protocol
	 *            transport protocol (http/https)
	 * @param hostname
	 *            analytics server hostname
	 * @param port
	 *            analytics server port
	 * @param username
	 *            analytics security username, use null when security not
	 *            enabled
	 * @param password
	 *            analytics security password, use null when security not
	 *            enabled
	 */
	public HttpAnalyticsClient(HttpClient client, String protocol, String hostname, int port, String username, String password) {

		this.client = new HttpClientWrapper(client, protocol, hostname, port);

		if (!(username == null && password == null)) {
			login(username, password);
		}
	}

	/**
	 * Creates a client for executing AQL statements over HTTP.
	 * 
	 * @return AQL client implementation
	 */
	public AqlClient getAqlClient() {
		return new HttpAqlClient(client);
	}

	/**
	 * 
	 * Creates a client for sending events over HTTP. Events are batched up
	 * before sending, and a worker thread pool is used to carry out the
	 * transfer. A bounded queue sits in front of the worker threads, allowing
	 * client threads to place events and blocking only when the queue has
	 * reached its limit.
	 * 
	 * Pass thread pool implementation, batch size and queue size.
	 * 
	 * @param executor
	 *            Thread pool implementation
	 * @param batchsize
	 *            size of event batches
	 * @param queuesize
	 *            size of event queue
	 * @return Event client implementation
	 */
	public EventClient getEventClient(Executor executor, int batchsize, int queuesize) {
		return new HttpEventClient(client, executor, batchsize, queuesize);
	}

	/**
	 * Creates a client for executing queries and stored procedures over HTTP.
	 * Results are transparently paged from the server, with configurable page
	 * size.
	 * 
	 * @param pagesize
	 *            size of result pages
	 * @return Query client implementation
	 */
	public QueryClient getQueryClient(int pagesize) {
		return new HttpQueryClient(client, pagesize);
	}

	private void login(String username, String password) {

		Map<String, String> headers = new HashMap<String, String>();
		headers.put("Accept", "application/json");
		headers.put("Content-Type", "application/json");
		String data = String.format("{\"username\":\"%s\",\"password\":\"%s\"}", JSONValue.escape(username),
				JSONValue.escape(String.valueOf(password)));

		client.post(defaultAuthPath, headers, data);
	}

}
