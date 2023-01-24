package jpabook.jpashop.domain.Item;


import lombok.Getter;
import lombok.Setter;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Getter
@Setter
@DiscriminatorValue("B") //'단일 테이블 전략'에 따라 작성함
                         //'("B")' 넣지 않고, 그냥 기본으로 '@DiscriminatorValue'만 쓰면, 그냥 자동으로
                         //'("Book")'으로 들어간다는 의미가 내포되어 있음
@Entity
public class Book extends Item{

    private String author;

    private int isbn;

}
