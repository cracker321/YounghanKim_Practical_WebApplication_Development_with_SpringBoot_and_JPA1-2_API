package jpabook.jpashop.api;


import jpabook.jpashop.domain.Address;
import jpabook.jpashop.domain.Order;
import jpabook.jpashop.domain.OrderStatus;
import jpabook.jpashop.repository.OrderRepository;
import jpabook.jpashop.repository.OrderSampleQueryDto;
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
    //- 권장하는 방법임.
    //  만약 여기서 'N+1 문제로 인한 성능 이슈'가 발생하면, 저 아래의 v3에서의 'fetch join'을 사용하면 된다!
    //  (v3를 통해 대부분의 N+1 성능 이슈는 해결 가능함.)


    //< N+1 문제를 해결하지 못한 JPQL 쿼리문을 담고 있는 '레펏 OrderRepository의 메소드 findAllByString'
    @GetMapping("api/v2/sample-orders")
    public List<SampleOrderDto> odersV2(){

        List<Order> orders = orderRepository.findAllByString(new OrderSearch());

        List<SampleOrderDto> sampleOrderDtos = orders.stream()
                .map(o -> new SampleOrderDto(o))
                //- '.map(SampleOrderDto::new'와 동일하다! 'Lambda 레퍼런스'를 사용한 것임.
                //- 'DB로부터 가져온 Order 객체'를 'SampleOrderDto 타입'으로 변형시키는 것임.
                //- 1)'static 클래스 SampleOrderDto'는 '정적 클래스'이므로, 그 외부에서 '클참뉴클 을 통한 SampleOrderDto 객체'를
                //    생성하지 않고도, 바로 여기서 'new SampleOrderDto 객체'를 생성할 수 있고,
                //  2)'static 클래스 SampleOrderDto에서 Order 객체를 매개변수로 받는 사용자 생성자'가 있기 때문에,
                //    여기서 'new 키워드로 SampleOrderDto 객체 생성하고 그 파라미터로 Order 객체의 alias인 'o'를 넣어주는 것이
                //    가능'한 것이다!
                .collect(Collectors.toList()); //'리스트 List'로 변환시키는 것임.

        return sampleOrderDtos;
    }

    //======================================================================================================


    //[ '간단한 주문 조회 V3: 엔티티를 DTO로 변환 - 페치 조인 최적화'강. 00:00~ ]. 실전! 스프링 부트와 JPA 활용2 - API 개발과 성능 최적화
    //- 권장하는 방법임.
    //  v2에서의 'N+1 문제로 인한 성능 이슈'가 발생하면, 여기서 v3에서의 'fetch join'을 사용하면 된다!
    //  (v3를 통해 대부분의 N+1 성능 이슈는 해결 가능함.)

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

    //======================================================================================================


    //[ '간단한 주문 조회 V4: JPA에서 DTO로 바로 조회'강. 00:00~ ]. 실전! 스프링 부트와 JPA 활용2 - API 개발과 성능 최적화. pdf p20
    //- 권장하지 않는 방법임(권장하는 방법은 v2와 v3(v2에서 성능이슈 발생할 경우 v3 사용)
    //- v2에서 발생한 N+1 성능 이슈를 v3의 'fetch join'으로도 해결하지 못할 경우, 여기의 v4를 사용하는 것이다.
    //  사실, v3를 통해 대부분의 N+1 성능 이슈는 해결 가능함.

    @GetMapping("/api/v4/sample-orders")
    public List<OrderSampleQueryDto> ordersV4(){

        return orderRepository.findOrderDtos();


    }




    //======================================================================================================


    //< DTO 생성 >
    //- v2와 v3에서 활용됨.
    //- v4에서는 별도의 '클래스 OrderSampleQueryDto'를 활용함.
    @Data
    static class SampleOrderDto{ //'배송지 정보'

        private Long orderId;
        private String name;
        private LocalDateTime orderDate;
        private OrderStatus orderStatus;
        private Address address;

        public SampleOrderDto(Order order){ //DTO의 생성자
            orderId = order.getId();
            name = order.getMember().getName(); //LAZY 초기화
            orderDate = order.getOrderDate();
            address = order.getDelivery().getAddress(); //LAZY 초기화
        }

    }


    //======================================================================================================





}
