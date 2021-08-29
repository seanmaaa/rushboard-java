package com.rushboard.rdbms.event;

import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Repository
public interface EventBoardListRepository extends R2dbcRepository<EventBoardList, UUID> {
  Mono<EventBoardList> findOneByBoardid(UUID uuid);
}
