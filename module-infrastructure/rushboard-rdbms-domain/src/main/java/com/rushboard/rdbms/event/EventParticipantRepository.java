package com.rushboard.rdbms.event;

import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EventParticipantRepository extends R2dbcRepository<EventParticipant, Integer> {

}
