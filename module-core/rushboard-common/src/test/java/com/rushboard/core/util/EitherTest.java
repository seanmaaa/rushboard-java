package com.rushboard.core.util;

import static org.junit.Assert.*;

import java.util.function.Function;
import org.junit.Test;

public class EitherTest {

  Function<Integer, Either<Exception, Integer>> div =
      num -> {
        try {
          return Either.right(3 / num);
        } catch (Exception e) {
          return Either.left(e);
        }
      };

  @Test
  public void eitherNormalCaseTest() {
    assertTrue(div.apply(2).isRight());
    assertTrue(div.apply(0).isLeft());
  }

  @Test
  public void eitherFoldTest() {
    assertTrue(div.apply(2).fold(left -> false, right -> true));
    assertTrue(div.apply(0).fold(left -> true, right -> false));
  }

  @Test
  public void eitherGetValueTest() {
    assertTrue(div.apply(2).right() instanceof Integer);
    assertTrue(div.apply(0).left() instanceof Exception);
  }
}
