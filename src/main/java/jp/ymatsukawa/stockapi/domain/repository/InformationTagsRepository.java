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
   * @param informationIds list of informationid
   * @return List of DBInformationTags; informationId and tagname
   */
  List<BridgeInformationTags> findTagNameByInfomationIds(
    @Param("tags") Set<String> tags,
    @Param("tagNumbers") int tagNumbers
  );

  void saveRelationByInfoIdAndTagNames(
    @Param("informationid") Long informationId,
    @Param("names")Set<String> names
  );
}
