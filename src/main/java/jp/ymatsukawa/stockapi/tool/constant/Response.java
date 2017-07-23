package jp.ymatsukawa.stockapi.tool.constant;

import lombok.Getter;

public enum Response {
  SUCCESS_OK(200, "OK"),
  SUCCESS_CREATED(201, "Created"),
  CLIENT_BAD_REQUEST(400, "Bad Request"),
  CLIENT_ERROR_NOT_FOUND(404, "Not Found"),
  SERVER_INTERNAL_ERROR(500, "Internal Server Error");

  Response(int code, String message) {
    this.code = code;
    this.message = message;
  }

  @Getter
  private final int code;
  @Getter
  private final String message;
}
