package jpabook.jpashop.service;

import com.fasterxml.classmate.MemberResolver;
import jpabook.jpashop.domain.Address;
import jpabook.jpashop.domain.Member;
import jpabook.jpashop.repository.MemberRepository;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.Assert.*;


@RunWith(SpringRunner.class)
@SpringBootTest
@Transactional //'테스트 케이스일 경우에서 사용되는 '@Transactional''은 기본적으로 'SQL 쿼리문을 DB로 커밋 안 하고, 롤백을 해버린다'.
               //즉, 이건 테스트코드이기에 당연히 DB로 'INSERT ...' 이런 쿼리문을 애초에 날리지 않고, 다시 롤백해버린다.
               //(왜냐하면, '보통 테스트를 반복해서 하고', 그에 따라 DB에 쿼리문을 날려서 CRUD된 데이터가 남아있으면 안되기 때문!)
               //그럼에도 만약 내가 '롤백하지 않고 최종 커밋한 결과를 보고 싶다면', '해당 테스트 메소드 위에 '@Rollback(false)'를 넣는다!
               //'해당 테스트 메소드 위에 '@Rollback(false)'를 넣으면', 현재 이 테스트를 실행시켰을 때 롤백하지 않은 최종 커밋 결과를
               //볼 수 있다.
               //즉, 이 테스트 코드를 실행시킨 후 뜨는 콘솔창에, 마치 테스트코드가 아닌 실제 정상 서버에서 작성한 코드를 실행시킨
               //것처럼, 'DB로 쿼리문('INSERT ...' 등)을 날리고 그 쿼리문 자체를 콘솔창에 보여준다!!'
               //테스트코드이더라도, '@Rollback(false)'해버리면, 실제로 DB에 쿼리문이 날라가서 DB에 데이터가 저장 등 CRUD된다!!
public class MemberServiceTest {

    @Autowired
    MemberService memberService;
    @Autowired
    MemberRepository memberRepository;


//===============================================================================================================


    @Rollback(false)
    @Test
    public void 회원가입_정상적으로_되는지() throws Exception{


        //# 전제 given
        //'새로운 하나의 Member 객체를 생성'함.
        Member mmm = new Member();

        //'새롭게 생성한 Member 객체'에 '1~2가지 속성들을 넣어줌'.
        mmm.setName("yujnog");
        mmm.setAddress(new Address("서울", "마들로", "110")); //사실 이건 필요 없긴 함
        //중요!!
        //'Member 객체의 속성 중 하나인 Address'를 '여기 현재 Member 객체'에 넣으려면,
        //'그 인자값'으로 당연히 'Address 객체를 넣어줘야 한다!'
        //따라서, 넣을 때는 당연히 '새로운 Address 객체를 생성'해서 넣어주는 것이다!



        //# 조건 when
        //이 부분이 실제적으로 '해당 회원가입 메소드를 검증하는 곳'
        Long savedId = memberService.join(mmm);


        //# 검증 then
        //- 'mmm': 'Memeber 객체의 id값'
        //- 'memberRepository.findOne(savedId)': '레펏 MemberRepository'가 'DB로부터 찾아온 id값'
        //위 두 개의 리턴값(결괏값)이 같은지 여부를 검증하는 것!
        assertEquals(mmm, memberRepository.findOne(savedId));
        //= Assert.assertEquals(mmm, memberRepository.findOne(savedId));
        //그냥 'Assert'는 생략 가능한 듯..?
    }


//===============================================================================================================


    //[ '회원 기능 테스트'강 12:00~ ]
    @Test(expected = IllegalStateException.class)
    public void 중복회원검증이_잘_검사되는지() throws Exception{


        //# 전제 given
        //'중복된 회원을 잘 검증해서 골라내는지 여부'를 파악하기 위해, '동일한 이름 ahra를 가진 'mm2 객체'와 'mm3 객체'를 생성'함.
        Member mmm2 = new Member();
        mmm2.setName("ahra");
        mmm2.setAddress(new Address("Van", "Robson", "11555"));


        Member mmm3 = new Member();
        mmm3.setName("ahra");
        mmm3.setAddress(new Address("Seoul", "Madeulro", "31119"));


        //# 조건 when
        memberService.join(mmm2); //먼저, 'mmm2 객체'를 DB에 저장한 후, 그 저장한 회원 id값을 그냥 별 뜻 없이 리턴함.
        memberService.join(mmm3); //- 이렇게 입력한다면 당연히 '중복회원 검증 로직에 따라 지금 여기 중복회원이 입력되었으므로,
                                  //  정상적인 예외가 발생해서 테스트서버 실행 시 테스트가 실패해야 한다!'.
                                  //  그게 이 예외 검증 테스트코드 작성의 목표인 것임.
                                  //- 그런데, '현재 메소드 위 @Test 옆에 'expected = IllegalStateException.class'를
                                  //  입력해줬기에, 이제 이걸 '주석 해제'해서 테스트서버 동작시켜도, 이제 테스트서버 자체가
                                  //  에러 없이 정상적으로 작동한다!

          //원래 아래 로직처럼 작성해야 하는데, 위에 이 '현재 메소드의 @Test 옆에 'expected = IllegalStateException.class'를
          //입력해주면, 그게 아래 로직을 다 대체해준다. 즉, 깔끔하게 해주는 것임.
          //그러면 이제, 아래 로직을 이렇게 주석처리하고, 위에 'memberService.join(mmm3)'를 '주석 해제'해서 테스트해 줄 수 있다!
//        //[ '회원 기능 테스트'강 15:00~ ]
//        try{    //따라서, 일단 이 테스트코드를 정상적으로 동작시켜서 확인이라도 해봐야 하기 때문에, 'try-catch문'으로
//                //'memberService.join(mmm3)'를 넣어준다
//            memberService.join(mmm3);
//        }catch(IllegalStateException e){ //이 '테스트코드의 예외'를 발생시킴
//
//            return; //'return;'와 'return null' 은 동일한 뜻임!
//        }



        //# 검증 then
        fail("중복회원 검증에서 exception이 정상적으로 발생하도록 테스트코드를 설계해서 테스트하는 것이 원래 목적이기에," +
                "여기 fail문에 도달하기 전에 당연히 exception이 발생했었어야 하는데, 그 exception이 발생하지 않아서" +
                "여기 fail문까지 도달했으므로, 위에 테스트로직에서 뭔가를 잘못 작성한 것임. 따라서 위에 테스트로직 수정해서" +
                "중복회원 검증시 exception이 정상적으로 발생하는지 여부 체크되어야 함.");
        //여기 쓴 그대로, 이 테스트서버 실행해서 여기 fail까지 도달해서 위 'fail문 내부의 긴~ 문장들'이 정상적으로 콘솔에
        //출력된거라면, 그건 지금 테스트코드를 내가 원래 의도한대로 제대로 작성한 것이 아님.
        //따라서, 위에 테스트코드 수정해야 함.


    }
//    @Test
//    public void 중복회원여부_잘_검사되는지() throws Exception{
//
//        //# 전제 given
//
//        //# 조건 when
//
//        //# 검증 then
//
//    }

}