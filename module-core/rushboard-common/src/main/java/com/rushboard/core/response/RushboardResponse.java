package com.rushboard.core.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class RushboardResponse {

  private final String error;
  private final String message;

  public static RushboardResponse ApiResponse(String message, String description) {
    return new RushboardResponse(message, description);
  }

  public static RushboardResponse ApiResponse(RushboardType rushboardType) {
    return new RushboardResponse(rushboardType.toString(), rushboardType.getDescription());
  }
}
