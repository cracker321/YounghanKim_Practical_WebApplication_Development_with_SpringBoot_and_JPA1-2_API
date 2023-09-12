package jpabook.jpashop.api;


import jpabook.jpashop.domain.*;
import jpabook.jpashop.domain.Item.Album;
import jpabook.jpashop.domain.Item.Book;
import jpabook.jpashop.domain.Item.Item;
import jpabook.jpashop.repository.OrderRepository;
import jpabook.jpashop.repository.OrderSearch;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;


//@AllArgsConstructor
//[ Controller 위에 @AllArgsConstructor 를 붙이는 것에 관하여 ]


//1. [ 스프링이 아닌 Java 자체만을 사용하는 경우 ]. 우연히 어찌되었든 스프링 JPA에서 Entity 객체를 사용하는 경우에도 아래 내용이 적용됨.
//(근데 사실, 스프링이 아닌 Java 자체만을 사용하는 경우인데, @AllArgsConstructor와 같은 어노테이션을 붙인다는 게 말이 안 되긴 하지만,
// 어거지로 어찌됐든 설명하자면 아래와 같다는 말임!)
//- 사용자 정의 생성자가 없는 경우에는 '기본 생성자'가 기본적으로 숨겨져 있지만,
//  이렇게 @AllArgsConstructor를 사용하여 '모든 매개변수를 포함하는 생성자'를 만드는 경우나
//  @ReuqiredArgsConstructor를 사용하여 final 또는 @NonNull이 붙은 필드를 선별하여(=Requirerd)
//  그 필요로 하게 되는 필드만(=Required)을 매개변수로 하는 생성자를 만들어주는 경우,
//  모든 매개변수를 포함하는 생성자나 final 또는 @NonNull이 붙은 필드를 매개변수로 갖는 생성자나 어찌되었든 '사용자 정의 생성자'이고,
//  그에 따라 아무 매개변수도 들어가 있지 않은 '기본 생성자'를 직접 'public BoardController(){}' 이렇게 생성하든가,
//  아니면, @NoArgsConstructor를 어노테이션 붙여줘야 한다!


//*****매우매우 중요*****
//2. [ 스프링을 사용하는 경우 ]

//< 1. 스프링 컨테이너가 관리해주는 Bean 객체인 경우 >
//- 스프링 컨테이너가 관리해주는 Bean 객체(Controller, Service, Repository, Compenent, Configuration, Interceptor,
//  Filter, Aspect, TaskExecutor, Listner 클래스 등)의 클래스에서는
//  그 클래스 위에 @AllArgsConstructor를 붙여서 그 클래스 내부의 모든 필드를 매개변수로 갖는 생성자를 만들어주거나,
//  @RequqiredArgsConstructor를 붙여서 그 클래스 내부에 있는 fianl 또는 @NonNull이 붙은 필드를 매개변수를 갖는 생성자를 만들어줘도,
//  이 Bean 객체들은 스프링 컨테이너가 자동으로 관리해주므로, '별도의 기본 생성자를 따로 작성해주거나 @NoArgsConstructor를 붙이지 않아도'
//  잘 작동한다!


