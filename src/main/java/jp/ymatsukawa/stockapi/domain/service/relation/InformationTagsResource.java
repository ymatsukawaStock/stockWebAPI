package jp.ymatsukawa.stockapi.domain.service.relation;

import jp.ymatsukawa.stockapi.domain.entity.bridge.BridgeInformationTags;
import jp.ymatsukawa.stockapi.domain.repository.InformationTagsRepository;

import java.util.*;

public class InformationTagsResource {
  private InformationTagsResource() {}
  private static class InformationTagsSupport {
    private static final InformationTagsResource INSTANCE = new InformationTagsResource();
  }

  public static InformationTagsResource getInstance() {
    return InformationTagsSupport.INSTANCE;
  }

  /**
   * get map of informationId to tag name list.
   * @param informationTagsRepository to get information and tag name
   * @param tags set of tag
   * @return Map&lt;Long, List&lt;String&gt;&gt; that is map of informationid to tag name list
   */
  public Map<Long, List<String>> getInformationidToTags(
    InformationTagsRepository informationTagsRepository,
    Set<String> tags
  ) {
    /**
     * get records of BridgeInformation(informationId, tag's name)
     *
     * ex.
     * [BridgeInformationTags(1, "example"),
     *  BridgeInformationTags(1, "sample"),
     *  BridgeInformationTags(2, "example"),
     *  ...]
     */
    List<BridgeInformationTags> informationTags = informationTagsRepository.findInformationByTag(tags, tags.size());
    return this.convertInformationIdToTags(informationTags);
  }

  public Map<Long, List<String>> getInformationidToTags(
    InformationTagsRepository informationTagsRepository,
    long informationId
  ) {
    /**
     * get records of BridgeInformation(informationId, tag's name)
     *
     * ex.
     * [BridgeInformationTags(1, "example"),
     *  BridgeInformationTags(1, "sample"),
     *  BridgeInformationTags(2, "example"),
     *  ...]
     */
    List<BridgeInformationTags> informationTags = informationTagsRepository.findTagByInformation(informationId);
    return this.convertInformationIdToTags(informationTags);
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
