package com.rushboard.rdbms.event;

import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface EventAttachmentRepository extends R2dbcRepository<EventAttachment, UUID> {

}
