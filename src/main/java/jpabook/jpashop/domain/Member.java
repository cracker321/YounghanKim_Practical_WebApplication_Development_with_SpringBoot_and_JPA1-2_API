package jpabook.jpashop.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
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

    @NotEmpty
    private String name; //'회원명'

    @Embedded //'Address 객체 타입'으로 'JPA의 내장 타입(임베디드 타입)을 사용했다'라는 뜻.
              //'저기 클래스 Address'위에 '@Embeddable'을 붙이거나,
              //'여기 클래스 Member의 필드 address' 위에 '@Embedded'를 붙이거나
              // 둘 중 하나만 붙여도 되고, 둘 다 붙여도 된다!
    private Address address; //'회원 주소'


    //[ '간단한 주문 조회 V1: 엔티티를 직접 노출'강. 06:00~ ]. 실전! 스프링 부트와 JPA 활용2 - API 개발과 성능 최적화
    @JsonIgnore //- 1)만약, 정말 어쩔 수 없이 DTO가 아닌 '엔티티'를 생으로 노출시켜 데이터를 송수신해야 하는 경우일 때,
                //  2)그리고, 그 경우가 'N:1 또는 1:1 '양'방향 매핑'일 때에는,
                //  => 1.두 연관관계 엔티티들 중 하나는 반드시 @JsonIgnore 해주고, 그리고
                //     2.'서버 실행 JpashopApplication'의 내부에 'Hibernate5Module 객체' 관련 로직을작성해줘야 한다!
                //       ('Hibernate5Module 라이브러리' 의존성을 build.gradle에 추가해줘야 함)
                //  이렇게 함을 통해서 둘 중의 하나를 @JsonIgnore로 끊어줘야, 무한루프 문제가 발생하지 않음!
                //- 프론트단에서 포스트맨으로 '회원 Member'를 조회할 때, 아래 '해당 회원의 주문 내역'은 제외되어 조회됨.
    @OneToMany(mappedBy = "member") //'주인인 Order 객체의 필드 member'와 'N:1 양방향 연결(매핑)되어 있다'라는 뜻
                                    //현재 'Member 객체의 필드 orders'는 반대편 주인 객체 Order'와 매핑되어 있는
                                    //거울일 뿐이다 라는 뜻!
    private List<Order> orders = new ArrayList<>(); //'클래스 Order의 필드 id'를 '참조한 필드'
}
