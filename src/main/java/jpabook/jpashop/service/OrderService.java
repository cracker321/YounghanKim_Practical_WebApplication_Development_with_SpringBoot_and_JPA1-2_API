package jpabook.jpashop.service;


import jpabook.jpashop.domain.*;
import jpabook.jpashop.domain.Item.Item;
import jpabook.jpashop.repository.ItemRepository;
import jpabook.jpashop.repository.MemberRepository;
import jpabook.jpashop.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static jpabook.jpashop.domain.DeliveryStatus.READY;


@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class OrderService { //'ctrl + shift + T' 누르면, 바로 '테스트코드' 만들 수 있는 단축키 나옴


//=================================================================================================================


    //- 바로 아래 '< 신규주문 저장 >'에서 사용된 'memberRepository.findOne(memberId)'와 'itemRepository.findOne(itemId)'를
    //  사용하기 위해 아래에서 '레펏 OrderRepository'와 '레펏 ItemRepository'를 '의존성주입 DI로 가져옴'.
    //- '엔티티 객체(e.g: Member 객체, Order 객체 등)'는 '@Autowired'와 같은 의존성 주입을 할 필요 없고,
    //  그냥 가져와서 쓸 수 있다!
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
        //- '엔티티 OrderItem 객체'와 같은 '엔티티 객체'는 'public이기 떄문에 다른 외부 클래스에서 자유롭게 사용 가능!'
        //- '주문 서비스 개발'강. 09:10~
        //  아래의 '생성자와 동일한 역할을 하는 생성자 기능 신규주문 생성 메소드 createOrderItem을 사용(호출)하여 만든
        //  'OrderItem orderItem = OrderItem.createOrderItem(item, item.getPrice(), count)'는
        //  바로 아래 로직과 똑같다.
        //  바로 아래 로직처럼 하거나 '생성자와 동일한 역할을 하는 생성자 기능 신규주문 생성 메소드 createOrderItem을 사용(호출)하여
        //  만든 'OrderItem orderItem = OrderItem.createOrderItem(item, item.getPrice(), count)'를 사용하거나
        //  둘 중 하나의 스타일만 사용해야 함. 그래야 나중에 혼란스럽지 않고 유지보수하기 편함
        //  여기서는, '생성자와 동일한 역할을 하는 생성자 기능..'을 사용하기로 했으니,
        //  이제 바로 아래 로직처럼 'new OrderItem 만들어서 하는 스타일'은 사용하면 안됨.
        //  개발은 혼자 하는 것이 아니라, 다른 개발자들도 같이 협업하면서 하기 때문에, 다른 개발자들이 혹시라도
        //  'new OrderItem 만들어서 하는 스타일'을 사용하지 않도록(못하도록)
        //  'OrderItem 객체 내부'에
        //  'protected OrderItem(){
        //      }'
        //  를 넣어줘서 그 스타일 사용을 막아야(protect) 한다! 이렇게 하게 되면, 바로 아래에서 'new OrderItem'을 쓴다면
        //  바로 컴파일 에러남.
        //  참고로, 바로 위 'protected OrderItem(){}'는 '클래스 OrderItem의 맨 위에 클래서 어노테이션
        //  @NoAragsConstructor(access = AccessLevel.PROTECTED'를 붙이면 동일한 기능이 되게 된다.
        /*
        OrderItem orderItem = new OrderItem();
        orderItem.setItem(item);
        orderItem.setOrderPrice(item.getPrice());
        orderItem.setCount(count);
        */
        OrderItem orderItem = OrderItem.createOrderItem(item, item.getPrice(), count);


        //4.(위 1~3번 과정을 바탕으로)신규주문 생성. '주문 서비스 개발'강. 03:25~
        //- 이것도 3번과 마찬가지로
        /*
        Order order = new Order();
        order.setMember(member);
        order.setDelivery(delivery);
        order.getOrderItems().add(orderItem);
        */
        //위에 로직처럼 해 줄 수 있으나, 아래 스타일처럼 하기로 내가 결정했으니, ...

        Order order = Order.createOrder(member, delivery, orderItem);


        //5.위 4번에서 생성된 신규주문을 DB에 저장하기. '주문 서비스 개발'강. 04:02~
        orderRepository.save(order); //여기서 'Order 객체를 DB에 영속화 persist 시킨다면', 'Order 객체와 Cascade 관계 되어있는
                                     //'OrderItem 객체('엔티티 Order' 참조)', 'Delivery 객체('엔티티 Delivery' 참조)'도
                                     //'그와 동시에 DB에 영속화 persist 된다!'.

        return order.getId(); //05:10~
    }


//=================================================================================================================


    //< 주문 취소 >. '주문 서비스 개발'강. 11:30~
    //
    @Transactional
    public void cancelOrder(Long orderId){ //여기서 '매개변수 orderId'는 그냥 여기서 메소드 안에서만 사용할 수 있는
                                           //내가 임의로 정한 매개변수임.

        //취소할 주문 조회
        Order order = orderRepository.findOne(orderId);


        //해당 주문 취소
        order.cancel();

    }


//=================================================================================================================


//    //< 전체 주문 검색 >. '주문 서비스 개발'강. 14:55~
//    public List<Order> findOrders(OrderSearch orderSearch){ //'Order 객체 내부의
//
//        return orderRepository.findAll(orderSearch);
//    }


//=================================================================================================================




//=================================================================================================================



//=================================================================================================================



//=================================================================================================================

}
