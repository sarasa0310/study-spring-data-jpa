package study.datajpa.repository;

import lombok.RequiredArgsConstructor;
import study.datajpa.entity.Member;

import javax.persistence.EntityManager;
import java.util.List;

@RequiredArgsConstructor
public class MemberRepositoryImpl implements MemberRepositoryCustom { // 클래스 네이밍 컨벤션 -> ~Impl

    private final EntityManager em;

    @Override
    public List<Member> findMemberCustom() {
        return em.createQuery(
                "select m from Member m", Member.class)
                .getResultList();
    }

}
