package com.mikosik.stork.tool;

import com.mikosik.stork.common.table.Table;
import com.mikosik.stork.data.model.Expression;

public class Binary {
  public final Table<String, Expression> table;

  private Binary(Table<String, Expression> table) {
    this.table = table;
  }

  public static Binary binary(Table<String, Expression> table) {
    return new Binary(table);
  }
}
