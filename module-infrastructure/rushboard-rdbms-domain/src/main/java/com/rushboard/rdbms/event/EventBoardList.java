package com.rushboard.rdbms.event;

import com.rushboard.rdbms.event.type.EventBoardType;
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
@Table("event_board_list")
public class EventBoardList {

  public EventBoardList(UUID adminId, String title, EventBoardType boardType) {
    this.adminid = adminId;
    this.title = title;
    this.boardtype = boardType;
  }

  @Id private UUID boardid;

  // Foreign Key - R2DBC Doesn't support relations yet.
  private UUID adminid;

  private String title;

  private EventBoardType boardtype;

  private boolean approved;

  private Timestamp createdat;

  private Timestamp updatedat;

  private Timestamp deletedat;
}
