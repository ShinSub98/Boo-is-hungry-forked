package com.chaeshin.boo.repository;

import com.chaeshin.boo.domain.Member;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@Transactional
public class MemberRepositoryTest {

    @Autowired
    MemberRepository memberRepository;


    @Test
    public void 회원가입() {
        /*given*/
        Member member = createMember("1", "kim", "ko");

        /*when*/
        Member savedMember = memberRepository.save(member);

        /*then*/
        Assertions.assertEquals(member, savedMember);
    }

    @Test
    public void 구글ID로_조회() {
        /*given*/
        Member member = createMember("1", "kim", "ko");
        Member savedMember = memberRepository.save(member);

        /*when*/
        Member searchedMember = memberRepository.findByGoogleId("1");

        /*then*/
        Assertions.assertEquals(savedMember, searchedMember);
    }

    private Member createMember(String googleId, String nickname, String memberLang) {
        return new Member().builder()
                .googleId(googleId).nickname(nickname).memberLang(memberLang).build();
    }
}
