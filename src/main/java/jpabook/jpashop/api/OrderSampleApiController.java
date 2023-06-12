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
    
    @GetMapping("api/v2/sample-orders")
    public List<SampleOrderDto> odersV2(){

        List<Order> orders = orderRepository.findAllByString(new OrderSearch());

        List<SampleOrderDto> result = orders.stream()
                .map(o -> new SampleOrderDto(o)) //- '.map(SampleOrderDto::new'와 동일하다! 'Lambda 레퍼런스'를 사용한 것임.
                                                 //- 'DB로부터 가져온 Order 객체'를 'SampleOrderDto 타입'으로 변형시키는 것임.
                .collect(Collectors.toList()); //'리스트 List'로 변환시키는 것임.

        return result;
    }

    //------------------------------------------------------------------------------------------------------


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




    //======================================================================================================

}