//< 2. JPA에서의 Entity 클래스인 경우 >
//- 그러나, 'JPA를 사용했을 때의 Entity 클래스'의 경우는 @AllArgsConstuctor 또는 @RequiredArgsConstructor를 Entity 클래스 위에
//  붙일 경우, 반드시 public 또는 protected를 사용한 '기본 생성자 직접 작성 또는 @NoArgsConstructor 붙이기'가 필요하다!!
//  엔티티 클래스의 기본 생성자를 작성할 때 private을 붙이면 안된다!!
//- 왜냐하면, JPA에서 엔티티 클래스를 사용하는 경우에는, JPA 구현체(Hibernate 등)가 엔티티 객체를 생성할 때,
//  '리플렉션'을 사용하기 때문에, 반드시 그 엔티티 클래스 내부에서 '매개변수가 없는 기본 생성자'를 작성해줘야 한다!
//  그리고, 그 기본 생성자의 접근제어자는 반드시 public 또는 protected로 선언되어야 하고,
//  private으로 선언되면 안된다!
//- JPA에서의 Java 리플렉션은, JPA 구현체(Hibernate 등)의 실행 시점에 클래스의 메타데이터 정보를 얻거나, 수정하거나, 메소드와 필드에
//  접근할 수 있도록 해주는 Java API임.
//  JPA가 엔티티 객체를 생성할 때도 이 Java 리플렉션을 사용하는데, 이 때 인자(매개변수)가 없는 기본 생성자가 필요함.
//  왜냐하면, Java 리플렉션으로 객체를 생성할 때 Calss.newInstance() 메소드 또는 Constructor,newInstance() 메소드 등을 사용하느넫,
//  이들 메소드는 모두 인자(매개변수)가 없는 기본 생성자를 호출하기 때문임.
//  만약 해당 클래스에 기본 생성자가 없거나, 있더라도 그 기본 생성자가 private으로 선언되어 있는 상태라면,
//  위 메소드들은 IllegalAccessException을 발생시켜 객체 생성에 실패하게 됨.
//  따라서, JPA를 사용할 때는 엔티티 클래스 내부에 반드시 public 또는 protected로 선언된 (인자(매개변수)가 없는)기본 생성자가
//  반드시 있어야 한다!
//  그렇기에, 이 때는 기본 생성자를 직접 작성해주거나(e.g: public Board(){} 또는 protected Board(){}),
//  @NoArgsConstructor를 그 엔티티 클래스 위에 붙이면 된다!!
//  - JPA에서 Entity 클래스 작성 시 반드시 지켜야 하는 규칙
//    (1) 기본 생성자 필요
//    (2) @Entity 어노테이션
//    (3) ID 어노테이션: 엔티티의 기본키(primary key)를 나타냄. 각 엔티티 객체를 유일하게 식별하기 위해 필요함.


//- @RequiredArgsConstructor는 final과 @NonNull만 붙은 필드를 선별하여(=Requirerd) 그 필요로 하게 되는 필드만(=Required)을
//  매개변수로 하는 생성자를 만들어주는 것이므로,
//  절대 @RequiredArgsConstructor가 '기본 생성자를 작성해주거나 @NoArgsConstructor를 포함하고 있지 않다'!!!


//*****중요*****
//cf) - 컨트롤러 클래스에는 일반적으로 @AllArgsConstructor 나 @NoArgsConstructor를 사용하지 않음.
//      왜냐하면, 컨트롤러 클래스는 웹 요청을 처리하는 역할만 수행하해야 하고, 따라서 컨트롤러가 여러가지 다른 Serivce, Repository를
//      컨트롤러 클래스 내부에 private final MemberService memberSerive와 같이 의존성 주입받는 경우는 1개 또는 소수의 서비스만을
//      필요로 하는 것이 대부분이다.
//    - 즉, 예를 들어, MemberController는 MemberService만 private final MemberService memberService를 통해
//      의존성주입 받는 것이 일반적이고,
//      만약에 MemberController가 MemberService, OrderService, ReviewService, MailService 등 4개의 Service를
//      동시에 의존하고 있다면, 이 MemberController는 회원, 주문, 리뷰, 메일 기능까지 처리하고 있다는 것을 의미하는 것이고,
//      이는 결코 바람직하지 않으며, 이럴 경우 각각의 위 4개의 기능에 대해 각각의 별도의 개별 컨트롤러를 만드는 것이 일반적이다!
//    - 따라서, @AllArgsConstructor를 사용하여 굳이 컨트롤러 내부의 모~든 필드 값을 매개변수로 받는 생성자로 만들어 줄 필요 없이,
//      RequiredArgsConstructor를 사용하여 final이나 @NonNull이 붙은 필드만을 사용하는 생성자를 만들어주는 것이 더 바람직하다!
//      왜냐하면, @AllArgsConstructor를 사용하여 컨트롤러 내부의 모~든 필드를 매개변수로 받는 생성자를 자동으로 만들어버리면,
//      '실제로 필요하지 않는 의존성까지 주입받게 될 수 있기 때문'에, 이렇게 하는 것보다
//      final 또는 @NonNull이 붙어있는 필드만을 선택적으로 골라서 그 필드만을 매개변수로 갖는 생성자를 만들어주는 것이 더 현명하다!
//      뭐 굳이 따지자면, 어쨌든 @AllArgsConstructor를 사용하면 모든 필드를 갖는 생성자를 만들어주기 때문에,
//      당연히 final과 @NonNull이 붙은 필드를 매개변수로 포함하는 생성자를 만들어주고,
//      이에 따라, 당연히 final과 @NonNull이 붙은 필드를 생성자 주입 받게 되는 것도 맞음. 그런데 이러지 말자는 거지!
//      왜냐하면, 스프링에서는 일반적으로 필요한 의존성만 주입받도록 설계하는 것이 바람직하다고 권장하기 때문에!!


