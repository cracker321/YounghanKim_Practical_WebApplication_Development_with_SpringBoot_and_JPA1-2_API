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
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.Assert.*;


@RunWith(SpringRunner.class)
@SpringBootTest
@Transactional
public class MemberServiceTest {

    @Autowired
    MemberService memberService;
    @Autowired
    MemberRepository memberRepository;

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
        Assert.assertEquals(mmm, memberRepository.findOne(savedId));



    }



    @Test
    public void 중복회원여부_잘_검사되는지() throws Exception{

        //given

        //when

        //then

    }

}