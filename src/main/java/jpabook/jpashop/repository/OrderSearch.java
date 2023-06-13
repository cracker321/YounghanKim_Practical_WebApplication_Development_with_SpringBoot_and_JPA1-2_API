package jpabook.jpashop.repository;

import jpabook.jpashop.domain.OrderStatus;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;


//[ '주문 검색 기능 개발'강. 00:00~ ]
@RequiredArgsConstructor
@Data
@Repository
public class OrderSearch {


    private String memberName; //회원 이름
    private OrderStatus orderStatus; //주문 상태[Order, Cacncel]



}
