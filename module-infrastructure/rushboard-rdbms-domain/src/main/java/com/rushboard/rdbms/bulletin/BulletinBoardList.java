package com.rushboard.rdbms.bulletin;

import com.rushboard.rdbms.bulletin.type.BulletinBoardType;
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
@Table("bulletin_board_list")
public class BulletinBoardList {

  public BulletinBoardList(UUID adminId, String title, BulletinBoardType boardType) {
    this.adminid = adminId;
    this.title = title;
    this.boardtype = boardType;
  }

  @Id private UUID boardid;

  // Foreign Key - R2DBC Doesn't support relations yet.
  private UUID adminid;

  private String title;

  private BulletinBoardType boardtype;

  private Timestamp createdat;

  private Timestamp updatedat;

  private Timestamp deletedat;
}
