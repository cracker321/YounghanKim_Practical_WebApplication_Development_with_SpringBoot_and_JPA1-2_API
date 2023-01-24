package jpabook.jpashop.domain.Item;


import lombok.Getter;
import lombok.Setter;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;


@Getter
@Setter
@DiscriminatorValue("A") //'단일 테이블 전략'에 따라 작성함
                         //'("A")' 넣지 않고, 그냥 기본으로 '@DiscriminatorValue'만 쓰면, 그냥 자동으로
                         //'("Album")'으로 들어간다는 의미가 내포되어 있음
@Entity
public class Album extends Item{

    private String artist;

    private String etc;

}
