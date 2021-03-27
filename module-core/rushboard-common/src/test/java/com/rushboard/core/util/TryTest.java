package com.rushboard.core.util;

import static org.junit.Assert.*;

import java.util.Optional;
import org.junit.Test;

public class TryTest {

  @Test
  public void tryNormalCaseTest() {
    assertNotNull(Try.of(num -> 3 / num, 2d).getOrElseNull());
  }

  @Test
  public void tryExceptionalCaseTest() {
    assertNull(Try.of(num -> 3 / num, 0).getOrElseNull());
  }

  @Test
  public void tryToOptionalNormalCaseTest() {
    assertEquals(Try.of(num -> 3 / num, 2d).toOptional(), Optional.of(1.5));
  }

  @Test
  public void tryToOptionalEmptyCaseTest() {
    assertEquals(Try.of(num -> 3 / num, 0).toOptional(), Optional.empty());
  }
}
