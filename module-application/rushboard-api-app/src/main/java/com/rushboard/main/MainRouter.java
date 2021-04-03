package com.rushboard.main;

import org.springdoc.core.annotations.RouterOperation;
import org.springdoc.core.annotations.RouterOperations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.GET;

@Configuration
public class MainRouter {

  @Autowired
  public MainRouter(MainHandler mainHandler) {}

  @RouterOperations({
    @RouterOperation(path = "/hello", beanClass = MainHandler.class, beanMethod = "helloWorld")
  })
  @Bean(name = "mainRoute")
  public RouterFunction<ServerResponse> routes(MainHandler mainHandler) {
    return RouterFunctions.route(GET("/hello"), mainHandler::helloWorld);
  }
}
