package jpabook.jpashop.domain;


import jpabook.jpashop.domain.Item.Item;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static javax.persistence.FetchType.LAZY;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Setter
@Table(name = "ORDERS") //'실제 DB의 테이블명은 ORDERS'이기 때문.
                        //왜 '테이블명을 ORDER'로 안 했냐면, '자바 예약어 Order'가 이미 존재하기 때문에,
                        //따라서, 아래 'Order 객체'를 그 '실제 DB의 테이블 ORDERS'와 매핑시켜줘야 한다!
@Entity
public class Order {


    @Id
    @GeneratedValue
    @Column(name = "ORDER_ID") //'테이블 ORDER의 PK 컬럼명'은 'ORDER_ID'이기 떄문에,
                               //여기 자바 객체에서도 반드시 '컬럼 id'가 아니라, '컬럼 ORDER_ID'와 매핑시켜야함!
                               //대소문자는 상관없음. "order_id"로 해도 상관없음.
    private Long id;


    @ManyToOne(fetch = LAZY) //*****중요*****
                             // 교재 p296~ 확인. '@ManyToOne', '@OneToOne'은 기본설정이 '즉시로딩 EAGER'이므로,
                             //반드시 '지연로딩 LAZY'로 설정 바꿔줘야 한다!
                             //(1) @ManyToOne이 붙은 모든 곳
                             //(2) @OneToOne이 붙은 모든 곳
                             //   1) public class User{
                             //             ...
                             //         @OneToOne(fetch=LAZY)  <-- 1:1 양방향 매핑에서 주인 객체 User
                             //         @JoinColumn(name="CART_ID")
                             //         private Cart cart;
                             //              ...
                             //      }
                             //   2) public class Cart{
                             //              ...
                             //         @OneToOne(mappedBy="cart", fetch=LAZY) <- 1:1 양방향 매핑에서 비주인 객체 Cart
                             //         private User user;
                             //              ...
                             //      }

    @JoinColumn(name = "MEMBER_ID") //'주인이 아닌 테이블 MEMBER의 PK 컬럼인 MEMBER_ID'
                                    //= '주인 테이블(현재 테이블) ORDERS의 FK 컬럼인 MEMBER_ID'
    private Member member; //'클래스 Member의 필드 id'를 '참조한 필드'
                           //'한 명의 회원(1)'이 '여러 개의 주문(N. 주인)'을 하는 구조.
/*

다대일 양방향 매핑
@Table(name="ORDERS")
@Entity
public Class Order{

@Id
@GeneratedValue
@Column(name="ORDER_ID") //테이블 Order의 pk 컬럼명은 order_id 임
private Long id;

@ManyToOne //하나의 회원(1)이 여러 개의 주문(N)을 하는 구조
@JoinColumn(name = "MEMBER_ID") //'주인이 아닌 테이블 MEMBER의 PK 컬럼인 MEMBER_ID'
                                //= '주인 테이블(현재 테이블) ORDERS의 FK 컬럼인 MEMBERID'
                                /
private Member member;

}

 */


    //- '주인 객체'는 '저쪽 객체인 OrderItem 객체'임
    //- '주인이 아닌 객체(1. 여기 'Order 객체')'가 '주인 객체(N. 저기 'OrderItem 객체')'를
    //  '종속시켜 관리하기' 때문에, '주인이 아닌 객체(여기 'Order 객체')'의 내부 필드에
    //  'cascade'를 작성하는 것이다!
    //  cf)'주문 서비스 개발'강. 05:25~
    //- 만약, 'Order 객체'가 'OrderItem 객체'를 '종속시켜 관리하는 상황이 아니라면', 즉, 'OrderItem 객체가 되게 중요해서
    //  다른 엔티티 객체들과도 서로 얽히고 많이 연관되어 있어서 다른 엔티티 객체에서도 OrderItem 객체를 가져와서 막 연결시키고
    //  그런 상황이라면', '그럴 때는 여기서 Cascade를 남용해서 사용하면 안된다!
    //  왜냐하면, 'Order 객체 또는 OrderItem 객체와 연관되어 있는 어떤 다른 엔티티 객체가 Order 객체 또는 OrderItem 객체를
    //  삭제하거나 변경시키거나 할 경우', 'Order 객체와 OrderItem 객체는 서로 연관되어 있기에 이로부터 동시에 같이 삭제되거나
    //  변경되는 불상사가 일어나기 때문이다!'
    //  그냥, 딱 '현재의 Order 객체와 OrderItem 객체 간 관계' 정도에서나 쓰는 것임.
    //- 따라서, '만약 '서비스 OrderService 등'에서 'Order 객체를 영속화 persist 시킨다면'',
    //  '그와 동시에 OrderItem 객체도 같이 자동으로 영속화됨!'
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
    private List<OrderItem> orderItems = new ArrayList<>();


