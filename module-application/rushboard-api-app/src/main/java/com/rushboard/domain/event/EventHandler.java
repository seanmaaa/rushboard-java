package com.rushboard.domain.event;

import com.rushboard.client.auth.model.AuthProperty;
import com.rushboard.core.constant.CommonConstants;
import com.rushboard.core.response.ExceptionType;
import com.rushboard.core.response.RushboardResponse;
import com.rushboard.core.util.Try;
import com.rushboard.domain.event.EventRequest.EventCreateRequest;
import com.rushboard.rdbms.event.EventBoardAssociate;
import com.rushboard.rdbms.event.EventBoardAssociateRepository;
import com.rushboard.rdbms.event.EventBoardListRepository;
import com.rushboard.rdbms.event.type.EventBoardRoleType;
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
public class EventHandler {
  private final Logger logger = LoggerFactory.getLogger(getClass());
  private final EventBoardListRepository eventBoardListRepository;
  private final EventBoardAssociateRepository eventBoardAssociateRepository;

  @Autowired
  public EventHandler(
      EventBoardListRepository eventBoardListRepository,
      EventBoardAssociateRepository eventBoardAssociateRepository) {
    this.eventBoardListRepository = eventBoardListRepository;
    this.eventBoardAssociateRepository = eventBoardAssociateRepository;
  }

  @Transactional
  public Mono<ServerResponse> createEventBoard(ServerRequest request) {
    return Mono.justOrEmpty(
            Try.of(request.headers()::firstHeader, CommonConstants.AUTH_PROPERTY).toOptional())
        .flatMap(
            apBase64 -> Mono.justOrEmpty(Try.of(AuthProperty::fromBase64, apBase64).toOptional()))
        .flatMap(
            authProperty ->
                request
                    .bodyToMono(EventCreateRequest.class)
                    .map(req -> req.toEventBoardList(authProperty.getMemberId()))
                    .flatMap(
                        eventBoardList ->
                            eventBoardListRepository
                                .save(eventBoardList)
                                .flatMap(
                                    creationResult ->
                                        eventBoardAssociateRepository
                                            .save(
                                                new EventBoardAssociate(
                                                    creationResult.getBoardid(),
                                                    creationResult.getAdminid(),
                                                    EventBoardRoleType.ADMIN))
                                            .flatMap(
                                                associateResult ->
                                                    ServerResponse.status(HttpStatus.CREATED)
                                                        .body(
                                                            Mono.just(
                                                                RushboardResponse.apiResponse(
                                                                    CommonConstants.CREATED,
                                                                    eventBoardList.getTitle())),
                                                            RushboardResponse.class)))))
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
