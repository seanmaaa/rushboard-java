package com.rushboard.rdbms.event;

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
@Table("event_participant")
public class EventAttachment {

  public EventAttachment(
      UUID eventId, FileExtension fileExtension, String fileName, String filePath) {
    this.eventid = eventId;
    this.fileextension = fileExtension;
    this.filename = fileName;
    this.filepath = filePath;
  }

  @Id private int seqid;

  // Foreign Key - R2DBC Doesn't support relations yet.
  private UUID eventid;

  private FileExtension fileextension;

  private String filename;

  private String filepath;

  private Timestamp createdat;

  private Timestamp updatedat;

  private Timestamp deletedat;
}