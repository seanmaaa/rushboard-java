package com.rushboard.client.auth.jwt;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class RushboardTokenSet {
  private final String accessToken;
  private final String refreshToken;
}
