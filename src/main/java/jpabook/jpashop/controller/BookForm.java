package jpabook.jpashop.controller;


import jpabook.jpashop.service.MemberService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import javax.annotation.sql.DataSourceDefinitions;


//< 엔티티 객체 >
@Data
public class BookForm {

    //'종류 불문하고 상품의 공통속성'
    private Long id;

    private String name;
    private int price;
    private int stockQuantity;

    //'여러 상품 중 하나인 '책' 상품의 고유 개별 속성'
    private String author;
    private int isbn;


}
