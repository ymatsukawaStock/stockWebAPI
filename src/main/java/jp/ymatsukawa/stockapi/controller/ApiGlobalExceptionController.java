package jp.ymatsukawa.stockapi.controller;

import org.springframework.boot.autoconfigure.web.ErrorController;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

@RestController
public class ApiGlobalExceptionController implements ErrorController {
  /**
   * below response code should be executed at each controller class.
   * - 404 Bad Request
   * - 500 Internal Server Error
   */

  /**
   * Global error handler.<br />
   * When to handle specific status code,<br />
   * write it on each controller.<br />
   * @param httpRequest client's request data.
   * @param httpResponse status is already defined by spring lib.
   * @return
   */
  @RequestMapping(value = ERROR_PATH)
  public Map<String, Object> handleError(
    HttpServletRequest httpRequest,
    HttpServletResponse httpResponse
  ) {
    logger.info("client ip={} requested but happened error. status code is {}", httpRequest.getRemoteAddr(), httpResponse.getStatus());

    int statusCode = httpResponse.getStatus();
    Map<String, Object> responseBody = null;

    switch(statusCode) {
      default:
        // fall-through. response body is blank.
    }
    return responseBody;
  }

  @Override
  public String getErrorPath() {
    return ERROR_PATH;
  }

  /**
   * spring-boot's error handling path
   */
  private static final String ERROR_PATH = "/error";
  private static final Logger logger = LoggerFactory.getLogger(ApiGlobalExceptionController.class);
}
