package com.rushboard.client.auth.jwt;

import org.springframework.security.oauth2.jwt.JwtClaimNames;

public interface RushboardJwtClaimNames extends JwtClaimNames {

  String TOKEN_TYPE = "tktyp";

  String USER_ID = "usrid";

  String USER_ROLE = "role";

}
