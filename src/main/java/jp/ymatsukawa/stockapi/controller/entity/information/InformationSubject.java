package jp.ymatsukawa.stockapi.controller.entity.information;

import jp.ymatsukawa.stockapi.tool.constant.RegExp;
import lombok.Data;

import javax.validation.constraints.Pattern;

@Data
public class InformationSubject {
  /**
   * constructor should be executed by developer in controller, not by @RequestBody<br />
   * ex. new InformationSubject(20, "foo,bar", "created", "asc")
   * @param limit
   * @param tag
   * @param sort
   * @param sortBy
   */
  public InformationSubject(long limit, String tag, String sort, String sortBy) {
    this.limit = limit;
    this.tag = tag;
    this.sort = sort;
    this.sortBy = sortBy;
  }

  private long limit;

  @Pattern(
    regexp = RegExp.Pattern.COMMA_SEPARATED,
    message = "tag name should be a single word or comma separated"
  )
  private String tag;

  @Pattern(
    regexp = RegExp.Pattern.CREATED_OR_UPDATED,
    message = "sort should be createdor updated"
  )
  private String sort;

  @Pattern(
    regexp = RegExp.Pattern.ASC_OR_DESC,
    message = "sortBy should be asc or desc"
  )
  private String sortBy;
}