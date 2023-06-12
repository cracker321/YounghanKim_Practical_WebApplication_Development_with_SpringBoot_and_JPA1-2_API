package jpabook.jpashop;


import jpabook.jpashop.domain.*;
import jpabook.jpashop.domain.Item.Book;
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
@Component //이 어노테이션을 설정하면, 서버가 실행될 때 아래 '클래스 InitDb'를 이제 스프링의 컴퍼넌트 스캔의 대상으로 인식하여,
           //이제 '클래스 InitDb'를 'InitDb 객체(빈)'으로 등록함.
public class InitDb {

    private final InitService initService;
    //cf) '회원 엔티티 Member', '배송 엔티티 Delivery' 등은 여기저기에서 다 왔다갔다 쓰이는 것이고,
    //    이런 것은 '의존성 주입의 대상이 아니다!'!!
    //    '엔티티 클래스'는 그냥 그런 거 신경쓰지 않고 아무렇게나 이리저리 갖다 쓰면 되는 것임!

//============================================================================================================


    //[ '조회용 샘플 데이터 입력'강. 00:00~ ]. 실전! 스프링 부트와 JPA 활용2 - API 개발과 성능 최적화


    //-------------------------------------------------------------------------------------------------------


    //< 코드의 순서상 아래 3개의 코드, 즉 3단계로 나눠서 작성한 이유 >

    //(1)public void init()
    //(2)static class InitService
    //(3)public void dbInit1()

    //1.DB 초기화 작업의 순차적 진행
    //- 1)'@PostConstruct 를 통해, 현재 InitDb 객체 생성 후 의존성 주입 된 뒤 호출되는 메소드 init()',
    //  2)'DB 초기화 작업 관련 메소드들을 하나의 클래스로 묶어 모듈화하는 내부 클래스 InitService',
    //  3)'실제 DB 초기화 작업 진행을 위핸 구체적인 실행 작업 메소드 dbInit1()'
    //  이렇게 3단계로 나누어 각각의 역할을 명확하게 구분하여 코드의 가독성을 높이고 유지보수에 유리하게 만듦.

    //2.의존성 주입 및 실행 순서 관리
    //- 1)'최종 클래스 InitDb'에 '@Component'를 붙혀서 그것을 '스프링 객체(빈)'을 등록하고,
    //    '내부 클래스 InitService'의 'EntityManger 필드'에 'final 키워드'와 함께 '의존성 주입'시킴으로써,
    //  2)'@PostConstruct가 붙은 메소드 init()'을 실행 시에, '스프링 객체(빈) 생성'과 '의존성 주입'이 '이미 완료된 상태'에서
    //  3)실제 db작업 초기화 메소드 dbInit1()'이 실행됨.

    //3.DB 초기화 작업의 의도적인 구분
    //- DB의 초기화 과정이 아래처럼 여러 단계로 나뉘어져 있을 경우, 서로 다른 초기화 작업이 중복되지 않도록 하기 위해
    //  별도의 메소드를 만듦.
    //- 이 경우, '최종 클래스 InitDb'와 '내부 클래스 static class InitService'는 각각의 하위 메소드인
    //  '메소드 @PostConstruct init()'과 '메소드 dbInit1'에서 사용되는 '설정(value)'과 '실제 초기화 작업(logic)'을
    //  나누어 모아두어서, 실행순서 및 의존성관리와 코드 가독성을 위해 잘 구분해둠.

    //-------------------------------------------------------------------------------------------------------


    //< @PostConstruct 어노테이션 >

    //# 특징
    //- @PostConstruct가 붙은 메소드는 스프링 컨테이너가
    // '@PostConstruct 가 붙은 메소드가 속한 현재 최종의 @Component가 붙은 빈(객체)(여기서는 '클래스 InitDb')를 생성시키고',
    // '의존성 주입도 완료시키고(여기서는 'private final InitService initService')',
    //  그 후에 바로 @PostConstruct가 붙은 메소드가 호출되어 '객체의  생성 후 초기화를 수행'하는 순서임.
    //  여기까지 수행했으면, 이제 'InitDb 객체'를 '사용할 준비가 완료'된 상태임.
    //- @PostConstruct가 붙은 메소드는
    //  1)반드시 1개의 메소드만 가능
    //  2)파라미터가 없음
    //  3)public 접근 제어자를 가짐
    //  4)void로 선언됨

    //# 주로 사용되는 환경
    //- 'DB연결 초기화', '외부 리소스 초기화', '캐시 초기화' 등'빈(객체)의 초기화 작업을 수행해야 할 때' 사용됨.
    //- 보통 '생성자'로는 처리할 수 없거나, 생성자에서 처리하기 어려운 초기화 작업을 수행함.

