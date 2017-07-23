package jp.ymatsukawa.stockapi.domain.repository;

import jp.ymatsukawa.stockapi.domain.entity.db.Information;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Set;

@Mapper
public interface InformationRepository {
  /**
   * Find all record from information table. <br />
   * There is no sort restriction.
   * @return List of Information. If record does not found, empty list is returned.
   */
  List<Information> findAll(
    @Param("limit") long limit,
    @Param("tag") Set<String> tags,
    @Param("sort") String sort,
    @Param("sortBy") String sortBy
  );

  /**
   * Save information data(subject and detail) with tag. <br />
   * tag (name - List of String, raw data is comma separated) is optional. <br />
   * If requirements "no tags" exist, pass argument of "tag" to empty list.
   * @param subject - information's subject
   * @param detail - information's detail
   */
  void save(
    @Param("information") Information information,
    @Param("subject") String subject,
    @Param("detail")  String detail
  );
}
