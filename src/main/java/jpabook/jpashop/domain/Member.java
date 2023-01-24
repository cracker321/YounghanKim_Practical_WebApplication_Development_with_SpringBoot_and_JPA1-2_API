package jpabook.jpashop.domain;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
public class Member {


    @Id
    @GeneratedValue
    @Column(name = "MEMBER_ID") //'테이블 MEMBER의 PK 컬럼명'은 'MEMBER_ID'이기 떄문에,
                                //여기 자바 객체에서도 반드시 '컬럼 id'가 아니라, '컬럼 MEMBER_ID'와 매핑시켜야함!
                                //대소문자는 상관없음. "member_id"로 해도 상관없음.
    private Long id;

    private String name; //'회원명'

    @Embedded //'Address 객체 타입'으로 'JPA의 내장 타입(임베디드 타입)을 사용했다'라는 뜻.
              //'저기 클래스 Address'위에 '@Embeddable'을 붙이거나,
              //'여기 클래스 Member의 필드 address' 위에 '@Embedded'를 붙이거나
              // 둘 중 하나만 붙여도 되고, 둘 다 붙여도 된다!
    private Address address; //'회원 주소'


    @OneToMany(mappedBy = "member") //'주인인 Order 객체의 필드 member'와 'N:1 양방향 연결(매핑)되어 있다'라는 뜻
                                    //현재 'Member 객체의 필드 orders'는 반대편 주인 객체 Order'와 매핑되어 있는
                                    //거울일 뿐이다 라는 뜻!
    private List<Order> orders = new ArrayList<>(); //'클래스 Order의 필드 id'를 '참조한 필드'
}