    //'주인 객체'는 '현재 객체인 Order 객체'임
    @OneToOne(fetch = LAZY, cascade = CascadeType.PERSIST)
    //- '@ManyToOne', '@OneToOne'은 기본설정이 '즉시로딩 EAGER'이므로, 반드시 '지연로딩 LAZY'로 설정 바꿔줘야 한다!
    //- 'cascade = CascadeType.PERSIST':
    //  '1:1 양방향 매핑'에서는 꼭 반드시 '주인이 아닌 객체('Delivery 객체')의 내부 필드'에 'cascade'를 쓰는 것은 아니고,
    //  (여기서는 'Order 객체 = 주인 객체', 'Delivery 객체 = 주인이 아닌 객체')
    //  '더 상위 객체(하위 객체를 종속시켜 관리하는 객체)'에 Cascade를 붙이는 것이다!
    //  여기 'Order 객체와 Delivery 객체 간의 관계에서,
    //  '주문 Order'가 발생해야, 그에 이어져서 '배송 Delivery'라는 사건이 발생하므로,
    //  'Order 객체'가 'Delivery 객체'를 종속하고 있고, 그에 따라, 'Order 객체 내부의 필드'에 'Cascade를 붙은 것'이다!
    //  cf)'주문 서비스 개발'강. 05:25~
    //- 만약, 'Order 객체'가 'Delivery 객체'를 '종속시켜 관리하는 상황이 아니라면', 즉, 'Delivery 객체가 되게 중요해서
    //  다른 엔티티 객체들과도 서로 얽히고 많이 연관되어 있어서 다른 엔티티 객체에서도 Delivery 객체를 가져와서 막 연결시키고
    //  그런 상황이라면', '그럴 때는 여기서 Cascade를 남용해서 사용하면 안된다!
    //  왜냐하면, 'Order 객체 또는 Delivery 객체와 연관되어 있는 어떤 다른 엔티티 객체가 Order 객체 또는 Delivery 객체를
    //  삭제하거나 변경시키거나 할 경우', 'Order 객체와 Delivery 객체는 서로 연관되어 있기에 이로부터 동시에 같이 삭제되거나
    //  변경되는 불상사가 일어나기 때문이다!'
    //  그냥, 딱 '현재의 Order 객체와 Delivery 객체 간 관계' 정도에서나 쓰는 것임.
    //- 따라서, '만약 '서비스 OrderService 등'에서 'Order 객체를 영속화 persist 시킨다면'',
    //  '그와 동시에 Delivery 객체도 같이 자동으로 영속화됨!'
    @JoinColumn(name = "DELIEVERY_ID") //'주인이 아닌 테이블 DELIEVERY의 PK 컬럼인 DELIVERY_ID'
                                       //= '주인 테이블(현재 테이블) ORDERS의 FK 컬럼인 DELIVERY_ID'
    private Delivery delivery; //'클래스 Delivery의 필드 id'를 '참조한 필드'

    private LocalDateTime orderDate; //

    @Enumerated(EnumType.STRING)
    private OrderStatus status;


//=============================================================================================================


    //[ '엔티티 설계시 주의점'강. 22:50~ ]. 더 확인하기!

