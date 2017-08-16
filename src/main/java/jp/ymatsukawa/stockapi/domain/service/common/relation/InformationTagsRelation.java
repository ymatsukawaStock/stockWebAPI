package jp.ymatsukawa.stockapi.domain.service.common.relation;

import jp.ymatsukawa.stockapi.domain.entity.bridge.BridgeInformationTags;
import jp.ymatsukawa.stockapi.domain.repository.InformationTagsRepository;
import jp.ymatsukawa.stockapi.domain.repository.TagRepository;
import jp.ymatsukawa.stockapi.tool.converter.ListConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class InformationTagsRelation {
  @Autowired
  InformationTagsRepository informationTagsRepository;
  @Autowired
  TagRepository tagRepository;

  /**
   * get map of informationId to tag name list.
   * @param informationTagsRepository to get information and tag name
   * @param tags set of tag
   * @return Map&lt;Long, List&lt;String&gt;&gt; that is map of informationid to tag name list
   */
  public Map<Long, List<String>> getInformationIdToTags(Set<String> tags) {
    /**
     * get list of BridgeInformation(informationId, tag)
     * specified by tag
     *
     * ex.
     * when tag is <"example">
     *
     * [BridgeInformationTags(1, "example"),
     *  BridgeInformationTags(1, "sample"),
     *  BridgeInformationTags(2, "example"),
     *  ...]
     */
    List<BridgeInformationTags> informationTags = this.informationTagsRepository.findInformationByTag(tags);
    return this.convertInformationIdToTags(informationTags);
  }

  public Map<Long, List<String>> getInformationIdToTags(long informationId) {
    /**
     * get list of BridgeInformation(informationId, tag)
     * specified by informationId
     *
     * ex.
     * when informationId is 1
     * [BridgeInformationTags(1, "example"),
     *  BridgeInformationTags(1, "sample")]
     */
    List<BridgeInformationTags> informationTags = this.informationTagsRepository.findTagByInformation(informationId);
    return this.convertInformationIdToTags(informationTags);
  }

  public Set<String> saveTagRelationNotYetAdded(String tags) {
    /**
     * save tags which is not yet added to tag DB.
     * ex.
     *
     * when DB
     * tag: "foo", "qux", "sample", "example"
     *
     * when input
     * tags: "foo,bar"
     *
     * then
     * addedTags: <"bar">
     */
    Set<String> addedTags = new HashSet<>(ListConverter.getListBySplit(tags, ","));
    addedTags.removeAll(this.tagRepository.findSavedTag(addedTags));
    if(!addedTags.isEmpty()) {
      tagRepository.save(addedTags);
    }
    return addedTags;
  }

  private Map<Long, List<String>> convertInformationIdToTags(List<BridgeInformationTags> informationTags) {
    /**
     * map BridgeInformationTags' informationId -> name
     *
     * ex.
     * {
     *   1 -> ["example", "sample"],
     *   2 -> ["example"]
     *   ...
     * }
     */
    Map<Long, List<String>> informationIdToTags = new HashMap<>();
    informationTags.forEach(informationTag -> {
      Long id = informationTag.getInformationId();
      String newTag = informationTag.getTag();

      if (informationIdToTags.get(id) == null) {
        informationIdToTags.put(id, new ArrayList<String>() {
          {
            add(newTag);
          }
        });
      } else {
        List<String> newTags = informationIdToTags.get(id);
        newTags.add(newTag);
        informationIdToTags.put(id, newTags);
      }
    });

    return informationIdToTags;
  }
}
