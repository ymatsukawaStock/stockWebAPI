package jp.ymatsukawa.stockapi.tool.converter;

import jp.ymatsukawa.stockapi.domain.exception.StockException;

import java.util.Arrays;
import java.util.List;

public class ListConverter {
  /**
   * Returns List&lt;String&gt; each element is value which is separated by splitRegEx.<br />
   * @param value - string value you want to split.
   * @param splitRegEx - split regex you want to.
   * @return List&lt;String&gt; each element is value which is separated by splitRegEx
   * @throws StockException - when value or splitRegEx is null or blank
   */
  public static List<String> getListBySplit(String value, String splitRegEx) {
    return Arrays.asList(value.split(splitRegEx));
  }
}
