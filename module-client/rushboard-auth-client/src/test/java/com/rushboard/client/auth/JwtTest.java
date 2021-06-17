package com.rushboard.client.auth;

import static org.junit.jupiter.api.Assertions.*;

import com.rushboard.client.auth.constants.TokenType;
import com.rushboard.client.auth.jwt.RushboardJwtToken;
import com.rushboard.client.auth.jwt.RushboardTokenSet;
import com.rushboard.client.auth.manager.JwtAuthenticationManager;
import com.rushboard.client.auth.manager.JwtProviderManager;
import com.rushboard.core.mapping.RoleType;
import java.util.UUID;
import javax.crypto.spec.SecretKeySpec;
import org.junit.jupiter.api.Test;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
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
    jwtReactiveAuthenticationManager = new JwtAuthenticationManager();
  }

  @Test
  void issueTokenSet() {
    RushboardTokenSet rushboardTokenSet =
        jwtProviderManager.issueTokenSet(UUID.randomUUID(), RoleType.MEMBER);
    RushboardJwtToken decodedAccessToken = new RushboardJwtToken(reactiveJwtDecoder.decode(rushboardTokenSet.getAccessToken()).block());
    assertEquals(TokenType.ACCESS_TOKEN.toString(), decodedAccessToken.getName());
  }

  @Test
  void authenticate() {
    RushboardTokenSet rushboardTokenSet = jwtProviderManager.issueTokenSet(UUID.randomUUID(), RoleType.MEMBER);
    assertNotNull(reactiveJwtDecoder
        .decode(rushboardTokenSet.getAccessToken())
        .map(RushboardJwtToken::new)
        .filter(jwtToken -> jwtToken.getName().equals(TokenType.ACCESS_TOKEN.toString()))
        .flatMap(jwtReactiveAuthenticationManager::authenticate).block());
  }
}