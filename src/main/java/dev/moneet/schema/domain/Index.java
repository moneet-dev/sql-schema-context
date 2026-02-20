package dev.moneet.schema.domain;

import java.util.List;

public final class Index {
	String name;
	List<String> columns;
	boolean unique;
	public Index(String name, List<String> columns, boolean unique) {
		this.name = name;
		this.columns = columns;
		this.unique = unique;
	}
}
