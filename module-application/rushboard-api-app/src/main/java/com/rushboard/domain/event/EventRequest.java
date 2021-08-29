package com.rushboard.domain.event;

import com.rushboard.rdbms.event.EventBoardList;
import com.rushboard.rdbms.event.type.EventBoardType;
import java.util.UUID;
import javax.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

public interface EventRequest {

  @Data
  @AllArgsConstructor
  @NoArgsConstructor(force = true, access = AccessLevel.PRIVATE)
  class EventCreateRequest implements EventRequest {
    @NotNull private final String title;
    private final String boardType;

    public EventBoardList toEventBoardList(UUID adminId) {
      return new EventBoardList(adminId, title, EventBoardType.valueOf(boardType));
    }
  }
}
