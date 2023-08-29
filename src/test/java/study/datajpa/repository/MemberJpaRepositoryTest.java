package study.datajpa.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import study.datajpa.entity.Member;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@Transactional
@SpringBootTest
class MemberJpaRepositoryTest {

    @Autowired
    private MemberJpaRepository memberJpaRepository;
    @PersistenceContext
    private EntityManager em;

    @Test
    void save() {
        // Given
        Member member = new Member("memberA", 20);

        // When
        Member savedMember = memberJpaRepository.save(member);

        // Then
        Member findMember = memberJpaRepository.find(savedMember.getId());

        assertThat(findMember.getId()).isEqualTo(member.getId());
        assertThat(findMember.getUsername()).isEqualTo(member.getUsername());
        assertThat(findMember).isEqualTo(member);
    }

    @Test
    void basicCRUD() {
        Member jimmy = new Member("jimmy", 28);
        Member dubu = new Member("dubu", 28);

        memberJpaRepository.save(jimmy);
        memberJpaRepository.save(dubu);

        Member find1 = memberJpaRepository.findById(jimmy.getId()).get();
        Member find2 = memberJpaRepository.findById(dubu.getId()).get();

        List<Member> all = memberJpaRepository.findAll();
        long count = memberJpaRepository.count();

        assertThat(all).contains(find1, find2);
        assertThat(all).hasSize(2);
        assertThat(count).isEqualTo(2);

        memberJpaRepository.delete(jimmy);
        memberJpaRepository.delete(dubu);

        long deletedCount = memberJpaRepository.count();
        assertThat(deletedCount).isEqualTo(0);
    }

    @Test
    void findByUsernameAndAgeGreaterThan() {
        // Given
        Member jimmy = new Member("jimmy", 20);
        Member dubu = new Member("dubu", 30);
        Member oldJimmy = new Member("jimmy", 30);
        Member oldDubu = new Member("dubu", 40);

        memberJpaRepository.save(jimmy);
        memberJpaRepository.save(dubu);
        memberJpaRepository.save(oldJimmy);
        memberJpaRepository.save(oldDubu);

        // When
        List<Member> actual = memberJpaRepository.findByUsernameAndAgeGreaterThan("dubu", 30);

        // Then
        assertThat(actual).contains(oldDubu);
        assertThat(actual).hasSize(1);
    }

    @Test
    void pageTest() {
        // Given
        memberJpaRepository.save( new Member("member1", 10));
        memberJpaRepository.save( new Member("member2", 10));
        memberJpaRepository.save( new Member("member3", 10));
        memberJpaRepository.save( new Member("member4", 10));
        memberJpaRepository.save( new Member("member5", 10));

        int age = 10;
        int offset = 0;
        int limit = 3;

        // When
        List<Member> members = memberJpaRepository.findByPage(age, offset, limit);
        long count = memberJpaRepository.totalCount(age);

        // Then
        assertThat(members).hasSize(3);
        assertThat(count).isEqualTo(5);
    }

    @Test
    void bulkAgePlus() {
        // Given
        memberJpaRepository.save( new Member("member1", 10));
        memberJpaRepository.save( new Member("member2", 20));
        memberJpaRepository.save( new Member("member3", 30));
        memberJpaRepository.save( new Member("member4", 40));
        memberJpaRepository.save( new Member("member5", 50));

        // When
        int updatedCount = memberJpaRepository.bulkAgePlus(30);
        em.clear();

        // Then
        List<Member> result = memberJpaRepository.findByAgeGreaterThanEqual(30);
        result.forEach(System.out::println);

        assertThat(result).hasSize(3);
        assertThat(updatedCount).isEqualTo(3);
    }

}