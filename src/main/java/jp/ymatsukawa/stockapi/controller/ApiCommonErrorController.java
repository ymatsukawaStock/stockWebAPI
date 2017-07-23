package jp.ymatsukawa.stockapi.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.web.DefaultErrorAttributes;
import org.springframework.boot.autoconfigure.web.ErrorAttributes;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.RequestAttributes;

import java.util.HashMap;
import java.util.Map;

// FIXME: output reason each reason why bad request happened.
// FIXME: Write Comment
@RestController
public class ApiCommonErrorController extends DefaultErrorAttributes {
  @Autowired
  ErrorAttributes errorAttributes;

  @Override
  public Map<String, Object> getErrorAttributes(RequestAttributes requestAttributes,
                                                boolean includeStackTrace) {
    Map<String, Object> errorAttributes = new HashMap<>();
    addStatus(errorAttributes, requestAttributes);
    return errorAttributes;
  }

  /**
   * imitates DefaultErrorAttributes' getErrorAttributes
   * @param errorAttributes
   * @param requestAttributes
   */
  private void addStatus(Map<String, Object> errorAttributes,
                         RequestAttributes requestAttributes) {
    Integer status = getAttribute(requestAttributes, "javax.servlet.error.status_code");
    if (status == null) {
      errorAttributes.put("statusCode", 999);
      errorAttributes.put("errorMessage", "None");
      return;
    }

    errorAttributes.put("statusCode", status);
    try {
      errorAttributes.put("message", HttpStatus.valueOf(status).getReasonPhrase());
    } catch (Exception ex) {
      // Unable to obtain a reason
      errorAttributes.put("message", "Http Status " + status);
    }
  }

  @SuppressWarnings("unchecked")
  private <T> T getAttribute(RequestAttributes requestAttributes, String name) {
    return (T) requestAttributes.getAttribute(name, RequestAttributes.SCOPE_REQUEST);
  }

}
