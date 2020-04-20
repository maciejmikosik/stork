package com.mikosik.stork.tool.run;

import com.mikosik.stork.data.model.Expression;

public interface Runner {
  Expression run(Expression expression);
}
