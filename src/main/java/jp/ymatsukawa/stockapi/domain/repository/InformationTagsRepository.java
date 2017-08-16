package jp.ymatsukawa.stockapi.domain.repository;

import jp.ymatsukawa.stockapi.domain.entity.bridge.BridgeInformationTags;
import jp.ymatsukawa.stockapi.domain.entity.db.Information;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Set;

@Mapper
public interface InformationTagsRepository {
  List<BridgeInformationTags> findInformationByTag(
    @Param("tags") Set<String> tags
  );

  List<BridgeInformationTags> findTagByInformation(
    @Param("informationId") long informationId
  );

  void saveRelationByInfoIdAndTag(
    @Param("informationId") Long informationId,
    @Param("tags")Set<String> names
  );

  void deleteRelationByInformationId(
    @Param("informationId") long informationid
  );

  void deleteRelationByInformationIdAndTag(
    @Param("informationId") long informationId,
    @Param("tags") Set<String> tags
  );
}
