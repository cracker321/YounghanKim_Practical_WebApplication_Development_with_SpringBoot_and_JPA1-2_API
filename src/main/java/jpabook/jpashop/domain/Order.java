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


    @ManyToOne(fetch = LAZY) //'@ManyToOne', '@OneToOne'은 기본설정이 '즉시로딩 EAGER'이므로,
                             //반드시 '지연로딩 LAZY'로 설정 바꿔줘야 한다!
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
    //- 구체적 설명은 저~ 아래에 입력해놨음.
  
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
  
       /*

    < 연관관계 편의 메소드란? >

    - '양방향 매핑'에서 사용. 주로 '다대일', '일대다' 관계에서 사용됨.
    - '주인 엔티티(N)'가 변경될 때, '주인이 아닌 엔티티(1)'도 '자동으로 변경'됨.
      '주인 엔티티(N) 클래스에 작성된 연관관계 편의 메소드'가 호출되면, '주인이 아닌 엔티티(1)의 연관관계'도 함께 설정됨.
    - '주인 엔티티(N) 클래스'에 '반대편 엔티티를 인자값으로 받아 설정하는 메소드 로직'을 작성함.
      그 메소드의 이름은 일반적으로 'addXXX', 'removeXXX'와 같은 형태로 작성함.
    - 예를 들어, 게시물(Post)와 댓글(Comment)이 '일대다 관계'일 때, '게시물(주인. 1) 엔티티 클래스의
      내부에 '메소드 addComment()'를 작성'하여, '댓글 Comment 를 추가'할 수 있음.
    - 양방향 매핑 연관관계를 객체 지향적으로 다룰 수 있으며, 중복 코드를 줄이고 코드 가독성을 향상시킴.
    - 그러나, 잘못 사용할 경우, 무한루프나 스택오버플로우 등의 문제가 발생할 수 있음.


    //=========================================================================================================

    < 예시 >

    - 다대일 '양'방향 핑.
      주인 엔티티(N. 회원 Member 객체) : 주인 아닌 엔티티(1. 팀 Team 객체)

    //=========================================================================================================

      < 주인 엔티티(N. 회원 Member 객체) >

      @Entity
      public class Member{

        ...

        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "TEAM_ID")
        private Team team;

        ...

      //-------------------------------------------------------------------------------------------------------

        < 연관관계 편의 메소드 1 >

        public void setTeam(Team team){
        //- 'Team team': '외부 클래스 어딘가에서 현재 메소드를 호출할 때 새로운 인자값으로 들어온 새롭게 주어질 팀 team'
        //- 'void': 어차피, 특별한 반환값을 내놓지 않아도 되고, 그냥 서로 간의 관계를 재설정 또는..


            # Step 1: 이전에 해당 회원이 속해 있던 팀과의 관계를 해제함

            if(this.team != null){
                this.team.getMembers().remove(this);
                }

            //- 'if(this.team != null)': 현재 회원('this')이 기존에 어떤 팀이든 소속되어 있던 팀이 있는가 를 확인하는 것.
            //- 'this.team.getMembers().remove(this)': 현재 회원('this')을 이전 팀의 회원 목록에서 제거함.
            //    - 'this.team': 현재 회원('this')의 이전 팀('this.team')에 접근함.
            //    - 'getMembers()': 현재 회원의 이전 팀의 '회원 전체 목록'에 접근함.
            //    - 'remove(this)': 현재 회원의 이전 팀의 '회원 전체 목록'에서 '현재 회원'을 '제거'함.
            //- 여기서 'this.team'은 '현재 다루고 있는 회원 Member 객체의 필드 team'을 의미함.
            //  즉, 'this'는 '현재 회원 Member 객체'을 의미하고,
            //  'this.team'은 '현재 회원이 현재 가지고 있는(소속되어 있는) 팀'을 의미함.
            //- 'this'는 '해당 메소드(여기서는 메소드 setTeam())를 호출하는 객체, 즉 현재 인스턴스'를 가리킴.
            //  여기서 'this'는 '현재의 회원 Member 객체 그 자체'를 의미함.
            //- *****중요*****
            //  'Step 1'에서의 'this.team'은 '외부 클래스 어딘가에서 현재 메소드를 새로운 인자값으로 들어온 새롭게 주어질 team이
            //   들어오기 '전'에 원래 현재 회원이 소속되어 있던 기존 팀'.
            //  'Step 3'에서의 'team'은 '외부 클래스 어딘가에서 현재 메소드를 호출할 때 새로운 인자값으로 들어온 새롭게 주어질 team'.



            # Step 2: 외부 클래스 어디에선가 이 메소드 setMember를 새로운 인자값(= 새로운 팀 'team')으로 호출할 때,
                      그 때 주어질 새로운 팀('team')을 '현재 회원의 팀('this.team')'으로 새롭게 설정함.
            this.team = team;



            # Step 3: 새로운 팀('team')에 현재 멤버('this')를 추가('add(this)')함
            if(team != null){
                team.getMembers().add(this);
            }

            //- 'team': 외부 클래스 어디에선 이 메소드 setMember를 새로운 인자값(새로운 팀 'team')으로 호출할 때의,
            //          '새로운 팀'. 이를 통해 새롭게 주어진 팀에 접근함.
            //- 'team.getMembers()': 새롭게 주어질 팀의 '전체 회원 목록'에 접근함.
            //- 'add(this)': 새롭게 주어질 팀의 '전체 회원 목록'에 '현재 회원('this')'을 새로운 회원으로 그 목록에 추가함.
            //- '메소드 add': '인터페이스 List'의 내장 메소드.
            //- *****중요*****
            //  'Step 1'에서의 'this.team'은 '외부 클래스 어딘가에서 현재 메소드를 새로운 인자값으로 들어온 새롭게 주어질 team이
            //   들어오기 '전'에 원래 현재 회원이 가지고 있는(소속되어 있는) 기존 팀'.
            //  'Step 3'에서의 'team'은 '외부 클래스 어딘가에서 현재 메소드를 호출할 때 새로운 인자값으로 들어온 새롭게 주어질 team'.
         }
      }


    //=========================================================================================================


      < 주인 아닌 엔티티(1. 팀 Team 객체) >

      @Entity
      public class Team{

        ...

        @OneToMany(mappedBy = "team", cascade = CascadeType = ALL)
        private List<Member> members = new ArrayList<>();

        ...

      //-------------------------------------------------------------------------------------------------------

        < 연관관계 편의 메소드 2 >

        public void addMember(Member member){


            //# Step 1: 현재의 팀의 전체 회원 목록에 새롭게 들어온 회원을 추가하여, 새로운 회원으로 받아들임.
            members.add(member);
            //- '외부 클래스 어딘가에서 현재 메소드를 호출할 때 새로운 인자값으로 들어온 새롭게 주어질 회원 Member'을
            //  '현재의 팀의 '전체 회원 목록(members)'에 새롭게 추가함'.
            //- '메소드 add': '인터페이스 List'의 내장 메소드.




            //# Step 2: 스텝 1에서 팀에 새로운 회원을 추가해줬으니, 이제 그 새로운 회원이 현재 팀을 본인의 팀으로 인식하도록 설정하는 것.
            member.setTeam(this);
            //- '외부 클래스 어딘가에서 현재 메소드를 호출할 때 새로운 인자값으로 들어온 새롭게 주어질 회원 Member가 소속될 팀'을
            //  '현재의 팀으로 지정함'.

        }

      //-------------------------------------------------------------------------------------------------------

        < 연관관계 편의 메소드 3 >

        public void removeMember(Member member){

            # Step 1:
            members.remove(member)
            //- '외부 클래스 어딘가에서 현재 메소드를 호출할 때 새로운 인자값으로 들어온 새롭게 주어질 회원 Member'를
            //  '현재 팀의 '전체 회원 목록 members''에서 '삭제해버림'.
            //- '메소드 remove': '인터페이스 List'의 내장 메소드.


            # Step 2:
            member.setTeam(null);
            //- '외부 클래스 어딘가에서 현재 메소드를 호출할 때 새로운 인자값으로 들어온 새롭게 주어질 회원 Member'의
            //  '현재 소속 팀'을 '없음 null'로 설정함.
            //  이렇게 하면, 해당 회원 Member가 현재 Team 에서 제거되는 작업 완료됨.

        }
      }
     */




}




}

