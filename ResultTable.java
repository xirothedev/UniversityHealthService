package com.uhs.model;

import java.util.List;

/** A query result held in memory: column labels plus rows of display values. */
public record ResultTable(List<String> columns, List<Object[]> rows) {}
