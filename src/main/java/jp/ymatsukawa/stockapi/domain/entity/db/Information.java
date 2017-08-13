package jp.ymatsukawa.stockapi.domain.entity.db;

import lombok.Data;
import lombok.NonNull;
import org.springframework.context.annotation.Bean;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;

@Data
public class Information {
  public Information() {}

  public Information(
    long informationId, String subject, String detail, Timestamp created, Timestamp updated
  ) {
    this.informationId = informationId;
    this.subject = subject;
    this.detail = detail;
    this.created = new SimpleDateFormat("yyyy/MM/dd").format(created);
    this.updated = new SimpleDateFormat("yyyy/MM/dd").format(updated);
  }

  @NonNull
  private long informationId;
  @NonNull
  private String subject;
  @NonNull
  private String detail;
  @NonNull
  private String created;
  @NonNull
  private String updated;
}