package com.acunu.performance;

import com.beust.jcommander.Parameter;

public class Arguments {

	@Parameter(names = "-ssl", description = "use https (SSL)")
	public boolean ssl = false;
	
	@Parameter(names = "-keystore", description = "SSL keystore file")
	public String keystore = "keystore";
	
	@Parameter(names = "-kspassword", description = "SSL keystore password")
	public String kspassword = "password";
	
	@Parameter(names = "-truststore", description = "SSL truststore file")
	public String truststore = "truststore";
	
	@Parameter(names = "-tspassword", description = "SSL truststore password")
	public String tspassword = "password";
	
	@Parameter(names = "-verifyhostname", arity = 1, description = "SSL ver. Set to false to disable hostname verification")
	public Boolean verifyhostname = true;	
	
	@Parameter(names = "-hostname", required = true, description = "target analytics host")
	public String hostname;

	@Parameter(names = "-port", required = true, description = "target analytics port")
	public Integer port;
	
	@Parameter(names = "-connections", description = "max number of HTTP connections to use")
	public Integer connections = 20;

	@Parameter(names = "-username", description = "target analytics security username")
	public String username;

	@Parameter(names = "-password", description = "target analytics security password")
	public String password;

	@Parameter(names = "-verbose", arity = 1, description = "Verbose. Set to false to produce data only output suitable for piping.")
	public Boolean verbose = true;

	@Parameter(names = "-eventmultiplier", description = "Event rate multiplier.")
	public Integer eventmultiplier = 1;

	@Parameter(names = "-querymultiplier", description = "Query rate multiplier.")
	public Integer querymultiplier = 1;

	@Parameter(names = "-eventbatchsize", description = "Event batch size for submission.")
	public Integer eventbatchsize = 100;
	
	@Parameter(names = "-eventqueuesize", description = "Event queue size for submission.")
	public Integer eventqueuesize = 300;
	
	@Parameter(names = "-querypagesize", description = "Query page size.")
	public Integer querypagesize = 100;

	@Parameter(names = "-duration", required = true, description = "Number of seconds for which to run the perf tool")
	public Long duration;

	@Parameter(names = "-threads", description = "Number of threads servicing the event queue")
	public Integer threads = 10;

	@Parameter(names = "-file", required = true, description = "JSON file from which to read experiment definition.")
	public String file;
	
	@Parameter(names = "-loglevel", description = "logging level: trace | debug | info | warn | error")
	public String loglevel = "info";
	
}
