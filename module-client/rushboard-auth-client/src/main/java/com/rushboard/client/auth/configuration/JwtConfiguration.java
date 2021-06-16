package com.rushboard.client.auth.configuration;

import com.rushboard.client.auth.manager.JwtAuthenticationManager;
import com.rushboard.client.auth.manager.JwtProviderManager;
import javax.crypto.spec.SecretKeySpec;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.NimbusReactiveJwtDecoder;
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtReactiveAuthenticationManager;

@Configuration
public class JwtConfiguration {
  private final Logger logger = LoggerFactory.getLogger(getClass());

  @Value("${rushboard.auth.jwt.secretKey?:9z$C&F)J@NcQfTjWnZr4u7x!A%D*G-Ka}")
  String secretKey;

  @Bean
  public ReactiveJwtDecoder reactiveJwtDecoder() {
    logger.info("using reactiveJwtDecoder with secretKey {}", secretKey);
    return NimbusReactiveJwtDecoder.withSecretKey(new SecretKeySpec(secretKey.getBytes(), "HMACSHA256"))
        .macAlgorithm(MacAlgorithm.HS256)
        .build();
  }

  @Bean
  public JwtProviderManager jwtProviderManager() {
    logger.info("using JwtProviderManager with secretKey {}", secretKey);
    return new JwtProviderManager(new SecretKeySpec(secretKey.getBytes(), "HMACSHA256"));
  }

  @Bean
  public JwtAuthenticationManager authenticationManager() {
    return new JwtAuthenticationManager(reactiveJwtDecoder());
  }
}
