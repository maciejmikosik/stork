package com.mikosik.stork.common.text;

import java.lang.reflect.Field;

public class Templater {
  private final Formatter formatter;

  private Templater(Formatter formatter) {
    this.formatter = formatter;
  }

  public static Templater templater(Formatter formatter) {
    return new Templater(formatter);
  }

  public Template template(String template) {
    return new Template(template);
  }

  public class Template {
    private final String template;

    private Template(String template) {
      this.template = template;
    }

    public String model(Object model) {
      var result = template;
      for (var field : model.getClass().getFields()) {
        result = result.replace(
            "{" + field.getName() + "}",
            formatter.format(read(field, model)));
      }
      return result;
    }
  }

  private static Object read(Field field, Object instance) {
    try {
      return field.get(instance);
    } catch (ReflectiveOperationException e) {
      throw new RuntimeException(e);
    }
  }
}
