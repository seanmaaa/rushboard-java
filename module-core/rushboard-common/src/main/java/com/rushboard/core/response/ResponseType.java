package com.rushboard.core.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum ResponseType implements RushboardType {
  AVAILABLE(200, "Available"),
  CREATED(201, "Created"),
  UNAUTHORIZED(401, "Bad credentials"),
  PARAMETER_MISSING(404, "Missing Parameters."),
  EMAIL_ALREADY_EXISTS(409, "Already existing email address."),
  USERNAME_ALREADY_EXISTS(409, "Already existing username.");

  private final int code;

  private final String description;
}
