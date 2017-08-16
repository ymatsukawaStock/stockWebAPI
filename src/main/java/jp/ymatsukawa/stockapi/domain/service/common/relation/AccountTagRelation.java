package jp.ymatsukawa.stockapi.domain.service.common.relation;

import jp.ymatsukawa.stockapi.domain.repository.AccountRepository;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class AccountTagRelation {
  public void chainsRelationBetweenAccountAndTag(
    AccountRepository accountRepository,
    long accountId, Set<String> addedTags
  ) {
    // chains relation between informationId and tagId
     accountRepository.saveRelationByAccountIdAndTagNames(
      accountId,
      addedTags
    );
  }
}
