package jpabook.jpashop.repository;


import jpabook.jpashop.domain.Order;
import jpabook.jpashop.repository.order.samplequery.OrderSampleQueryDto;
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
    public void save(Order order) {
        em.persist(order);
    }


//==================================================================================================================


    //< '개별 주문(1건)을 DB에서 '해당 주문의 id값'으로 조회'하기 >

    //'클라이언트로부터 매개변수로 들어온 id에 해당하는 1개의 주문'을 DB에서 찾아와서 '그 주문을 리턴'해줌
    public Order findOne(Long orderId) { //'여기서의 매개변수 orderId'는 그냥 여기 메소드에서만 통용되는 것에 불과하고,
                                         //중요한 것은, '레펏 OrderRepository의 메소드 findOne을 호출할 때는',
                                         //반드시 '그 매개변수로 Long 타입'을 넣어주어야 하는 것이다!

        Order order = em.find(Order.class, orderId);

        return order;
    }


//==================================================================================================================


    //[ '주문 리포지토리 개발'강. 01:00~ ]. 코드 pdf p63

    //< 전체 주문 조회 >
    public List<Order> findAll(OrderSearch orderSearch) {
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


    //[ JPQL 강의 부분임 ].

    //< N+1 문제를 해결하지 못한 JPQL 쿼리문 >

    public List<Order> findAllByString(OrderSearch orderSearch) {

        //language=JPAQL
        String jpql = "select o From Order o join o.member m";
        boolean isFirstCondition = true;

        //'주문 상태 검색'
        if (orderSearch.getOrderStatus() != null) {
            if (isFirstCondition) {
                jpql += " where";
                isFirstCondition = false;
            } else {
                jpql += " and";
            }
            jpql += " o.status = :status";
        }


        //'회원 이름 검색'
        if (StringUtils.hasText(orderSearch.getMemberName())) {
            if (isFirstCondition) {
                jpql += " where";
                isFirstCondition = false;
            } else {
                jpql += " and";
            }
            jpql += " m.name like :name";
        }
        TypedQuery<Order> query = em.createQuery(jpql, Order.class)
                .setMaxResults(1000); //􀭭􀘀 1000􀑤
        if (orderSearch.getOrderStatus() != null) {
            query = query.setParameter("status", orderSearch.getOrderStatus());
        }
        if (StringUtils.hasText(orderSearch.getMemberName())) {
            query = query.setParameter("name", orderSearch.getMemberName());
        }
        return query.getResultList();
    }


//==================================================================================================================


    //[ '간단한 주문 조회 V3: 엔티티를 DTO로 변환 - 페치 조인 최적화'강. 01:10~ ]. 실전! 스프링 부트와 JPA 활용2 - API 개발과 성능 최적화

    //< N+1 문제를 해결한 JPQL 쿼리문 >
    public List<Order> findAllWithMemberDelivery() {

        //- 아래 쿼리 한 번으로 '주문 Order 객체', '회원 Member 객체', '배송 Delivery 객체'를 각각 조회하는 대신
        //  '한 번의 쿼리로 조회'가 가능해짐. 성능 향상.
        //  결과적으로, 아래 한 번의 쿼리를 통해 '주문', '회원', '배송' 데이터를 '함께 조회(가져옴)'한 후,
        //  '각 정보를 모두 포함하는 List<Order> 객체'를 반환함. 이렇게 함으로써 N+1과 같은 성능 저하 이슈 방지 가능.
        //- 'em.createQuery': 이것을 선언해줌으로써, 'JPQL 쿼리'를 생성해주는 것이 가능함.
        //- 'select o from Order o': '주문 엔티티 Order 객체'를 '조회'하기 위한 쿼리.
        //                          '객체'를 가져오는 것이므로, '별칭(alias) o'를 사용하여, '별칭 o'에 '쿼리로 가져온 결과를 저장'함.
        //- ' join fetch o.member m': '주문 엔티티 Order 객체'와 '회원 엔티티 Member 객체'를 '조인'하고,
        //                            그리고 '회원 엔티티 Member 객체의 정보'를 '가져오기'위한 '조인(fetch join)'임.
        //- ' join fetch o.delivery d': '주문 엔티티 Order 객체'와 '배송 엔티티 Delivery 객체'를 '조인'하고,
        //                              그리고, '배송 엔티티 Delivery 객체의 정보'를 '가져오기'위한 '조인(fetch join)'임.
        //- '.getResultList()': 'em.createQuery(...)'가 다 실행되고 나면, 그 결과로 반환된 TypedQuery 객체에 대해
        //                      '.getResultList()'를 호출하여 결과를 반환함.
        //                      '메소드 getResult()'는 '쿼리를 실행한 후 결과를 List<Order> 형태로 반환'함.
        //- 'Order.class': '메소드 createQuery의 두 번째 파라미터'로, JPQL 쿼리를 통해 반환되는 결과를 어떤 객체 타입'으로
        //                  반환할지 지정하는 역할임.

        //< 'join fetch >
        //*****중요중요!!*** 아주아주 자주 사용함! 100% 이해해야 함! N+1 문제를 해결하는 방법임.
        //- 'join fetch'를 통해 '객체 그래프'와 'select 조회 데이터'를 한 방에 동시에 같이 가져오는 것임!
        //   fetch join'을 아주 적극적으로 활용해야 함!
        //- 여기서 'fetch join'을 사용하였기에 'order -> member'와 'order -> delivery'는 '이미 조회 완료'된 상태이므로,
        //  '주문 엔티티 Order 객체' 내부의 '필드 @ManyToOne(fetch=LAZY) Member'와 '필드 @OneToOne(fetch=LAZY)의
        //  'LAZY'는 이제 무시되고, 따라서 당연히 지연로딩은 없다!

        //-'메소드 createQuery'는 '첫 번째 매개변수'로 'JPQL 쿼리를 사용하여 조회할 대상 및 연관관계를 정의'하고
        // '두 번재 매개변수'로 'JPQL 쿼리의 반환 결과를 어떤 객체 타입으로 반환할지를 지정함'.

        return em.createQuery(
                "select o from Order o" +
                        " join fetch o.member m" + //'주문 Order 객체 내부의 필드 @ManyToOne(fetch=LAZY) Member'와 조인
                        " join fetch o.delivery d", Order.class
        ).getResultList();
    }


//==================================================================================================================


    //[ '주문 조회 V3: 엔티티를 DTO로 변환 - 페치 조인 최적화'강. 00:00~ ]. 실전! 스프링 부트와 JPA 활용2 - API 개발과 성능 최적화. pdf p25~


    public List<Order> findAllWithItem() {

        return em.createQuery("select o from Order o" +
                " join fetch o.member m" + //@ManyToOne
                " join fetch o.delivery d" + //@OneToOne
                " join fetch o.orderItems oi" +
                " join fetch oi.item i", Order.class
                ).getResultList();
    }



    //==========================================================================================================

    //SQL쿼리
    //'Do It! 오라클 p234'
    //- 'SQL에서 컬럼(열)'은 '자바 엔티티 객체에서의 필드(속성)'과 동일한 것이다!

    /*
    SELECT *
    FROM orders o
    JOIN order_item oi
    ON order_id = oi.order_id
    WHERE o.order_id = 4

    - 'SELECT *': 결과로 얻고자 하는 컬럼(열)을 선택하는 작업. '*'을 사용하여, 모든 열을 선택해서 가져오도록함.
                  최종 결과로 얻을 테이블에 관련 테이블의 모든 열이 포함될 것임.
    - 'FROM orders o': '테이블 order'에 있는 데이터에 한해 데이터를 조회해서 가져온다.
    - 'FROM orders o JOIN order_item oi': '테이블 order'와 '테이블 order_item'을 '결합(JOIN)'한다.
    - 'ON o.order_id = oi.order_id': '테이블 order의 컬럼(열) order_id에 줄줄이 달린 수많은 데이터들(행)'과
                                     '테이블 order_item의 컬럼(열) order_id에 줄줄이 달린 수많은 데이터들(행)'들 중에
                                     '서로의 값이 같은 행'을 '결합 및 조회해서 가져와'라는 뜻임.
    - 'WHERE o.order_id = 4': 그런데, '서로의 값이 같은 행을 결합 및 조회해서 그것을 모두 가져오는 것이 아니'라,
                              '테이블 order의 컬럼(열) order_id에 줄줄이 달린 수많은 데이터들(행) 중에서 그 order_id의 값이 4인 행'만
                              조회해서 가져와!
                              그리고, 당연히 '*'를 입력했으니, 그 데이터를 가지고 있는 '테이블 order의 모든 컬럼(열)'을 가져와!


     */


    //==========================================================================================================

//    //'레퍼지토리'와 '컨트롤러'를 역으로 참조해서 서로 '의존관계'가 '절대 생겨서는 안된다!!'
//    //(즉, '컨 -> 서 -> 리'처럼 순방향으로 해야 하는데, '리 -> 서 -> 컨' 처럼 역방향으로 참조하는 것은 안됨)
//
//
//    //[ '간단한 주문 조회 V4: JPA에서 DTO로 바로 조회'강. 02:00~ ]. 실전! 스프링 부트와 JPA 활용2 - API 개발과 성능 최적화.'pdf p20'
//
//    //*****중요*****
      //- '클래스 OrderSampleQueryRepository'로 아래 내용들 다 옮김.
      //- 옮긴 이유: [ '간단한 주문 조회 V4: JPA에서 DTO로 바로 조회'강. 18:30~ ]

//    //- 권장하지 않는 방법임(권장하는 방법은 v2와 v3(v2에서 성능이슈 발생할 경우 v3 사용)
//    //- v2에서 발생한 N+1 성능 이슈를 v3의 'fetch join'으로도 해결하지 못할 경우, 여기의 v4를 사용하는 것이다.
//    //  사실, v3를 통해 대부분(95% 이상)의 N+1 성능 이슈는 해결 가능함.
//    //- 레퍼지토리의 재사용성이 떨어짐. 다른 API에서는 '메소드 findOrderDtos'를 사용할 수 없음.
//    //  오직 '컨트롤러의 메소드 ordersV4()'에서만 이 '메소드 findOrderDtos'를 사용할 수 있게 딱 맞게 설계됨. 즉, 유연성이 아예 없음.
//
//    //- 일반적인 SQL을 사용할 때처럼 원하는 값을 선택해서 조회
//    //- new 명령어를 사용해서 JPQL의 결과를 DTO로 즉시 변환
//    //- SELECT절에서 원하는 데이터를 직접 선택하므로 DB에서 애플리케이션 네트웍 용량 최적화(그러나, 그 최적화는 생각보다 미미함)
//    //- 레포지터리 재사용성이 떨어짐. API스펙에 맞춘 코드가 레퍼지터리에 들어가는 단점.
//
//    public List<OrderSampleQueryDto> findOrderDtos(){ //DB로부터 QueryDto를 반환하게 할 것임.
//
//        return em.createQuery("select new jpabook.jpashop.repository.order.samplequery.OrderSampleQueryDto(o.id, m.name," +
//                "o.orderDate, o.status, d.address)" +
//                " from Order o" +
//                " join o.member m" +
//                " join o.delivery d", OrderSampleQueryDto.class //DB로부터 'OrderSampleQueryDto 객체' 타입으로 데이터를 가져옴.
//        ).getResultList();
//    }

    //==========================================================================================================





}
