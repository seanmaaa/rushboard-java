package com.rushboard.configuration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity.CsrfSpec;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.Pbkdf2PasswordEncoder;
import org.springframework.security.web.server.SecurityWebFilterChain;

@EnableWebFluxSecurity
@EnableReactiveMethodSecurity
@Configuration
public class SecurityConfiguration {
  private final Logger logger = LoggerFactory.getLogger(getClass());

  @Value("${rushboard.app.security.salt ?:}")
  String passwordSalt;

  @ConditionalOnMissingBean
  public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
    logger.info("Enable security");
    return http.authorizeExchange(exchanges -> exchanges.anyExchange().authenticated()).build();
  }

  @Bean
  @ConditionalOnProperty(prefix = "rushboard.app.security", name = "enable", havingValue = "false")
  public SecurityWebFilterChain securityWebFilterChainDisable(ServerHttpSecurity http) {
    logger.info("Disable security");
    return http.authorizeExchange(exchanges -> exchanges.anyExchange().permitAll())
        .csrf(CsrfSpec::disable)
        .build();
  }

  @Bean
  public Pbkdf2PasswordEncoder passwordEncoder() {
    logger.info("Using Pbkdf2PasswordEncoder.");
    return new Pbkdf2PasswordEncoder(passwordSalt);
  }
}
