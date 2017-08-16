package jp.ymatsukawa.stockapi.domain.repository;

import jp.ymatsukawa.stockapi.domain.entity.db.Account;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Set;

@Mapper
public interface AccountRepository {
  Account findAuthenticateAccountByToken (
    @Param("token") String token
  );

  void saveRelationByAccountIdAndTag (
    @Param("accountId") long accountId,
    @Param("tags") Set<String> tags
  );

  void saveRelationByAccountIdAndInformationId (
    @Param("accountId") long accountId,
    @Param("informationId") long informationId
  );

  Account findAccountByAccountIdAndInformationId (
    @Param("accountId") long accountId,
    @Param("informationId") long informationId
  );
}
