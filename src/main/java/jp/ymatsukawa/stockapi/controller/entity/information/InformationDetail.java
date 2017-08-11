package jp.ymatsukawa.stockapi.controller.entity.information;

import jp.ymatsukawa.stockapi.tool.constant.RegExp;
import lombok.Data;

import javax.validation.constraints.Pattern;

@Data
public class InformationDetail {
  /**
   * constructor should be executed by developer in controller, not by @RequestBody<br />
   * ex. new InformationDetail(20)
   * @param informationId
   */
  public InformationDetail(long informationId) {
    this.informationId = informationId;
  }

  private long informationId;
}