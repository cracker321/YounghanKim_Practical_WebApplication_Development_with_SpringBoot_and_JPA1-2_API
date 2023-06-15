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
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;



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

    @GetMapping("/api/v3/orders")
    public List<OrderDto> ordersV3(){

        List<Order> orders = orderRepository.findAllWithItem();

        List<OrderDto> orderDtos = orders.stream()
                .map(o -> new OrderDto(o))
                .collect(Collectors.toList());

        return orderDtos;
    }




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
