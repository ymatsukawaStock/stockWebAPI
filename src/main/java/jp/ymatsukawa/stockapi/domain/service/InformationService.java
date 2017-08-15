package jp.ymatsukawa.stockapi.domain.service;

import java.util.*;
import java.util.stream.Collectors;

import jp.ymatsukawa.stockapi.domain.entity.bridge.BridgeInformation;
import jp.ymatsukawa.stockapi.domain.entity.bridge.BridgeInformationTags;
import jp.ymatsukawa.stockapi.domain.entity.db.Account;
import jp.ymatsukawa.stockapi.domain.entity.db.Information;
import jp.ymatsukawa.stockapi.domain.entity.db.Tag;
import jp.ymatsukawa.stockapi.domain.repository.AccountRepository;
import jp.ymatsukawa.stockapi.domain.repository.InformationTagsRepository;
import jp.ymatsukawa.stockapi.domain.repository.TagRepository;
import jp.ymatsukawa.stockapi.domain.service.relation.AccountInformationRelation;
import jp.ymatsukawa.stockapi.domain.service.relation.AccountTagRelation;
import jp.ymatsukawa.stockapi.domain.service.relation.InformationTagsRelation;
import jp.ymatsukawa.stockapi.tool.converter.ListConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import jp.ymatsukawa.stockapi.domain.repository.InformationRepository;

/**
 * Service works for usecase, not for entity.
 */
@Service
public class InformationService {
  @Autowired
  private InformationRepository informationRepository;
  @Autowired
  private TagRepository tagRepository;
  @Autowired
  private AccountRepository accountRepository;
  @Autowired
  private InformationTagsRepository informationTagsRepository;
  @Autowired
  private InformationTagsRelation informationTagsRelation;
  @Autowired
  private AccountInformationRelation accountInformationRelation;
  @Autowired
  private AccountTagRelation accountTagRelation;

  /**
   * Get list of information. <br />
   * When information is not found, returns null.
   * @param limit limit of getting information.
   * @param tags tags information has. blank, single word or comma separated.
   * @param sort sort object, "id" or "date"
   * @param sortBy sort way "asc" or "desc"
   * @return List of Information entity.
   * @return null when information is not found.
   * @throws Exception when error occurs at process of RDBMS.
   */
  @Transactional
  public List<BridgeInformation> getAll(
    long limit, String tags, String sort, String sortBy
  ) throws Exception {
    // TODO: set rate limit ... need KVS ... Redis
    /**
     * Get map of "informationId to its related tags".
     * parameter's tags should be subset of informationId's tags
     *
     * ex. of matches
     * tags: "foo,bar"
     * matches     ... [informationId: 1, tag: "foo", tag: "bar", tag: "qux"]
     * not matches ... [informationId: 2, tag: "foo", tag: "char"]
     * not matches ... [informationId: 3, tag: "foo"]
     *
     * ex. of map
     * parameter of tag is "foo,bar"
     * {
     *   informationId: 1 -> name: "foo",
     *   informationId: 1 -> name: "bar",
     *   informationId: 1 -> name: "qux",
     *   informationId: 2 -> name: "foo",
     *   informationId: 2 -> name: "bar",
     *   ...
     * }
     */
    Set<String> tagSet = new HashSet<>();
    if(!tags.isEmpty()) {
      tagSet = new HashSet<>(ListConverter.getListBySplit(tags, ","));
    }
    Map<Long, List<String>> informationIdToTags = informationTagsRelation.getInformationidToTags(
      informationTagsRepository, tagSet
    );

    /**
     * return empty list if record is not found.
     */
    if(informationIdToTags.isEmpty()) {
      return null;
    }

    /**
     * get limited information data constrained by
     * "limit":  list size
     * "sort":   sort object; what to sort
     * "sortBy": sort way; how to sort
     * "informationIds": informationIds which have all parameter's tags
     */
    List<Information> information = informationRepository.findAll(limit, sort, sortBy, informationIdToTags.keySet());

    /**
     * return empty list if record is not found.
     */
    if(information.isEmpty()) {
      return null;
    }

    /**
     * create entity Information list which has tag list.
     */
    List<BridgeInformation> entities = new ArrayList<>();
    information.forEach(info -> {
      List<String> tagsOfInformation = informationIdToTags.get(info.getInformationId());
      entities.add(new BridgeInformation(info, tagsOfInformation));
    });

    return entities;
  }

  /**
   * Get specific information by informationId.<br />
   * When information is not found, return null.
   * @param informationId to specify information
   * @return specified information entity.
   * @return null when data is not found.
   * @throws Exception when error occurs at process of RDBMS.
   */
  @Transactional
  public BridgeInformation getSpecificInformation(
    long informationId
  ) throws Exception {
    /**
     * When called and get record by informationId,
     * the record can be multiple.
     * because there is case tag is registered over two times to one informationId.
     */
    Information information = this.informationRepository.findByInformationId(informationId);
    if(information == null) {
      return null;
    }

    Map<Long, List<String>> informationIdToTags = informationTagsRelation.getInformationidToTags(
      informationTagsRepository, informationId
    );

    BridgeInformation entity = null;
    if(informationIdToTags.isEmpty()) {
      entity = new BridgeInformation(information, new ArrayList<String>(){});
    } else {
      entity = new BridgeInformation(information, informationIdToTags.get(informationId));
    }
    return entity;
  }

