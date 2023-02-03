package jpabook.jpashop.service;


import jpabook.jpashop.domain.*;
import jpabook.jpashop.domain.Item.Item;
import jpabook.jpashop.repository.ItemRepository;
import jpabook.jpashop.repository.MemberRepository;
import jpabook.jpashop.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static jpabook.jpashop.domain.DeliveryStatus.READY;


@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final MemberRepository memberRepository;
    private final ItemRepository itemRepository;


//=================================================================================================================


    //[ '주문 서비스 개발'강. 00:30~ ]

    //< 신규주문 저장 >
    //: DB에 1개의 주문 객체를 저장시킴
    @Transactional //'신규주문 저장'은 DB에 새로운 데이터를 넣는 것, 즉 DB의 기존상태를 변화시키는 것이므로 '@Transactional'사용!
    public Long order(Long memberId, Long itemId, int count){ //'신규 주문하기 위해서는', '회원 ID', '상품 식별번호',
                                                              //'주문 수량'이라는 정보가 필요하다!

        //1.엔티티 조회
        Member member = memberRepository.findOne(memberId); //DB로부터 일단 '해당하는 회원 ID'를 찾아서 가져옴
        Item item = itemRepository.findOne(itemId); //DB로부터 일단 '해당하는 상품 식별번호 ID'를 찾아서 가져옴


        //2.배송정보 생성
        //'생성'이기에 '클참뉴클' 사용한 것임.
        Delivery delivery = new Delivery();
        delivery.setAddress(member.getAddress());


        //3.주문상품 생성. '주문 서비스 개발'강. 02:50~
        OrderItem orderItem = OrderItem.createOrderItem(item, item.getPrice(), count);
                //- '엔티티 OrderItem 객체'와 같은 '엔티티 객체'는 'public이기 떄문에 다른 외부 클래스에서 자유롭게 사용 가능!'


        //4.(위 1~3번 과정을 바탕으로)신규주문 생성. '주문 서비스 개발'강. 03:25~
        Order order = Order.createOrder(member, delivery, orderItem);


        //5.위 4번에서 생성된 신규주문을 DB에 저장하기. '주문 서비스 개발'강. 04:02~
        orderRepository.save(order); //여기서 'Order 객체를 DB에 영속화 persist 시킨다면', 'Order 객체와 Cascade 관계 되어있는
                                     //'OrderItem 객체('엔티티 Order' 참조)', 'Delivery 객체('엔티티 Delivery' 참조)'도
                                     //'그와 동시에 DB에 영속화 persist 된다!'.



        return order.getId(); //05:10~
    }


//=================================================================================================================


    //< 주문 취소 >
    //
    public Order cancelOrder(Order oder){

        orderRepository.



        return null;
    }


//=================================================================================================================


    //< 주문 검색 >




//=================================================================================================================




//=================================================================================================================



//=================================================================================================================



//=================================================================================================================

}
