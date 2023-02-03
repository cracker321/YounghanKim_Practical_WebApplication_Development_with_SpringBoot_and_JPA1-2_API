package jpabook.jpashop.domain;


import jpabook.jpashop.domain.Item.Item;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@NoArgsConstructor(access = AccessLevel.PROTECTED) //아래 중간 즈음에 있는 'protected OrderItem(){}' 부분을 대신해서 붙여줌.
@Getter
@Setter
@Entity
public class OrderItem {

    @Id
    @GeneratedValue
    @Column(name = "ORDER_ITEM_ID")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ITEM_ID")
    private Item item;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ORDER_ID")
    private Order order;

    private int orderPrice; //고객이 주문하는 당시의 가격

    private int count; //주문 수량

//==================================================================================================================


    //'주문 서비스 개발'강. 09:10~

    //''서비스 OrderService 내부'의 '< 신규주문 저장 > 내부'의 '3.주문상품 생성' 부분 참조'
    //'protected OrderItem(){}'는 '클래스 OrderItem의 맨 위에 클래스 어노테이션
    //@NoAragsConstructor(access = AccessLevel.PROTECTED'를 붙이면 동일한 기능이 되게 된다.
//    protected OrderItem(){
//
//    }


//==================================================================================================================


    //[ '주문, 주문상품 엔티티 개발'강. 13:15~ ]

    //< '신규주문 생성' 메소드 >
    //- '사용자 생성자'와 정확히 동일한 역할을 함.
    //  '생성자를 사용하지 않고' 이렇게 '생성하는 메소드를 따로 쓴 이유'는,
    //  '생성자와 달리 내 임의로 내가 원하는 이름을 줄 수 있기 때문'임.
    //  실제로 이 경우처럼 '명확함'을 위해 '생성자 역할을 하지만, 그 고유한 이름을 가지는 메소드'를 만들어 '객체를 생성하는 역할'을
    //  하는 경우도 많이 있다!
    public static OrderItem createOrderItem(Item item, int orderPrice, int count){ //- 'Item': 어떤 상품을
                                                                                   //- 'orderPrice': 얼마의 가격에
                                                                                   //- 'count': 몇 개나 샀는지

        OrderItem orderItem = new OrderItem();
        orderItem.setItem(item);
        orderItem.setOrderPrice(orderPrice);
        orderItem.setCount(count);

        item.removeStock(count); //신규주문이 발생하여, 신규주문품목이 생기면, 당연히 '기존의 재고에서 신규주문 수량만큼 감소'시켜야
                                 //함. 당연..

        return orderItem;
    }


//==================================================================================================================


    //============= 비즈니스로직 =============

    //[ '주문, 주문상품 엔티티 개발'강. 07:00~ ]

    //< 주문상품에 대한 주문취소 >
    //고객이 '하나의 건 주문할 때('Order 객체' 호출)', 그 하나의 건 주문 안에는 'N개의
    //주문상품 OrderItem'이 있을 수 있기 때문에, for문을 돌려서 그 하나의 건 주문 안에 있는
    //'N개의 모든 주문상품들 OrderItem 취소(메소드 cancel)시켜야 함'.
    public void cancel(){

        getItem().addStock(count); //- '기존 주문수량 count'만큼 '기존 재고에 다시 더해줘야 한다!'.
                                   //- '필드 item'과 '메소드 cancel'이 '같은 엔티티 OrderItem의 내부에 동시에 존재'하고 있는 상황에서,
                                   //  '필드 item을 통해 엔티티 Item 객체를 호출할 때'에는, '게터 Item'을 사용한다!
                }


//==================================================================================================================


    //============= 조회 로직 =============

    //[ '주문, 주문상품 엔티티 개발'강. 10:00~ ]

    //< 해당 하나의 주문 건에 속한 총 주문상품들의 총 주문금액 합 조회(=신규주문으로 들어온 전체 상품의 총 가격 조회) >
    public int getTotalPrice(){


        return getCount() * getOrderPrice(); //'현재 같은 클래스 OrderItem 내부의 필드 count와 필드 orderPrice'를
                                             //여기서 사용함에도 불구하고, 그 필드(데이터)를 직접 사용하는 것이 아니라,
                                             //그것들의 '게터 getter'를 사용해야 한다!
    }





//==================================================================================================================




}
