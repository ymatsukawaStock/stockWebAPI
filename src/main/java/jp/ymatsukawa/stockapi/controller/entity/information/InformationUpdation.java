package jp.ymatsukawa.stockapi.controller.entity.information;

import jp.ymatsukawa.stockapi.tool.constant.Message;
import jp.ymatsukawa.stockapi.tool.constant.RegExp;
import lombok.Data;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.Valid;
import javax.validation.constraints.Pattern;

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
    @NotBlank(message = Message.Sentence.SUBJECT_NOT_BLANK)
    private String subject;
    @NotBlank(message = Message.Sentence.DETAIL_NOT_BLANK)
    private String detail;
    @Pattern(
      regexp = RegExp.Pattern.COMMA_SEPARATED,
      message = Message.Sentence.TAG_COMMA_SEPARATED
    )
    private String tag;
  }

  @Valid
  private Information information;
}