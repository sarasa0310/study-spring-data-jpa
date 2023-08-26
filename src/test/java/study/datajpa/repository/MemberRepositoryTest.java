package study.datajpa.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import study.datajpa.entity.Member;

import java.util.List;
import java.util.NoSuchElementException;

import static org.assertj.core.api.Assertions.assertThat;

@Transactional
@SpringBootTest
class MemberRepositoryTest {

    @Autowired
    private MemberRepository memberRepository;

    @Test
    void save() {
        // Given
        Member member = new Member("memberA", 20);

        // When
        Member savedMember = memberRepository.save(member);

        // Then
        Member findMember = memberRepository.findById(savedMember.getId())
                .orElseThrow(NoSuchElementException::new);

        assertThat(findMember.getId()).isEqualTo(member.getId());
        assertThat(findMember.getUsername()).isEqualTo(member.getUsername());
        assertThat(findMember).isEqualTo(member);
    }

    @Test
    void basicCRUD() {
        System.out.println("memberRepository.getClass() = " + memberRepository.getClass());

        Member jimmy = new Member("jimmy", 28);
        Member dubu = new Member("dubu", 28);

        memberRepository.save(jimmy);
        memberRepository.save(dubu);

        Member find1 = memberRepository.findById(jimmy.getId()).get();
        Member find2 = memberRepository.findById(dubu.getId()).get();

        List<Member> all = memberRepository.findAll();
        long count = memberRepository.count();

        assertThat(all).contains(find1, find2);
        assertThat(all).hasSize(2);
        assertThat(count).isEqualTo(2);

        memberRepository.delete(jimmy);
        memberRepository.delete(dubu);

        long deletedCount = memberRepository.count();
        assertThat(deletedCount).isEqualTo(0);
    }

}