package com.acunu.performance.senders;

import com.acunu.client.QueryClient;

public class PlainQuerySender implements QuerySender {

	private int rate;
	private String aql;

	@Override
	public void send(QueryClient client) {
		client.query(aql);
	}

	public int getRate() {
		return rate;
	}

	public void setRate(int rate) {
		this.rate = rate;
	}

	public String getAql() {
		return aql;
	}

	public void setAql(String aql) {
		this.aql = aql;
	}

}