//@NoArgsConstructor: 매개변수가 없는 기본 생성자를 만들어줌.
//@RequiredArgsConstructor: //@RequiredArgsConsturctor 어노테이션을 사용하면,
//final로 선언된 필드나 @NonNull 어노테이션을 사용한 필드만을 필요로 하는 생성자를 만들어준다!

//cf) '회원 엔티티 Member', '배송 엔티티 Delivery'와 같은 엔티티 객체들은 컨트롤러, 서비스, 레펏 등 여기저기에 다 걸쳐가며
//    왔다갔다하며 사용되는 것이고, 따라서 이 엔티티 객체들은 의존성 주입의 대상이 아님!
//    의존성 주입의 대상은 대부분 Service, Repository 이다!!
@RequiredArgsConstructor
@RestController
public class OrderApiController {

    private final OrderRepository orderRepository;


    //======================================================================================================

    //[ '주문 조회 V1: 엔티티 직접 노출'강. 00:00~ ]. 실전! 스프링 부트와 JPA 활용2 - API 개발과 성능 최적화. pdf p21~

    //< '컬렉션(리스트, 집합, 맵..)'에서 '1:N 관계를 조회'하고, 이를 최적화하는 방법을 알아본다 >. pdf p21~

    //- '컨트롤러 OrderSampleApiController'에서는 'N:1', '1:1' 관계만 다뤘고,
    //  여기서는 이제 '컬렉션(리스트, 집합, 맵..)'에서 '1:N' 관계를 조회하고, 이를 최적화하는 방법을 알아본다.

    //< v1: 아래처럼 '엔티티를 직접 노출'해서 클라이언트에게 반환하면 절대 네버 안된다! >
    //- 그냥 이건 강의에서 설명해 주는 거니깐, 그냥 아래를 그런 차원에서 강의에서 설명해준 것임.
    //- Hibernate5Module 을 통한 강제 LAZY 초기화


    @GetMapping("/api/v1/orders")
    public List<Order> ordersV1(){

        //'검색조건(=orderSerach)' '없이(=new OrderSearch()에 파라미터 넣지 않았기 때문)',
        //db로부터 '모든 주문 데이터 중에서 'OrderSearch 객체의 내부 필드(속성)에 있는 모든 정보'를 다 가져오는 것.
        List<Order> all = orderRepository.findAllByString(new OrderSearch());

        //단축키: iter 누르고 + tab 누르면 아래 forEach문인 'for (Order order : all){ }'를 자동으로 생성해줌.
        for (Order order : all) { //'여기서 변수 order'는 '이 forEach 문 내부'에서만 사용되는 자체적 변수임.

            order.getMember().getName(); //LAZY 강제 초기화
            order.getDelivery().getAddress(); //LAZY 강제 초기화


            List<OrderItem> orderItems = order.getOrderItems(); //'전체 주문상품 OrderItem 객체 리스트 목록'을 가져와서
            orderItems.stream().forEach(o -> o.getItem().getName()); //'그 주문상품 OrderItem 객체들의 '이름 name'만 추출함.
        }

        return all;
    }


    //======================================================================================================


    //[ '주문 조회 V2: 엔티티를 DTO로 변환'강. 00:00~ ]. 실전! 스프링 부트와 JPA 활용2 - API 개발과 성능 최적화. pdf p23~

    //- v2는 컬렉션(리스트, 셋, 맵..)을 사용하고 있고, 이에 따라 지연로딩으로 인한 쿼리가 아주 많이 실행되고 있음.(pdf p24 참조)
    //  따라서, 이 부분 성능이 많이 떨어지므로, 최적화가 필요함.
    //  v3의 페치 조인으로 이 성능 이슈를 해결할 수 있음.


