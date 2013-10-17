package com.acunu.client.http;

import java.io.Reader;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.acunu.client.AqlClient;

class HttpAqlClient implements AqlClient {

	public static final String DEFAULT_AQL_PATH = "/analytics/api/aql";

	private static final Logger log = LoggerFactory.getLogger(HttpAqlClient.class);

	private final HttpClientWrapper client;
	private final String aqlpath;

	HttpAqlClient(HttpClientWrapper client) {
		this.client = client;
		this.aqlpath = DEFAULT_AQL_PATH;
	}

	@Override
	public Reader execute(final String aql) {

		log.debug("executing aql: '{}', path: {}", aql, aqlpath);

		Map<String, String> headers = new HashMap<String, String>();
		headers.put("Accept", "application/json, text/javascript");
		headers.put("Content-Type", "text/plain");
		return new StringReader(client.post(aqlpath, headers, aql));
	}

}
