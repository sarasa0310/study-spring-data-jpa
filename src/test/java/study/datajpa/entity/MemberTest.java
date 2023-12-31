package study.datajpa.entity;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import static org.assertj.core.api.Assertions.assertThat;

@Transactional
@SpringBootTest
class MemberTest {

    @PersistenceContext
    private EntityManager em;

    @Test
    void testEntityRelation() {
        // Given
        Team ms = new Team("ms");
        Team apple = new Team("apple");

        Member bill = new Member("bill", 20, ms);
        Member steve = new Member("steve", 30, apple);
        Member tim = new Member("tim", 40, apple);

        // When
        em.persist(ms);
        em.persist(apple);

        em.persist(bill);
        em.persist(steve);
        em.persist(tim);

        // Then
        assertThat(ms.getMembers()).contains(bill);
        assertThat(apple.getMembers()).hasSize(2);
        assertThat(bill.getTeam().getName()).isEqualTo(ms.getName());
        assertThat(steve.getTeam()).isEqualTo(tim.getTeam());
    }

    @Test
    void jpaEventBaseEntity() throws Exception {
        Member jimmy = new Member("jimmy", 20);
        em.persist(jimmy); // @PrePersist 호출

        Thread.sleep(1000);
        jimmy.setUsername("kim");

        em.flush(); // @PreUpdate 호출
        em.clear();

        Member found = em.find(Member.class, jimmy.getId());
        System.out.println("found.getCreatedDate() = " + found.getCreatedDate());
        System.out.println("found.getLastModifiedDate() = " + found.getLastModifiedDate());
        System.out.println("found.getCreatedBy() = " + found.getCreatedBy());
        System.out.println("found.getLastModifiedBy() = " + found.getLastModifiedBy());
    }

}