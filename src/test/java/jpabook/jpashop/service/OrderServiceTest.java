package jpabook.jpashop.service;

import jpabook.jpashop.domain.Address;
import jpabook.jpashop.domain.Item.Book;
import jpabook.jpashop.domain.Item.Item;
import jpabook.jpashop.domain.Member;
import jpabook.jpashop.domain.Order;
import jpabook.jpashop.domain.OrderStatus;
import jpabook.jpashop.repository.OrderRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;

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


//=============================================================================================================


    @Autowired
    EntityManager em;
    @Autowired
    OrderService orderService; //'테스트코드' 작성 시에는 'private final'을 안 붙여도 된다!
    @Autowired
    OrderRepository orderRepository;




//=============================================================================================================


    //[ '주문 기능 테스트'강. 02:05~ ]

    //< '신규주문 생성'이 잘 작동하는지 >
    @Test
    public void 신규상품_책_주문이_정상적으로_작동되는지() throws Exception {

        //# 전제 given

        //'주문할 테스트코드용 회원 Member 객체'를 생성해줌
        Member member = new Member();
        member.setName("yujnog");
        member.setAddress(new Address("Seoul", "Dobong-gu", "01336"));
        em.persist(member);


        //'주문되는 테스트코드용 상품 책 Book 객체'를 생성해줌
        Book book = new Book(); //여기서는 여러 상품들 중에서 '책 Book 객체'를 신규주문 한다고 가정함
        book.setName("객체지향의 사실과 오해");
        book.setPrice(8000);
        book.setStockQuantity(10); //'책 Book 객체의 현재 재고'를 10개로 임의로 잡음
        em.persist(book);



        //# 조건 when
        //바로 위에서 작성한 '테스트코드용 회원 Member객체의 속성'과 '테스트코드용 책 Book 객체의 속성'을 불러옴
        //'서비스 OrderService의 메소드 order의 리턴타입'으로 단순 식별 목적으로 '자료형 Long'을 해줬기 때문에,
        //여기서 '변수 testOrderId'를 만들어서 그것에 넣어줄 때 '변수 testOrderId의 자료형을 Long 타입'으로 해주는 것이 가능하다!
        Long testOrderId = orderService.order(member.getId(), book.getId(), 2); //테스트코드용으로 2권 주문하기로 함



        //# 검증 then
        Order getOrder = orderRepository.findOne(testOrderId);
        //- '레펏 OrderRepository의 메소드 findOne'은 현재 'DB에 있는 데이터를 가져오는 기능!'
        //- '레펏 OrderRepsoitory의 메소드 findOne의 매개변수 orderId'는 그냥 그 메소드에서만 통용되는 매개변수에 불과하고,
        //  중요한 것은, '레펏 OrderRepsitory의 메소드 findOne을 호출할 때는',
        //  반드시 '그 매개변수로 매개변수 orderId의 자료형인 Long 타입'을 넣어주어야 하는 것이다!
        //- 여기서 '메소드 findOne을 호출할 때 넣는 매개변수'는 당연히 '여기 테스트코드의 '메소드
        //  신규상품_책_주문이_정상적으로_작동되는지()'에서 정의된 변수'만 가능하다!


        assertEquals("상품주문 정상 완료되면 그 상태는 ORDER여야 함", OrderStatus.ORDER, getOrder.getStatus());
        //- '(String message, Object expected, Object actual)' 형식을 따라감.
        //- 'Object expected': 상품주문이 정상 완료되면, 그 때 출력 예상되는 결괏값
        //- 'Object actual': 상품주문이 정상 완료되면, 그 대 출력되는 실제값(실제 애플리케이션의 코드를 가져와야 함)





//=============================================================================================================

    
    //< '주문 취소'가 잘 작동하는지 테스트 >
    @Test
    public void 주문취소가_정상적으로_작동되는지() throws Exception{




    }


//=============================================================================================================


    //< '재고수량 초과'
    @Test
    public void 상품주문_재고수량초과() throws Exception{




    }



//=============================================================================================================


}