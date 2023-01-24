package jpabook.jpashop.domain.Item;


import jpabook.jpashop.domain.CategoryItem;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

/*
참고: 이론적으로 Getter, Setter 모두 제공하지 않고, 꼭 필요한 별도의 메서드를 제공하는게 가장
이상적이다. 하지만 실무에서 엔티티의 데이터는 조회할 일이 너무 많으므로, Getter의 경우 모두 열어두는
것이 편리하다. Getter는 아무리 호출해도 호출 하는 것 만으로 어떤 일이 발생하지는 않는다. 하지만
Setter는 문제가 다르다. Setter를 호출하면 데이터가 변한다. Setter를 막 열어두면 가까운 미래에
엔티티에가 도대체 왜 변경되는지 추적하기 점점 힘들어진다. 그래서 엔티티를 변경할 때는 Setter 대신에
변경 지점이 명확하도록 변경을 위한 비즈니스 메서드를 별도로 제공해야 한다.
 */
@Getter
@Setter
@DiscriminatorColumn(name = "DTYPE")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE) //'단일 테이블 전략'
@Entity
public abstract class Item { //[ '엔티티 클래스 개발1'강 19:04~ ]
    // - '추상 클래스': '하나 이상의 추상 메소드'만 가지면 성립되고(cf: '추상 메소드가 아예 없더라도',
    //               그냥 클래스명 옆에 'abstract'만 붙여주고 추상 클래스로 선언해도 되긴 하다!
    //               이 때는 그냥 그 해당 '추상 클래스'는 프로젝트에서 객체화하지 않아(@Entity 붙이지 않아),
    //               DB에 해당 테이블 만들지 않을 거라는 뜻
    //               https://rap0d.github.io/study/2019/08/28/java_16_%EC%B6%94%EC%83%81/ )
    //               그리고, 모든 추상 클래스는 '상속을 위한 클래스이기 때문'에, '객체화(인스턴스화)할 수 없다!'
    //               그러면 '일반 변수', '일반 생성자', '일반 메소드' 등
    //               '일반 클래스가 가지는 내부 요소들'을 동일하게 가지는 것 가능함.
    //               또한, '하나의 자식 클래스'가 '다중 추상 클래스로부터 상속 불가능'.
    //               내부 메소드로 '추상 메소드'를 여러 개 간략히 만들어놓고, 이 '추상 클래스를 상속받는 자식 클래스'가
    //               그 '추상 클래스의 여러 추상 메소드들 중'에서 '필요한 추상 메소드'를 취사선택하여
    //               그것을 세부적으로 구현하는 것이 목적!
    //               '자식 클래스'는 '부모 클래스(추상 클래스) 내부의 모든 추상 메소드들'을
    //              '반드시 오버라이딩하여 모두 구현해줘야' 한다!
    //- '인터페이스': 오직 '추상 클래스', '상수'만 그 내부 요소로 가질 수 있음.
    //              '하나의 자식 클래스'가 '다중 추상 클래스로부터 상속'과 '다중 인터페이스로부터(를) 구현' 가능하다!
    //              '인터페이스의 자식 클래스'는 '부모 클래스(인터페이스) 내부의 모든 추상 메소드들'
    //              '반드시 오버라이딩하여 모두 구현해줘야' 한다!

    @Id
    @GeneratedValue
    @Column(name = "ITEM_ID")
    private Long id;

    private String name;

    private int price;

    private int stockQuantity;

    @OneToMany(mappedBy = "item")
    private List<CategoryItem> categoryItems = new ArrayList<>();



}
