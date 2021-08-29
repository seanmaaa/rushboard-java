package com.rushboard.domain.event;

import org.springdoc.core.annotations.RouterOperation;
import org.springdoc.core.annotations.RouterOperations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.web.reactive.function.server.RequestPredicates.accept;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Configuration
public class EventRouter {

  @Autowired
  public EventRouter(EventHandler eventHandler) {}

  @RouterOperations({
    @RouterOperation(
        path = "/eventboard/create",
        beanClass = EventHandler.class,
        beanMethod = "createEventBoard")
  })
  @Bean(name = "eventRoute")
  public RouterFunction<ServerResponse> routes(EventHandler eventHandler) {
    return route()
        .path(
            "/eventboard",
            eventPath ->
                eventPath.nest(
                    accept(APPLICATION_JSON),
                    subPath -> subPath.POST("/create", eventHandler::createEventBoard)))
        .build();
  }
}
