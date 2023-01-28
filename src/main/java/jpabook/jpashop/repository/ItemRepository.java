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


    //[ '상품 리포지토리 개발'강. 00:40~ ]
    //< 클라이언트로부터 들어온 Item 객체를 DB에 저장 >
    public void save(Item item){ //

        if(item.getId() == null){ //- '클라이언트로부터 받아온 Item 객체'를 'DB에 저장만 하고 리턴값을 따로 반환해주지 않아도
                                  //   되기에, 이 메소드의 자료형은 void임!
                                  //- '클라이언트로부터 받아온 현재 Item 객체'는 당연히 'JPA에 저장되기 전까지 그 id값이 없기
                                  //  때문에(당연..왜냐면 DB에 저장된 적이 없는 완전 신선한 새로운 대상 Item 객체이기 때문),
                                  //  DB에 저장되어야 하는 대상인 Item 객체는 당연히 그 id값이 없어야 함!
            em.persist(item); //'id값이 없는 Item 객체는 정상적으로 DB에 저장됨(=영속화됨)'
        }else{
            em.merge(item); //만약, 클라이언트로부터 받아온 Item 객체가 이미 id값이 있는 상태라면(이미 예전에 영속화되어 DB에
                            //저장되어 이미 id값이 있는 상태라면),
                            //DB에 저장되어 있는 기존의 그 Item 객체를 '수정(업데이트)'한다는 뜻임.
        }
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
