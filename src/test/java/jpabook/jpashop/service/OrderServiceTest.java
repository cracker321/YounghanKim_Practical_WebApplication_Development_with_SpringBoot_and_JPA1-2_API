package jpabook.jpashop.service;

import jpabook.jpashop.domain.Address;
import jpabook.jpashop.domain.Item.Book;
import jpabook.jpashop.domain.Item.Item;
import jpabook.jpashop.domain.Member;
import jpabook.jpashop.domain.Order;
import jpabook.jpashop.domain.OrderStatus;
import jpabook.jpashop.exception.NotEnoughStockException;
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
        //테스트케이스에서 현재 메소드 내부에서 'ctrl + shift + T' 누르면,
        //'현재 메소드와 연관되어 있는 메인 애플리케이션의 해당 부분'으로 이동이 가능함

        //# 전제 given

        //'주문할 테스트코드용 회원 Member 객체'를 생성해줌
        Member member = new Member();
        member.setName("yujong"); //'자료형 String 타입'
        member.setAddress(new Address("Seoul", "Dobong-gu", "01336"));
        //'클래스 Member의 필드 Address(private Address address)'는,
        //'자료형 Address '객체타입''이기에, 당연히 'Address 객체'를 사용할 때는 당연히 '새로운 객체 생성하는 객체 타입'으로만
        //사용하는 것이 가능한 것임.
        em.persist(member);


        //'주문되는 테스트코드용 상품 책 Book 객체'를 생성해줌
        Book book = new Book(); //여기서는 여러 상품들 중에서 '책 Book 객체'를 신규주문 한다고 가정함
        book.setName("객체지향의 사실과 오해");
        book.setPrice(8000);
        book.setStockQuantity(10); //'책 Book 객체의 현재 재고'를 10개로 임의로 잡음
        em.persist(book);


        //# 조건 when
        //테스트코드용으로 책 2권 주문하기로 함
        //바로 위에서 작성한 '테스트코드용 회원 Member객체의 속성'과 '테스트코드용 책 Book 객체의 속성'을 불러옴.
        //'서비스 OrderService의 메소드 order의 리턴타입'으로 단순 식별 목적으로 '자료형 Long'을 해줬기 때문에,
        //여기서 '변수 testOrderId'를 만들어서 그것에 넣어줄 때 '변수 testOrderId의 자료형을 Long 타입'으로 해주는 것이 가능하다!
        Long testOrderId = orderService.order(member.getId(), book.getId(), 2);


        //# 검증 then
        Order getOrder = orderRepository.findOne(testOrderId);
        //- '레펏 OrderRepository의 메소드 findOne'은 현재 'DB에 있는 데이터를 가져오는 기능!'
        //- '레펏 OrderRepsoitory의 메소드 findOne의 매개변수 orderId'는 그냥 그 메소드에서만 통용되는 매개변수에 불과하고,
        //  중요한 것은, '레펏 OrderRepsitory의 메소드 findOne을 호출할 때는',
        //  반드시 '그 매개변수로 매개변수 orderId의 자료형인 Long 타입'을 넣어주어야 하는 것이다!
        //- 여기서 '메소드 findOne을 호출할 때 넣는 매개변수'는 당연히 '여기 테스트코드의 '메소드
        //  신규상품_책_주문이_정상적으로_작동되는지()'에서 정의된 변수'만 가능하다!


        //검증내용 (1)
        assertEquals("상품주문이 정상 완료되면, 그 상태는 ORDER여야 함", OrderStatus.ORDER, getOrder.getStatus());
        //- '(String message, Object expected, Object actual)' 형식을 따라감.
        //- 'Object expected': 상품주문이 정상 완료되면, 그 때 실제 애플리케이션에서 출력되는 결괏값(실제 애플리케이션의 코드를 가져와야 함)
        //- 'Object actual': 상품주문이 정상 완료되면, 그 때 테스트코드에서 출력되는 실제값(테스트코드를 가져와야 함)


        //검증내용 (2)
        assertEquals("상품주문이 정상 완료되면, 주문한 상품의 '종류'와 '수량이 정확해야 함", 2,
                getOrder.getOrderItems().size());
        //테스트코드에서 '책 Book 객체 2권'을 주문했으니, 당연히 'expected는 '2''이 되어야 함


        //검증내용 (3)
        assertEquals("상품주문이 정상 완료되면, '주문 가격'은 '가격'*'수량'이 되어야 함", 8000 * 2,
                getOrder.getTotalPrice());
        //테스트코드에서 '가격이 8000원인 책 Book 객체 2권'을 주문했으니,
        //상품주문 코드를 다 정상적으로 작성했다면, 출력되는 실제값도 1600이어야 한다.d


        //검증내용 (4)
        assertEquals("상품주문이 정상 완료되면, '현재 재고'에서 '해당 주문 수량만큼 감소'되어야 함", 8,
                book.getStockQuantity());
        //테스트코드에서 '현재 재고를 10권으로 설정했고', 이후 '신규주문으로 책 2권을 주문했으니', '주문 후 재고는 8권'이 되어야 함.
        //그런데,


    }


