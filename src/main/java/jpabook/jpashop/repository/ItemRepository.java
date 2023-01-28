package jpabook.jpashop.repository;

import jpabook.jpashop.domain.Item.Item;
import jpabook.jpashop.domain.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.List;


@RequiredArgsConstructor
@Repository
public class ItemRepository {

    //< 생성자를 통한 의존성 주입 >
    private final EntityManager em;


//================================================================================================================


    //< 클라이언트로부터 들어온 Item 객체를 DB에 저장 >
    public void save(Item item){

        em.persist(item);
    }


//================================================================================================================


    //< '개별 상품(1개)을 DB로부터 'id값'으로 조회'하기 >
    public Item findOne(Long itemId){


        Item item = em.find(Item.class, itemId);

        return item;
    }


//================================================================================================================


    //< '모든 상품 조회'하기 >
    public List<Item> findAll(){


        List<Item> items = em.createQuery("select item from Item item", Item.class)
                .getResultList();

        return items;

    }


//================================================================================================================




//================================================================================================================



//================================================================================================================





}
