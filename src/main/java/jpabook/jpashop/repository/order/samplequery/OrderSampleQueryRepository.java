package jpabook.jpashop.repository.order.samplequery;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.List;


@RequiredArgsConstructor
@Repository
public class OrderSampleQueryRepository {


    private final EntityManager em;


    //'레퍼지토리'와 '컨트롤러'를 역으로 참조해서 서로 '의존관계'가 '절대 생겨서는 안된다!!'
    //(즉, '컨 -> 서 -> 리'처럼 순방향으로 해야 하는데, '리 -> 서 -> 컨' 처럼 역방향으로 참조하는 것은 안됨)


    //[ '간단한 주문 조회 V4: JPA에서 DTO로 바로 조회'강. 02:00~ ]. 실전! 스프링 부트와 JPA 활용2 - API 개발과 성능 최적화.'pdf p20'

    //- 권장하지 않는 방법임(권장하는 방법은 v2와 v3(v2에서 성능이슈 발생할 경우 v3 사용)
    //- v2에서 발생한 N+1 성능 이슈를 v3의 'fetch join'으로도 해결하지 못할 경우, 여기의 v4를 사용하는 것이다.
    //  사실, v3를 통해 대부분(95% 이상)의 N+1 성능 이슈는 해결 가능함.
    //- 레퍼지토리의 재사용성이 떨어짐. 다른 API에서는 '메소드 findOrderDtos'를 사용할 수 없음.
    //  오직 '컨트롤러의 메소드 ordersV4()'에서만 이 '메소드 findOrderDtos'를 사용할 수 있게 딱 맞게 설계됨. 즉, 유연성이 아예 없음.

    //- 일반적인 SQL을 사용할 때처럼 원하는 값을 선택해서 조회
    //- new 명령어를 사용해서 JPQL의 결과를 DTO로 즉시 변환
    //- SELECT절에서 원하는 데이터를 직접 선택하므로 DB에서 애플리케이션 네트웍 용량 최적화(그러나, 그 최적화는 생각보다 미미함)
    //- 레포지터리 재사용성이 떨어짐. API스펙에 맞춘 코드가 레퍼지터리에 들어가는 단점.

    public List<OrderSampleQueryDto> findOrderDtos(){ //DB로부터 OrderSampleQueryDto를 반환하게 할 것임.

        return em.createQuery("select new jpabook.jpashop.repository.order.samplequery.OrderSampleQueryDto(o.id, m.name," +
                "o.orderDate, o.status, d.address)" +
                " from Order o" +
                " join o.member m" +
                " join o.delivery d", OrderSampleQueryDto.class //DB로부터 'OrderSampleQueryDto 객체' 타입으로 데이터를 가져옴.
        ).getResultList();
    }


}
