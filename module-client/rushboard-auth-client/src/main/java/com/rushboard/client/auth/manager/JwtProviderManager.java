package com.rushboard.client.auth.manager;

import com.rushboard.core.mapping.RoleType;
import com.rushboard.client.auth.constants.TokenType;
import com.rushboard.client.auth.jwt.RushboardTokenSet;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import javax.crypto.spec.SecretKeySpec;

public class JwtProviderManager {

  private final SecretKeySpec secretKey;

  public JwtProviderManager(SecretKeySpec secretKey) {
    this.secretKey = secretKey;
  }

  public RushboardTokenSet issueTokenSet(int memberId, RoleType memberRole) {
    return new RushboardTokenSet(
        issueToken(memberId, memberRole, TokenType.ACCESS_TOKEN), issueToken(memberId, memberRole, TokenType.REFRESH_TOKEN));
  }

  private String issueToken(int memberId, RoleType memberRole, TokenType tokenType) {
    Long currentTime = System.currentTimeMillis();
    Map<String, Object> claims = new HashMap<>();
    claims.put("id", memberId);
    claims.put("role", memberRole);
    claims.put("tktyp", tokenType);

    return Jwts.builder()
        .setClaims(claims)
        .setIssuer("rushboard.io")
        .setIssuedAt(new Date(currentTime))
        .setExpiration(new Date(currentTime + (60 * 60 * 60)))
        .signWith(SignatureAlgorithm.HS256, secretKey)
        .compact();
  }
}
