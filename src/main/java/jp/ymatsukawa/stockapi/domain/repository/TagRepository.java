package jp.ymatsukawa.stockapi.domain.repository;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Set;

@Mapper
public interface TagRepository {
  List<String> findSavedTag(
    @Param("tags") Set<String> tags
  );

  void save(
    @Param("tags") Set<String> tags
  );
}
