package com.rushboard.rdbms.event;

import com.rushboard.rdbms.event.type.EventType;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;

import java.sql.Timestamp;
import org.springframework.data.relational.core.mapping.Table;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Table("event_board")
public class Event {

  public Event(
      UUID boardId,
      UUID issuer,
      String title,
      EventType eventType,
      String location,
      String description,
      Timestamp eventDate) {
    this.boardid = boardId;
    this.issuer = issuer;
    this.title = title;
    this.eventtype = eventType;
    this.location = location;
    this.description = description;
    this.eventdate = eventDate;
  }

  // Foreign Key - R2DBC Doesn't support relations yet.
  private UUID boardid;

  @Id private UUID eventid;

  // Foreign Key - R2DBC Doesn't support relations yet.
  private UUID issuer;

  private String title;

  private EventType eventtype;

  private String location;

  private String description;

  private Timestamp eventdate;

  private Timestamp createdat;

  private Timestamp updatedat;

  private Timestamp deletedat;
}
