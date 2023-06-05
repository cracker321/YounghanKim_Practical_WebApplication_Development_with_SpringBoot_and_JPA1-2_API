package jpabook.jpashop.repository;


import jpabook.jpashop.domain.Order;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.util.List;


//[ '주문 리포지토리 개발'강 ]

@RequiredArgsConstructor
@Repository
public class OrderRepository {

    private final EntityManager em;
    /*
    EntityManager는 JPA의 중심적인 인터페이스입니다. 이 객체를 사용해서 데이터베이스와 상호 작용할 수 있습니다.
    EntityManager는 엔티티를 저장, 수정, 삭제, 조회 등의 역할을 담당하고, 데이터베이스랑 통신하며,
    영속성 컨텍스트를 관리하는 등의 역할을 합니다. 예를 들어, 개별적인 데이터를 데이터베이스에 저장하거나,
    데이터베이스 조회 결과를 Java 객체로 변환(Materialization)하는 등의 작업을 담당
     */


//=================================================================================================================


    //< 신규주문 저장 >
    public void save(Order order){
        em.persist(order);
    }


//==================================================================================================================


    //< '개별 주문(1건)을 DB에서 '해당 주문의 id값'으로 조회'하기 >
    //'클라이언트로부터 매개변수로 들어온 id에 해당하는 1개의 주문'을 DB에서 찾아와서 '그 주문을 리턴'해줌
    public Order findOne(Long orderId){ //'여기서의 매개변수 orderId'는 그냥 여기 메소드에서만 통용되는 것에 불과하고,
                                        //중요한 것은, '레펏 OrderRepository의 메소드 findOne을 호출할 때는',
                                        //반드시 '그 매개변수로 Long 타입'을 넣어주어야 하는 것이다!

        Order order = em.find(Order.class, orderId);

        return order;
    }


//==================================================================================================================


    //[ '주문 리포지토리 개발'강. 01:00~ ]. 코드 pdf p63
    //< 전체 주문 조회 >
    public List<Order> findAll(OrderSearch orderSearch){
        return em.createQuery("select o from Order o join o.member m" + " WHERE o.status = :status "
                + " and m.name like :name ", Order.class)
        //1.'select o from Order o': 'Oder 엔티티의 모든 필드들'과
        //2.'join o.member m': Order 객체와 연관관계 매핑되어 있는 Member객체를 서로 '내부 조인'시킨다.
                .setParameter("status", orderSearch.getOrderStatus())
                .setParameter("name", orderSearch.getMemberName())
                .setMaxResults(1000) //페이징을 하는데, 최대 1000건만 가져오는 것으로 제한 걸기.
                .getResultList();
    }





//==================================================================================================================


}
