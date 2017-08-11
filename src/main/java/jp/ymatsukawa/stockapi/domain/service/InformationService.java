package jp.ymatsukawa.stockapi.domain.service;

import java.util.*;

import jp.ymatsukawa.stockapi.domain.entity.bridge.BridgeInformation;
import jp.ymatsukawa.stockapi.domain.entity.db.Information;
import jp.ymatsukawa.stockapi.domain.entity.db.Tag;
import jp.ymatsukawa.stockapi.domain.repository.InformationTagsRepository;
import jp.ymatsukawa.stockapi.domain.repository.TagRepository;
import jp.ymatsukawa.stockapi.domain.service.relation.InformationTagsResource;
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
  private InformationTagsRepository informationTagsRepository;

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
    Map<Long, List<String>> informationIdToTags = InformationTagsResource.getInstance().getInformationidToTags(
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

    Map<Long, List<String>> informationIdToTags = InformationTagsResource.getInstance().getInformationidToTags(
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
  public BridgeInformation create(Information information, Tag tag) throws Exception {
    /**
     * save information with "subject" and "detail"
     */
    informationRepository.save(information, information.getSubject(), information.getDetail());

    // TODO: avoid multiple split
    /**
     * when parameter's tag exist,
     * 1. save tag name which is not yet saved at DB.
     * 2. chains relation between informationId and tagId
     * ex. of 1.
     * DB
     * tag: name ... "foo", "qux", "sample", "example"
     * when parameter tags "foo,bar"
     * then
     * tag: name ... "foo", "qux", "sample", "example", "bar"
     */
    if(!tag.getName().isEmpty()) {
      // 1. save tag name which is not yet saved at DB.
      Set<String> newAddedTags = new HashSet<>(ListConverter.getListBySplit(tag.getName(), ","));
      newAddedTags.removeAll(tagRepository.findSavedName(newAddedTags));
      if(!newAddedTags.isEmpty()) {
        tagRepository.save(newAddedTags);
      }

      // 2. chains relation between informationId and tagId
      Set<String> tagsRelatedToInformationId = new HashSet<>(ListConverter.getListBySplit(tag.getName(), ","));
      informationTagsRepository.saveRelationByInfoIdAndTagNames(
        information.getInformationId(),
        tagsRelatedToInformationId
      );
    }

    return (new BridgeInformation(information, ListConverter.getListBySplit(tag.getName(), ",")));
  }
}
