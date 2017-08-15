package jp.ymatsukawa.stockapi.controller.interceptor;

import jp.ymatsukawa.stockapi.domain.entity.db.Account;
import jp.ymatsukawa.stockapi.domain.repository.AccountRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class AuthnInterceptorHandler extends HandlerInterceptorAdapter {
  @Autowired
  AccountRepository accountRepository;

  /**
   * check whether requested client is authenticated by console.<br />
   * if not, return 403.
   * @param request
   * @param response
   * @param handler
   * @return
   * @throws Exception
   */
  @Override
  public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
    String accessToken = request.getHeader("Authorization");
    if(accessToken == null || !accessToken.startsWith("Bearer ")) {
      response.setStatus(HttpStatus.UNAUTHORIZED.value());
      response.setHeader(HttpHeaders.WWW_AUTHENTICATE, "not_requested_token_or_invalid_token");
      logger.info("not authenticated request from {}", request.getRemoteAddr());
      return false;
    }

    Account account = null;
    try {
      account = accountRepository.findAuthenticateAccountByToken(accessToken.replaceAll("Bearer ", ""));
    } catch (Exception e) {
      logger.warn("at interceptor, exception happens because of RDBMS process. Detail is ... {}", e.getMessage());
      response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
      return false;
    }
    if(account == null) {
      response.setStatus(HttpStatus.FORBIDDEN.value());
      logger.info("not authenticated request from {}, specified accessToken query", request.getRemoteAddr());
      return false;
    }

    request.setAttribute("accountId", account.getAccountId());
    return true;
  }

  private static final Logger logger = LoggerFactory.getLogger(AuthnInterceptorHandler.class);
}
