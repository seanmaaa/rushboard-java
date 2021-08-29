package com.rushboard.rdbms.event;

import com.rushboard.rdbms.event.type.EventBoardRoleType;
import com.rushboard.rdbms.event.type.EventBoardType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.sql.Timestamp;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Table("event_board_associate")
public class EventBoardAssociate {

  public EventBoardAssociate(UUID boardid, UUID memberId, EventBoardRoleType role) {
    this.boardid = boardid;
    this.memberid = memberId;
    this.role = role;
  }

  @Id private int seqid;

  // Foreign Key - R2DBC Doesn't support relations yet.
  private UUID boardid;

  // Foreign Key - R2DBC Doesn't support relations yet.
  private UUID memberid;

  private EventBoardRoleType role;

  private Timestamp createdat;
}
