package jpabook.jpashop.domain;


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

    @OneToOne(mappedBy = "delivery")
    private Order order;

    @Embedded //'Address 객체 타입'으로 'JPA의 내장 타입(임베디드 타입)을 사용했다'라는 뜻.
              //'저기 클래스 Address'위에 '@Embeddable'을 붙이거나,
              //'여기 클래스 Delivery의 필드 address' 위에 '@Embedded'를 붙이거나
              // 둘 중 하나만 붙여도 되고, 둘 다 붙여도 된다!
    private Address address;


    @Enumerated(EnumType.STRING) //'Enum 타입 필드'를 가질 때에는, 반드시 '@Enumerated(EnumType.String'을
                                 //붙여줘야 한다!
                                 //cf)'(EnumType.ORDINAl)': 디폴트값으로 이게 설정되어 있긴 한데,
                                 //                         자동으로 컬럼에 1, 2, 3, ..이렇게 늘어나게 해주는건데
                                 //                         중간에 어떤 다른 상태가 생기면, DB에서 순서가
                                 //                         다 이상하게 꼬여서 DB 조회해보면 다 망해버리는 것 확인됨.
    private DeliveryStatus status;
}
