package com.rushboard.rdbms.member;

import java.util.UUID;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public interface MemberRepository extends R2dbcRepository<Member, UUID> {

  Mono<Member> findOneByMemberid(UUID uuid);

  Mono<Member> findOneByUsername(String username);

  Mono<Member> findOneByEmail(String username);
}
