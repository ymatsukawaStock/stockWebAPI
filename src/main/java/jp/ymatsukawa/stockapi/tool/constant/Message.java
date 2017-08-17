package jp.ymatsukawa.stockapi.tool.constant;

import lombok.Getter;

public enum Message {
  LIMIT_GREATER_THAN(Sentence.LIMIT_GREATER_THAN),
  SUBJECT_NOT_BLANK(Sentence.SUBJECT_NOT_BLANK),
  DETAIL_NOT_BLANK(Sentence.DETAIL_NOT_BLANK),
  TAG_COMMA_SEPARATED(Sentence.TAG_COMMA_SEPARATED),
  SORT_ASC_OR_DESC(Sentence.SORT_ASC_OR_DESC),
  SORT_BY_CREATED_OR_UPDATED(Sentence.SORT_BY_CREATED_OR_UPDATED);

  public static class Sentence {
    public static final String LIMIT_GREATER_THAN = "limit should be greater than 1.";
    public static final String SUBJECT_NOT_BLANK = "subject should not be blank.";
    public static final String DETAIL_NOT_BLANK = "detail should not be blank.";
    public static final String TAG_COMMA_SEPARATED = "tag should be blank, single word or comma separated.";
    public static final String SORT_ASC_OR_DESC = "sort should be asc or desc.";
    public static final String SORT_BY_CREATED_OR_UPDATED = "sortBy should be created or updated.";
  }

  private Message(String message) {
    this.message= message;
  }

  @Getter
  private final String message;
}