    //< 연관관계 편의 메소드1 > : '양방향 매핑'일 때 사용하는 것!
    //'주인인 Order 객체(N)'와 '주인이 아닌 Member 객체(1)' 연관관계에서 '주인인 Order 객체'의 입장
    public void changeMember(Member m){

        //여기 'if문 로직'은 '들어온 매개변수 m'과는 무관함
        if(this.member != null){
                this.member.getOrders().remove(this);
        }
        this.member = m;

        m.getOrders().add(this);

    }


//=============================================================================================================


    //< 연관관계 편의 메소드2 >
    //'Order 객체(1)'와 'OrderItem 객체(N)' 연관관계에서 '주인이 아닌 Order 객체'의 입장
    public void addOrderItem(OrderItem oi){

        orderItems.add(oi); //기존의 '주문상품 리스트 OrderItems 객체'에 '새롭게 하나의 주문상품 OrderItem 객체'를 추가함.
        oi.setOrder(this); //'OrderItem 객체의 속성'으로 'Order 객체 this..이거 더 확인하기!'를 넣어줌.
    }


//=============================================================================================================


//    //< 연관관계 편의 메소드3 >
//    //'주인인 Order 객체(1)'와 '주인이 아닌 Delivery 객체(1)' 연관관계에서 '주인인 Order 객체'의 입장
//    public void changeDelivery(Delivery d){
//
//        if(this.delivery != null){
//            this.delivery.getOrder().remove(this);
//        }
//        this.delivery = d;
//
//        d.getOrder().add(this);
//
//    }


//=============================================================================================================


    //[ '주문, 주문상품 엔티티 개발'강. 02:10~ ]

    //< '신규 주문 생성'하는 '팩토리 메소드' >: '팩토리 메소드 패턴'이다!!!
    //('클래스 OrderItem' 내부에 '팩토리 메소드' 설명 길게 해놨음)


    //< '신규 주문 생성' 메소드. 상세설명파트 11:10~ >

    //- '사용자 생성자'와 정확히 동일한 역할을 함.
    //  '생성자를 사용하지 않고' 이렇게 '생성하는 메소드를 따로 쓴 이유'는,
    //  '생성자와 달리 내 임의로 내가 원하는 이름을 줄 수 있기 때문'임.
    //  실제로 이 경우처럼 '명확함'을 위해 '생성자 역할을 하지만, 그 고유한 이름을 가지는 메소드'를 만들어 '객체를 생성하는 역할'을
    //  하는 경우도 많이 있다!

    //'주문된 상품 을 생성하려면', '주문한 회원 정보 Member', '주문된 상품의 배송 정보 Delivery',
    //'주문한 상품들 OrderItem...'의 정보가 모두 필요하다!
    public static Order createOrder(Member member, Delivery delivery, OrderItem... orderItems){ //'... 문법'!
        //- '...': '클래스 InitDb 내부의 Order.createOrder(member, delivery, orderItem1, orderItem2);'를 참고하기

        Order order = new Order(); //- '엔티티 Order'를 사용하기 위해, 여기서 'Order 객체를 생성해줌'.
                                   //  여기서 '엔티티 Order 객체를 만들지 않으면', 아래에서 'Order 객체를 사용할 수 없게 됨'.
                                   //- 내 개인적인 생각이긴 한데, 만약 아래 리턴값이 order여야만 하지 않았다면,
                                   //  'Order 객체'를 만드는 대신, 'this'를 사용해도 괜찮은 것 아닌가..?
        order.setMember(member); //'현재 Order 객체에 (주문한)특정 회원 정보(속성)을 추가함'.
        order.setDelivery(delivery); //'현재 Order 객체에 (주문된 상품의)배송 정보(속성)을 추가함'.


        //[ '주문, 주문상품 엔티티 개발'강. 03:30~ ]
        //'신규 주문으로 들어온 주문상품 OrderItem 객체'를  '주문 Order 객체'에 '집어넣음'.
        for(OrderItem orderItem : orderItems){
            order.addOrderItem(orderItem);
        }
        order.setStatus(OrderStatus.ORDER); //일단은 '주문상태 OrderStatus의 최초설정'을 'ORDER'로 '강제 설정'함
        order.setOrderDate(LocalDateTime.now()); //일단은 '주문시각 LocalDateTime의 최초설정'을 '현재시간 now'로 '강제 설정'함

        return order;
    }


//=============================================================================================================


