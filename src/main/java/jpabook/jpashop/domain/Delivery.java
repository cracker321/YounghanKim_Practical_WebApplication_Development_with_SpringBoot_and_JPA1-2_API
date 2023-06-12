package jpabook.jpashop.domain;


import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@Entity
public class Delivery {

    @Id
    @GeneratedValue
    @Column(name = "DELIVERY_ID")
    private Long id;

    //[ '간단한 주문 조회 V1: 엔티티를 직접 노출'강. 07:58~ ]. 실전! 스프링 부트와 JPA 활용2 - API 개발과 성능 최적화
    @JsonIgnore //- 1)만약, 정말 어쩔 수 없이 DTO가 아닌 '엔티티'를 생으로 노출시켜 데이터를 송수신해야 하는 경우일 때,
                //  2)그리고, 그 경우가 'N:1 또는 1:1 '양'방향 매핑'일 때에는,
                //  => 1.두 연관관계 엔티티들 중 하나는 반드시 @JsonIgnore 해주고, 그리고
                //     2.'서버 실행 JpashopApplication'의 내부에 'Hibernate5Module 객체' 관련 로직을작성해줘야 한다!
                //       ('Hibernate5Module 라이브러리' 의존성을 build.gradle에 추가해줘야 함)
                //  이렇게 함을 통해서 둘 중의 하나를 @JsonIgnore로 끊어줘야, 무한루프 문제가 발생하지 않음!
    @OneToOne(mappedBy = "delivery", fetch = FetchType.LAZY)
    private Order order;

    @Embedded //'Address 객체 타입'으로 'JPA의 내장 타입(임베디드 타입)을 사용했다'라는 뜻.
              //'저기 클래스 Address'위에 '@Embeddable'을 붙이거나,
              //'여기 클래스 Delivery의 필드 address' 위에 '@Embedded'를 붙이거나
              // 둘 중 하나만 붙여도 되고, 둘 다 붙여도 된다!
    private Address address;


    @Enumerated(EnumType.STRING) //'Enum 타입 필드'를 가질 때에는, 반드시 '@Enumerated(EnumType.String)'을
                                 //붙여줘야 한다!
                                 //cf)'(EnumType.ORDINAl)': 디폴트값으로 이게 설정되어 있긴 한데,
                                 //                         자동으로 컬럼에 1, 2, 3, ..이렇게 늘어나게 해주는건데
                                 //                         중간에 어떤 다른 상태가 생기면, DB에서 순서가
                                 //                         다 이상하게 꼬여서 DB 조회해보면 다 망해버리는 것 확인됨.
    private DeliveryStatus status;
}
