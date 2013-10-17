package com.acunu.client.example;

import com.beust.jcommander.Parameter;

class Arguments
{	
	@Parameter(names = "-hostname", required = true, description = "target analytics host")
	public String hostname;

	@Parameter(names = "-port", required = true, description = "target analytics port")
	public Integer port;

	@Parameter(names = "-username", description = "target analytics security username")
	public String username;

	@Parameter(names = "-password",  description = "target analytics security password")
	public String password;
	
	@Parameter(names = "-loglevel", description = "logging level: trace | debug | info | warn | error")
	public String loglevel = "info";
}