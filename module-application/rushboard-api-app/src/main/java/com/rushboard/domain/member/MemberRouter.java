package com.rushboard.domain.member;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.web.reactive.function.server.RequestPredicates.accept;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

import org.springdoc.core.annotations.RouterOperation;
import org.springdoc.core.annotations.RouterOperations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

@Configuration
public class MemberRouter {

  @Autowired
  public MemberRouter(MemberHandler memberHandler) {}

  @RouterOperations({
    @RouterOperation(
        path = "/register/save",
        beanClass = MemberHandler.class,
        beanMethod = "saveMember"),
    @RouterOperation(
        path = "/register/emailCheck",
        beanClass = MemberHandler.class,
        beanMethod = "emailDuplicationCheck"),
    @RouterOperation(
        path = "/register/usernameCheck",
        beanClass = MemberHandler.class,
        beanMethod = "usernameDuplicationCheck")
  })
  @Bean(name = "memberRoute")
  RouterFunction<ServerResponse> routes(MemberHandler memberHandler) {
    return route()
        .path(
            "/member",
            memberPath ->
                memberPath.nest(
                    accept(APPLICATION_JSON),
                    subPath ->
                        subPath
                            .PATCH("/update", memberHandler::updateMember)
                            .DELETE("/delete", memberHandler::deleteMember)))
        .path(
            "/register",
            registerPath ->
                registerPath
                    .POST("/save", accept(APPLICATION_JSON), memberHandler::saveMember)
                    .GET("/emailCheck", memberHandler::emailDuplicationCheck)
                    .GET("/usernameCheck", memberHandler::usernameDuplicationCheck))
        .build();
  }
}
