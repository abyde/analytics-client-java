package com.acunu.client;

/**
 * Represents the result of query or stored procedure execution as a collection
 * of <tt>Row</tt> objects.
 * 
 * Implementations should stage the returning of rows through the iterator, so
 * that arbitrarily large result sets can be read.
 * 
 * @author rallison
 * 
 */
public interface Result extends Iterable<Row> {

}
