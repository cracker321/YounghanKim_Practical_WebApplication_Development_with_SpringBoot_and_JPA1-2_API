package jpabook.jpashop.repository;


import jpabook.jpashop.domain.Member;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

@Repository
public class MemberRepository {

    @PersistenceContext
    private EntityManager em;


    //저 아래 DB로부터 '회원 조회'하는 작업들 수행하기 위해 일단 그 전제가 되어야 하는 'DB에 회원 저장'하는 작업임
    //중요한 건 아님. 이 'Member 객체'를 '영속성 컨텍스트'에 넣는 것임.
    //아래처럼 이렇게 저장해주면 이제 DB에 'INSERT 쿼리'가 날라가서, DB에 아래 적히 1명의 member가 저장됨.
    public void save(Member member){

        em.persist(member);
    }



    //< '개별 회원(1명)을 'id값'으로 조회'하기 >
    //'클라이언트로부터 매개변수로 들어온 id에 해당하는 회원'을 DB에서 찾아와서 '그 회원을 리턴'해줌
    public Member findOne(Long id){

        Member member = em.find(Member.class, id); //'조회타입', '들어온 id값' 순

        return member;
    }



    //< '모든 회원 조회'하기 >
    public List<Member> findAll() {

        //JPQL 작성해서 이를 통해 전체 회원 목록 가져오기
        List<Member> members = em.createQuery("select m from Member m", Member.class)
                .getResultList();

        return members;
    }



    //< '다수 회원(N명) 중 '매개변수로 들어온 이름(변수 name)'에 부합하는 회원을 모두' 조회'하기 >
    public List<Member> findByName(String name){

       //JPQL 작성해서 이를 통해 전체 회원 목록 가져오기
       List<Member> result = em.createQuery("select m from Member m where m.name = :memberName",
                       //'파라미터로 들어온 name'을 '이 메소드에서 사용할 변수 memberName에' binding 해주는 것
                       Member.class)
               .setParameter("memberName", name)
               .getResultList();

       return result;
    }


}
