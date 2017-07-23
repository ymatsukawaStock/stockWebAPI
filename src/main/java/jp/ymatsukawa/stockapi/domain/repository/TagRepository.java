package jp.ymatsukawa.stockapi.domain.repository;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Set;

@Mapper
public interface TagRepository {
  List<String> findSavedName(
    @Param("names") Set<String> names
  );
  /**
   * Find all record from information table. <br />
   * There is no sort restriction.
   * @return List of Information. If record does not found, empty list is returned.
   */
  void save(
    @Param("names") Set<String> tagnames
  );
}
