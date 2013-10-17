package com.acunu.client;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Represents a result row as returned by a query or stored procedure execution.
 * 
 * A <tt>Row</tt> is an ordered map of string key / object values.
 * 
 * @author rallison
 * 
 */
public class Row implements Map<String, Object> {

	private final Map<String, Object> data;

	public Row() {
		this(new LinkedHashMap<String, Object>());
	}

	public Row(Map<String, Object> data) {
		this.data = data;
	}

	public List<String> getColumns() {
		List<String> cols = new LinkedList<String>();
		cols.addAll(keySet());
		return cols;
	}

	public Object get(int index) {
		return get(getColumns().get(index));
	}

	public Object put(int index, Object value) {
		return put(getColumns().get(index), value);
	}

	public int size() {
		return data.size();
	}

	public boolean isEmpty() {
		return data.isEmpty();
	}

	public boolean containsKey(Object key) {
		return data.containsKey(key);
	}

	public boolean containsValue(Object value) {
		return data.containsValue(value);
	}

	public Object get(Object key) {
		return data.get(key);
	}

	public Object put(String key, Object value) {
		return data.put(key, value);
	}

	public Object remove(Object key) {
		return data.remove(key);
	}

	public void putAll(Map<? extends String, ? extends Object> m) {
		data.putAll(m);
	}

	public void clear() {
		data.clear();
	}

	public Set<String> keySet() {
		return data.keySet();
	}

	public Collection<Object> values() {
		return data.values();
	}

	public Set<java.util.Map.Entry<String, Object>> entrySet() {
		return data.entrySet();
	}

	public boolean equals(Object o) {
		return data.equals(o);
	}

	public int hashCode() {
		return data.hashCode();
	}

	@Override
	public String toString() {
		return data.toString();
	}

}
