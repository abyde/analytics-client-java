package com.acunu.performance.senders;

import java.util.HashMap;
import java.util.Map;

import com.acunu.client.QueryClient;
import com.acunu.performance.ValueGenerator;

public class ProcedureQuerySender implements QuerySender {

	private String procedure;
	private int rate;
	private Map<String, Object> parameters;

	@Override
	public void send(QueryClient client) {

		Map<String, Object> generated = new HashMap<String, Object>();
		for (Map.Entry<String, Object> entry : parameters.entrySet()) {
			String key = entry.getKey();
			Object value = entry.getValue();
			if (value instanceof ValueGenerator) {
				value = ((ValueGenerator) value).generate();
			}
			generated.put(key, value);
		}
		
		client.queryProcedure(procedure, generated);
	}

	public String getProcedure() {
		return procedure;
	}

	public void setProcedure(String procedure) {
		this.procedure = procedure;
	}

	public int getRate() {
		return rate;
	}

	public void setRate(int rate) {
		this.rate = rate;
	}

	public Map<String, Object> getParameters() {
		return parameters;
	}

	public void setParameters(Map<String, Object> parameters) {
		this.parameters = parameters;
	}
}
