package jp.ymatsukawa.stockapi.controller;

import jp.ymatsukawa.stockapi.controller.entity.information.InformationCreation;
import jp.ymatsukawa.stockapi.controller.entity.information.InformationDetail;
import jp.ymatsukawa.stockapi.controller.entity.information.InformationSubject;
import jp.ymatsukawa.stockapi.domain.entity.bridge.BridgeInformation;
import jp.ymatsukawa.stockapi.tool.communication.RequestValidator;
import jp.ymatsukawa.stockapi.tool.communication.ResponseFormatter;
import jp.ymatsukawa.stockapi.domain.service.InformationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.*;
import java.util.List;
import java.util.Map;
import java.util.Set;

// FIXME: build V1 endpoint. create super class > infra
@RequestMapping(
  produces = { MediaType.APPLICATION_JSON_VALUE }
)
@RestController
public class InformationController {
  @Autowired
  private InformationService informationService;

  /**
   * get information with limit, tag search, sort by date.<br />
   * See External Design Stock API. TODO: put uri
   * @param httpRequest servlet request.
   * @param httpResponse servlet response.
   * @param limit request param how many get information. Should be 1 to 2^32.
   * @param tag request param what is tag of information. Should be blank, single word or comma separated.
   * @param sort request param what is sort object of information. Should be "created" or "updated".
   * @param sortBy request param how to sort information. Should be "desc" or "asc".
   * @return Map&lt;String, Object&gt;. At http resepose body, json is written.
   */
  @RequestMapping(
    method = RequestMethod.GET,
    value = "/information"
  )
  public Map<String, Object> getInformationSubject(
    HttpServletRequest httpRequest,
    HttpServletResponse httpResponse,
    @RequestParam(value = "limit") long limit,
    @RequestParam(value = "tag",    required = false, defaultValue = "")        String tag,
    @RequestParam(value = "sort",   required = false, defaultValue = "created") String sort,
    @RequestParam(value = "sortBy", required = false, defaultValue = "desc")    String sortBy
  ) {
    /**
     * validate request parameter
     * when invalid, response is "400 Bad Request"
     */
    InformationSubject subject = new InformationSubject(limit, tag, sort, sortBy);
    Set errors = RequestValidator.getErrors(subject);
    if(!errors.isEmpty()) {
      logger.info("Client:{} sent bad request, limit={}, tag={}, sort={}, sortBy={}", httpRequest.getRemoteAddr(), limit, tag, sort, sortBy);
      return ResponseFormatter.makeErrorResponse(errors, httpResponse);
    }

    /**
     * 1. get entity-list from service layer
     * 2. set http header
     * 3. return http body as response by entity-list
     */
    try {
      List<BridgeInformation> result = this.informationService.getAll(
        subject.getLimit(), subject.getTag(), subject.getSort(), subject.getSortBy()
      );
      return ResponseFormatter.makeResponse(result, httpResponse);
    } catch (Exception e) {
      logger.warn("client ip={} is requested to information domain and happened error: {}", httpRequest.getRemoteUser(), e.getMessage());
      return ResponseFormatter.makeResponse(HttpStatus.INTERNAL_SERVER_ERROR, httpResponse);
    }
  }


  /**
   * get specific information <br />
   * See External Design Stock API. TODO: put uri
   * @param httpRequest servlet request.
   * @param httpResponse servlet response.
   * @return Map&lt;String, Object&gt;. At http resepose body, json is written.
   */
  @RequestMapping(
    method = RequestMethod.GET,
    value = "/information/{informationId}"
  )
  public Map<String, Object> getInformationDetail(
    HttpServletRequest httpRequest,
    HttpServletResponse httpResponse,
    @PathVariable("informationId") long informationId
  ) {
    /**
     * validate request parameter
     * when invalid, response is "400 Bad Request"
     */
    InformationDetail detail = new InformationDetail(informationId);
    /**
     * 1. get entity from service layer
     * 2. set http header
     * 3. return http body as response by entity-list
     */
    try {
      BridgeInformation result = this.informationService.getSpecificInformation(detail.getInformationId());
      return ResponseFormatter.makeResponse(result, httpResponse);
    } catch (Exception e) {
      logger.warn("client ip={} is requested to information domain and happened error: {}", httpRequest.getRemoteUser(), e.getMessage());
      return ResponseFormatter.makeResponse(HttpStatus.INTERNAL_SERVER_ERROR, httpResponse);
    }
  }

  /**
   * create information which includes tag and return it.
   * @param httpRequest servlet request.
   * @param httpResponse servlet response.
   * @param creation union beans of Information(subject, detail) and Tag(name)
   * @param bindingResult if not valid at "creation" bean, size is over 0.
   * @return Map&lt;String, Object&gt;. created information which includes tag
   */
  @RequestMapping(
    consumes = MediaType.APPLICATION_JSON_VALUE,
    method = RequestMethod.POST,
    value = "/information/create"
  )
  public Map<String, Object> createInformation(
    HttpServletRequest  httpRequest,
    HttpServletResponse httpResponse,
    @Valid @RequestBody InformationCreation creation,
    BindingResult bindingResult
  ) {
    // TODO: "limit" should be validated when tag is not set.
    /**
     * validate request parameter
     * when invalid, response as "400 bad request"
     */
    Set errors = RequestValidator.getErrors(bindingResult);
    if(!errors.isEmpty()) {
      logger.info("client ip={} sent bad request with body={}", httpRequest.getRemoteAddr(), httpResponse);
      return ResponseFormatter.makeErrorResponse(errors, httpResponse);
    }

    /**
     * 1. get entity from service layer
     * 2. set http header
     * 3. return http body as response by entity
     */
    try {
      BridgeInformation result = this.informationService.create(creation.getInformation(), creation.getTag());
      return ResponseFormatter.makeResponse(result, httpResponse);
    } catch (Exception e) {
      logger.warn("client ip={} requested information domain and happened error={}", httpRequest.getRemoteAddr(), e.getMessage());
      return ResponseFormatter.makeResponse(HttpStatus.INTERNAL_SERVER_ERROR, httpResponse);
    }
  }

  private static final Logger logger = LoggerFactory.getLogger(InformationController.class);
}