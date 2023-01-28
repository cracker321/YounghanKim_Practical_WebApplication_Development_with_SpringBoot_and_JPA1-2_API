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
    @Test
    public void 중복회원검증이_잘_검사되는지() throws Exception{


        //# 전제 given
        Member mmm2 = new Member();
        mmm2.setName("ahra");
        mmm2.setAddress(new Address("Van", "Robson", "11555"));

        Member mmm3 = new Member();
        mmm3.setName("wonhee");
        mmm3.setAddress(new Address("Seoul", "Madeulro", "31119"));


        //# 조건 when
        Long savedId2 = memberService.join(mmm2);
        Long savedId3 = memberService.join(mmm3); //여기서 반드시 예외가 발생해야 함


        //# 검증 then
        assertEquals(mmm2, memberRepository.findOne(savedId2));


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