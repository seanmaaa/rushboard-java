package com.rushboard.rdbms.member;

import com.rushboard.rdbms.member.type.RegType;
import com.rushboard.core.mapping.RoleType;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;

import java.sql.Timestamp;
import java.util.function.UnaryOperator;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Member {

  public Member(String username, String password, String email, String mobile) {
    this.username = username;
    this.password = password;
    this.email = email;
    this.mobile = mobile;
  }

  @Id private UUID memberid;

  private String username;

  private String email;

  private String password;

  private String mobile;

  private RegType regtype;

  private RoleType role;

  private boolean verified;

  private Timestamp createdat;

  private Timestamp updatedat;

  private Timestamp deletedat;

  public Member encodePassword(UnaryOperator<String> encoder) {
    password = encoder.apply(password);
    return this;
  }
}
