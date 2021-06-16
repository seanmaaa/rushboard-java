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
        path = "/member/save",
        beanClass = MemberHandler.class,
        beanMethod = "saveMember"),
    @RouterOperation(
        path = "/member/emailCheck",
        beanClass = MemberHandler.class,
        beanMethod = "emailDuplicationCheck"),
    @RouterOperation(
        path = "/member/usernameCheck",
        beanClass = MemberHandler.class,
        beanMethod = "usernameDuplicationCheck")
  })
  @Bean(name = "memberRoute")
  RouterFunction<ServerResponse> routes(MemberHandler memberHandler) {
    return route()
        .path(
            "/member",
            rootPath ->
                rootPath
                    .nest(
                        accept(APPLICATION_JSON),
                        subPath ->
                            subPath
                                .POST("/save", memberHandler::saveMember)
                                .PATCH("/update/{username}", memberHandler::updateMember))
                    .GET("/emailCheck", memberHandler::emailDuplicationCheck)
                    .GET("/usernameCheck", memberHandler::usernameDuplicationCheck)
                    .DELETE("/delete/{username}", memberHandler::deleteMember))
        .build();
  }
}
