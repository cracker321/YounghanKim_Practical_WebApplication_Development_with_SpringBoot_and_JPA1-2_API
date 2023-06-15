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

        //- '각 주문(Order)'에 대한 정보와, 그 해당 주문의 '회원(Member) 정보'와 '배송(Delivery) 정보'가 포함된
        //  'List<Order> 객체'가 반환된다!
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
        //- JPA에서 '엔티티 객체 간의 연관 관계를 조회할 때', 지연로딩이 아닌 즉시로딩으로 한 번에 데이터를 함께 조회하는 방법임.
        //- 'join fetch'를 통해 '객체 그래프'와 'select 조회 데이터'를 한 방에 동시에 같이 가져오는 것임!
        //   fetch join'을 아주 적극적으로 활용해야 함!
        //- 여기서 'fetch join'을 사용하였기에 'order -> member'와 'order -> delivery'는 '이미 조회 완료'된 상태이므로,
        //  '주문 엔티티 Order 객체' 내부의 '필드 @ManyToOne(fetch=LAZY) Member'와 '필드 @OneToOne(fetch=LAZY)의
        //  'LAZY'는 이제 무시되고, 따라서 당연히 지연로딩은 없다!

        //-'메소드 createQuery'는 '첫 번째 매개변수'로 'JPQL 쿼리를 사용하여 조회할 대상 및 연관관계를 정의'하고
        // '두 번재 매개변수'로 'JPQL 쿼리의 반환 결과를 어떤 객체 타입으로 반환할지를 지정함'.

        return em.createQuery(
                //'주문 : 회원', '주문 : 배송' 관계는 '~ToOne' 관계이기 때문에, 아래 쿼리 한 방으로 바로 데이터 빠르게 조회 가능함.
                "select o from Order o" +
                        " join fetch o.member m" +  //@ManyToOne: '주문 Order 객체(N. 주인)'와 '회원 Member 객체(1)'
                                                    //'주문 Order 객체 내부의 필드 @ManyToOne(fetch=LAZY) Member'와 조인
                        " join fetch o.delivery d", Order.class //@OneToOne: '주문 Order 객체(1. 주 테이블)와 '배송 Delivery 객체(1)'
                                                                //           여기까지는 쿼리 한 방에 데이터들 다 가져올 수 있음.

        ).getResultList();
    }


    //==================================================================================================================


    //[ '주문 조회 V3: 엔티티를 DTO로 변환 - 페치 조인 최적화'강. 00:00~ ]. 실전! 스프링 부트와 JPA 활용2 - API 개발과 성능 최적화. pdf p25~

    //- 'Order 객체(1) : OrderItems 객체(N) 관계'로 인해 JPA의 distinct를 사용해서 '주문 Order 엔티티 객체의 중복을 제거'해주는 것임.
    //  즉, '일대다' 관계일 대문 JPA의 distinct를 사용해주는 것이다!
    //- 중복을 제거하는 'distinct'를 사용!
    //- 단, '일대다 fetch join'의 '치명적 단점': 페이징이 불가능하다!


    //< 컬렉션(리스트, 집합, 맵..)을 fetch join하면, 페이징이 불가능하다 >

    //- 컬렉션을 페치 조인하면 '일대다 조인이 발생'하므로, 데이터(행)가 예측할 수 없이 증가한다.
    //- 일대다에서 일(1)을 기준으로 페이징하는 것이 목적이다. 그런데, 데이터(행)는 다(N)를 기준으로 행(row)가 생성됨.
    //  '일(1)'인 '주문 Order 객체'를 기준으로 페이징을 하고 싶으넫, '다(N)'인 '주문상품 OrderItem 객체'를 JOIN하면
    //  '주문상품 OrderItem 객체'가 '기준이 되어버림'. 
    //  이 경우, 하이버네이트는 경고 로그를 남기고 모든 db를 읽어서 메모리에서 페이징을 시도함. 최악의 경우, 장애로 이어질 수 있음.


    public List<Order> findAllWithItem() {

        return em.createQuery(

                //'주문 : 회원', '주문 : 배송' 관계는 '~ToOne' 관계이기 때문에, 아래 쿼리 한 방으로 바로 데이터 빠르게 조회 가능함.
                "select distinct o from Order o" + //'distinct'를 통해 중복을 제거해서 데이터를 가져옴.
                " join fetch o.member m" + //@ManyToOne: '주문 Order 객체(N. 주인)'와 '회원 Member 객체(1)'
                " join fetch o.delivery d" + //@OneToOne: '주문 Order 객체(1. 주 테이블)와 '배송 Delivery 객체(1)'
                                             //           여기까지는 쿼리 한 방에 데이터들 다 가져올 수 있음.
                 //아래처럼 '@OneToMany' 관계에서 'fetch join'을 사용하면 안된다!
                 //왜냐하면, '주문 : 주문상품' 관계가 일대다 관계이기 때문에, 쿼리 결과에서 데이터의 행(row) 수가
                 //예측할 수 없이 늘어나기 때문에, 이로 인해 페이징이 제대로 작동하지 않게 된다.
                " join fetch o.orderItems oi" + //@OneToMany: '주문 Order 객체(1. 주인)'와 '주문 OrderItems 객체(N)'
                " join fetch oi.item i", Order.class
                )
                //.setFirstResult(1) //페이징 작성할 때 이렇게 작성하면 절대 안됨. 아래 v3.1 방법으로 작성해야 함.
                //.setMaxResults(100) //페이징 작성할 때 이렇게 작성하면 절대 안됨. 아래 v3.1 방법으로 작성해야 함.
                .getResultList();
    }



    //==========================================================================================================


    //[ '주문 조회 V3.1: 엔티티를 DTO로 변환 - 페이징과 한계 돌파'강. 00:00~ ]. 실전! 스프링 부트와 JPA 활용2 - API 개발과 성능 최적화.
    //pdf p26~

    //- '주문 : 회원', '주문 : 배송' 관계는 '~ToOne 관계'이기 때문에, 쿼리 한 방으로 바로 데이터 빠르게 조회 가능함.
    //- 또한, '~ToOne 관계'는 아무리 많이 fetch join를 해도 정상적으로 페이징이 작동한다.
    //  '~ToOne 관계'는 fetch join 해도 페이징에 영향을 주지 않음. 따라서, '~ToOne 관계'는 fetch join으로 쿼리 수를 줄여 해결하고,
    //  문제가 되는 '~ToMany 관계'에서의 페이징은
    //  yml파일에서 'hibernate.default_batch_fetch_size = 1000'을 추가하여 해결한다.
    //  (그냥 맥시멈 데이터 사이즈인 1000으로 하는 게 좋음)
    //- v3에서의 컬렉션 fetch join은 페이징이 불가능하지만, v3.1의 이 방법은 페이징이 가능하다.

    //< 컬렉션(리스트, 집합, 맵..)을 fetch join하면, 페이징이 불가능하다 >

    //- 컬렉션을 페치 조인하면 '일대다 조인이 발생'하므로, 데이터(행)가 예측할 수 없이 증가한다.
    //- 일대다에서 일(1)을 기준으로 페이징하는 것이 목적이다. 그런데, 데이터(행)는 다(N)를 기준으로 행(row)가 생성됨.
    //  '일(1)'인 '주문 Order 객체'를 기준으로 페이징을 하고 싶은데, '다(N)'인 '주문상품 OrderItem 객체'를 JOIN하면
    //  '주문상품 OrderItem 객체'가 '기준이 되어버림'.
    //  이 경우, 하이버네이트는 경고 로그를 남기고 모든 db를 읽어서 메모리에서 페이징을 시도함. 최악의 경우, 장애로 이어질 수 있음.


    //< 페이징의 한계 돌파 >: 페이징 + 컬렉션(리스트, 집합, 맵..) 엔티티를 함께 조회하는 방법

    //- 일대일(OneToOne), 다대일(ManyToOne) 관계는 모두 쿼리에 fetch join을 넣어준다!
    //  (아래 쿼리에서 이 부분: " join fetch o.member m" +  " join fetch o.delivery d")
    //  ~ToOne 관계는 데이터(행) 수를 증가시키지 않으므로, 페이징 쿼리에 영향을 주지 않음.
    //- *****중요*****
    //  그러나, '주문(1. 주인) : 주문상품(N)'의 관계와 같은 '~ToMany'관계에 'fetch join'을 넣어서 조회하면,
    //  이렇게 컬렉션을 fetch join하면, 데이터(행) 수가 증가하므로, 일대다에서 일(1)을 기준으로 페이징하는 것이 어려움.
    //  따라서, 그 대신 컬렉션은 지연로딩으로 설정한다.
    //  (지연로딩은 주문 데이터를 조회할 때, 주문 상품 데이터를 즉시 가져오지 않고, 실제로 접근할 때 가져오는 방식)
    //  이렇게 함으로써 데이터의 증가나 성능 저하를 방지할 수 있음.
    //- 성능 최적화를 위해 yml파일에 hibernate.default_batch_fetch_size 및 @BatchSize를 사용함.
    //- 컬렉션(일대다 또는 다대다 관계 및 리스트, 집합, 맵 등)은 지연로딩을 조회한다.
    //  여기서는 하나의 주문(Order)을 기준으로 여러 개의 주문상품(OrderItem)이 컬렉션 형태로 연결되어 있음.
    //- 지연로딩 성능 최적화를 위해 hibernate.default_batch_fetch_size 및 @BatchSize를 적용한다.
    //  'hibernate.default_batch_fetch_size': 글로벌 설정
    //  '@BatchSize': 개별 최적화
    //  이 옵션을 사용하면, 컬렉션이나 프록시 객체를 한꺼번에 설정한 size만큼 IN 쿼리로 조회한다.
    public List<Order> findAllWithMemberDelivery(int offset, int limit){

        return em.createQuery(
                "select o from Order o" +
                        " join fetch o.member m" +
                        " join fetch o.delivery d", Order.class
                //여기까지만 딱 작성해줘도, 마치 v3에서의 아래 쿼리 부분이 정상적으로 작동되고,
                //원래라면 v3에서 발생해야 할 데이터 행(row)의 기하급수적 증가 이슈가 발생하지 않게 된다!
                //( " join fetch o.orderitem oi" +
                //" join fetch oi.item i " )
        ).setFirstResult(offset)
         .setMaxResults(limit)
         .getResultList();
    }






    //==========================================================================================================



    //< JOIN ~ ON ~ >. SQL쿼리

    //'Do It! 오라클 p234'
    //- 'SQL에서 컬럼(열)'은 '자바 엔티티 객체에서의 필드(속성)'과 동일한 것이다!

    /*
    SELECT *  //아래 '테이블 order'에 있는 '모든 컬럼(열)'과 그 모든 컬럼에 줄줄이 달려 있는 '데이터(행)' 중 아래 조건에 맞는 데이터를 가져옴.
    FROM orders o JOIN order_item oi  //'테이블 order'와 '테이블 order_item'을 결합(JOIN)함.
    ON order_id = oi.order_id
    WHERE o.order_id = 4  //'주문 아이디(=컬럼) orderId'가 '4(=데이터(행))'인 '주문 데이터'와,
                          //그 주문과 관련된 '테이블 order에 있는 모든 컬럼(열)'에 해당하는 데이터를 가져오는 것.

    - 'SELECT *': 결과로 얻고자 하는 컬럼(열)을 선택하는 작업. '*'을 사용하여, 모든 열을 선택해서 가져오도록함.
                  최종 결과로 얻을 테이블에 관련 테이블의 모든 열이 포함될 것임.
    - 'FROM orders o': '테이블 order'에 있는 데이터(행)에 한해 아래 조건에 맞는 데이터(행)들를 조회해서 가져온다.
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

    /*
    [ distinct 사용 ]
    :[ '주문 조회 V3: 엔티티를 DTO로 변환 - 페치 조인 최적화'강. 00:00~ ]. 실전! 스프링 부트와 JPA 활용2 - API 개발과 성능 최적화. pdf p25~
    이 강의 관련 내용임.

    - SELECT문의 대상 데이터(행)들에서 '중복된 값을 가진 데이터(행)'를 제거함.
      즉, 중복된 결과를 제외한 유일한 값들을 반환함.
    - 일대다 조인이 발생하는 경우:
      일대다 조인에 의해 db의 데이터(행)가 증가할 수 있ㅇ므.
      엔티티 객체의 조회 수가 증가할 수 있음.

    cf) JPA의 distinct: 엔티티 객체 레벨에서 중복 제거
        SQL의 distinct: 쿼리 결과 데이터(행)에 대해 중복 제거


    < JPA의 distinct >

    - '엔티티 객체의 중복을 제거'하기 위해 사용됨.
      중복된 엔티티 객체를 애플리케이션에서 제거하여 유일한 결과를 얻을 수 있음.
    - 'fetch join 시 중복 처리'
      : JPA에서 일대다 관계와 연관된 테이블 간의 JOIN 연산 시, 특히 fetch join을 사용할 때,
        결과 격체 수가 증가하는 경우가 있음. 이 경우, 객체 간의 중복 결과를 제거하기 위해 JPA의 distinct를 사용함.
        주로 fetch join을 사용할 때, 각 엔티티 객체에 대해 중복을 줄이기 위해 사용됨.
        일대다 또는 다대다 관계에서 발생할 수 있는 중복 결과를 애플리케이션에서 필터링해줌.
    - JPA의 distinct는 일단 기본적으로 SQL에 distinct를 추가해주고 시작함. 더해서 여기 위와 같은'엔티티 객체의 중복 처리'를 해줌.
    -


    < SQL의 distinct >

    - '행(데이터) 수준에서 중복 제거'
      : 데이터 조회 결과에서 '중복된 행(데이터)'을 제거하기 위해 사용됨.
      중복된 값을 가진 행을 db에서 제거하여 유일한 결과를 얻을 수 있음.
      같은 값이라도 컬럼의 다른 값들이 다르면 중복으로 간주되지 않고 유지됨.


    < 예시 데이터 >

    Member 테이블 데이터:
    회원 A: ID = 1, 이름 = "Member A"
    회원 B: ID = 2, 이름 = "Member B"


    Delivery 테이블 데이터:
    배송 정보 1: ID = 1, 정보 = "Delivery 1"
    배송 정보 2: ID = 2, 정보 = "Delivery 2"


    Item 테이블 데이터:
    상품 1: ID = 1, 이름 = "Item 1", 가격 = 10
    상품 2: ID = 2, 이름 = "Item 2", 가격 = 20
    상품 3: ID = 3, 이름 = "Item 3", 가격 = 30


    Order 테이블 데이터:
    주문 1: ID = 1, 회원 ID = 1, 배송 ID = 1, 날짜 = "2023-01-01"
    주문 2: ID = 2, 회원 ID = 2, 배송 ID = 2, 날짜 = "2023-01-02"


    OrderItem 테이블 데이터:
    주문 상품 1: ID = 1, 주문 ID = 1, 상품 ID = 1, 수량 = 1
    주문 상품 2: ID = 2, 주문 ID = 1, 상품 ID = 2, 수량 = 1
    주문 상품 3: ID = 3, 주문 ID = 2, 상품 ID = 1, 수량 = 1
    주문 상품 4: ID = 4, 주문 ID = 2, 상품 ID = 3, 수량 = 2


    < 적용할 쿼리 >

    "select distinct o from Order o" + //'distinct'를 통해 중복을 제거해서 데이터를 가져옴.
                " join fetch o.member m" + //@ManyToOne
                " join fetch o.delivery d" + //@OneToOne
                " join fetch o.orderItems oi" +
                " join fetch oi.item i", Order.class
                )


    < distinct를 사용하지 않았을 경우 쿼리로 가져온 데이터 >

    주문 1: 회원 A, 배송 정보 1, 주문 상품 1, 상품 1
    주문 1: 회원 A, 배송 정보 1, 주문 상품 2, 상품 2
    주문 2: 회원 B, 배송 정보 2, 주문 상품 3, 상품 1
    주문 2: 회원 B, 배송 정보 2, 주문 상품 4, 상품 3

    : 위 결과를 보면 중복된 주문 데이터가 포함되어 있음을 확인할 수 있습니다. 이 경우, "distinct"를 사용하여 중복된 주문 데이터를 제거하겠습니다.


    < distinct를 사용할 경우 쿼리로 가져온 데이터 >

    주문 1: 회원 A, 배송 정보 1, 주문 상품 1 & 2, 상품 1 & 2
    주문 2: 회원 B, 배송 정보 2, 주문 상품 3 & 4, 상품 1 & 3

    : 이제 결과에 각 주문 상품 및 상품 정보가 포함되어 있으며, 주문 데이터의 중복이 제거된 것을 확인할 수 있습니다.
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
