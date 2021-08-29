package com.rushboard.rdbms.event;

import java.sql.Timestamp;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Table("event_participant")
public class EventParticipant {

  public EventParticipant(UUID eventId, UUID memberId) {
    this.eventid = eventId;
    this.memberid = memberId;
  }

  @Id private int seqid;

  // Foreign Key - R2DBC Doesn't support relations yet.
  private UUID eventid;

  // Foreign Key - R2DBC Doesn't support relations yet.
  private UUID memberid;

  private Timestamp createdat;
}
