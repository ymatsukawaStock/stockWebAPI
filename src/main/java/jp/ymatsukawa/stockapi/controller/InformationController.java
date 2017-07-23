package jp.ymatsukawa.stockapi.controller;

import jp.ymatsukawa.stockapi.controller.entity.information.InformationCreation;
import jp.ymatsukawa.stockapi.controller.entity.information.InformationSubject;
import jp.ymatsukawa.stockapi.tool.communication.RequestValidator;
import jp.ymatsukawa.stockapi.tool.constant.Response;
import jp.ymatsukawa.stockapi.tool.communication.ResponseFormatter;
import jp.ymatsukawa.stockapi.domain.service.InformationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.*;
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
   * @param request servlet request.
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
    HttpServletRequest request,
    @RequestParam("limit") long limit,
    @RequestParam(value = "tag", required = false, defaultValue = "") String tag,
    @RequestParam(value = "sort", required = false, defaultValue = "created") String sort,
    @RequestParam(value = "sortBy", required = false, defaultValue = "desc") String sortBy
  ) {
    InformationSubject subject = new InformationSubject(limit, tag, sort, sortBy);
    Set errors = RequestValidator.getErrors(subject);
    if(!errors.isEmpty()) {
      logger.info("Client:{} sent bad request, limit={}, tag={}, sort={}, sortBy={}", request.getRemoteAddr(), limit, tag, sort, sortBy);
      return ResponseFormatter.makeErrorResponse(Response.CLIENT_BAD_REQUEST, errors);
    }
    try {
      return ResponseFormatter.makeResponse(
        this.informationService.getAll(
          subject.getLimit(), subject.getTag(), subject.getSort(), subject.getSortBy()
        )
      );
    } catch (Exception e) {
      logger.warn("When client:{} requested, information domain happened error: {}", request.getRemoteUser(), e.getMessage());
      return ResponseFormatter.makeResponse(Response.SERVER_INTERNAL_ERROR);
    }
  }

  /**
   * create information which includes tag and return it.
   * @param request servlet request.
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
    HttpServletRequest request,
    @Valid @RequestBody InformationCreation creation,
    BindingResult bindingResult
  ) {
    Set errors = RequestValidator.getErrors(bindingResult);
    if(!errors.isEmpty()) {
      logger.info("Client IP:{} sent bad request, with body=", request.getRemoteAddr());
      return ResponseFormatter.makeErrorResponse(Response.CLIENT_BAD_REQUEST, errors);
    }
    try {
      return ResponseFormatter.makeResponse(
        this.informationService.create(creation.getInformation(), creation.getTag())
      );
    } catch (Exception e) {
      logger.warn("When client:{} requestd, information domain happened error: {}", request.getRemoteAddr(),e.getMessage());
      return ResponseFormatter.makeResponse(Response.SERVER_INTERNAL_ERROR);
    }
  }

  private static final Logger logger = LoggerFactory.getLogger(InformationController.class);
}