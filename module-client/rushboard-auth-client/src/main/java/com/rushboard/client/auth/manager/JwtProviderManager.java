package com.rushboard.client.auth.manager;

import com.rushboard.client.auth.constants.RushboardJwtClaimNames;
import com.rushboard.core.mapping.RoleType;
import com.rushboard.client.auth.constants.TokenType;
import com.rushboard.client.auth.jwt.RushboardTokenSet;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import javax.crypto.spec.SecretKeySpec;

public class JwtProviderManager {

  private final SecretKeySpec secretKey;

  public JwtProviderManager(SecretKeySpec secretKey) {
    this.secretKey = secretKey;
  }

  public RushboardTokenSet issueTokenSet(UUID memberId, RoleType memberRole) {
    return new RushboardTokenSet(
        issueToken(memberId, memberRole, TokenType.ACCESS_TOKEN),
        issueToken(memberId, memberRole, TokenType.REFRESH_TOKEN));
  }

  private String issueToken(UUID memberId, RoleType memberRole, TokenType tokenType) {
    long currentTime = System.currentTimeMillis();
    Map<String, Object> claims = new HashMap<>();
    claims.put(RushboardJwtClaimNames.USER_ID.getValue(), memberId);
    claims.put(RushboardJwtClaimNames.USER_ROLE.getValue(), memberRole);

    return Jwts.builder()
        .setSubject(tokenType.toString())
        .addClaims(claims)
        .setIssuer("rushboard.io")
        .setIssuedAt(new Date(currentTime))
        .setExpiration(new Date(currentTime + 1000 * ((tokenType == TokenType.ACCESS_TOKEN) ? 300 : 3600)))
        .signWith(SignatureAlgorithm.HS256, secretKey)
        .compact();
  }
}
