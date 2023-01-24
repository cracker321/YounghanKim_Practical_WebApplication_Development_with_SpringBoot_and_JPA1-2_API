package jpabook.jpashop.domain;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Embeddable;


/*
참고: 값 타입은 변경 불가능하게 설계해야 한다.
@Setter 를 제거하고, 생성자에서 값을 모두 초기화해서 변경 불가능한 클래스를 만들자. JPA 스펙상
엔티티나 임베디드 타입( @Embeddable )은 자바 기본 생성자(default constructor)를 public 또는
protected 로 설정해야 한다. public 으로 두는 것 보다는 protected 로 설정하는 것이 그나마 더
안전하다.
JPA가 이런 제약을 두는 이유는 JPA 구현 라이브러리가 객체를 생성할 때 리플랙션 같은 기술을 사용할 수
있도록 지원해야 하기 때문이다.

 */
@Embeddable //'Address 객체 타입'으로 'JPA의 내장 타입(임베디드 타입)을 사용했다'라는 뜻.
            //'여기 클래스 Address'위에 '@Embeddable'을 붙이거나,
            //'저기 클래스 Delivery와 저기 클래스 Member의 필드 address' 위에 '@Embedded'를 붙이거나
            // 둘 중 하나만 붙여도 되고, 둘 다 붙여도 된다!
@Getter
@AllArgsConstructor //'객체의 값 타입'을 쓸 때는 '모든 매개변수 생성자'와 '기본 생성자' 모두 포함시킨다!
public class Address {

    private String city;
    private String street;
    private String zipcode;

    protected Address(){ //'객체의 값 타입'을 쓸 때는 '모든 매개변수 생성자'와 '기본 생성자' 모두 포함시킨다!
                         //어차피 '클래스 Address'를 다른 클래스가 상속하는 것 아니기 때문에, 
                         //'public' 대신 'protected'를 쓴다
        
    }
}
