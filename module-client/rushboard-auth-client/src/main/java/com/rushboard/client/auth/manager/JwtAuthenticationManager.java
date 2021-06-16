package com.rushboard.client.auth.manager;

import com.rushboard.client.auth.jwt.RushboardJwtToken;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.jwt.BadJwtException;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder;
import org.springframework.security.oauth2.server.resource.BearerTokenAuthenticationToken;
import org.springframework.security.oauth2.server.resource.InvalidBearerTokenException;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.ReactiveJwtAuthenticationConverterAdapter;
import org.springframework.util.Assert;
import reactor.core.publisher.Mono;

public final class JwtAuthenticationManager implements ReactiveAuthenticationManager {

  private final ReactiveJwtDecoder jwtDecoder;

  private Converter<Jwt, ? extends Mono<? extends AbstractAuthenticationToken>> jwtAuthenticationConverter = new ReactiveJwtAuthenticationConverterAdapter(
      new JwtAuthenticationConverter());

  public JwtAuthenticationManager(ReactiveJwtDecoder jwtDecoder) {
    Assert.notNull(jwtDecoder, "jwtDecoder cannot be null");
    this.jwtDecoder = jwtDecoder;
  }

  @Override
  public Mono<Authentication> authenticate(Authentication authentication) {
    // @formatter:off
    return Mono.justOrEmpty(authentication)
        .filter(RushboardJwtToken.class::isInstance)
        .cast(RushboardJwtToken.class)
        .map(RushboardJwtToken::getToken)
        .flatMap(this.jwtAuthenticationConverter::convert)
        .cast(Authentication.class)
        .onErrorMap(JwtException.class, this::onError);
    // @formatter:on
  }

  /**
   * Use the given {@link Converter} for converting a {@link Jwt} into an
   * {@link AbstractAuthenticationToken}.
   * @param jwtAuthenticationConverter the {@link Converter} to use
   */
  public void setJwtAuthenticationConverter(
      Converter<Jwt, ? extends Mono<? extends AbstractAuthenticationToken>> jwtAuthenticationConverter) {
    Assert.notNull(jwtAuthenticationConverter, "jwtAuthenticationConverter cannot be null");
    this.jwtAuthenticationConverter = jwtAuthenticationConverter;
  }

  private AuthenticationException onError(JwtException ex) {
    if (ex instanceof BadJwtException) {
      return new InvalidBearerTokenException(ex.getMessage(), ex);
    }
    return new AuthenticationServiceException(ex.getMessage(), ex);
  }

}
