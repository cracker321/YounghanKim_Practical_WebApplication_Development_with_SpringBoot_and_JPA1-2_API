package jpabook.jpashop.repository;

import jpabook.jpashop.domain.Item.Item;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;


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


    //< 개별 item(1개)를 DB에서 '조회'하기 >


//================================================================================================================



//================================================================================================================


//================================================================================================================



//================================================================================================================
}
