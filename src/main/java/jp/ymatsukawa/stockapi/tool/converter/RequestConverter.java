package jp.ymatsukawa.stockapi.tool.converter;

import javax.servlet.http.HttpServletRequest;

public class RequestConverter {
  public static<T> T getRequestAttribute(HttpServletRequest httpServletRequest, String keyName) {
    T object = (T) httpServletRequest.getAttribute(keyName);
    return object;
  }
}
