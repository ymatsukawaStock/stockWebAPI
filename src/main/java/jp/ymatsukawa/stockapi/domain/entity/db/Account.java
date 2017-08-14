package jp.ymatsukawa.stockapi.domain.entity.db;

import jp.ymatsukawa.stockapi.tool.constant.RegExp;
import lombok.Data;

@Data
public class Account {
  public Account(long accountId) {
    this.accountId = accountId;
  }

  private long accountId;
  /*
  private String email;
  private String name;
  private String created;
  private String updated;
  */
}