package com.acunu.performance;

import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

import com.acunu.client.QueryClient;
import com.acunu.client.Result;

public class CountingQueryClient implements QueryClient {

	private final QueryClient client;
	private final AtomicLong count = new AtomicLong();

	public CountingQueryClient(QueryClient client) {
		this.client = client;
	}
	
	public long getCount( ){
		return count.getAndSet(0);
	}

	@Override
	public Result query(String aql) {
		Result result = client.query(aql);
		count.incrementAndGet();
		return result;
	}

	@Override
	public Result queryProcedure(String name, Map<String, Object> params) {
		Result result = client.queryProcedure(name, params);
		count.incrementAndGet();
		return result;
	}

}
