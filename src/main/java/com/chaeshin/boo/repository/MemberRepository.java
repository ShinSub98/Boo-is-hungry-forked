package com.chaeshin.boo.repository;

import com.chaeshin.boo.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<Member, Long> {

    /*구글ID를 통해 유저 조회*/
    Member findByGoogleId(String googleId);
}