    //# '생성자'와의 차이
    //- '@PostConstruct'는 '객체의 초기화를 지정'하는 것에 초점을 맞추고,
    //  '생성자'는 '객체의 생성과 초기 상태 설정'에 초점을 맞춤.
    //- '@PostConstruct'는 '@PostConstruct가 붙은 메소드가 속한 클래스의 객체(빈)가 생성되고', 이후 '의존성 주입까지 완료'된 후에,
    //  그 뒤에 '@PostConstruct가 붙은 메소드가 호출되어', '객체(빈)의 (추가적인)초기화 작업을 수행'하는 용도임
    //  '생성자'는 '외부 클래스에서 new 키워드로 해당 객체를 생성할 때 호출'되고,
    //  '생성자 실행이 완료'되면, '객체가 생성되고 그 객체를 사용할 준비가 완료'됨.
    //- 즉, '@PostConstruct'는 '@PostConstruct가 붙은 메소드가 속한 클래스의 객체가 생성되고', '의존성 주입까지 완료된 후'에
    //  그 이후에 '@PostConstruct'가 실행되는 반면에,
    //  '생성자'는 'new 키워드로 객체가 생성될 때 실행되어 그 객체의 초기 상태 지정 작업'을 수행함.

    @PostConstruct //@PostConstruct 어노테이션으로 인해, '현재 클래스 InitDb 객체'가 생성된 후에, 바로 @PostConstruct가 호출되며,
                   //그에 따라, 바로 그 내부 로직 'initService.dbInit1()'이 실행된다!
    public void init(){
        initService.dbInit1();
    }
    //- 만약, 아래 '내부 클래스 satic class InitService'가 'static'으로 선언되지 않았다면,
    //  여기서, 당연히 아래 'InitService 객체!!'를 호출하고자 할 때,
    //  'InitService initService = new InitService()'를 먼저 선언한 후에, 그 뒤에 'initService.dbInit1();'을 작성해줘야
    //  하는 것이다.

    //-------------------------------------------------------------------------------------------------------

    @RequiredArgsConstructor
    @Transactional //이제 '메소드 InitService'의 실행은, '트랜잭션 내부'에서 이루어짐.
    @Component //'최종 클래스 InitDb'에 '@Component'가 붙어있어서, '클래스 InitDb 자체'가 '스프링 객체(빈)'으로 등록되지만,
               //'내부 클래스 InitService'는 '@Component'를 붙히지 않으면, '내부 클래스'이기에 '스프링 객체(빈)'으로
               //자동 등록되지 않음. 따라서, @Component를 붙혀서 스프링이 '클래스 InitService'를 '객체(빈)'로 등록할 수 있도록 해야 함.
    static class InitService{
        //cf) '@PostConstruct'는 '메소드 단위'위에서만 붙힐 수 있기 때문에, 여기 '클래스 InitService' 위에는 붙힐 수 없다!


        // < 내부클래스 InitService >

        //- *****중요*****
        // '내부 클래스'를 사용한 이유는, 'DB 초기화 작업 관련 메소드들을 하나의 클래스로 묶어 모듈화'하고,
        //  이에 필요한 의존성을 주입받아 사용하기 위함임.

        //- '내부 클래스'로 선언되어 있기 때문에, '최종 클래스 InitDb의 내부'에서만 '접근 가능함'.
        //- '내부 클래스 InitService'는 그 내부에 필드로 'EntityManager'를 필드로 가지고 있어서,
        //  'JPA 엔티티'와 상호작용할 수 있음.
        //- '내부 클래스 InitService'에는 '@Transactional'이 적용되었기 때문에, '트랜잭션 내에서 메소드가 실행됨.'

        //# '정적 static'으로 선언한 이유
        //- '내부 클래스 InitService'는 '클래스 InitDb'의 '내부'에서만 사용되는 클래스이고, '클래스 InitDb의 내부'에서
        //  'InitService 객체'를 생성하여 '클래스 InitService에 접근'하고자 할 때,
        //  어차피 '내부 클래스 InitService'는 '클래스 InitDb의 내부에서만 사용되기 때문'에 '정적 static'으로 선언하여
        //  'InitService 객체의 인스턴스'를 따로 생성하지 않고 '저 위의 public void init(){ ..}'에서
        //  따로 '클참뉴클 InitService initService = new InitService()'라는 코드를 작성하지 않고도,
        //  바로 'InitService 객체에 접근'할 수 있도록 한 것임.
        //- '초기화 작업을 수행하는 구체적인 메소드 dbInit1'을 포함하고 있음.
        //- '엔티티매니저 EntityManager'를 필드로 가지고 있어서


        //- '엔티티매니저 EntityManager'를 사용하여 DB 초기화 작업을 수행함.
        //- '내부 클래스 initService'에 그 필드(속성)으로 'EntityManger em'가 '의존성 주입'되었기 때문에,
        //  이제 'initService 객체'를 통해 '실제 DB에 초기화 작업을 수행하는 것이 가능'하게 됨.
        private final EntityManager em;


        //< 엔티티매니저 EntityManager >

        //- JPA의 핵심 요소로, 엔티티 객체를 DB와 매핑하고 조작할 수 있는 기능을 제공함.
        //  따라서, EntityManager를 통해 직접 객체를 생성하고 DB에 저장할 수 잇음.
        //- 여기서 Repository를 거치지 않고 직접 EntityManager를 사용하는 이유는, 초기화 데이터를 더 효율적으로 처리하기 위함임.
        //  Repository를 사용하면, 많은 객체를 일일이 생성하고 저장하는 과정이 번거롭기에, 이 코드에서는 EntityManager를 사용하여
        //  더 간단하게 DB를 초기화함.

//============================================================================================================