//=============================================================================================================


    //[ '주문 기능 테스트'강. 09:10~ ]

    //< 고객이 상품을 신규 주문 하였으나, 현재 재고수량보다 더 많이 주문해서 주문이 안 되는 경우 테스트 >

    @Test(expected = NotEnoughStockException.class) //현재 재고수량보다 많은 주문이 들어온 경우를 테스트해서,
    //테스트코드가 정상적으로 작동한다면 이 예외를 발생시키는 것임.
    public void 신규주문량이_현재재고수량보다_많은_경우() throws Exception {

        //# 전제 given

        //'주문할 테스트코드용 회원 Member 객체'를 생성해줌. '주문 기능 테스트'강. 10:20~
        Member member = createMember(); //- 'Extract Method 기능': 'Member 객체를 새로 만들고, 그 내부에 속성 집어넣어주고
        //   이런 코드들'을 다 드래그로 영역설정해서 'ctrl + alt + M' 누르면,
        //   이제 편리하게 그 코드들을 '하나의 메소드'로 저 ~아래에 별도로 자동으로 만들어주고,
        //   이제 그 메소드를 호출해서 이것처럼 간단히 '테스트코드용 새로운 Member 객체'를 만들 수 있음.


        //'주문되는 테스트코드용 상품 책 Book 객체'를 생성해줌
        Item item = createBook("ORM교재", 4000, 15); //'현재 재고수량'을 15개로 가정, 설정함

        //신규 주문수량을 20개로 가정, 설정함. '신규 주문수량(20개)'이 '현재 재고수량(15개)'보다 많으니,
        int orderCount = 20; //그렇기에, 테스트코드가 정상적으로 작동한다면, 예외가 빵~ 터져야 정상적으로 테스트코드 성공한 것임


        //# 조건 when
        //테스트코드용으로 5권 주문하기로 함
        orderService.order(member.getId(), item.getId(), orderCount); //이렇게 테스트코드 설정해줬으니,
        //신규 주문수량이 현재 재고수량 초과로 예외 터져야
        //정상적으로 테스트코드 성공한 것임

        //# 검증 then
        fail("현재 재고수량보다 초과로 신규 주문수량이 들어왔기 때문에, '재고 부족 예외'가 터져야 합니다..그 예외가 안 터져서" +
                "코드 실행이 여기 라인까지가 내려왔으니, 이건 유종아 너가 테스트코드를 잘못 작성한 것이야.");
    }


//=============================================================================================================


    //< '주문 취소'가 잘 작동하는지 테스트 >. '주문 기능 테스트'강. 14:45~
    @Test
    public void 주문취소가_정상적으로_작동되는지() throws Exception {


        //# 전제 given
        Member member = createMember();
        Item item = createBook("객사오", 12000, 7); //'현재 책 재고수량'은 7개로 가정, 설정함.

        int orderCount = 2; //'신규 주문수량'을 2권으로 가정, 설정함.
        //이제 그럼 '주문 후 재고는 5개'가 되었으나, 아래에서 '주문 취소가 이루어지고, '원상복구된 후 재고'를 다시 테스트하면
        //예상되는 출력값(남은 재고량)은 당연히 원래대로 '7'이 돼야 함

        Long testOrderId = orderService.order(member.getId(), item.getId(), orderCount);
        //여기까지가, 아래 '조건 when'에서 '주문취소'를 테스트하기 위한 기본 준비 세팅임.


        //# 조건 when(실제 내가 테스트하고자 하는 부분)
        orderService.cancelOrder(testOrderId);


        //# 검증 then. '주문 기능 테스트'강. 16:20~
        //'주문 취소가 되었으니', '기존재고에서 해당 취소된 수량이 더 들어오니' '재고가 원상대로 복귀하는지 여부를 검증'함.
        //'취소를 적용해야 할 주문 Order'를 '레펏 OrderRepository'를 통해 'DB로부터 가져와서 변수 getOrder에 저장시킴'.
        Order getOrder = orderRepository.findOne(testOrderId);


        //검증내용 (1)
        assertEquals("주문취소가 정상 완료되면, 주문상태는 CANCEL이 되어야 함", OrderStatus.CANCEL,
                getOrder.getStatus());

        //검증내용 (2)
        assertEquals("주문취소가 정상 완료되면, 재고가 원상복구 되어야 함", 7, item.getStockQuantity());
        //'item.addStock(orderCount)'가 아니라, 'item.getStockQuantity()'를 호출하는 것이 맞는 것이다!

    }


//=============================================================================================================


    //'주문 기능 테스트'강. 10:20~
    //- 'Extract Method 기능': 'Member 객체를 새로 만들고, 그 내부에 속성 집어넣어주고
    //   이런 코드들'을 다 드래그로 영역설정해서 'ctrl + alt + M' 누르면,
    //   이제 편리하게 그 코드들을 '하나의 메소드'로 별도로 자동으로 만들어주고,
    //   이제 그 메소드를 호출해서 위에서 간단히 '테스트코드용 새로운 Member 객체'를 만들 수 있음.

    private Member createMember() {
        Member member = new Member();
        member.setName("yujong");
        member.setAddress(new Address("Seoul", "Dobong-gu", "01336"));
        //'클래스 Member의 필드 Address(private Address address)'는,
        //'자료형 Address '객체타입''이기에, 당연히 'Address 객체'를 사용할 때는 당연히 '새로운 객체 생성하는 객체 타입'으로만
        //사용하는 것이 가능한 것임.
        em.persist(member);
        return member;
    }


    private Book createBook(String name, int price, int stockQuantity) {
        Book book = new Book();
        book.setName(name); //인자값 위에서 'ctrl + alt + P' 누르면, 그 '인자값 name'을 '해당 메소드의 매개변수로 꺼낼 수 있음'
        book.setPrice(price);
        book.setStockQuantity(stockQuantity);
        em.persist(book);
        return book;
    }


//=============================================================================================================


}