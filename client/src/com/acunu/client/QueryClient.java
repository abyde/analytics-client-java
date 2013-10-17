package com.acunu.client;

import java.util.Map;

/**
 * Client interface for querying analytics data. Provides for the execution of
 * AQL queries and stored procedures. Each returns a list of rows, the rows are
 * staged such that large result sets can be returned.
 * 
 * @author rallison
 * 
 */
public interface QueryClient {

	/**
	 * Executes an AQL query and returns the result rows.
	 * 
	 * @param aql
	 *            A valid AQL string corresponding to an analytics query.
	 * @return A Result object containing result rows from the query execution.
	 */
	public Result query(String aql);

	/**
	 * Executes a stored procedure passing a map of parameters and returns the
	 * result rows.
	 * 
	 * @param name
	 *            the name of the stored procedure to be executed.
	 * @param params
	 *            A map of parameters to be passed to the stored procedure
	 * @return A Result object containing result rows from the stored procedure
	 *         execution.
	 */
	public Result queryProcedure(String name, Map<String, Object> params);

}
