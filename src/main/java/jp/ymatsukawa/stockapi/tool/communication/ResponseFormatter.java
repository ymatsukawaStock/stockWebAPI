package jp.ymatsukawa.stockapi.tool.communication;

import org.springframework.http.HttpStatus;

import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ResponseFormatter {
  /**
   * make return able "string to single data" object to client. <br />
   * when entity is null or empty, response becomes 400 and body is null.<br />
   * when entity is not empty, response is 200 and body is formatted like <br />
   * entity; 1st arg should override toString() as JSON's root key name<br />
   *
   * ex. <br />
   * Status: 200 OK<br />
   * {<br />
   * &nbsp;&nbsp; "entityRootKeyName": { <br/>
   * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; "key1": "value", <br />
   * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; "key2": "value" <br />
   * &nbsp;&nbsp;&nbsp;&nbsp;}<br />
   * }<br />
   * <br />
   * Status: 404 Not Found<br />
   * -- body is blank
   * @param entity DB entity
   * @param httpResponse httpServletResponse
   * @param <T> EntityBean class
   * @return Map&lt;String, Object&gt; - return able string to single object
   */
  public static<T> Map<String, Object>makeResponse(T entity, HttpServletResponse httpResponse) {
    Map<String, Object> result = null;
    if(entity == null) {
      httpResponse.setStatus(HttpStatus.NOT_FOUND.value());
    } else {
      httpResponse.setStatus(HttpStatus.OK.value());
      result = new HashMap<>();
      result.put(entity.toString(), entity);
    }

    return result;
  }

  /**
   * make return able "string to list data" object to client. <br />
   * when entity is null or empty, response becomes 400 and body is null.
   * when entity is not empty, response is 200 and body is formatted like <br />
   * each entity of entityList; 1st arg should override toString() as JSON's root key name<br />
   * <br />
   * ex. <br />
   * Status: 200 OK<br />
   * {<br />
   * &nbsp;&nbsp; "entityName":{<br/>
   * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; [<br />
   * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; {"key1": "value"}, <br />
   * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; {"key1": "value"} <br />
   * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; ]<br />
   * &nbsp;&nbsp;&nbsp;&nbsp;}<br />
   * }<br />
   * <br />
   * Status: 404 Not Found<br />
   * -- body is blank
   * @param entityList list which same DB entity is added
   * @param httpResponse httpServletResponse
   * @param <T> EntityBean class
   * @return Map&lt;String, Object&gt; - return able string to list object
   */
  public static<T> Map<String, Object> makeResponse(List<T> entityList, HttpServletResponse httpResponse) {
    Map<String, Object> result = null;
    if((entityList == null) || entityList.isEmpty()) {
      httpResponse.setStatus(HttpStatus.NOT_FOUND.value());
    } else {
      httpResponse.setStatus(HttpStatus.OK.value());
      result = new HashMap<>();
      result.put(entityList.get(0).toString(), entityList);
    }
    return result;
  }

  /**
   * make return able "message to 'http response reason'" object.<br />
   * 'http response reason' is decided by status code.<br />
   * @param httpStatus HTTP Status, HttpStatus.OK, HttpStatus.INTERNAL_SERVER_ERROR and etc.
   * @param httpResponse HttpServletResponse
   * @return Map&lt;String, Object&gt; - "message to 'http response reason'"
   */
  public static Map<String, Object> makeResponse(HttpStatus httpStatus, HttpServletResponse httpResponse) {
    httpResponse.setStatus(httpStatus.value());
    return (new HashMap<String, Object>() {
      { put("message", httpStatus.getReasonPhrase()); }
    });
  }

  /**
   * make return able "message to 'array of error messages'" object.<br />
   * response status code is 404 fixed.<br />
   * <br />
   * Status: 404 Bad Request<br />
   * {<br />
   * &nbsp;&nbsp; "message":[<br/>
   * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"abc is blank", <br />
   * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"def is blank", <br />
   * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"ghi is permitted only number"<br />
   * &nbsp;&nbsp;&nbsp;&nbsp;]<br />
   * }<br />
   * @param errorMessageSet set of error messages
   * @param httpResponse HttpServletResponse
   * @return Map&lt;String, Object&gt; - "message to 'array of error messages'"
   */
  public static Map<String, Object> makeErrorResponse(Set<String> errorMessageSet, HttpServletResponse httpResponse) {
    httpResponse.setStatus(HttpStatus.BAD_REQUEST.value());
    return (new HashMap<String, Object>() {
      { put("message", errorMessageSet); }
    });
  }

}
