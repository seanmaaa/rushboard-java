package com.rushboard.rdbms.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;

import java.sql.Timestamp;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Event {

  public Event(String title, int createduser, Timestamp schedule) {
    this.title = title;
    this.createduser = createduser;
    this.schedule = schedule;
  }

  @Id private int seqid;

  private String title;

  // Foreign Key - R2DBC Doesn't support relations yet.
  private int createduser;

  private Timestamp schedule;

  private Timestamp createdat;

  private Timestamp updatedat;

  private Timestamp deletedat;
}
