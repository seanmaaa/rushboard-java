package com.rushboard.domain.main;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Component
public class MainHandler {
  private final Logger logger = LoggerFactory.getLogger(getClass());

  public Mono<ServerResponse> helloWorld(ServerRequest request) {
    return ServerResponse.ok().body(Mono.just("Hello, World!"), String.class);
  }
}
