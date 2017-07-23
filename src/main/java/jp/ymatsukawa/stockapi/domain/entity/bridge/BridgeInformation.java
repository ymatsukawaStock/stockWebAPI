package jp.ymatsukawa.stockapi.domain.entity.bridge;


import com.fasterxml.jackson.annotation.JsonInclude;
import jp.ymatsukawa.stockapi.domain.entity.db.Information;
import lombok.Data;

import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BridgeInformation {
  public BridgeInformation(Information Information, List<String> tag) {
    this.informationId = Information.getInformationId();
    this.subject = Information.getSubject();
    this.created = Information.getCreated();
    this.updated = Information.getUpdated();
    this.tag = tag;
  }

  private long informationId;
  private String subject;
  private String created;
  private String updated;
  private List<String> tag;

  /**
   * return projection class name by lower case.
   * @return "information"
   */
  @Override
  public String toString() {
    // FIXME: use constant name
    return "information";
  }
}
