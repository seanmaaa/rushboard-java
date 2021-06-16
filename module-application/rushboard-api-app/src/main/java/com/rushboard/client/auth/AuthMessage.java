package com.rushboard.client.auth;

import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;

public interface AuthMessage {
  @AllArgsConstructor
  @Data
  class LoginRequest implements AuthMessage {
    @NotNull private final String username;
    @NotNull private final String password;
  }
}
