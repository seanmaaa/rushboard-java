package com.rushboard.domain.member;

import com.rushboard.client.auth.model.AuthProperty;
import com.rushboard.core.constant.CommonConstants;
import com.rushboard.core.response.ExceptionType;
import com.rushboard.core.response.ResponseType;
import com.rushboard.core.response.RushboardResponse;
import com.rushboard.core.util.Try;
import com.rushboard.domain.member.MemberRequest.MemberSaveRequest;
import com.rushboard.domain.member.MemberRequest.MemberUpdateRequest;
import com.rushboard.rdbms.member.MemberRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.Pbkdf2PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import org.springframework.web.server.ServerWebInputException;
import reactor.core.publisher.Mono;

import java.sql.Timestamp;
import java.time.LocalDateTime;

@Component
public class MemberHandler {
  private final Logger logger = LoggerFactory.getLogger(getClass());
  private final MemberRepository memberRepository;
  private final Pbkdf2PasswordEncoder passwordEncoder;

  @Value("${rushboard.app.member.registeration.email_verification_required?:false}")
  private boolean emailVerificationEnabled;

  @Autowired
  public MemberHandler(MemberRepository memberRepository, Pbkdf2PasswordEncoder passwordEncoder) {
    this.memberRepository = memberRepository;
    this.passwordEncoder = passwordEncoder;
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
                            .body(
                                Mono.just(RushboardResponse.apiResponse(error)),
                                RushboardResponse.class),
                    member ->
                        memberRepository
                            .save(
                                member
                                    .encodePassword(passwordEncoder::encode)
                                    .setVerified(!emailVerificationEnabled))
                            .flatMap(
                                result ->
                                    ServerResponse.status(HttpStatus.CREATED)
                                        .body(
                                            Mono.just(
                                                RushboardResponse.apiResponse(
                                                    CommonConstants.CREATED, member.getUsername())),
                                            RushboardResponse.class))))
        .onErrorResume(this::fallBack);
  }

  @Transactional
  public Mono<ServerResponse> updateMember(ServerRequest request) {
    return Mono.justOrEmpty(
            Try.of(request.headers()::firstHeader, CommonConstants.AUTH_PROPERTY).toOptional())
        .flatMap(
            apBase64 -> Mono.justOrEmpty(Try.of(AuthProperty::fromBase64, apBase64).toOptional()))
        .flatMap(
            authProperty ->
                request
                    .bodyToMono(MemberUpdateRequest.class)
                    .map(MemberUpdateRequest::validate)
                    .flatMap(
                        validRequest ->
                            validRequest.fold(
                                error ->
                                    ServerResponse.badRequest()
                                        .body(
                                            Mono.just(RushboardResponse.apiResponse(error)),
                                            RushboardResponse.class),
                                updateRequest ->
                                    memberRepository
                                        .findOneByMemberid(authProperty.getMemberId())
                                        .flatMap(
                                            member -> {
                                              if (updateRequest.getPassword() != null)
                                                member.setPassword(
                                                    passwordEncoder.encode(
                                                        updateRequest.getPassword()));
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
                    Mono.just(RushboardResponse.apiResponse(ResponseType.PARAMETER_MISSING)),
                    RushboardResponse.class))
        .onErrorResume(this::fallBack);
  }

  @Transactional
  public Mono<ServerResponse> deleteMember(ServerRequest request) {
    return Mono.justOrEmpty(
            Try.of(request.headers()::firstHeader, CommonConstants.AUTH_PROPERTY).toOptional())
        .flatMap(
            apBase64 -> Mono.justOrEmpty(Try.of(AuthProperty::fromBase64, apBase64).toOptional()))
        .flatMap(
            authProperty ->
                memberRepository
                    .findOneByMemberid(authProperty.getMemberId())
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
                    Mono.just(RushboardResponse.apiResponse(ResponseType.PARAMETER_MISSING)),
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
                                    Mono.just(
                                        RushboardResponse.apiResponse(
                                            ResponseType.EMAIL_ALREADY_EXISTS)),
                                    RushboardResponse.class))
                    .switchIfEmpty(
                        ServerResponse.ok()
                            .body(
                                Mono.just(RushboardResponse.apiResponse(ResponseType.AVAILABLE)),
                                RushboardResponse.class)))
        .switchIfEmpty(
            ServerResponse.badRequest()
                .body(
                    Mono.just(RushboardResponse.apiResponse(ResponseType.PARAMETER_MISSING)),
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
                                    Mono.just(
                                        RushboardResponse.apiResponse(
                                            ResponseType.USERNAME_ALREADY_EXISTS)),
                                    RushboardResponse.class))
                    .switchIfEmpty(
                        ServerResponse.ok()
                            .body(
                                Mono.just(RushboardResponse.apiResponse(ResponseType.AVAILABLE)),
                                RushboardResponse.class)))
        .switchIfEmpty(
            ServerResponse.badRequest()
                .body(
                    Mono.just(RushboardResponse.apiResponse(ResponseType.PARAMETER_MISSING)),
                    RushboardResponse.class))
        .onErrorResume(this::fallBack);
  }

  private Mono<ServerResponse> fallBack(Throwable error) {
    if (error instanceof DataIntegrityViolationException) {
      logger.error("DataIntegrityViolationException: {}", error.getCause().getMessage());
      return ServerResponse.badRequest()
          .body(
              Mono.just(RushboardResponse.apiResponse(ExceptionType.DATA_INTEGRITY_VIOLATION)),
              RushboardResponse.class);
    } else if (error instanceof DataAccessException) {
      logger.error("DataAccessException: {}", error.getCause().getMessage());
      return ServerResponse.badRequest()
          .body(
              Mono.just(RushboardResponse.apiResponse(ExceptionType.DATABASE_EXCEPTION)),
              RushboardResponse.class);
    } else if (error instanceof ServerWebInputException
        || error instanceof IllegalArgumentException) {
      logger.warn("Wrong formatted request: {}", error.getCause().getMessage());
      return ServerResponse.badRequest()
          .body(
              Mono.just(RushboardResponse.apiResponse(ExceptionType.WRONG_REQUEST_FORMAT)),
              RushboardResponse.class);
    } else {
      logger.error("Severe exception occurred.", error);
      return ServerResponse.badRequest()
          .body(
              Mono.just(RushboardResponse.apiResponse(ExceptionType.BAD_REQUEST)),
              RushboardResponse.class);
    }
  }
}
