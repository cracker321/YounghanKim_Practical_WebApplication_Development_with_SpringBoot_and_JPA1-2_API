package jpabook.jpashop.service;

import jpabook.jpashop.domain.Address;
import jpabook.jpashop.domain.Member;
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

    @Test
    public void 회원가입_정상적으로_되는지() throws Exception{


        //# given
        Member member = new Member();
        member.setName("yujnog");
        member.setAddress(new Address("서울", "마들로", "110"));
        //중요!!
        //'Member 객체의 속성 중 하나인 Address'를 '여기 현재 Member 객체'에 넣으려면,
        //'그 인자값'으로 당연히 'Address 객체를 넣어줘야 한다!'
        //따라서, 넣을 때는 당연히 '새로운 Address 객체를 생성'해서 넣어주는 것이다!



        //# when



        //# then
    }



    @Test
    public void 중복회원여부_잘_검사되는지() throws Exception{

        //given

        //when

        //then

    }

}