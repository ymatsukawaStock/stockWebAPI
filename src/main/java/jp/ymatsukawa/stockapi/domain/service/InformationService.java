package jp.ymatsukawa.stockapi.domain.service;

import java.util.*;
import java.util.stream.Collectors;

import jp.ymatsukawa.stockapi.domain.entity.bridge.BridgeInformation;
import jp.ymatsukawa.stockapi.domain.entity.bridge.BridgeInformationTags;
import jp.ymatsukawa.stockapi.domain.entity.db.Account;
import jp.ymatsukawa.stockapi.domain.entity.db.Information;
import jp.ymatsukawa.stockapi.domain.repository.AccountRepository;
import jp.ymatsukawa.stockapi.domain.repository.InformationTagsRepository;
import jp.ymatsukawa.stockapi.domain.repository.TagRepository;
import jp.ymatsukawa.stockapi.domain.service.common.relation.InformationTagsRelation;
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

  @Transactional
  public List<BridgeInformation> getSubject (
    long limit, String tagNames, String sort, String sortBy
  ) throws Exception {
    /**
     * Get map of { informationId -> tag } to prepare informationId's tag-array
     *
     * ex.
     * when tags is <"foo", "bar">
     * then returned map is
     * {
     *   informationId: 1 -> name: "foo",
     *   informationId: 1 -> name: "bar",
     *   informationId: 1 -> name: "qux",
     *   informationId: 2 -> name: "foo",
     *   informationId: 2 -> name: "bar",
     *   ...
     * }
     */
    Set<String> tags = new HashSet<>();
    if(!tagNames.isEmpty()) {
      tags = new HashSet<>(ListConverter.getListBySplit(tagNames, ","));
    }
    Map<Long, List<String>> informationIdToTags = informationTagsRelation.getInformationIdToTags(tags);
    /**
     * return null if relation is not found between informationId and tag.
     */
    if(informationIdToTags.isEmpty()) {
      return null;
    }

    // TODO: re-check limit is only check when tag is empty.
    /**
     * get limited information data constrained by
     * "limit":  list size
     * "sort":   sort object; what to sort
     * "sortBy": sort way; how to sort
     * "informationIds": informationIds which have all parameter's tags
     */
    List<Information> information = informationRepository.findAll(limit, sort, sortBy, informationIdToTags.keySet());
    /**
     * return empty list if no information.
     */
    if(information.isEmpty()) {
      return null;
    }

    /**
     * create information-list which has tag array.
     */
    List<BridgeInformation> entities = new ArrayList<>();
    information.forEach(info -> {
      List<String> tagsOfInformation = informationIdToTags.get(info.getInformationId());
      entities.add(new BridgeInformation(info, tagsOfInformation));
    });

    return entities;
  }

  @Transactional
  public BridgeInformation getSpecificInformation(
    long informationId
  ) throws Exception {
    /**
     * get specific information by informationId
     * return null if information does not found
     */
    Information information = this.informationRepository.findByInformationId(informationId);
    if(information == null) {
      return null;
    }

    /**
     * Prepare map "{ informationId -> tag array }"
     * to create information entity.
     */
    Map<Long, List<String>> informationIdToTags = informationTagsRelation.getInformationIdToTags(informationId);

    BridgeInformation entity = null;
    if(informationIdToTags.isEmpty()) {
      entity = new BridgeInformation(information, new ArrayList<String>(){});
    } else {
      entity = new BridgeInformation(information, informationIdToTags.get(informationId));
    }
    return entity;
  }

  @Transactional
  public BridgeInformation create(
    String subject, String detail, String tagNames, long accountId
  ) throws Exception {
    /**
     * save information with "subject" and "detail"
     * inserted informationId is added at bean's information.informationId
     */
    Information information = new Information();
    this.informationRepository.save(information, subject, detail);

    /**
     * save tags and its relation.
     *
     * 1st, chains "information and new added tags", "account and new added tags".
     * "new added tags" means "input tags which not yet registered at tag DB"
     * ex.
     *   "foo,bar,qux" ... "bar" and "qux" is not saved at tag DB
     *   "new added tags" points "bar" and "qux"
     *
     * 2nd, register all input tags to "information to tags" DB
     */
    if(!tagNames.isEmpty()) {
      /**
       * chains "account and new added tag".
       * new added tags can be got from informationTagsRelation.saveTagRelationNotYetAdded()
       */
      Set<String> newAddedTags = informationTagsRelation.saveTagRelationNotYetAdded(tagNames);
      if(!newAddedTags.isEmpty()) {
        this.accountRepository.saveRelationByAccountIdAndTag(accountId, newAddedTags);
      }

      /**
       * register informationId to all input tags at DB "information to tags"
       */
      Set<String> inputTags = new HashSet<>(ListConverter.getListBySplit(tagNames, ","));
      this.informationTagsRepository.saveRelationByInfoIdAndTag(information.getInformationId(), inputTags);
    }

    /**
     * chains account and information.
     */
    this.accountRepository.saveRelationByAccountIdAndInformationId (accountId, information.getInformationId());

    /**
     * create and return information entity.
     */
    information = this.informationRepository.findByInformationId(information.getInformationId());
    return (new BridgeInformation(information, ListConverter.getListBySplit(tagNames, ",")));
  }

  @Transactional
  public BridgeInformation update(
    long informationId, String subject, String detail, String tagNames, long accountId
  ) throws Exception {
    /**
     * check whether input accountId and informationId is related data.
     * if not matched them, it's illegal; authenticated someone intended to edit others resource.
     * do not permit above operation.
     */
    Account checkAccount = this.accountRepository.findAccountByAccountIdAndInformationId(accountId, informationId);
    if(checkAccount == null) {
      return null;
    }

    /**
     * tag edit operation.
     *
     * case A. input tag is empty
     * delete tag related to informationId
     * do not think relation about "account to tag"
     *
     * case B. input tag is not empty
     * before operation, register tag which is not yet added to tag DB.
     *
     * B-1.
     * when information does not have tag
     * then register tags to information.
     *
     * B-2.
     * when information has tag
     * then get B-2-1; "delete tags from information" and B-2-2; "add tags to information"
     * and delete tags with B-2-1, add tags with B-2-2 to information
     */
    List<BridgeInformationTags> informationTags = this.informationTagsRepository.findTagByInformation(informationId);
    if(tagNames.isEmpty()) {
     /*
      * when information has tag and input tag is empty,
      * delete all tag related by informationId
      */
      if(!informationTags.isEmpty()) {
        informationTagsRepository.deleteRelationByInformationId(informationId);
      }
    } else {
      /*
       * before register tag, save tag not yet added to tag DB
       */
      Set<String> addedTag = informationTagsRelation.saveTagRelationNotYetAdded(tagNames);

      /**
       * chain relation between "account and added tag". it's NOT "information and added tag"
       */
      if(!addedTag.isEmpty()) {
        accountRepository.saveRelationByAccountIdAndTag(accountId, addedTag);
      }

      if(informationTags.isEmpty()) {
        /**
         * when information does not have tag and input tag is empty,
         * register all tag to information.
         */
        Set<String> inputTags = new HashSet<>(ListConverter.getListBySplit(tagNames, ","));
        this.informationTagsRepository.saveRelationByInfoIdAndTag(informationId, inputTags);
      } else {
        /**
         * when information has tag and input tag is not empty
         *
         * 1st, get "delete tag" and "add tag"
         * 2nd, delete tag if "delete tag" exists.
         * 3rd, register tag if "add tag" exists.
         */

        /**
         * get deleted tag set by Set calculation ... "record tag set" - "input tag set"
         * also get add tag set by Set calculation ... "input tag set" - "record tag set"
         */
        Set<String> inputTags = new HashSet<>(ListConverter.getListBySplit(tagNames, ","));
        Set<String> recordTags = informationTags.stream().map(infoTag -> infoTag.getTag()).collect(Collectors.toSet());
        // copy because set calculation is mutable.
        Set<String> recordTagsCopy = recordTags.stream().map(String::new).collect(Collectors.toSet());

        /**
         * delete tag if "delete tag" exist.
         * NOTE: deletion of tag from information DOES NOT MEAN deletion of tag from account
         *
         * NOTE: get "deleted tag set" by Set calculation ... "record tag set" - "input tag set"

         */
        recordTags.removeAll(inputTags);
        if(!recordTags.isEmpty()) {
          this.informationTagsRepository.deleteRelationByInformationIdAndTag(informationId, recordTags);
        }

        /*
         * check whether exist add tag.
         * add them to informationTags and accountTags
         *
         * NOTE: get "add tag set" by Set calculation ... "input tag set" - "record tag set"
         */
        inputTags.removeAll(recordTagsCopy);
        if(!inputTags.isEmpty()) {
          this.informationTagsRepository.saveRelationByInfoIdAndTag(informationId, inputTags);
        }
      }
    }

    /**
     * update information and return re-get bean.
     */
    this.informationRepository.update(informationId, subject, detail);
    Information information = this.informationRepository.findByInformationId(informationId);
    return (new BridgeInformation(information, ListConverter.getListBySplit(tagNames, ",")));
  }

}
