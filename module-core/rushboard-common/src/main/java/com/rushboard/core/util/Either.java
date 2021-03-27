package com.rushboard.core.util;

import java.util.function.Function;

public final class Either<A,B> {
  private A left = null;
  private B right = null;

  private Either(A a,B b) {
    left = a;
    right = b;
  }

  public static <A,B> Either<A,B> left(A a) {
    return new Either<A,B>(a,null);
  }

  public A left() {
    return left;
  }

  public boolean isLeft() {
    return left != null;
  }

  public boolean isRight() {
    return right != null;
  }

  public B right() {
    return right;
  }

  public static <A,B> Either<A,B> right(B b) {
    return new Either<A,B>(null,b);
  }

  public <R> R fold(Function<? super A, ? extends R> leftOperation, Function<? super B, ? extends R> rightOperation) {
    if(right == null)
      return leftOperation.apply(left);
    else
      return rightOperation.apply(right);
  }


}
