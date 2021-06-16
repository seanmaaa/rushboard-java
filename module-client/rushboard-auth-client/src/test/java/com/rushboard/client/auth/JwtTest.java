package com.rushboard.client.auth;

import static org.junit.jupiter.api.Assertions.*;

import com.rushboard.client.auth.constants.TokenType;
import com.rushboard.client.auth.jwt.RushboardJwtToken;
import com.rushboard.client.auth.jwt.RushboardTokenSet;
import com.rushboard.client.auth.manager.JwtAuthenticationManager;
import com.rushboard.client.auth.manager.JwtProviderManager;
import com.rushboard.core.mapping.RoleType;
import javax.crypto.spec.SecretKeySpec;
import org.junit.jupiter.api.Test;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.NimbusReactiveJwtDecoder;
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder;

class JwtTest {
  private final JwtProviderManager jwtProviderManager;
  private final ReactiveJwtDecoder reactiveJwtDecoder;
  private final JwtAuthenticationManager jwtReactiveAuthenticationManager;

  JwtTest() {
    SecretKeySpec secretKey = new SecretKeySpec("hvaIVtNQkFUJjcfJ1cQzojaNHqCsPgra".getBytes(), "HMACSHA256");
    jwtProviderManager = new JwtProviderManager(secretKey);
    reactiveJwtDecoder = NimbusReactiveJwtDecoder.withSecretKey(secretKey)
        .macAlgorithm(MacAlgorithm.HS256)
        .build();
    jwtReactiveAuthenticationManager = new JwtAuthenticationManager(reactiveJwtDecoder);
  }

  @Test
  void issueTokenSet() {
    RushboardTokenSet rushboardTokenSet = jwtProviderManager.issueTokenSet(1, RoleType.MEMBER);

    Jwt decodedAccessToken = reactiveJwtDecoder.decode(rushboardTokenSet.getAccessToken()).block();

    System.out.println(decodedAccessToken.getClaims());

    assertEquals(decodedAccessToken.getClaim("tktyp"), TokenType.ACCESS_TOKEN.toString());
  }

  @Test
  void authenticate() {
    RushboardTokenSet rushboardTokenSet = jwtProviderManager.issueTokenSet(1, RoleType.MEMBER);
    assertNotNull(reactiveJwtDecoder
        .decode(rushboardTokenSet.getAccessToken())
        .filter(accessToken -> accessToken.getClaim("tktyp").equals(TokenType.ACCESS_TOKEN.toString()))
        .flatMap(jwtToken -> jwtReactiveAuthenticationManager.authenticate(new RushboardJwtToken(jwtToken))).block());
  }
}