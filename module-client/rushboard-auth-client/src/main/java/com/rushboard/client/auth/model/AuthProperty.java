package com.rushboard.client.auth.model;

import com.rushboard.core.mapping.RoleType;
import java.io.Serializable;
import java.util.Base64;
import java.util.UUID;
import lombok.Getter;

@Getter
public class AuthProperty implements Serializable {

  private final UUID memberId;
  private final RoleType userRole;

  public AuthProperty(UUID memberId, RoleType userRole) {
    this.memberId = memberId;
    this.userRole = userRole;
  }

  public AuthProperty(String[] decodedBase64) {
    this.memberId = UUID.fromString(decodedBase64[0]);
    this.userRole = RoleType.valueOf(decodedBase64[1]);
  }

  @Override
  public String toString() {
    return memberId + ":" + userRole;
  }

  public String toBase64() {
    return Base64.getEncoder().encodeToString(this.toString().getBytes());
  }

  public static AuthProperty fromBase64(String base64) {
    return new AuthProperty(new String(Base64.getDecoder().decode(base64)).split(":"));
  }
}
