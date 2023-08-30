package study.datajpa.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;
import study.datajpa.dto.MemberDto;
import study.datajpa.entity.Member;
import study.datajpa.entity.Team;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

@Transactional
@SpringBootTest
class MemberRepositoryTest {

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private TeamRepository teamRepository;

    @PersistenceContext
    private EntityManager em;

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

    @Test
    void findByUsernameAndAgeGreaterThan() {
        // Given
        Member jimmy = new Member("jimmy", 20);
        Member dubu = new Member("dubu", 30);
        Member oldJimmy = new Member("jimmy", 30);
        Member oldDubu = new Member("dubu", 40);

        memberRepository.save(jimmy);
        memberRepository.save(dubu);
        memberRepository.save(oldJimmy);
        memberRepository.save(oldDubu);

        // When
        List<Member> actual = memberRepository.findByUsernameAndAgeGreaterThan("dubu", 30);

        // Then
        assertThat(actual).contains(oldDubu);
        assertThat(actual).hasSize(1);
    }

    @Test
    void findByUsernameAndAge() {
        // Given
        Member walter = new Member("walter", 30);
        Member jessie = new Member("jessie", 20);

        memberRepository.save(walter);
        memberRepository.save(jessie);

        // When
        List<Member> result = memberRepository.findByUsernameAndAge("jessie", 20);

        // Then
        assertThat(result).contains(jessie);
        assertThat(result).hasSize(1);
    }

    @Test
    void findUsernameList() {
        // Given
        Member jimmy = new Member("jimmy", 40);
        Member chuck = new Member("chuck", 60);

        memberRepository.save(jimmy);
        memberRepository.save(chuck);

        // When
        List<String> usernameList = memberRepository.findUsernameList();

        // Then
        usernameList.forEach(System.out::println);

        assertThat(usernameList).contains(jimmy.getUsername(), chuck.getUsername());
        assertThat(usernameList).hasSize(2);
    }

    @Test
    void findMemberDto() {
        // Given
        Team saul = new Team("better call saul");
        Team hhm = new Team("HHM");

        teamRepository.save(saul);
        teamRepository.save(hhm);

        Member jimmy = new Member("jimmy", 40, saul);
        Member chuck = new Member("chuck", 60, hhm);

        memberRepository.save(jimmy);
        memberRepository.save(chuck);

        // When
        List<MemberDto> memberDtoList = memberRepository.findMemberDto();
        for (MemberDto memberDto : memberDtoList) {
            System.out.println(memberDto);
        }

        // Then
        assertThat(memberDtoList).hasSize(2);
    }

    @Test
    void pageTest() {
        // Given
        memberRepository.save( new Member("member1", 10));
        memberRepository.save( new Member("member2", 10));
        memberRepository.save( new Member("member3", 10));
        memberRepository.save( new Member("member4", 10));
        memberRepository.save( new Member("member5", 10));

        int age = 10;
        PageRequest pageRequest = PageRequest.of(0, 3, Sort.by(Sort.Direction.DESC, "username"));

        // When
        Page<Member> page = memberRepository.findByAge(age, pageRequest);
        Page<MemberDto> dtoPage = page.map(MemberDto::toDto); // API는 엔티티가 아닌 DTO로 반환!

        page.map(Member::getUsername).forEach(System.out::println);

        // Then
        assertThat(page).hasSize(3);
        assertThat(page.getSize()).isEqualTo(3);
        assertThat(page.getContent().size()).isEqualTo(3);

        assertThat(page.getTotalElements()).isEqualTo(5);

        assertThat(page.getNumber()).isEqualTo(0);
        assertThat(page.getTotalPages()).isEqualTo(2);

        assertThat(page.isFirst()).isTrue();
        assertThat(page.hasNext()).isTrue();
    }

    @Test
    void slicePageTest() {
        memberRepository.save( new Member("member1", 10));
        memberRepository.save( new Member("member2", 10));
        memberRepository.save( new Member("member3", 10));
        memberRepository.save( new Member("member4", 10));
        memberRepository.save( new Member("member5", 10));

        int age = 10;
        PageRequest pageRequest = PageRequest.of(0, 3, Sort.by(Sort.Direction.DESC, "username"));

        // When
        Slice<Member> page = memberRepository.findByAge(age, pageRequest);

        page.map(Member::getUsername).forEach(System.out::println);

        // Then
        assertThat(page).hasSize(3);
        assertThat(page.getSize()).isEqualTo(3);
        assertThat(page.getContent().size()).isEqualTo(3);

//        assertThat(page.getTotalElements()).isEqualTo(5);

        assertThat(page.getNumber()).isEqualTo(0);
//        assertThat(page.getTotalPages()).isEqualTo(2);

        assertThat(page.isFirst()).isTrue();
        assertThat(page.hasNext()).isTrue();
    }

    @Test
    void bulkAgePlus() {
        // Given
        memberRepository.save( new Member("member1", 10));
        memberRepository.save( new Member("member2", 20));
        memberRepository.save( new Member("member3", 30));
        memberRepository.save( new Member("member4", 40));
        memberRepository.save( new Member("member5", 50));

        // When
        int updatedCount = memberRepository.bulkAgePlus(30);
        List<Member> members = memberRepository.findByAgeGreaterThanEqual(30);
        members.forEach(System.out::println);

        // Then
        assertThat(updatedCount).isEqualTo(3);
    }

    @Test
    void findMemberLazy() {
        // Given
        Team teamA = new Team("teamA");
        Team teamB = new Team("teamB");

        teamRepository.save(teamA);
        teamRepository.save(teamB);

        Member member1 = new Member("member1", 20, teamA);
        Member member2 = new Member("member2", 30, teamB);

        memberRepository.save(member1);
        memberRepository.save(member2);

        em.flush();
        em.clear();

        // When
        List<Member> members = memberRepository.findAll();
        List<MemberDto> memberDtos = members.stream().map(MemberDto::toDto).collect(Collectors.toList());
        System.out.println(memberDtos);

        // Then
    }

    @Test
    void queryHint() {
        // Given
        Member member = memberRepository.save(new Member("member1", 10));
        em.flush();
        em.clear();

        // When
        Member findMember = memberRepository.findReadOnlyByUsername("member1");
//        findMember.setUsername("member2"); // update문 생성 X

        em.flush();
    }

    @Test
    void lock() {
        // Given
        Member member = memberRepository.save(new Member("member1", 10));
        em.flush();
        em.clear();

        // When
        List<Member> members = memberRepository.findLockByUsername("member1");
    }

    @Test
    void callCustom() {
        List<Member> members = memberRepository.findMemberCustom();
    }

}