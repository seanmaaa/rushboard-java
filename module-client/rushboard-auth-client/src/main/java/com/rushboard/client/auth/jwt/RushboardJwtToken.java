package com.rushboard.client.auth.jwt;

import com.rushboard.client.auth.constants.TokenType;
import com.rushboard.core.mapping.RoleType;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

public class RushboardJwtToken extends JwtAuthenticationToken {

  private final TokenType tokenType;

  private RoleType userRole;

  private int userId;

  public RushboardJwtToken(Jwt jwt) {
    super(jwt);
    super.setAuthenticated(true);
    this.tokenType = TokenType.ACCESS_TOKEN;
  }

  public RoleType getRole() {
    return (RoleType) getTokenAttributes().get("Role");
  }

  //  private final RoleType roleType;
  //  private final int memberSeqId;
  //  private final DecodedJWT jwtToken;

  //  @Override
  //  public Object getCredentials() {
  //    return jwtToken;
  //  }
  //
  //  @Override
  //  public Object getPrincipal() {
  //    return memberSeqId;
  //  }

  //  public RushboardJwtToken(int memberSeqId, RoleType roleType, DecodedJWT jwtToken) {
  //    super();
  ////    super(Collections.singletonList(new SimpleGrantedAuthority(roleType.name())));
  //    this.roleType = roleType;
  //    this.jwtToken = jwtToken;
  //    this.memberSeqId = memberSeqId;
  //    super.setAuthenticated(true);
  //  }

}
