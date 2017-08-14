package jp.ymatsukawa.stockapi.controller.interceptor;

import jp.ymatsukawa.stockapi.domain.entity.db.Account;
import jp.ymatsukawa.stockapi.domain.repository.AccountRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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
    // TODO: not request parameter but http header.
    String accessToken = request.getParameter("accessToken");
    if(accessToken == null) {
      response.setStatus(HttpStatus.FORBIDDEN.value());
      logger.info("not authenticated request from {}", request.getRemoteAddr());
      return false;
    }

    Account account = null;
    try {
      account = accountRepository.findAuthenticateAccountByToken(accessToken);
    } catch (Exception e) {
      logger.warn("at interceptor, exception happens because of RDBMS process. Detail is ... {}", e.getMessage());
    }
    if(account == null) {
      response.setStatus(HttpStatus.FORBIDDEN.value());
      logger.info("not authenticated request from {}, specified accessToken query", request.getRemoteAddr());
      return false;
    }

    request.setAttribute("accountId", String.valueOf(account.getAccountId()));
    return true;
  }

  private static final Logger logger = LoggerFactory.getLogger(AuthnInterceptorHandler.class);
}
