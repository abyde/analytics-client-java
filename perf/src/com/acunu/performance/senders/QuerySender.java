package com.acunu.performance.senders;

import com.acunu.client.QueryClient;

public interface QuerySender {

	public int getRate();

	public void send(QueryClient client);

}
