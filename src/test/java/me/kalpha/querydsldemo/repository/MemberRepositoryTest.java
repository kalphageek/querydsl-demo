package me.kalpha.querydsldemo.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import me.kalpha.querydsldemo.dto.MemberSearchCondition;
import me.kalpha.querydsldemo.dto.MemberTeamDto;
import me.kalpha.querydsldemo.entity.Member;
import me.kalpha.querydsldemo.entity.Team;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@Transactional
@SpringBootTest
class MemberRepositoryTest {
    @Autowired
    EntityManager em;
    JPAQueryFactory queryFactory;
    @Autowired
    MemberRepository memberRepository;

    @DisplayName("MemberRepository Basic 테스트")
    @Test
    public void basicTest() {
        Member member = new Member("member1", 10);
        memberRepository.save(member);

        Member findMember = memberRepository.findById(member.getId()).get();
        assertEquals(findMember.getId(), member.getId());

        List<Member> result1 = memberRepository.findAll();
        assertThat(result1).extracting("id").containsExactly(member.getId());

        List<Member> result2 = memberRepository.findByUsername(member.getUsername());
        assertThat(result2).extracting("id").containsExactly(member.getId());
    }

    @DisplayName("Custom MemberRepostory with SearchCondition 테스트")
    @Test
    public void searchTest() {
        Team teamA = new Team("teamA");
        Team teamB = new Team("teamB");
        em.persist(teamA);
        em.persist(teamB);

        Member member1 = new Member("member1", 10, teamA);
        Member member2 = new Member("member2", 30, teamA);
        Member member3 = new Member("member3", 20, teamB);
        Member member4 = new Member("member4", 40, teamB);
        em.persist(member1);
        em.persist(member2);
        em.persist(member3);
        em.persist(member4);

        MemberSearchCondition condition = new MemberSearchCondition();
        condition.setAgeGoe(35);
        condition.setAgeLoe(40);
        condition.setTeamName("teamB");

        List<MemberTeamDto> result = memberRepository.search(condition);

        assertThat(result).extracting("username").containsExactly("member4");
    }

}