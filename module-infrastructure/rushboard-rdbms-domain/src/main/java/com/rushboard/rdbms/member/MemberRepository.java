package com.rushboard.rdbms.member;

import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public interface MemberRepository extends R2dbcRepository<Member, Integer> {

  Mono<Member> findOneByUsername(String username);

  Mono<Member> findOneByEmail(String username);
}
