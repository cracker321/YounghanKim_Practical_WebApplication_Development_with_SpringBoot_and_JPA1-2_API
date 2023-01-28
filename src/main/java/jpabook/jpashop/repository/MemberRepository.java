package jpabook.jpashop.repository;


import jpabook.jpashop.domain.Member;
import jpabook.jpashop.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

@RequiredArgsConstructor
@Repository
public class MemberRepository {


    //[ '회원 서비스 개발'강 16:45~ ]
    //< 생성자를 통한 의존성 주입 >
    //'클래스레벨 어노테이션 @RequriedArgsConstructor와 결합하여' 'EntityManger 객체의 의존성 주입'이 가능해진다!
    private final EntityManager em;
//    또는
//    @PersistenceContext //또는 '@Autowired'를 사용하도 무방하다.
//    private EntityManager em;


    //저 아래 DB로부터 '회원 조회'하는 작업들 수행하기 위해 일단 그 전제가 되어야 하는 'DB에 회원 저장'하는 작업임
    //중요한 건 아님. 이 'Member 객체'를 '영속성 컨텍스트'에 넣는 것임.
    //아래처럼 이렇게 저장해주면 이제 DB에 'INSERT 쿼리'가 날라가서, DB에 아래 적히 1명의 member가 저장됨.
    public void save(Member member){ //이 'Member 객체'를 아래 '영속성 컨텍스트'에 넣을 때,
                                     //이 'Member 객체 자체'는 'Member 객체의 PK인 id값'으로 인식되어져 버려서,
                                     //이 'id값'이 영속성 컨텍스트에 저장되는 구조로 애초에 설계되어 있다.

        em.persist(member);
    }



    //< '개별 회원(1명)을 'id값'으로 조회'하기 >
    //'클라이언트로부터 매개변수로 들어온 id에 해당하는 1명의 회원'을 DB에서 찾아와서 '그 회원을 리턴'해줌
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
    //'클라이언트로부터 매개변수로 들어온 name에 해당하는 N명의 회원을 조회'하는 것
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
