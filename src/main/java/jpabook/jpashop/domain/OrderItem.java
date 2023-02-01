package jpabook.jpashop.domain;


import jpabook.jpashop.domain.Item.Item;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

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

    //[ '주문, 주문상품 엔티티 개발'강. 13:15~ ]

    //< '주문 생성' 메소드
    public static OrderItem createOrderItem(Item item, int orderPrice, int count){ //- 'Item': 어떤 상품을
                                                                                   //- 'orderPrice': 얼마의 가격에
                                                                                   //- 'count': 몇 개나 샀는지




        return null;
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


    //============= 비즈니스로직 =============

    //[ '주문, 주문상품 엔티티 개발'강. 10:00~ ]

    //< 해당 하나의 주문 건에 속한 총 주문상품들의 총 주문금액 합 조회 >
    public int getTotalPrice(){


        return getCount() * getOrderPrice(); //'현재 같은 클래스 OrderItem 내부의 필드 count와 필드 orderPrice'를
                                             //여기서 사용함에도 불구하고, 그 필드(데이터)를 직접 사용하는 것이 아니라,
                                             //그것들의 '게터 getter'를 사용해야 한다!
    }





//==================================================================================================================




}
