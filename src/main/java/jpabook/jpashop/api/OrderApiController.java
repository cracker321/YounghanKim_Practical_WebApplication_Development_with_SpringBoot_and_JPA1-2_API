package jpabook.jpashop.api;


import jpabook.jpashop.domain.Order;
import jpabook.jpashop.domain.OrderItem;
import jpabook.jpashop.repository.OrderRepository;
import jpabook.jpashop.repository.OrderSearch;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


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





}
