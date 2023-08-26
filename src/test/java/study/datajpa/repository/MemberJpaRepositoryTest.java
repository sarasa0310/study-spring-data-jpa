package study.datajpa.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import study.datajpa.entity.Member;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@Transactional
@SpringBootTest
class MemberJpaRepositoryTest {

    @Autowired
    private MemberJpaRepository memberJpaRepository;

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

}