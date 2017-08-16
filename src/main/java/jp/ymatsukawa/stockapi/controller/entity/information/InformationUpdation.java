package jp.ymatsukawa.stockapi.controller.entity.information;

import lombok.Data;
import lombok.NonNull;

import javax.validation.Valid;

@Data
public class InformationUpdation {
  @Data
  public class Information {
    public Information() {}
    @Valid
    public Information(String subject, String detail, String tag) {
      this.subject = subject;
      this.detail = detail;
      this.tag = tag;
    }
    @NonNull
    private String subject;
    @NonNull
    private String detail;
    @NonNull
    private String tag;
  }

  @Valid
  private Information information;
}