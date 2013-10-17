package com.acunu.client;

import java.io.Reader;

/**
 * Client interface for executing AQL statements. The result is returned as a
 * Reader to a JSON document.
 * 
 * @author rallison
 * 
 */
public interface AqlClient {

	/**
	 * Executes arbitrary AQL statements returning the results as a Reader to a
	 * JSON document
	 * 
	 * @param aql
	 *            valid AQL statement
	 * @return Reader to a JSON document with statement execution results
	 */
	public Reader execute(String aql);

}
