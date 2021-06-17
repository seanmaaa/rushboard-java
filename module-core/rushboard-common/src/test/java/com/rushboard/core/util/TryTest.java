package com.rushboard.core.util;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Optional;
import org.junit.jupiter.api.Test;

class TryTest {

  @Test
  void tryNormalCaseTest() {
    assertNotNull(Try.of(num -> 3 / num, 2d).getOrElseNull());
  }

  @Test
  void tryExceptionalCaseTest() {
    assertNull(Try.of(num -> 3 / num, 0).getOrElseNull());
  }

  @Test
  void tryToOptionalNormalCaseTest() {
    assertEquals(Try.of(num -> 3 / num, 2d).toOptional(), Optional.of(1.5));
  }

  @Test
  void tryToOptionalEmptyCaseTest() {
    assertEquals(Try.of(num -> 3 / num, 0).toOptional(), Optional.empty());
  }
}
