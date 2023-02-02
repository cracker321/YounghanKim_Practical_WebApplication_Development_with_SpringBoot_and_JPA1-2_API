package jpabook.jpashop.service;


import jpabook.jpashop.domain.Address;
import jpabook.jpashop.domain.Delivery;
import jpabook.jpashop.domain.Item.Item;
import jpabook.jpashop.domain.Member;
import jpabook.jpashop.domain.Order;
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

        return null;
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
