package jpabook.jpashop.service;


import jpabook.jpashop.domain.Member;
import jpabook.jpashop.repository.MemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MemberService {

    @Autowired
    private MemberRepository memberRepository;

    //개발해야 할 기능
    //< 회원 가입 >
    //'클라이언트로부터 전달받은 회원가입 하려는 '엔티티 Member 객체''를 '통으로 매개변수에 넣음'.
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
        //- '레퍼지토리'가 'DB 전 단계가 아니라', '서비스'가 'DB 전 단계'임.
        //  그리고, '레퍼지토리'는 '서비스'와 'DB'간의 연결을 담당해주는 것임
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
    List<Member> findMembers(){

        List<Member> members = memberRepository.findAll();


        return members;
    }




//=====================================================================================================


}
