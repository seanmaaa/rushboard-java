package com.rushboard.client.auth.constants;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.security.oauth2.jwt.JwtClaimNames;

@AllArgsConstructor
@Getter
public enum RushboardJwtClaimNames implements JwtClaimNames {

  TOKEN_TYPE("tktyp"),
  USER_ID("usrid"),
  USER_ROLE("role");

  private final String value;
}
