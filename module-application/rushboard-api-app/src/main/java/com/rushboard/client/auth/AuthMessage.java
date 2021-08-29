package com.rushboard.client.auth;

import javax.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

public interface AuthMessage {

  @Data
  @AllArgsConstructor
  @RequiredArgsConstructor
  @NoArgsConstructor(force = true, access = AccessLevel.PRIVATE)
  class LoginRequest implements AuthMessage {
    @NotNull private final String username;
    @NotNull private final String password;
  }
}