    @GetMapping("/api/v2/orders")
    public List<OrderDto> ordersV2(){


        List<Order> orders = orderRepository.findAllByString(new OrderSearch());

        List<OrderDto> orderDtos =  orders.stream()
                .map(o -> new OrderDto(o))
                .collect(Collectors.toList());

        return orderDtos;

    }


    //======================================================================================================


    //[ '주문 조회 V3: 엔티티를 DTO로 변환 - 페치 조인 최적화'강. 00:00~ ]. 실전! 스프링 부트와 JPA 활용2 - API 개발과 성능 최적화. pdf p25~

    //'Order 객체 : OrderItems 객체 = 1: N' 관계로 인해 JPA의 distinct를 사용해서 '주문 Order 엔티티 객체의 중복을 제거'해주는 것임.
    //- 단 v3에서는 '컬렉션', '일대다 관계'일 때 '페이징 처리'를 할 수 없다는 치명적 단점이 있다.

    @GetMapping("/api/v3/orders")
    public List<OrderDto> ordersV3(){

        List<Order> orders = orderRepository.findAllWithItem();

        List<OrderDto> orderDtos = orders.stream()
                .map(o -> new OrderDto(o))
                .collect(Collectors.toList());

        return orderDtos;
    }

    //======================================================================================================


    //[ '주문 조회 V3.1: 엔티티를 DTO로 변환 - 페이징과 한계 돌파'강. 00:00~ ]. 실전! 스프링 부트와 JPA 활용2 - API 개발과 성능 최적화.
    // pdf p26~

    //- 'yml 설정 파일'에 'default_batch_fetch_size: 1000'를 입력해줘야 한다!
    //  컬렉션(리스트, 집합, 맵, ..), 일대다 관계일 때 페이징 최적화 방법임.

    @GetMapping("/api/v3.1/orders")
    public List<OrderDto> ordersV3_paging(
            @RequestParam(value = "offset", defaultValue = "0") int offset,
            @RequestParam(value = "offset", defaultValue = "100") int limit){


        List<Order> orders = orderRepository.findAllWithMemberDelivery(offset, limit);

        List<OrderDto> orderDtos = orders.stream()
                .map(o -> new OrderDto(o))
                .collect(Collectors.toList());

        return orderDtos;

    }

    //======================================================================================================



    //======================================================================================================




    //< 주문 엔티티 Order 객체의 DTO 생성 >


    //- DTO의 필드에는 일단 이 프로젝트 안에 존재하는 '모든 엔티티의 모든 필드들'을 다 그냥 넣을 수 있음.
    //  중요한 건, 이 DTO를 사용하는 컨트롤러에서, 그 '어떤 엔티티'를 그때그때 넣는지가 중요한 거임.
    //  저 위에서는 계속 '주문 엔티티 Order 객체'를 그 파라미터로 넣어서 아래 DTO 중, '주문 Order 객체의 필드들'에 해당되는 부분만
    //  쏙 꺼내서 활용하고 있는 것임.
    @Data
    static class OrderDto{

        //클라이언트에게 JSON으로 데이터 보낼 때, 클라이언트가 '나는 다른 거 필요 없고 딱 아래 필드(속성) 관련 정보만 필요해!'라고 하니깐,
        //아래 필드(속성) 정보만 여기에 이렇게 작성한 것임.

        //아래 필드는, '주문 엔티티 Order 객체'의 내부 필드(속성)들 중 여기에서 필요한 일부를 연계시킨 것임.
        //'여기 OrderDto 클래스'에서의 아래 '필드명'은 '주문 Order 객체의 내부 필드들 필드명'과는
        //무관하게 임의로 내가 작명해도 상관 없음.
        private Long orderId;
        private String name;
        private LocalDateTime orderDate;
        private OrderStatus orderStatus;
        private Address address; //이런 '값 객체(VO. Value Object)'는 어차피 변경될 일도 없어서, 그냥 이렇게 엔티티로 노출해도 됨.
        //private List<OrderItem> orderItems; //이렇게 '주문상품 엔티티 OrderItem 객체'를 그대로 가져오면 JSON에서 null로 조회됨.
                                              //즉 문제임. 이건 지연로딩, 프록시 문제인 듯. 이거 강의 QnA에 관련 질의응답 있음.
                                              //이렇게 엔티티를 외부에 노출해서 사용하면 안되는 것이다!
        private List<OrderItemDto> orderItems; //대신 이렇게 DTO로 감싸줘야 한다.

