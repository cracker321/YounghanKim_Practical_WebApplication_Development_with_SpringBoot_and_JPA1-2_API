package jpabook.jpashop.service;

import jpabook.jpashop.domain.Member;
import jpabook.jpashop.domain.Order;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.Assert.*;


@RunWith(SpringRunner.class)
//https://ssons.tistory.com/63
//https://recordsoflife.tistory.com/1080
//https://stackoverflow.com/questions/58901288/springrunner-vs-springboottest
//- '@RunWith(SpringRunner.class)': '단위 테스트'를 수행하는 동안에만 사용되는 어노테이션.
//                             '현재 프로젝트 전체를 로드하여 테스트하는 것이 아니라', 여기처럼 '현재 클래스 OrderServiceTest'만
//                                  테스트할 때 사용하는 어노테이션임.
//- '@SpringBootTest': 이거 하나만 사용하고, '@RunWith(SpringRunner.class)'를 추가로 붙이지 않으면,
//                     '현재 프로젝트 전체의 application context 전부를 로딩'하여 '테스트 로딩 시간 지연'됨.
@Transactional
@SpringBootTest
public class OrderServiceTest {

    //[ '주문 기능 테스트'강 ]


//=============================================================================================================


    //[ '주문 기능 테스트'강. 02:05~ ]

    //< '신규주문 생성'이 잘 작동하는지 >
    @Test
    public void 상품주문() throws Exception {

        //# 전제 given
        Member member = new Member();


        //# 조건 when


        //# 검증 then


    }


//=============================================================================================================

    
    //< '주문 취소'가 잘 작동하는지 테스트 >
    @Test
    public void 주문취소() throws Exception{




    }


//=============================================================================================================


    //< '재고수량 초과'
    @Test
    public void 상품주문_재고수량초과() throws Exception{




    }



//=============================================================================================================


}