package com.rushboard.core.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum ExceptionType implements RushboardType {
  INVALID_EMAIL_ADDR(101, "Invalid email address."),
  INVALID_MOBILE(102, "Invalid mobile number pattern."),
  DATA_DUPLICATED(104, "Duplicated Data."),
  DATA_VALIDATION_FAILURE(105, "Data validation failed."),
  DATA_INTEGRITY_VIOLATION(106, "Invalid data."),
  EMPTY_REQUEST_BODY(107, "Empty request body."),
  DATABASE_EXCEPTION(501, "Database Exception."),
  WRONG_REQUEST_FORMAT(400, "Wrong input format."),
  WRONG_REQUEST_PARAMETERS(400, "Wrong input parameters."),
  BAD_REQUEST(700, "Bad request.");

  private final int code;

  private final String description;
}
