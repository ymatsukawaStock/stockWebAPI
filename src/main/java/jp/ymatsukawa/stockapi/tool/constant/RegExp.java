package jp.ymatsukawa.stockapi.tool.constant;

import lombok.Getter;

public enum RegExp {
  COMMA_SEPARATED(Pattern.COMMA_SEPARATED),
  ASC_OR_DESC(Pattern.ASC_OR_DESC),
  CREATED_OR_UPDATED(Pattern.CREATED_OR_UPDATED);

  public static class Pattern {
    public static final String COMMA_SEPARATED = "(\\A[^,]*\\z)|(\\A([^,]+,)+[^,]+\\z)";
    public static final String ASC_OR_DESC = "\\Aasc\\z|\\Adesc\\z";
    public static final String CREATED_OR_UPDATED = "\\Acreated\\z|^updated\\z";
  }

  private RegExp(String pattern) {
    this.pattern = pattern;
  }

  @Getter
  private final String pattern;
}
