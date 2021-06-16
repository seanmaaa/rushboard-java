package com.rushboard.client.auth;

import static com.rushboard.core.response.RushboardResponse.ApiResponse;

import com.rushboard.client.auth.AuthMessage.LoginRequest;
import com.rushboard.client.auth.manager.JwtProviderManager;
import com.rushboard.core.response.ExceptionType;
import com.rushboard.core.response.ResponseType;
import com.rushboard.core.response.RushboardResponse;
import com.rushboard.client.auth.jwt.RushboardTokenSet;
import com.rushboard.rdbms.member.MemberRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
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

@Component
@ConditionalOnProperty(value = "rushboard.auth.enable", havingValue = "true")
public class AuthHandler {
  private final Logger logger = LoggerFactory.getLogger(getClass());

  private final MemberRepository memberRepository;
  private final JwtProviderManager jwtProviderManager;
  private final Pbkdf2PasswordEncoder passwordEncoder;

  @Autowired
  public AuthHandler(
      MemberRepository memberRepository,
      JwtProviderManager jwtProviderManager,
      Pbkdf2PasswordEncoder passwordEncoder) {
    this.memberRepository = memberRepository;
    this.jwtProviderManager = jwtProviderManager;
    this.passwordEncoder = passwordEncoder;
  }

  @Transactional
  public Mono<ServerResponse> login(ServerRequest request) {
    return request
        .bodyToMono(LoginRequest.class)
        .flatMap(
            loginRequest ->{
                logger.info("{}",loginRequest);
                return memberRepository
                    .findOneByUsername(loginRequest.getUsername())
                    .flatMap(
                        member ->
                            passwordEncoder.matches(
                                    loginRequest.getPassword(), member.getPassword())
                                ? ServerResponse.ok()
                                    .body(
                                        Mono.just(jwtProviderManager.issueTokenSet(member.getSeqid(), member.getRole())),
                                        RushboardTokenSet.class)
                                : ServerResponse.status(HttpStatus.UNAUTHORIZED)
                                    .body(
                                        Mono.just(ApiResponse(ResponseType.UNAUTHORIZED)),
                                        RushboardResponse.class))
                    .switchIfEmpty(ServerResponse.notFound().build());})
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
