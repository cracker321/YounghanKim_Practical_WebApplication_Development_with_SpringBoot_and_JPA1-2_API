package jpabook.jpashop.repository;

import jpabook.jpashop.domain.Address;
import jpabook.jpashop.domain.Order;
import jpabook.jpashop.domain.OrderStatus;
import lombok.Data;

import java.time.LocalDateTime;


//[ '간단한 주문 조회 V4: JPA에서 DTO로 바로 조회'강. 01:40~ ]. 실전! 스프링 부트와 JPA 활용2 - API 개발과 성능 최적화

//- 권장하지 않는 방법임(권장하는 방법은 v2와 v3(v2에서 성능이슈 발생할 경우 v3 사용)
//- v2에서 발생한 N+1 성능 이슈를 v3의 'fetch join'으로도 해결하지 못할 경우, 여기의 v4를 사용하는 것이다.
//  사실, v3를 통해 대부분의 N+1 성능 이슈는 해결 가능함.
//- 레퍼지토리의 재사용성이 떨어짐. 다른 API에서는 'orderRepository.findOrderDtos()'를 사용할 수 없음.
//  오직 '컨트롤러의 메소드 ordersV4()'에서만 이 '메소드 findOrderDtos'를 사용할 수 있게 딱 맞게 설계됨.
//  즉, 유연성이 아예 없음.

//- 일반적인 SQL을 사용할 때처럼 원하는 값을 선택해서 조회
//- new 명령어를 사용해서 JPQL의 결과를 DTO로 즉시 변환
//- SELECT절에서 원하는 데이터를 직접 선택하므로 DB에서 애플리케이션 네트웍 용량 최적화(그러나, 그 최적화는 생각보다 미미함)
//- 레포지터리 재사용성이 떨어짐. API스펙에 맞춘 코드가 레퍼지터리에 들어가는 단점.


//< DTO 생성 >

@Data
public class OrderSampleQueryDto { //'배송지 정보'

        private Long orderId;
        private String name;
        private LocalDateTime orderDate;
        private OrderStatus orderStatus;
        private Address address;


        //< DTO의 생성자 >
        public OrderSampleQueryDto(Long orderId, String name, LocalDateTime orderDate, OrderStatus orderStatus,
                                   Address address){
            this.orderId = orderId;
            this.name = name;
            this.orderDate = orderDate;
            this.orderStatus = orderStatus;
            this.address = address;
        }

    }


