package jpabook.jpashop.service;


import jpabook.jpashop.domain.Member;
import jpabook.jpashop.repository.MemberRepository;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@RequiredArgsConstructor
//'현재 클래스 MemberService의 내부에 있는 필드들 중에서', '@NonNull' 또는 'final'이
//붙어있는 필드들을 매개변수로 갖는 생성자를 자동으로 만들어주는 어노테이션임.
//e.g) 아래 필드들 같은 경우,
//      MemberSerivce memberService(MemberRepository memberRepository, PostService, postService){
//                  this.memberRepository = memberRepository;
//                  this.postService = postService;
//      }
//위 생성자를 '대신 만들어주는' 어노테이션이 '@RequiredArgsConstructor'이다!!
//https://mangkyu.tistory.com/78
@Transactional(readOnly = true)
@Service
public class MemberService {

   //< 생성자를 통한 의존성 주입 >
   //'레펏 MemberRepository'와 '서비스 PostService'를 모두 '의존성 주입'한다.
   private final MemberRepository memberRepository;
   @NonNull
   private PostService postService; //또는, 'private final MemberRepository memberRepository'라고 써도 됨






    //개발해야 할 기능
    //< 회원 가입 >
    //'클라이언트로부터 전달받은 회원가입 하려는 '엔티티 Member 객체''를 '통으로 매개변수에 넣음'.
    @Transactional
    public Long join(Member member){ //[ '회원 서비스 개발'강 03:40~ ]
                                     //이 'Member 객체'를 아래 '영속성 컨텍스트'에 넣을 때,
                                     //이 'Member 객체 자체'는 'Member 객체의 PK인 id값'으로 인식되어져 버려서,
                                     //이 'id값'이 영속성 컨텍스트에 저장되는 구조로 애초에 설계되어 있다.
                                     //따라서, 'Member 객체를 매개변수로 넣을 때',
                                     //'이 메소드의 리턴타입이 Long'이 되게 된다!

        //[ '회원 서비스 개발'강 02:20~ ]
        //'클라이언트로부터 전달받은 회원가입 하려는 회원과 둉일한 회원이 DB에 이미 존재하는지 중복검사'하는 로직
        //즉, '만약 DB에 이미 중복된 회원이 존재하면', '에러 Exception을 발생시키고',
        //'만약 DB에 중복된 회원이 없다면', '그냥 바로 다음 로직으로 진행시키는 것'.
        validateDuplicateMember(member);

        //'레펏 MemberRepository의 메소드 save를 호출하여',
        //'클라이언트롭터 전달받은 회원가입 하려는 '엔티티 Member 객체'를 db에 저장함
        //- '레퍼지토리'가 'DB 전 단계가 아니라', '서비스'가 'DB 전 단계'임!!
        //  그리고, '레퍼지토리'는 '서비스'<<---->>>'DB' 간의 연결을 담당해주는 것임.
        memberRepository.save(member);

        return member.getId();
    }


    //< '새로 회원가입 하려는 회원과 중복되는 이름이 DB에 이미 있는지 여부'를 검사하는 메소드 >
    private void validateDuplicateMember(Member member){

        //'레펏 MemberRepsoitory'를 호출하여, '클라이언트로부터 들어온 새로운 회원 이름(member.getName())'이
        //DB에 이미 있는지 여부 확인하기. 있다면 '변수 findMembers'에 담아서 '서비스'로 가져오는 것
        List<Member> findMembers = memberRepository.findByName(member.getName());

        //만약 DB에 이미 중복된 이름이 있다면
        if(!findMembers.isEmpty()){
                    throw new IllegalStateException("이미 DB에 존재하는 회원입니다"); //에러를 발생시킨다!
        }
    }


//=====================================================================================================

    //< 전체 회원 조회 >
    //@Transactional(readOnly = true) //- '조회'를 수행할 때, '읽기 전용 트랜잭션 @Transactional(readOnly = true)'
                                      //  를 붙이면, 보다 좀 더 '성능 최적화'가 가능함.
                                      //- 그런데, '현재 컨트롤러 내부'에는 '개별회원 조회, 전체회원 조회 등'의
                                      // '회원 조회 메소드'가 상대적으로 많아서
                                      // '@Transactional(readOnly = true)' 를 사용할 곳이 상대적으로 많음.
                                      //  따라서, 아에 그냥 '서비스 MemberService 바로 위에
                                      //  @Transactional(readOnly = true)'를 붙여버림.
                                      //- 그리고, '조회'가 아닌 '신규생성(Creeate)', '수정(Update)',
                                      //  '삭제(Delete)' 등 '그냥 @Transactional(readOnly = false)만 쓰는 
                                      //  메소드' 위에는 그냥 그대로 '@Transactional(readOnly = false)'을 
                                      //  써주면, 그 '@Transactional(readOnly = false)'가
                                      //  그 메소드에 한해서는 '@Transactional(readOnly = true)'보다 더
                                      //  우선권을 갖게 되므로, 그 위에 그렇게 써주면 된다!

    List<Member> findMembers(){

        List<Member> members = memberRepository.findAll();


        return members;
    }

//=====================================================================================================


    //< 개별 회원 조회>
    //'개별 회원을 식별'하려면 '식별자인 id'가 필요하기 때문에, 매개변수로 id를 건네준 것임
    //@Transactional(readOnly = true) //'조회'를 수행할 때,'읽기 전용 트랜잭션 @Transactional(readOnly = true)'
                                      //를 붙이면, 보다 좀 더 '성능 최적화'가 가능함
    Member findMember(Long memberId){ //'조회'를 수행할 때 '@Transactional(readOnly = true)'를 붙이면,
                                      //보다 좀 더 '성능 최적화'가 가능함.

        Member member = memberRepository.findOne(memberId);

        return member;
    }

    //'회원 서비스 개발'강 06:14~

//=====================================================================================================


}