    // pdf p57 상단 참조
    //# 아래처럼 '엔티티 객체 내부에 비즈니스로직을 넣는 패턴'을 '도메인 모델 패턴'이라고 함.
    //  반면, '서비스 계층 내부에서 비즈니스로직을 처리하는 패턴'은 '트랜잭션 스크립트 패턴'이라고 함.
    //# 'JPA에서는 도메인 모델 패턴을 많이 활용'하고, 'MyBatis처럼 SQL 쿼리를 직접 사용하는 방법에서는 트랜잭션 스크립트 패턴'을
    //  많이 활용한다!

    //============= 비즈니스로직 =============('=' 13개)

    //[ '주문, 주문상품 엔티티 개발'강. 04:55~ ]

    //< 기존주문 취소 >. '주문, 주문상품 엔티티 개발'강. 05:15~. '서비스 OrderService'에서 사용됨.
    public void cancel(){
        if(delivery.getStatus() == DeliveryStatus.COMP){ //'배송이 이미 예전에 다 완료되어서 주문취소가 불가능한 경우'라면
            throw new IllegalStateException("이미 배송 완료된 상품은 주문취소가 불가능합니다잉!!");
        }

        this.setStatus(OrderStatus.CANCEL); //'취소되어야 하는 주문의 상태'를 이제 'CANCEL'로 바꿈


        for(OrderItem orderItem : this.orderItems){ //- '기존주문이 취소되었기 때문'에, '기존재고를 원래대로 복구시킴(+1 시킴)'.
                                                    //- 여기서 'this.orderItems' = 'orderItems' 임.
                                                    //- 'for문에 돌려지는 객체는 당연히 리스트 등 여러 개가 속해 있는 컬렉션이다!'
            //< 주문상품에 대한 주문취소. 07:00~ >
            orderItem.cancel(); //고객이 '하나의 건 주문할 때('Order 객체' 호출)', 그 하나의 건 주문 안에는 'N개의
                                //주문상품 OrderItem'이 있을 수 있기 때문에, for문을 돌려서 그 하나의 건 주문 안에 있는
                                //'N개의 모든 주문상품들 OrderItem 취소(메소드 cancel)시켜야 함'.
        }
    }


//=============================================================================================================


    //============= 조회 로직 =============

    //[ '주문, 주문상품 엔티티 개발'강. 09:00~ ]

