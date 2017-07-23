package jp.ymatsukawa.stockapi.tool.constant;

import lombok.Getter;

public enum RegExp {
  COMMA_SEPARATED(Pattern.COMMA_SEPARATED),
  ASC_OR_DESC(Pattern.ASC_OR_DESC),
  CREATED_OR_UPDATED(Pattern.CREATED_OR_UPDATED);

  public static class Pattern {
    public static final String COMMA_SEPARATED = "\\p{Alnum}*|(\\p{Alnum}+)(,\\p{Alnum}+)";
    public static final String ASC_OR_DESC = "^asc$|^desc$";
    public static final String CREATED_OR_UPDATED = "^created$|^updated$";
  }

  private RegExp(String pattern) {
    this.pattern = pattern;
  }

  @Getter
  private final String pattern;
}