        //아래는 그냥 이론적으로 DTO에는(어떤 DTO이든지 다 가능) 아무 유관 무관한 엔티티나 다 넣는 것이 가능하다는 것을 보여주기 위함..
        private Book lksj435dfldkjs;
        private Album alkjsdlwejtl2344;
        private Item lkaj234rlkjqlkfj;
        private Category lk3532jdglkdjgljd;


        public OrderDto(Order order){ //DTO의 생성자
                                      //이 생성자가 있기 때문에, 저~ 위에서 '스트림' 사용할 때
                                      //'map(o -> new SampleOrderDto(o))' 에서 'new SampleOrderDto(o)'에
                                      //'o'가 들어갈 수 있는 것이다! 당연함..
            orderId = order.getId();
            name = order.getMember().getName();
            orderDate = order.getOrderDate();
            orderStatus = order.getStatus();
            address = order.getDelivery().getAddress(); //이런 '값 객체(VO. Value Object)'는 어차피 변경될 일도 없어서,
                                                        // 그냥 이렇게 엔티티로 노출해도 됨.
            //orderItems = getOrderItems(); //이렇게 '주문상품 엔티티 OrderItem 객체'를 그대로 가져오면 JSON에서 null로 조회됨.
                                            //즉 문제임. 이건 지연로딩, 프록시 문제인 듯. 이거 강의 QnA에 관련 질의응답 있음.
                                            //이렇게 엔티티를 외부에 노출해서 사용하면 안되는 것이다!
            orderItems = order.getOrderItems().stream() //대신 이렇게 DTO로 감싸줘야 한다.
                    .map(orderItem -> new OrderItemDto(orderItem))
                    .collect(Collectors.toList());

            //cf) 생성자 내부에서 'this 키워드' 없이 'static class SampleOrderDto'의 필드들을 초기화 할 수 있는 이유
            //- '생성자 내부의 필드들의 이름(orderId, name, orderDate, address)'과 '생성자의 매개변수의 이름(Order order)'이
            //  '같지 않기 때문'에,
            //  (즉, '내부 필드의 이름 orderId'와 '변수 order.getId()' '문자 그대로 다른 문자'이기 때문에,
            //   자바는  '변수 order.getId()'가 이제 필드에 대입됨을 명확하게 인식하게 값을 할당할 수 있음.
            //  따라서, 굳이 this를 사용할 필요가 없음. 물론 당연히 this를 사용해도 아무 상관 없음.
            //- 위 코드에서 SampleOrderDto 클래스의 필드와 생성자의 매개변수를 비교하면 다음과 같습니다:
            //  orderId 필드와 order 매개변수의 이름이 다릅니다.
            //  name 필드와 order 매개변수의 이름이 다릅니다.
            //  orderDate 필드와 order 매개변수의 이름이 다릅니다.
            //  address 필드와 order 매개변수의 이름이 다릅니다.

        }

    }


    //======================================================================================================


    //< 주문상품 엔티티 OrderItem 객체의 DTO 생성 >

    @Data
    static class OrderItemDto{


        //클라이언트에게 JSON으로 데이터 보낼 때, 클라이언트가 '나는 다른 거 필요 없고 딱 아래 필드(속성) 관련 정보만 필요해!'라고 하니깐,
        //아래 필드(속성) 정보만 여기에 이렇게 작성한 것임.
        //아래 필드는, '주문상품 엔티티 OrderItem 객체'의 내부 필드(속성)들 중 여기에서 필요한 일부를 연계시킨 것임.
        //'여기 OrderItemDto 클래스'에서의 아래 '필드명'은 '주문상품 OrderItem 객체의 내부 필드들 필드명'과는
        //무관하게 임의로 내가 작명해도 상관 없음.
        private String itemNameee;
        private int orderPriceee;
        private int counttt;

        public OrderItemDto(OrderItem orderItem){ //DTO의 생성자

            itemNameee = orderItem.getItem().getName();
            orderPriceee = orderItem.getTotalPrice();
            counttt = orderItem.getCount();
        }
    }

    //======================================================================================================


    //======================================================================================================


    //======================================================================================================



}
