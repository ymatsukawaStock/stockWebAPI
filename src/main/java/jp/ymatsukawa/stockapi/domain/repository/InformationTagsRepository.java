package jp.ymatsukawa.stockapi.domain.repository;

import jp.ymatsukawa.stockapi.domain.entity.bridge.BridgeInformationTags;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Set;

@Mapper
public interface InformationTagsRepository {
  /**
   * Find informationid and tagname record which related informationid.
   * @param tags name set of tag
   * @return List of Bridge InformationTags
   */
  List<BridgeInformationTags> findInformationByTag(
    @Param("tags") Set<String> tags,
    @Param("tagNumbers") int tagNumbers
  );

  /**
   * Find informationid and tagname record which related informationid.
   * @param informationId list of informationid
   * @return InformationTag
   */
  List<BridgeInformationTags> findTagByInformation(
    @Param("informationId") long informationId
  );

  void saveRelationByInfoIdAndTagNames(
    @Param("informationid") Long informationId,
    @Param("names")Set<String> names
  );
}
