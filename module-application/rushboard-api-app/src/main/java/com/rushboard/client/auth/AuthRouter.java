package com.rushboard.client.auth;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.web.reactive.function.server.RequestPredicates.accept;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

import org.springdoc.core.annotations.RouterOperation;
import org.springdoc.core.annotations.RouterOperations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

@Configuration
@ConditionalOnProperty(value = "rushboard.auth.enable", havingValue = "true")
public class AuthRouter {

  @Autowired
  public AuthRouter(AuthHandler authHandler) {}

  @RouterOperations({
    @RouterOperation(path = "/login", beanClass = AuthHandler.class, beanMethod = "login"),
  })
  @Bean(name = "authRoute")
  RouterFunction<ServerResponse> routes(AuthHandler authHandler) {
    return route().POST("/login", accept(APPLICATION_JSON), authHandler::login).build();
  }
}