    //< 해당 하나의 주문 건에 속한 총 주문상품들의 총 주문금액 합 조회 >
    public int getTotalPrice(){

        int totalPrice = 0;

        for(OrderItem orderItem : orderItems){
            totalPrice += orderItem.getTotalPrice();
        }
        return totalPrice;

        //위 for문을 스트림으로 바꿔 아래처럼 작성할 수 있다!
//       return orderItems.stream()
//               .mapToInt(OrderItem::getTotalPrice)
//               .sum();

    }



//=============================================================================================================


//  [ N:1, 1:1 양방향 매핑 연관관계에서의 '주인' 설정 ]


//  ****중요****
//  JPA 연관관계에서 '주인 객체'는 아래 기준에 따라 설정한다.
//  (1) 더 자주 접근되거나 변경되는 엔티티
//      : 자주 접근하거나 수정하는 엔티티 쪽을 주인으로 설정하는 것이 효율적임.
//        JPA가 변경감지(dirty checking)을 수행할 때, 더 자주 변화가 발생하는 쪽이 주인 엔티티라면, 불필요한 DB 접근을 줄일 수 있음.
//  (2) 비즈니스 로직상 중요한 엔티티
//  (3) 상대방 엔티티 객체의 PK를 내 엔티티 클래스 안에 FK로 소유하고 있는 엔티티



//  *****중요*****
//    < 1:1 양방향 매핑에서 주인 지정하는 방법 > 교재 p215~
//    - 주 테이블(e.g: User <--> UserProfile 이라면 User 객체를 주인으로,
//      Member <--> Locker 이라면 Member 객체를 주인으로 지정)에 상대방 테이블(대상 테이블)의 외래키를 넣기 때문에,
//      주 테이블을 사용하는 자바 엔티티 객체를 주인으로 지정해라!
//    - 즉, 반대편 엔티티 객체를 소유하고 있다고 볼 수 있는 엔티티 객체를 주인으로 지정하는 것이다!!

//  참고사항 cf)
//  N:1 양방향 매핑에서 알 수 있듯이, N:1 에서 주인객체는 주로 N이고, 비주인객체가 1임.
//  보통 비주인객체에 변화가 먼저 발생(e.g: 게시글 Post(1. 비주인)와 댓글 Comment(N. 주인) 간 관계)하기 때문에,
//  1:1 양방향 매핑의 예시로 User 객체, UserProfile 객체 간 관계에서,
//  비주인객체를 User객체로 지정하고, 주인객체를 UserProfile 객체로 지정하는 것도 마찬가지로 자연스럽다고 할 수 있음.



//===================================================================================================




//    [ Cascade ] 교재 p308~
//
//    Cascade는 1:1, N:1 양방향 매핑에서 1인 부분의 엔티티 클래스 내부에 아래 필드 및 어노테이션을 넣는 거임.
//    사실, cascade는 단방향, 양방향 또는 주인 객체, 주인 아닌 객체 여부와 관계 없이
//    두 객체 간의 관계에서, 상대 객체를 나한테 종속시켜버리는 '그 종속시키는 우월한 객체(= 더 먼저 발생하는, 더 우선되는 객체)의
//    클래스 내부'의 필드에 '영속성 전이 Cascade'를 넣는 것이다
//
//    e.g) 주문 Order 객체와 배송 Delivery 객체가 있다고 하면, 양, 단방향, 주인, 비주인 관계 없이
//         먼저 일단 '주문'이라는 사건이 발생해야 하고, 그에 따라 이어져서 '배송'이 발생하는 것이기 때문에,
//         주문 Order 객체가 배송 Delivery 객체를 종속시키고,
//         따라서, '주문 Order 객체 클래스의 내부'에 영속성 전이 Cascade를 넣는 것이다




//    < 1. 양방향 1:1 매핑에서, 1인 부모(상위) 엔티티 클래스(상위 엔티티. 주인, 비주인 무관)의 내부에 아래 필드 및 어노테이션을 넣는 거임 >


//    *****중요*****
//    1:1 양방향 매핑에서 Cascade는 정말 정말 정말로, 주인-비주인 객체 여부와 아~~~무 상관 없다!!!!!
//    먼저 발생되고, 먼저 일어나게 되는 그런 쪽 엔티티 클래스의 내부에 Cascade를 입력하는 것이다!
//    여기 아래 예시에서는, 당연히 User 객체를 먼저 변화시키는 것이 일반적이고, UserProfile 객체는 그 User 객체의
//    변화에 이어져서 나오는 것이기 떼문에, User 엔티티 클래스 내부에 Cascade를 입력한 것이다!!
//    즉, User 객체가 반대편 UserProfile 객체를 '소유'하고 있는 그런 느낌이기 때문에,
//    User 엔티티 객체의 내부에 Cascade를 작성해주는 것이다!!
//    https://www.inflearn.com/questions/15855/cascade-%EC%A7%88%EB%AC%B8%EC%9E%85%EB%8B%88%EB%8B%A4



//    e.g) User(1) : UserProfile(1). 1:1 양방향 매핑. 주인객체: User(1) 객체 & 부모(상위) 엔티티: User(1)
//      - Cascade 기능을 사용하면, 예를 들어 User 엔티티 클래스 내부에 CascadeType.REMOVE를 작성하여 사용하면,
//        사용자 User를 DB에서 삭제시킬 때, 해당 사용자와 연결되어 있는 해당 사용자의 UserProfile 객체도 한 번에 다 DB에서
//        삭제시킬 수 있음.
//        만약, CascadeType.PERSIST 를 작성하여 사용하면,
//        새로운 사용자 User를 DB에 저장시킬 때, 해당 사용자와 연결되어 있는 해당 사용자의 UserProfile 객체도
//        한 번에 다 DB에 저장시킬 수 있음.
//      - 즉, 부모(상위) 엔티티 User(1)에서 발생한 모든 데이터 변경사항이 자식(하위) 엔티티 UserProfile(1)로 전파되어 영향을 미친다는 것임.
//      - CascadeType 중 ALL이 가장 많이 사용되고, 나머지는 자주 사용되지 않음.
//        즉, User 객체의 모든 변경(생성, 수정, 삭제)사항을 DB에 적용시킬 때,
//        그 User 객체와 연결되어 있는 UserProfile 객체에도 동일하게 해당 변경사항이 적용되어 DB에 적용 반영되게 되는 것임.
//        왜냐하면, 부모(상위) 엔티티의 저장(PERSIST), 삭제(REMOVE)를 연관된 자식(하위) 엔티티도 DB에 저장, 삭제하는 경우는 많지만,
//        병합(MERGE), 갱신(REFRESH), 분리(DETACH) 하는 작업은 비즈니스 로직에 따라 다르기 때문임.


//    @Entity
//    public class User {       // 부모(상위) 엔티티 User(1). 주인 객체임.
//        @Id
//        @GeneratedValue(strategy = GenerationType.IDENTITY)
//        private Long id;
//
//        //...
//
//        @OneToOne(cascade = CascadeType.ALL)
//        @JoinColumn(name="profile_id") //여기 주인 User 엔티티가 비주인 객체인 반대편 UserProfile 엔티티 테이블의 PK를 소유학고 있음.
//        private UserProfile userProfile;
//
//
//        //연관관계 매핑에서는 반드시 연관관계 편의 메소드를 작성해준다!!
//        public void setUserProfile(UserProfile userProfile) {
//            if (this.userProfile != null) {
//                this.userProfile.setUser(null);
//            }
//            this.userProfile = userProfile;
//            if (userProfile != null) {
//                userProfile.setUser(this);
//            }
//        }
//
//        // getters and setters...
//    }




//    @Entity
//    public class UserProfile {    // 자식(하위) 엔티티 UserProfile(1). 비주인 객체임.
//        @Id
//        @GeneratedValue(strategy = GenerationType.IDENTITY)
//        private Long id;
//
//
//        //연관관계 매핑에서는 반드시 연관관계 편의 메소드를 작성해준다!!
//        @OneToOne(mappedBy="user", fetch=FetchType.LAZY)
//        private User user;
//
//        public void setUser(User user) {
//            this.user = user;
//        }
//
//        // getters and setters...
//    }







//    < 2. 양방향 N:1 매핑에서, 1인 부모 엔티티 클래스(상위 엔티티. 주로 주인 아닌 객체)의 내부에 아래 필드 및 어노테이션을 넣는 것임 >

//      e.g) Comment(N) : Post(1). N:1 양방향 매핑. 주인객체: Comment 객체 & 부모(상위) 엔티티: Post(1)
//      - Cascade 기능을 사용하면, 예를 들어 Post 엔티티 클래스 내부에 CascadeType.REMOVE를 작성하여 사용하면,
//        게시글 Post를 DB에서 삭제시킬 때, 해당 게시글에 달린 모든 댓글 Comment도 한 번에 다 DB에서 삭제시킬 수 있음.
//        만약, CascadeType.PERSIST 를 작성하여 사용하면,
//        게시글 Post를 DB에 저장시킬 때, 해당 게시글에 달린 모든 댓글 Comment도 한 번에 다 DB에 저장시킬 수 있음.
//      - 즉, 부모(상위) 엔티티 Post(1)에서 발생한 모든 데이터 변경사항이 자식(하위) 엔티티 Comment(N)로 전파되어 영향을 미친다는 것임.
//      - CascadeType 중 ALL이 가장 많이 사용되고, 나머지는 자주 사용되지 않음.
//        왜냐하면, 부모(상위) 엔티티의 저장(PERSIST), 삭제(REMOVE)를 연관된 자식(하위) 엔티티도 DB에 저장, 삭제하는 경우는 많지만,
//        병합(MERGE), 갱신(REFRESH), 분리(DETACH) 하는 작업은 비즈니스 로직에 따라 다르기 때문임.


//        *****중요*****
//        1:1 양방향 매핑에서 Cascade는 정말 정말 정말로, 주인-비주인 객체 여부와 아~~~무 상관 없다!!!!!
//        먼저 발생되고, 먼저 일어나게 되는 그런 쪽 엔티티 클래스의 내부에 Cascade를 입력하는 것이다!
//        여기 아래 예시에서는, 당연히 Post 객체를 먼저 변화시키는 것이 일반적이고, Comment 객체는 그 Post 객체의
//        변화에 이어져서 나오는 것이기 떼문에, Post 엔티티 클래스 내부에 Cascade를 입력한 것이다!!
//        즉, Post 객체가 반대편 Comment 객체를 '소유'하고 있는 그런 느낌이기 때문에,
//        Post 엔티티 객체의 내부에 Cascade를 작성해주는 것이다!!
//        https://www.inflearn.com/questions/15855/cascade-%EC%A7%88%EB%AC%B8%EC%9E%85%EB%8B%88%EB%8B%A4



//    @Entity
//    public class Post {      // 부모(상위) 엔티티 Post(1). 비주인 객체임.
//        @Id
//        @GeneratedValue(strategy = GenerationType.IDENTITY)
//        private Long id;
//
//
//        //연관관계 매핑에서는 반드시 연관관계 편의 메소드를 작성해준다!!
//        @OneToMany(mappedBy = "post", cascade = CascadeType.ALL)
//        private List<Comment> comments = new ArrayList<>();
//
//        public void addComment(Comment comment) {
//            this.comments.add(comment);
//            comment.setPost(this);
//        }
//
//        // getters and setters...
//    }





//    @Entity
//    public class Comment {      // 자식(하위) 엔티티 Comment(1). 주인 객체임.
//        @Id
//        @GeneratedValue(strategy = GenerationType.IDENTITY)
//        private Long id;
//
//        //...
//
//        @ManyToOne(fetch=FetchType.LAZY)
//        @JoinColumn(name="post_id") //여기 주인 Comment 엔티티가 비주인 객체인 반대편 Post 엔티티 테이블의 PK를 소유학고 있음.
//        private Post post;
//
//
//        //연관관계 매핑에서는 반드시 연관관계 편의 메소드를 작성해준다!!
//        public void setPost(Post post) {
//            this.post = post;
//        }
//
//        // getters and setters...
//    }



//==============================================================================================================





//    [ Cascade ] 교재 p308~
//
//    Cascade는 1:1, N:1 양방향 매핑에서 1인 부분의 엔티티 클래스 내부에 아래 필드 및 어노테이션을 넣는 거임.
//    사실, cascade는 단방향, 양방향 또는 주인 객체, 주인 아닌 객체 여부와 관계 없이
//    두 객체 간의 관계에서, 상대 객체를 나한테 종속시켜버리는 '그 종속시키는 우월한 객체(= 더 먼저 발생하는, 더 우선되는 객체)의
//    클래스 내부'의 필드에 '영속성 전이 Cascade'를 넣는 것이다
//
//    e.g) 주문 Order 객체와 배송 Delivery 객체가 있다고 하면, 양, 단방향, 주인, 비주인 관계 없이
//         먼저 일단 '주문'이라는 사건이 발생해야 하고, 그에 따라 이어져서 '배송'이 발생하는 것이기 때문에,
//         주문 Order 객체가 배송 Delivery 객체를 종속시키고,
//         따라서, '주문 Order 객체 클래스의 내부'에 영속성 전이 Cascade를 넣는 것이다
//

//    < 1. 양방향 1:1 매핑에서, 1인 부모 엔티티 클래스(상위 엔티티. 주인, 비주인 무관)의 내부에 아래 필드 및 어노테이션을 넣는 거임 >
//    @OneToOne
//    @JoinColumn(name=" ", cascade=CascadeType.PERSIST 또는 ALL)
//

//    < 2. 양방향 N:1 매핑에서, 1인 부모 엔티티 클래스(상위 엔티티. 주로 주인 아닌 객체)의 내부에 아래 필드 및 어노테이션을 넣는 것임 >
//    @OneToMany(mappedBy=" " , cascade=CascadeType.PERSIST 또는 ALL)



//=============================================================================================================