        //< 실제 'DB 초기화'작업을 위한 구체적인 실행 작업 메소드 dbInit1 >
        public void dbInit1(){

            //-------------------------------------------------------------------------------------------------------


            //샘플데이터 '빈 신규 회원 객체 생성' 후,
            Member member = new Member();

            //아래의 '회원 Member 객체의 일부 속성(필드)들'을 넣어 '새로운 신규 회원 객체' 생성함.
            member.setName("userA");
            member.setAddress(new Address("서울", "1", "1111"));
            //서버 db에 영속화시켜 저장함.
            em.persist(member);

            //-------------------------------------------------------------------------------------------------------

            //샘플데이터 '빈 신규 상품(책 객체가 상속하는) 객체 생성' 후,
            Book book1 = new Book();

            //아래의 '상품 Item(상품 Book 객체가 상속하는) 객체의 일부 속성(필드)들'을 넣어 '새로운 신규 책 객체' 생성함.
            book1.setName("JPA1 BOOK");
            book1.setPrice(10000);
            book1.setStockQuantity(100);
            em.persist(book1);

            //-------------------------------------------------------------------------------------------------------

            //샘플데이터 '빈 신규 상품(책 객체가 상속하는) 객체 생성' 후,
            Book book2 = new Book();

            //아래의 '상품 Item(상품 Book 객체가 상속하는) 객체의 일부 속성(필드)들'을 넣어 '새로운 신규 책 객체' 생성함.
            book2.setName("JPA2 BOOK");
            book2.setPrice(20000);
            book2.setStockQuantity(100);
            em.persist(book2);

            //-------------------------------------------------------------------------------------------------------

            //< 새로운 주문상품 OrderItem 객체'를 2개 생성함 >

            //- '클래스 OrderItem'의 '팩토리메소드 createOrderItem'은 '정적 static 메소드'로 내가 만들었기 때문에,
            //  여기서 '새로운 OrderItem 객체를 생성할 필요 없이(= 클참뉴클 할 필요 없이)' 바로 '호출 가능'함.
            //(당연히, 원래는 어떤 클래스의 외부에서 그 클래스의 객체를 생성하려면, 당연히 먼저 그 클래스의 클참뉴클을 해줘야 하는 것은 맞음.
            //이 아래에서 '새로운 Delivery 객체를 생성'하는 경우처럼.
            //당연히, '배송 엔티티 Delivery 클래스'에서는 '정적 static 제어자인 팩토리 클래스를 사용하지 않았기 때문'에 이렇게
            //클참뉴클 Delivery delivery = new delivery();  해줘서 먼저 '새로운 배송 Delivery 객체'를 생성해줘야 함.
            //- '주문상품 엔티티 OrderItem의 여러 필드(속성)들 중에서', '일부 필드인 주문상품의 이름, 가격, 수량 속성만을 매개변수로 갖는
            //   팩토리메소드 createOrderItem'을 호출함으로써, '주문상품의 이름, 가격, 수량'만의 속성을 가진
            //  '새로운 주문상품 OrderItem 객체'를 '바로 여기서 객체 생성'할 수 있다!
            OrderItem orderItem1 = OrderItem.createOrderItem(book1, 10000, 1);
            OrderItem orderItem2 = OrderItem.createOrderItem(book2, 20000, 2);

            //-------------------------------------------------------------------------------------------------------

            //샘플데이터 '빈 신규 배송 객체 생성' 후,(= 배송 정보를 입력하기 위해 '신규 배송 객체 Delivery'를 생성함)
            Delivery delivery = new Delivery();

            //아래에 '배송 Delivery 객체의 일부 속성(필드)들'을 넣어 '새로운 신규 회원 객체' 생성함.
            delivery.setAddress(member.getAddress());

            //-------------------------------------------------------------------------------------------------------

            //< 새로운 주문 Order 객체'를 1개 생성함 >
            Order order = Order.createOrder(member, delivery, orderItem1, orderItem2);
            em.persist(order);
            //이제, 여기 '클래스 InitDb'에서 생성한 '모든 샘플 데이터가 다 담기 새로운 주문 Order 객체'가
            //DB에 영속화되어 저장된 것임.

            //-------------------------------------------------------------------------------------------------------





//==========================================================================================================

            //[ '조회용 샘플 데이터 입력'강. 09:10~ ]. 실전! 스프링 부트와 JPA 활용2 - API 개발과 성능 최적화

            //신규회원1 객체, 신규회원2 객체, Book1 객체, Book2 객체 ... 이렇게 위에서 일일이 하나씩 타이핑해서 하면
            //겉보기에 코드가 많이지고 좀 지저분해지니,
            //그것을 묶어서 재사용할 수 있도록 코드 수정하는 부분에 대한 강의임.

//==========================================================================================================

        }

    }

}

