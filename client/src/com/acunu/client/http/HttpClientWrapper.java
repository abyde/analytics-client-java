package com.acunu.client.http;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.HttpMessage;
import org.apache.http.HttpResponse;
import org.apache.http.ParseException;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.InputStreamEntity;
import org.apache.http.entity.StringEntity;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.helpers.MessageFormatter;

import com.acunu.client.AcunuClientException;

class HttpClientWrapper {

	private static final Logger log = LoggerFactory.getLogger(HttpClientWrapper.class);

	private final HttpClient apacheclient;
	private final String protocol;
	private final String hostname;
	private final int port;

	HttpClientWrapper(HttpClient apacheclient, String protocol, String hostname, int port) {
		this.apacheclient = apacheclient;
		this.protocol = protocol;
		this.hostname = hostname;
		this.port = port;
	}

	String get(String path, Map<String, String> headers) {

		try {
			URL url = new URL(protocol, hostname, port, path);
			HttpGet get = new HttpGet(url.toURI());
			setHeaders(get, headers);
			HttpResponse response = apacheclient.execute(get);
			checkResponse(response);
			return EntityUtils.toString(response.getEntity());

		} catch (Exception e) {
			String msg = MessageFormatter.format("exception executing get from url: {}", path).getMessage();
			log.error(msg, e);
			throw new AcunuClientException(msg, e);
		}
	}

	String post(String path, Map<String, String> headers, String data) {

		try {
			return post(path, headers, new StringEntity(data));
		} catch (UnsupportedEncodingException e) {
			String msg = MessageFormatter.format("exception executing post to url: {}", path).getMessage();
			log.error(msg, e);
			throw new AcunuClientException(msg, e);
		}
	}

	String post(String path, Map<String, String> headers, InputStream istream) {
		return post(path, headers, new InputStreamEntity(istream));
	}

	String post(String path, Map<String, String> headers, HttpEntity entity) {
		try {
			URL url = new URL(protocol, hostname, port, path);
			HttpPost post = new HttpPost(url.toURI());
			setHeaders(post, headers);
			post.setEntity(entity);
			HttpResponse response = apacheclient.execute(post);
			checkResponse(response);
			return EntityUtils.toString(response.getEntity());

		} catch (Exception e) {
			String msg = MessageFormatter.format("exception executing post to url: {}", path).getMessage();
			log.error(msg, e);
			throw new AcunuClientException(msg, e);
		}
	}

	private void setHeaders(HttpMessage message, Map<String, String> headers) {

		for (Map.Entry<String, String> entry : headers.entrySet()) {
			message.setHeader(entry.getKey(), entry.getValue());
		}
	}

	private void checkResponse(HttpResponse response) {
		StatusLine status = response.getStatusLine();
		if (status.getStatusCode() != 200) {

			String content = null;
			try {
				content = EntityUtils.toString(response.getEntity());
			} catch (ParseException e) {
				content = "!Content unparsable!";
			} catch (IOException e) {
				content = "!Content unreadable!";
			}

			throw new RuntimeException("bad HTTP response status: " + status.getStatusCode() + ": " + status.getReasonPhrase() + " " + content);
		}
	}
}
