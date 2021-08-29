package com.rushboard.rdbms.bulletin;

import com.rushboard.rdbms.common.FileExtension;
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
@Table("bulletin_board_comment")
public class BulletinBoardComment {

  public BulletinBoardComment(
      UUID contentId, UUID issuer, String body, int sequence, int depth, Integer parent) {
    this.contentid = contentId;
    this.issuer = issuer;
    this.body = body;
    this.sequence = sequence;
    this.depth = depth;
    this.parent = parent;
  }

  @Id private int seqid;

  // Foreign Key - R2DBC Doesn't support relations yet.
  private UUID contentid;

  // Foreign Key - R2DBC Doesn't support relations yet.
  private UUID issuer;

  private String body;

  private int sequence;

  private int depth;

  private Integer parent;

  private Timestamp createdat;

  private Timestamp updatedat;

  private Timestamp deletedat;
}
