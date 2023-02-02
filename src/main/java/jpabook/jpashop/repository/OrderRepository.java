package jpabook.jpashop.repository;


import jpabook.jpashop.domain.Order;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.List;


//[ '주문 리포지토리 개발'강 ]

@RequiredArgsConstructor
@Repository
public class OrderRepository {

    private final EntityManager em;


//=================================================================================================================


    //< 신규주문 저장 >
    public void save(Order order){
        em.persist(order);
    }


//==================================================================================================================


    //< '개별 주문(1건)을 DB에서 '해당 주문의 id값'으로 조회'하기 >
    //'클라이언트로부터 매개변수로 들어온 id에 해당하는 1개의 주문'을 DB에서 찾아와서 '그 주문을 리턴'해줌
    public Order findOne(Long orderId){

        Order order = em.find(Order.class, orderId);

        return order;
    }


//==================================================================================================================


    //[ '주문 리포지토리 개발'강. 01:00~ ]
    //< 전체 주문 조회 >
    public List<Order> findAll(OrderSearch orderSearch){

        return null;

    }





//==================================================================================================================


}
