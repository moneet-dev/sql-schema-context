package dev.moneet.schema.domain;

import java.util.List;
import java.util.Set;

public final class Table {
	String name;
	List<Column> columns;
	Set<PrimaryKey> primaryKeys;
	List<ForeignKey> foreignKeys;
	List<Index> indexes;
		public List<Column> getColumns() {
		return columns;
	}

	public Set<String> getPrimaryKeys() {
		return primaryKeys.stream()
				.map(PrimaryKey::getColumnName)
				.collect(java.util.stream.Collectors.toSet());
	}
	

	public List<ForeignKey> getForeignKeys() {
		return foreignKeys;
	}

	public Table(String name, List<Column> columns, Set<PrimaryKey> primaryKeys, List<ForeignKey> foreignKeys, List<Index> indexes) {
		this.name = name;
		this.columns = columns;
		this.primaryKeys = primaryKeys;
		this.foreignKeys = foreignKeys;
		this.indexes = indexes;
	}

	public String getName() {
		return name;
	}
}
