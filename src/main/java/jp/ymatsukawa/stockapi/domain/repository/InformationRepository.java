package jp.ymatsukawa.stockapi.domain.repository;

import jp.ymatsukawa.stockapi.domain.entity.db.Information;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Set;

@Mapper
public interface InformationRepository {
  List<Information> findAll(
    @Param("limit") long limit,
    @Param("sort") String sort,
    @Param("sortBy") String sortBy,
    @Param("informationIds") Set<Long> informationIds
  );

  Information findByInformationId(
    @Param("informationId") long informationId
  );

  void save(
    @Param("information") Information information,
    @Param("subject") String subject,
    @Param("detail")  String detail
  );

  void update(
    @Param("informationId") long informationId,
    @Param("subject") String subject,
    @Param("detail")  String detail
  );
}
