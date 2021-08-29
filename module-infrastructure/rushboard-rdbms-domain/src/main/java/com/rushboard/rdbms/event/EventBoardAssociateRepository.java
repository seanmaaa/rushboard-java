package com.rushboard.rdbms.event;

import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Repository
public interface EventBoardAssociateRepository extends R2dbcRepository<EventBoardAssociate, Integer> {
  Mono<EventBoardAssociate> findAllByMemberid(UUID memberid);

  Mono<EventBoardAssociate> findAllByBoardid(UUID boardid);

}
