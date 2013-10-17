package com.acunu.performance.senders;

import java.util.List;

import com.acunu.client.QueryClient;
import com.acunu.performance.ValueGenerator;

public class ParameterisedQuerySender implements QuerySender {

	private int rate;
	private String aql;
	private List<Object> parameters;

	public void send(QueryClient client) {

		Object[] values = new Object[parameters.size()];
		for (int i = 0; i < parameters.size(); i++) {

			Object value = parameters.get(i);
			if (value instanceof ValueGenerator) {
				value = ((ValueGenerator) value).generate();
			}
			values[i] = value;
		}

		String faql = String.format(aql, values);
		client.query(faql);
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

	public List<Object> getParameters() {
		return parameters;
	}

	public void setParameters(List<Object> parameters) {
		this.parameters = parameters;
	}
}
