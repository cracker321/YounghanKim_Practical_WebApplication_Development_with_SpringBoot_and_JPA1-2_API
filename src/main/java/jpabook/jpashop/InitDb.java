package jpabook.jpashop;


import jpabook.jpashop.domain.Address;
import jpabook.jpashop.domain.Item.Book;
import jpabook.jpashop.domain.Member;
import jpabook.jpashop.domain.OrderItem;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import javax.persistence.EntityManager;

/**
 * < 그냥 강의에서 예시용 아래 데이터를 db에 넣는 과정임 >
 * - 총 주문 2개
 *
 * - 주문 1
 * userA
 * JPA1 BOOK
 * JPA2 BOOK
 *
 * - 주문 2
 * userB
 * SPRING1 BOOK
 * SPRING2 BOOK
 */
@RequiredArgsConstructor
@Component //이 어노테이션을 설정하면, 이제 스프링의 컴퍼넌트 스캔의 대상이 됨.
public class InitDb {

    private final InitService initService;

    //[ '조회용 샘플 데이터 입력'강. 00:00~ ]. 실전! 스프링 부트와 JPA 활용2 - API 개발과 성능 최적화

    //-------------------------------------------------------------------------------------------------------
    @PostConstruct
    public void init(){
        initService.dbInit1();
    }

    //-------------------------------------------------------------------------------------------------------

    @RequiredArgsConstructor
    @Transactional
    @Component
    static class InitService{

        private final EntityManager em;

        public void dbInit1(){

            Member member = new Member();
            member.setName("userA");
            member.setAddress(new Address("서울", "1", "1111"));
            em.persist(member);
            
            
            Book book1 = new Book();
            book1.setName("JPA1 BOOK");
            book1.setPrice(10000);
            book1.setStockQuantity(100);
            em.persist(book1);


            Book book2 = new Book();
            book2.setName("JPA2 BOOK");
            book2.setPrice(20000);
            book2.setStockQuantity(100);
            em.persist(book2);


            //- '클래스 OrderItem'의 '팩토리메소드 createOrderItem'은 '정적 static 메소드'로 내가 만들었기 때문에,
            //  여기서 '새로운 OrderItem 객체를 생성할 필요 없이(= 클참뉴클 할 필요 없이)' 바로 '호출 가능'함.
            //- '주문상품 엔티티 OrderItem의 여러 필드(속성)들 중에서', '일부 필드인 주문상품의 이름, 가격, 수량 속성만을 매개변수로 갖는
            //   팩토리메소드 createOrderItem'을 호출함으로써, '주문상품의 이름, 가격, 수량'만의 속성을 가진
            //  '새로운 주문상품 OrderItem 객체'를 '바로 여기서 객체 생성'할 수 있다!
            OrderItem orderItem1 = OrderItem.createOrderItem(book1, 10000, 1);
            OrderItem orderItem2 = OrderItem.createOrderItem(book2, 20000, 2);


            Order.create

    //-------------------------------------------------------------------------------------------------------




            
        }


    }


}

