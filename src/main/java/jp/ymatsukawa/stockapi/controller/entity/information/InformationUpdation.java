package jp.ymatsukawa.stockapi.controller.entity.information;

import jp.ymatsukawa.stockapi.domain.entity.db.Information;
import jp.ymatsukawa.stockapi.domain.entity.db.Tag;
import lombok.Data;

import javax.validation.Valid;

/**
 * Union beans; Information and Tag.
 * Validation is delgated to each bean.
 */
@Data
public class InformationUpdation {
  @Valid
  private Information information;
  @Valid
  private Tag tag;
}