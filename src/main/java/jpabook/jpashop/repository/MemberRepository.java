package jpabook.jpashop.repository;


import jpabook.jpashop.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

@Repository
public class MemberRepository {

    @PersistenceContext
    private EntityManager em;


    //< '회원 저장'하기 >
    public void save(Member member){

        em.persist(member);
    }


    //< '개별 회원(1명) 조회'하기 >
    //'매개변수로 들어온 id에 해당하는 회원'을 DB에서 찾아와서 '그 회원을 리턴'해줌
    public Member findOne(Long id){

        Member member = em.find(Member.class, id); //'반환타입', '들어온 id값' 순
        return member;

    }


    //< '전체 회원(N명) 조회'하기 >
    public List<Member> findAll() {

        //'JPQL 작성해서 이를 통해 전체 회원 목록 가져오기>
        List<Member> result = em.createQuery("select m from Member", Member.class)
                .getResultList();

        return result;

    }


}
