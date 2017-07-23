package jp.ymatsukawa.stockapi.tool.communication;

import jp.ymatsukawa.stockapi.tool.constant.Response;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

// FIXME: any challenging utilize
public class ResponseFormatter {
  public static<T> Map<String, Object> makeResponse(T domain) {
    Map response = new HashMap<String, Object>();
    if(domain == null) {
      response.put("statusCode", Response.CLIENT_ERROR_NOT_FOUND.getCode());
    } else {
      response.put("statusCode", Response.SUCCESS_OK.getCode());
      response.put(domain.toString(), domain);
    }

    return response;
  }

  // TODO: use general class T for put simplename
  // FIXME: IS get(0) needed? any alternative?
  public static<T> Map<String, Object> makeResponse(List<T> domainList) {
    Map response = new HashMap<String, Object>();
    if((domainList == null) || domainList.isEmpty()) {
      response.put("statusCode", Response.CLIENT_ERROR_NOT_FOUND.getCode());
    } else {
      response.put("statusCode", Response.SUCCESS_OK.getCode());
      response.put(domainList.get(0).toString(), domainList);
    }

    return response;
  }

  public static Map<String, Object> makeResponse(Response response) {
    Map res = new HashMap<String, Object>();
    res.put("statusCode", response.getCode());
    res.put("message", response.getMessage());
    return res;
  }

  public static Map<String, Object> makeErrorResponse(Response response, Set dataSet) {
    Map res = new HashMap<String, Object>();
    res.put("statusCode", response.getCode());
    res.put("message", dataSet);
    return res;
  }
}
