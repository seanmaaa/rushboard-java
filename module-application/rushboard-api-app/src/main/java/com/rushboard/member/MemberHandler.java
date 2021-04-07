package com.rushboard.member;

import com.rushboard.core.constant.CommonConstants;
import com.rushboard.core.response.ExceptionType;
import com.rushboard.core.response.ResponseType;
import com.rushboard.core.response.RushboardResponse;

import static com.rushboard.core.response.RushboardResponse.ApiResponse;

import com.rushboard.core.util.Try;
import com.rushboard.member.MemberRequest.*;
import com.rushboard.rdbms.member.MemberRepository;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import org.springframework.web.server.ServerWebInputException;
import reactor.core.publisher.Mono;

@Component
public class MemberHandler {
  private final Logger logger = LoggerFactory.getLogger(getClass());
  private final MemberRepository memberRepository;

  @Autowired
  public MemberHandler(MemberRepository memberRepository) {
    this.memberRepository = memberRepository;
  }

  @Transactional
  public Mono<ServerResponse> saveMember(ServerRequest request) {
    return request
        .bodyToMono(MemberSaveRequest.class)
        .map(MemberSaveRequest::validate)
        .flatMap(
            validResult ->
                validResult.fold(
                    error ->
                        ServerResponse.badRequest()
                            .body(Mono.just(ApiResponse(error)), RushboardResponse.class),
                    member ->
                        memberRepository
                            .save(member)
                            .flatMap(
                                result ->
                                    ServerResponse.status(HttpStatus.CREATED)
                                        .body(
                                            Mono.just(
                                                ApiResponse(
                                                    CommonConstants.CREATED, member.getUsername())),
                                            RushboardResponse.class))))
        .onErrorResume(this::fallBack);
  }

  @Transactional
  public Mono<ServerResponse> updateMember(ServerRequest request) {
    return Mono.justOrEmpty(Try.of(request::pathVariable, CommonConstants.USERNAME).toOptional())
        .flatMap(
            username ->
                request
                    .bodyToMono(MemberUpdateRequest.class)
                    .map(MemberUpdateRequest::validate)
                    .flatMap(
                        validRequest ->
                            validRequest.fold(
                                error ->
                                    ServerResponse.badRequest()
                                        .body(
                                            Mono.just(ApiResponse(error)), RushboardResponse.class),
                                updateRequest ->
                                    memberRepository
                                        .findOneByUsername(username)
                                        .flatMap(
                                            member -> {
                                              if (updateRequest.getPassword() != null)
                                                member.setPassword(updateRequest.getPassword());
                                              if (updateRequest.getEmail() != null)
                                                member.setEmail(updateRequest.getEmail());
                                              if (updateRequest.getMobile() != null)
                                                member.setMobile(updateRequest.getMobile());
                                              member.setUpdatedat(
                                                  Timestamp.valueOf(LocalDateTime.now()));
                                              return memberRepository
                                                  .save(member)
                                                  .flatMap(
                                                      result -> ServerResponse.noContent().build());
                                            })
                                        .switchIfEmpty(ServerResponse.notFound().build()))))
        .switchIfEmpty(
            ServerResponse.badRequest()
                .body(
                    Mono.just(ApiResponse(ResponseType.PARAMETER_MISSING)),
                    RushboardResponse.class))
        .onErrorResume(this::fallBack);
  }

  @Transactional
  public Mono<ServerResponse> deleteMember(ServerRequest request) {
    return Mono.justOrEmpty(Try.of(request::pathVariable, CommonConstants.USERNAME).toOptional())
        .flatMap(
            username ->
                memberRepository
                    .findOneByUsername(username.toLowerCase())
                    .flatMap(
                        member ->
                            memberRepository
                                .delete(member)
                                .flatMap(result -> ServerResponse.noContent().build())
                                .switchIfEmpty(ServerResponse.noContent().build()))
                    .switchIfEmpty(ServerResponse.notFound().build()))
        .switchIfEmpty(
            ServerResponse.badRequest()
                .body(
                    Mono.just(ApiResponse(ResponseType.PARAMETER_MISSING)),
                    RushboardResponse.class))
        .onErrorResume(this::fallBack);
  }

  @Transactional
  public Mono<ServerResponse> emailDuplicationCheck(ServerRequest request) {
    return Mono.justOrEmpty(request.queryParam(CommonConstants.EMAIL))
        .flatMap(
            email ->
                memberRepository
                    .findOneByEmail(email.toLowerCase())
                    .flatMap(
                        result ->
                            ServerResponse.status(HttpStatus.CONFLICT)
                                .body(
                                    Mono.just(ApiResponse(ResponseType.EMAIL_ALREADY_EXISTS)),
                                    RushboardResponse.class))
                    .switchIfEmpty(
                        ServerResponse.ok()
                            .body(
                                Mono.just(ApiResponse(ResponseType.AVAILABLE)),
                                RushboardResponse.class)))
        .switchIfEmpty(
            ServerResponse.badRequest()
                .body(
                    Mono.just(ApiResponse(ResponseType.PARAMETER_MISSING)),
                    RushboardResponse.class))
        .onErrorResume(this::fallBack);
  }

  @Transactional
  public Mono<ServerResponse> usernameDuplicationCheck(ServerRequest request) {
    return Mono.justOrEmpty(request.queryParam(CommonConstants.USERNAME))
        .flatMap(
            username ->
                memberRepository
                    .findOneByUsername(username.toLowerCase())
                    .flatMap(
                        result ->
                            ServerResponse.status(HttpStatus.CONFLICT)
                                .body(
                                    Mono.just(ApiResponse(ResponseType.USERNAME_ALREADY_EXISTS)),
                                    RushboardResponse.class))
                    .switchIfEmpty(
                        ServerResponse.ok()
                            .body(
                                Mono.just(ApiResponse(ResponseType.AVAILABLE)),
                                RushboardResponse.class)))
        .switchIfEmpty(
            ServerResponse.badRequest()
                .body(
                    Mono.just(ApiResponse(ResponseType.PARAMETER_MISSING)),
                    RushboardResponse.class))
        .onErrorResume(this::fallBack);
  }

  private Mono<ServerResponse> fallBack(Throwable error) {
    if (error instanceof DataIntegrityViolationException) {
      logger.error("DataIntegrityViolationException: {}", error.getCause().getMessage());
      return ServerResponse.badRequest()
          .body(
              Mono.just(ApiResponse(ExceptionType.DATA_INTEGRITY_VIOLATION)),
              RushboardResponse.class);
    } else if (error instanceof DataAccessException) {
      logger.error("DataAccessException: {}", error.getCause().getMessage());
      return ServerResponse.badRequest()
          .body(Mono.just(ApiResponse(ExceptionType.DATABASE_EXCEPTION)), RushboardResponse.class);
    } else if (error instanceof ServerWebInputException
        || error instanceof IllegalArgumentException) {
      logger.warn("Wrong formatted request: {}", error.getCause().getMessage());
      return ServerResponse.badRequest()
          .body(
              Mono.just(ApiResponse(ExceptionType.WRONG_REQUEST_FORMAT)), RushboardResponse.class);
    } else {
      logger.error("Severe exception occurred.", error);
      return ServerResponse.badRequest()
          .body(Mono.just(ApiResponse(ExceptionType.BAD_REQUEST)), RushboardResponse.class);
    }
  }
}
