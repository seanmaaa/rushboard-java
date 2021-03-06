package com.rushboard.member;

import com.rushboard.core.response.ExceptionType;
import com.rushboard.core.util.Either;
import com.rushboard.rdbms.member.Member;
import java.util.regex.Pattern;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;

public interface MemberRequest {
  Pattern mobilePattern = Pattern.compile("^\\d{2,3}-\\d{3,4}-\\d{4}$");
  Pattern emailPattern =
      Pattern.compile(
          "^[0-9a-zA-Z]([-_.]?[0-9a-zA-Z])*@[0-9a-zA-Z]([-_.]?[0-9a-zA-Z])*.[a-zA-Z]{2,3}$");

  Either<ExceptionType, ?> validate();

  @RequiredArgsConstructor
  @Data
  class MemberSaveRequest implements MemberRequest {
    @NotNull private final String username;
    @NotNull private final String password;
    @NotNull private final String email;
    @NotNull private final String mobile;

    @Override
    public Either<ExceptionType, Member> validate() {
      try {
        if (emailPattern.matcher(email).find()) {
          if (mobilePattern.matcher(mobile).find()) {
            return Either.right(
                new Member(username.toLowerCase(), password, email.toLowerCase(), mobile));
          }
          return Either.left(ExceptionType.INVALID_MOBILE);
        } else {
          return Either.left(ExceptionType.INVALID_EMAIL_ADDR);
        }
      } catch (Exception e) {
        return Either.left(ExceptionType.WRONG_REQUEST_PARAMETERS);
      }
    }
  }

  @AllArgsConstructor
  @Data
  class MemberUpdateRequest implements MemberRequest {
    private final String password;
    private final String email;
    private final String mobile;

    @Override
    public Either<ExceptionType, MemberUpdateRequest> validate() {
      if (password == null && email == null && mobile == null) {
        return Either.left(ExceptionType.EMPTY_REQUEST_BODY);
      } else {
        if (email != null && !emailPattern.matcher(email).find()) {
          return Either.left(ExceptionType.INVALID_EMAIL_ADDR);
        }
        if (mobile != null && !mobilePattern.matcher(mobile).find()) {
          return Either.left(ExceptionType.INVALID_MOBILE);
        }
      }
      return Either.right(this);
    }
  }
}
