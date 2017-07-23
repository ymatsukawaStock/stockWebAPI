package jp.ymatsukawa.stockapi.domain.entity.bridge;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BridgeInformationTags {
  public BridgeInformationTags(long informationId, String tag) {
    this.informationId = informationId;
    this.tag = tag;
  }
  private long informationId;
  private String tag;
}
