package com.rushboard.rdbms.event;

import org.springframework.data.r2dbc.repository.R2dbcRepository;

public interface EventRepository extends R2dbcRepository<Event, Integer> {}