  /**
   * Registers information with tag.<br />
   * if tag(s) is not registered, save it as new.
   * @param information - Information entity. Required properties are "subject" and "detail".
   * @param tag - Tag entity. Required property is "name".
   * @throws Exception - when error occurs at process of RDBMS.
   */
  @Transactional
  public BridgeInformation create(Information information, Tag tag, long accountId) throws Exception {
    /**
     * save information with "subject" and "detail"
     */
    informationRepository.save(information, information.getSubject(), information.getDetail());

    if(!tag.getName().isEmpty()) {
      /**
       * divide new added tag("foo") and all request tag("foo", "bar")
       */
      Set<String> newAddedTags = informationTagsRelation.saveTagRelationNotYetStoraged(tagRepository, tag.getName());
      Set<String> allRequestTags = new HashSet<>(ListConverter.getListBySplit(tag.getName(), ","));
      informationTagsRelation.chainsRelationBetweenInformationIdAndTag(
        informationTagsRepository,
        information.getInformationId(), allRequestTags
      );
      /**
       * chains account and information
       */
      accountInformationRelation.chainsRelationBetweenAccountAndInformation(
        accountRepository,
        accountId, information.getInformationId()
      );
      /**
       * chains account and tag. if relation is already binded,
       * do thing.
       */
      if(!newAddedTags.isEmpty()) {
        accountTagRelation.chainsRelationBetweenAccountAndTag(
          accountRepository,
          accountId, newAddedTags
        );
      }
    }

    return (new BridgeInformation(information, ListConverter.getListBySplit(tag.getName(), ",")));
  }

  /**
   * update information with tag.<br />
   * if informationId does not exist, return null.<br />
   *
   * @param informationId - specific informationId
   * @param subject - information subject to update .
   * @param detail - information detail to update.
   * @param tags - tag name comma separated, single word or empty to update.
   * @throws Exception - when error occurs at process of RDBMS.
   */
  @Transactional
  public BridgeInformation update(
    long informationId, String subject, String detail, String tags, long accountId
  ) throws Exception {
    /**
     * check whether informationId exist
     */
    Information checkInfo = this.informationRepository.findByInformationId(informationId);
    if(checkInfo == null) {
      return null;
    }
    /**
     * check whether request account is related to edit information.
     * if not, do not permit to edit resource.
     */
    Account checkAccount = this.accountRepository.findAccountByAccountIdAndInformationId(accountId, informationId);
    if(checkAccount == null) {
      return null;
    }

    /**
     * when request tag is empty -- delete tag related by informationId<br />
     * when request tag is not empty<br/>
     * before process, register tag which is not yet storaged.<br />
     * 1. if information does not have tag, register them all.
     * 2. if information has tag,<br />
     * check "delete tags" from exist tag and "add tags"<br />
     * 1st, delete them if element exists.
     * 2nd, add them  if element exists.
     */
    List<BridgeInformationTags> informationTags = this.informationTagsRepository.findTagByInformation(informationId);
    if(tags.isEmpty()) {
     /*
      * when information has tag and request comes empty tag,
      * delete all tag related by informationId
      */
      if(!informationTags.isEmpty()) {
        informationTagsRepository.deleteRelationByInformationId(informationId);
      }
    } else {
      /*
       * before register tag, save tag not yet storaged to DB
       */
      Set<String> addedTag = informationTagsRelation.saveTagRelationNotYetStoraged(tagRepository, tags);
      /**
       * chain relation between "account and tag" not "information and tag"
       */
      accountRepository.saveRelationByAccountIdAndTagNames(accountId, addedTag);

      if(informationTags.isEmpty()) {
        /**
         * when information does not have tag and request tag comes,
         * register all tag to information.
         */
        Set<String> newAddedTags = new HashSet<>(ListConverter.getListBySplit(tags, ","));
        informationTagsRelation.chainsRelationBetweenInformationIdAndTag(
          informationTagsRepository,
          informationId, newAddedTags
        );
      } else {
        /**
         * when information has tag and request tag comes,
         * 1st, clear up "delete tag" and "add tag"
         * 2nd, delete tag if "delete tag" exists.
         * 3rd, register tag if "add tag" exists.
         */

        /**
         * get deleted tag set by Set calculation ... "exist tag set" - "request tag set"
         * also get add tag set by Set calculation ... "request tag set" - "exist tag set"
         */
        Set<String> requestTagSet = new HashSet<>(ListConverter.getListBySplit(tags, ","));
        Set<String> existTagSet = informationTags.stream().map(infoTag -> infoTag.getTag()).collect(Collectors.toSet());
        // copy because set calculation is mutable.
        Set<String> existTagSetCopy = existTagSet.stream().map(String::new).collect(Collectors.toSet());

        /*
         * check whether exist delete tags.
         * note: tag deletion happens when "delete tag"
         * deletion of information tag does not mean deletion of account tag
         */
        existTagSet.removeAll(requestTagSet);
        if(!existTagSet.isEmpty()) {
          this.informationTagsRepository.deleteRelationByInformationIdAndTag(informationId, existTagSet);
        }

        /*
         * check whether exist add tag
         * add them from informationTags and accountTags
         */
        requestTagSet.removeAll(existTagSetCopy);
        if(!requestTagSet.isEmpty()) {
          informationTagsRelation.chainsRelationBetweenInformationIdAndTag(
            informationTagsRepository,
            informationId, requestTagSet
          );
        }
      }

    }

    /**
     * update information and return re-get bean.
     */
    this.informationRepository.update(informationId, subject, detail);
    Information information = this.informationRepository.findByInformationId(informationId);
    return (new BridgeInformation(information, ListConverter.getListBySplit(tags, ",")));
  }

}
