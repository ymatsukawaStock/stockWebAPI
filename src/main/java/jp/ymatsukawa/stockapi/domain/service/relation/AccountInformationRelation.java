package jp.ymatsukawa.stockapi.domain.service.relation;

import jp.ymatsukawa.stockapi.domain.repository.AccountRepository;
import org.springframework.stereotype.Service;

@Service
public class AccountInformationRelation {
  public void chainsRelationBetweenAccountAndInformation(
    AccountRepository accountRepository,
    long accountId, long informationId
  ) {
    // chains relation between informationId and tagId
     accountRepository.saveRelationByAccountIdAndInformationId (
      accountId,
      informationId
    );
  }
}
