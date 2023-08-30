package study.datajpa.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import study.datajpa.dto.MemberDto;
import study.datajpa.entity.Member;

import javax.persistence.LockModeType;
import javax.persistence.QueryHint;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long>, MemberRepositoryCustom {

    // 메서드 이름으로 쿼리 생성 -> 편리하지만, 파라미터가 많아지면 메서드 이름이 너무 길어질 수 있음 -> 그럴 땐 @Query 사용
    List<Member> findByUsernameAndAgeGreaterThan(String username, int age);

    // @NamedQuery 방식 - 실무 잘 사용 X
    @Query(name = "Member.findByUsername") // 생략 가능 -> 기본적으로 Member의 NamedQuery 참조
    List<Member> findByUsername(@Param("username") String username);

    // Repository에 직접 Query 작성 -> 실무에서 자주 사용
    @Query("select m from Member m where m.username = :username and m.age = :age")
    List<Member> findByUsernameAndAge(@Param("username") String username,
                                      @Param("age") int age);

    // Member 값 조회
    @Query("select m.username from Member m")
    List<String> findUsernameList();

    // DTO 조회 -> QueryDSL 사용 시 편리하게 변경 가능
    @Query("select new study.datajpa.dto.MemberDto(m.id, m.username, t.name) from Member m join m.team t")
    List<MemberDto> findMemberDto();

    // 컬렉션 파라미터 바인딩
    @Query("select m from Member m where m.username in :names")
    List<Member> findByNames(@Param("names") Collection<String> names);

    // 유연한 반환 타입 ---
    Member findMemberByUsername(String username); // 단건 조회는 결과가 없으면 null, 2건 이상 -> NonUniqueResultException
    Optional<Member> findOptionalMemberByUsername(String username); // 단건 조회는 Optional 사용 추천
    List<Member> findMembersByUsername(String username); // 컬렉션 조회는 결과가 없으면 null이 아닌 빈 컬렉션 반환(size = 0)
    // ------------------

//    @Query(value = "select m from Member m left join m.team t",
//            countQuery = "select count(m) from Member m") // count 쿼리 분리 가능
    Page<Member> findByAge(int age, Pageable pageable);
//    Slice<Member> findByAge(int age, Pageable pageable); // count 쿼리 나가지 않고 다음 페이지 유무만 확인(limit + 1), 총 페이지, 총 요수의 수가 필요 없을 때 성능상 우위
//    List<Member> findByAge(int age, Pageable pageable); // 끊어서 가져오고 싶을 때 유용

    @Modifying(clearAutomatically = true) // bulk 연산 이후 영속성 컨텍스트 비우기, why? 벌크성 수정 쿼리는 영속성 컨텍스트를 건너뛰고 DB에 바로 업데이트 하기 때문에 데이터 정합성에 문제 발생 가능
    @Query("update Member m set m.age = m.age + 1 where m.age >= :age")
    int bulkAgePlus(@Param("age") int age);

    List<Member> findByAgeGreaterThanEqual(int age);

    // Lazy 로딩 시 쿼리 성능 개선 fetch join & @EntityGraph
    @Query("select m from Member m left join fetch m.team")
    List<Member> findMemberFetchJoin();
    @Override
//    @EntityGraph("Member.all") -> Member 클래스의 @NamedEntityGraph 사용 방법
    @EntityGraph(attributePaths = {"team"})
    List<Member> findAll();
    // ----------------------------------

    @QueryHints(value = @QueryHint(name = "org.hibernate.readOnly", value = "true"))
    Member findReadOnlyByUsername(String username);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    List<Member> findLockByUsername(String username);

}
