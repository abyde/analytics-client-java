package com.acunu.client.http;

import java.io.Reader;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;

import net.minidev.json.JSONArray;
import net.minidev.json.parser.JSONParser;
import net.minidev.json.parser.ParseException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.helpers.MessageFormatter;

import com.acunu.client.AcunuClientException;
import com.acunu.client.QueryClient;
import com.acunu.client.Result;
import com.acunu.client.Row;

class HttpQueryClient implements QueryClient {

	public static final String DEFAULT_QUERY_PATH = "/analytics/api/data?q=%s";
	public static final String DEFAULT_PROCEDURE_PATH = "/analytics/api/data/procedure/%s%s";
	public static final int DEFAULT_PAGE_SIZE = 100;

	private static final Logger log = LoggerFactory.getLogger(HttpQueryClient.class);

	private static final String CHARSET = "UTF-8";
	private final HttpClientWrapper client;
	private final String querypath;
	private final String procedurepath;
	private final int pagesize;

	HttpQueryClient(HttpClientWrapper client, int pagesize) {
		this.client = client;
		this.querypath = DEFAULT_QUERY_PATH;
		this.procedurepath = DEFAULT_PROCEDURE_PATH;
		this.pagesize = pagesize;
	}

	@Override
	public Result query(String aql) {

		try {
			String encodedaql = URLEncoder.encode(aql, CHARSET);
			String qpath = String.format(querypath, encodedaql);
			log.debug("executing query: '{}', path: {}", aql, qpath);

			return pagedResult(qpath);
		} catch (UnsupportedEncodingException e) {
			String msg = MessageFormatter.format("exception executing query: '{}', url: {}", querypath, aql).getMessage();
			log.error(msg, e);
			throw new AcunuClientException(msg, e);
		}
	}

	@Override
	public Result queryProcedure(String name, Map<String, Object> params) {

		try {
			StringBuilder builder = new StringBuilder();
			boolean first = true;
			for (Map.Entry<String, Object> param : params.entrySet()) {

				if (first) {
					builder.append('?');
				} else {
					builder.append('&');
					first = false;
				}
				builder.append(URLEncoder.encode(param.getKey(), CHARSET));
				builder.append("=");
				builder.append(URLEncoder.encode(param.getValue().toString(), CHARSET));
			}

			String qpath = String.format(procedurepath, name, builder.toString());
			log.debug("executing procedure: {}, params: {}, path: {}", name, params, qpath);
			return pagedResult(qpath);

		} catch (UnsupportedEncodingException e) {
			String msg = MessageFormatter.format("exception executing procedure: {}, params: {}, path: {}",
					new Object[] { name, params, procedurepath }).getMessage();
			log.error(msg, e);
			throw new AcunuClientException(msg, e);
		}
	}

	private Result pagedResult(final String qpath) {

		return new Result() {

			@Override
			public Iterator<Row> iterator() {
				return new Iterator<Row>() {

					private int index = 0;
					private Iterator<Row> iterator;

					{
						nextPage();
					}

					@Override
					public boolean hasNext() {
						if (iterator.hasNext()) {
							return true;
						} else {
							nextPage();
							if (iterator.hasNext()) {
								return true;
							} else {
								return false;
							}
						}
					}

					@Override
					public Row next() {
						if (iterator.hasNext()) {
							return iterator.next();
						} else {
							nextPage();
							if (iterator.hasNext()) {
								return iterator.next();
							} else {
								throw new NoSuchElementException();
							}
						}
					}

					@Override
					public void remove() {
						throw new UnsupportedOperationException();
					}

					private void nextPage() {

						String pagedpath = String.format("%s&_page_start=%d&_page_size=%d&_headers=all", qpath, index, pagesize);

						log.debug("paging query, path: {}", pagedpath);

						Map<String, String> headers = new HashMap<String, String>();
						headers.put("Accept", "application/json");
						String results = client.get(pagedpath, headers);
						iterator = wrapResultStream(new StringReader(results));
						index += pagesize;
					}
				};
			}
		};
	}

	private Iterator<Row> wrapResultStream(Reader reader) {

		try {
			JSONArray array = (JSONArray) new JSONParser(JSONParser.DEFAULT_PERMISSIVE_MODE).parse(reader);

			final Iterator<Object> iterator = array.iterator();
			
			final JSONArray names = (JSONArray)iterator.next();
			
			// read in the types information
			// as results are in JSON, strings and numerical values are disambiguated
			// however it may be nice to promote things like timestamps etc using types
			@SuppressWarnings("unused")
			final JSONArray types = (JSONArray)iterator.next();

			return new Iterator<Row>() {

				@Override
				public boolean hasNext() {
					return iterator.hasNext();
				}

				@Override
				public Row next() {

					Row row = new Row();
					JSONArray values = (JSONArray) iterator.next();
					for (int i = 0; i < values.size(); i++ )
					{
						row.put((String)names.get(i), values.get(i));
					}
					return row;
				}

				@Override
				public void remove() {
					throw new UnsupportedOperationException();
				}
			};
		} catch (ParseException e) {
			String msg = "exception opening rows";
			log.error(msg, e);
			throw new AcunuClientException(msg, e);
		}
	}
}