    //[ cascade = CascadeType.ALL ] 교재 p308~

    //: 부모 엔티티(상위 엔티티. 1)에서 발생하는 CRUD, 병합(Merge), 영속화(Persist) 등의 모든 변경이
    //  자식 엔티티(하위 엔티티. N)에 바로 전파되도록 설정하는 옵션.
    //  여기서는 '새로운 팀 엔티티 Team 객체'를 'db에 저장 Insert'할 때, 그와 연관되어 매핑된 '회원 Member 객체'도 자동으로 함께 저장됨.
    //  e.g) db에 새로운 team 객체를 저장 Insert
    /*
    # 부모 엔티티 Team 객체(1) : 자식 엔티티 Member 객체(N)

    < db에 새로운 team 객체를 저장 Insert >

    Team team = new Team();

    team.setName("TeamA");

    Member member1 = new Member();
    member1.setUsername("John");
    member1.setTeam(team);

    Member member2 = new Member();
    member2.setUsername("Jane");
    member2.setTeam(team);

    team.getMembers().add(member1);
    team.getMembers().add(member2);

    entityManager.persist(team);

    이 경우, Team과 Member 객체는 모두 저장되며, Member 객체의 team 속성에는 Team 객체가 연결됩니다.
    따라서 Team 테이블과 Member 테이블에는 새로운 행이 추가되며, Member 테이블의 TEAM_ID 열은 Team 테이블의 기본 키(id)와 관계가
    형성됩니다.

    //===================================================================================================

    < db에 기존 저장되어 있는 Team 객체를 수정 Update >

    Team team = em.find(Team.class, 1L); //'팀 id가 1'인 '팀 Team 객체'를 db로부터 조회해서 가져옴.

    team.setName("New Team"); //수정사항 1) '팀 Team 객체'의 이름을 '수정(update)'함.

    Member member = new Member();
    member.setUsername("Mark");
    member.setTeam(team);

    team.getMembers().add(member); //수정사항 2) '새로운 회원 Member 객체(=이름이 Mark)'를 '기존 팀 Team 객체의 내부'에 추가함

    em.merge(team); //변경내용(수정사항 1)과 2))를 기존 db에 있는 Team 객체에 병합하여 최종 업데이트함.
                    //따라서 이제, 1) 기존 팀 객체의 '팀 이름'이 수정되었고, 2) 새로운 회원 객체가 기존 팀 내부에 추가됨.

    //===================================================================================================

    < db에 기존 저장되어 있는 Team 객체를 삭제 Delete >
    Team team = em.find(Team.class, 1L); //'팀 id가 1'인 팀 Team 객체를 db로부터 조회해서 가져옴

    em.remove(team); //db로부터 가져온 해당 Team 객체를 삭제함.

    : 팀 id가 1인 Team 객체를 삭체할 때, 그 해당 Team 객체에 속한(연관된) 모든 회원 Member 객체도 자동으로 연동되어 삭제됨.
      (즉, 여기 예시에서는 '이름이 Jane인 회원 객체', '이름이 John인 회원 객체', '이름이 Mark인 회원 객체'가 모두 다 연동되어 삭제됨)

    //===================================================================================================

    이렇게, CascadeType.ALL은 부모 엔티티(1. Team 객체)의 모든 변경이 자식 엔티티(N. Member 객체)에 전파되므로, 편리하게 관리 가능.
    */

    //===================================================================================================


}

