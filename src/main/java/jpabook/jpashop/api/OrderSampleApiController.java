package jpabook.jpashop.api;


import jpabook.jpashop.domain.Address;
import jpabook.jpashop.domain.Order;
import jpabook.jpashop.domain.OrderStatus;
import jpabook.jpashop.repository.OrderRepository;
import jpabook.jpashop.repository.OrderSearch;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.persistence.EntityManager;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * '다대일 @ManyToOne, 일대일 @OneToOne' 관계에서의 '성능 최적화'를 다루는 강의
 * Order
 * Order -> Many
 * Order -> Delivery
 */


@RequiredArgsConstructor //private fianl 할 때 같이 반드시 인 듯.
@RestController
public class OrderSampleApiController {

    //======================================================================================================

    //[ '간단한 주문 조회 V1: 엔티티를 직접 노출'강. 00:00~ ]. 실전! 스프링 부트와 JPA 활용2 - API 개발과 성능 최적화

    private final OrderRepository orderRepository;

    @GetMapping("/api/v1/sample-orders")
    public List<Order> orderV1(){

        List<Order> all = orderRepository.findAllByString(new OrderSearch());

        return all;
    }

    //======================================================================================================


    //[ '간단한 주문 조회 V2: 엔티티를 DTO로 변환'강. 00:00~ ]. 실전! 스프링 부트와 JPA 활용2 - API 개발과 성능 최적화

    //< N+1 문제를 해결하지 못한 JPQL 쿼리문을 담고 있는 '레펏 OrderRepository의 메소드 findAllByString'
    @GetMapping("api/v2/sample-orders")
    public List<SampleOrderDto> odersV2(){

        List<Order> orders = orderRepository.findAllByString(new OrderSearch());

        List<SampleOrderDto> sampleOrderDtos = orders.stream()
                .map(o -> new SampleOrderDto(o)) //- '.map(SampleOrderDto::new'와 동일하다! 'Lambda 레퍼런스'를 사용한 것임.
                                                 //- 'DB로부터 가져온 Order 객체'를 'SampleOrderDto 타입'으로 변형시키는 것임.
                .collect(Collectors.toList()); //'리스트 List'로 변환시키는 것임.

        return sampleOrderDtos;
    }


    //======================================================================================================


    //[ '간단한 주문 조회 V3: 엔티티를 DTO로 변환 - 페치 조인 최적화'강. 00:00~ ]. 실전! 스프링 부트와 JPA 활용2 - API 개발과 성능 최적화

    //v2와 v3는 똑같은 로직이지만, v3는 그 내부 실행 쿼리가 N+1 문제를 해결한 쿼리이다ㅣ!

    //< N+1 문제를 '해결한' JPQL 쿼리문을 담고 있는 '레펏 OrderRepository의 메소드 findAllWithMemberDelivery'

    //< '레펏 orderRepository의 메소드 findAllWithMemberDelivery'의 'join fetch' >
    //*****중요중요!!*** 아주아주 자주 사용함! 100% 이해해야 함! N+1 문제를 해결하는 방법임.
    //- 'join fetch'를 통해 '객체 그래프'와 'select 조회 데이터'를 한 방에 동시에 같이 가져오는 것임!
    //   fetch join'을 아주 적극적으로 활용해야 함!
    //- 여기서 'fetch join'을 사용하였기에 'order -> member'와 'order -> delivery'는 '이미 조회 완료'된 상태이므로,
    //  '주문 엔티티 Order 객체' 내부의 '필드 @ManyToOne(fetch=LAZY) Member'와 '필드 @OneToOne(fetch=LAZY)의
    //  'LAZY'는 이제 무시되고, 따라서 당연히 지연로딩은 없다!
    
    @GetMapping("/api/v3/sample-orders")
    public List<SampleOrderDto> ordersV3(){

        List<Order> orders = orderRepository.findAllWithMemberDelivery();

        List<SampleOrderDto> sampleOrderDtos = orders.stream()
                .map(o -> new SampleOrderDto(o))
                .collect(Collectors.toList());



        return sampleOrderDtos;
    }


    //------------------------------------------------------------------------------------------------------


    //< DTO 생성 >
    @Data
    static class SampleOrderDto{ //'배송지 정보'

        private Long orderId;
        private String name;
        private LocalDateTime orderDate;
        private OrderStatus orderStatus;
        private Address address;

        public SampleOrderDto(Order order){ //생성자
            orderId = order.getId();
            name = order.getMember().getName(); //LAZY 초기화
            orderDate = order.getOrderDate();
            address = order.getDelivery().getAddress(); //LAZY 초기화
        }

    }


    //======================================================================================================





}
