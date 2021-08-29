package com.rushboard.rdbms.bulletin;

import com.rushboard.rdbms.bulletin.type.ContentType;
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
@Table("bulletin_board")
public class BulletinBoardContent {

  public BulletinBoardContent(
      UUID boardid,
      UUID issuer,
      String title,
      ContentType contentType,
      String content,
      UUID refEventId) {
    this.boardid = boardid;
    this.issuer = issuer;
    this.title = title;
    this.contenttype = contentType;
    this.content = content;
    this.refeventid = refEventId;
  }

  // Foreign Key - R2DBC Doesn't support relations yet.
  private UUID boardid;

  @Id private UUID contentid;

  // Foreign Key - R2DBC Doesn't support relations yet.
  private UUID issuer;

  private String title;

  private ContentType contenttype;

  private String content;

  // Foreign Key - R2DBC Doesn't support relations yet.
  private UUID refeventid;

  private Timestamp createdat;

  private Timestamp updatedat;

  private Timestamp deletedat;
}
