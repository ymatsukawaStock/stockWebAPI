package jp.ymatsukawa.stockapi.domain.entity.db;

import jp.ymatsukawa.stockapi.tool.constant.Message;
import jp.ymatsukawa.stockapi.tool.constant.RegExp;
import lombok.Data;

import javax.validation.constraints.Pattern;

@Data
public class Tag {
  public Tag() {}
  public Tag(String name) {
    this.name = name;
  }
  private long tagId;
  private String name;
}