package com.rushboard.client.auth.manager;

import com.rushboard.client.auth.jwt.RushboardJwtToken;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.jwt.BadJwtException;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.security.oauth2.server.resource.InvalidBearerTokenException;
import reactor.core.publisher.Mono;

public final class JwtAuthenticationManager implements ReactiveAuthenticationManager {

  @Override
  public Mono<Authentication> authenticate(Authentication authentication) {
    return Mono.justOrEmpty(authentication)
        .filter(RushboardJwtToken.class::isInstance)
        .cast(RushboardJwtToken.class)
        .cast(Authentication.class)
        .onErrorMap(JwtException.class, this::onError);
  }

  private AuthenticationException onError(JwtException ex) {
    if (ex instanceof BadJwtException) {
      return new InvalidBearerTokenException(ex.getMessage(), ex);
    }
    return new AuthenticationServiceException(ex.getMessage(), ex);
  }
}
