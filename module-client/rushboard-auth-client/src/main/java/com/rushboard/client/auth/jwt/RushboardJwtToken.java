package com.rushboard.client.auth.jwt;

import com.rushboard.client.auth.constants.RushboardJwtClaimNames;
import com.rushboard.client.auth.model.AuthProperty;
import com.rushboard.core.mapping.RoleType;
import java.util.Map;
import java.util.UUID;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

public class RushboardJwtToken extends JwtAuthenticationToken {

  private final AuthProperty authProperty;

  public RushboardJwtToken(Jwt jwt) {
    super(jwt);
    this.authProperty =
        new AuthProperty(
            UUID.fromString(jwt.getClaim(RushboardJwtClaimNames.USER_ID.getValue())),
            RoleType.valueOf(jwt.getClaim(RushboardJwtClaimNames.USER_ROLE.getValue())));
  }

  @Override
  public Map<String, Object> getTokenAttributes() {
    return null;
  }

  public AuthProperty getAuthProperty() {
    return authProperty;
  }
}
