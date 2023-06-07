package jpabook.jpashop.service;


import jpabook.jpashop.domain.Item.Book;
import jpabook.jpashop.domain.Item.Item;
import jpabook.jpashop.repository.ItemRepository;
import jpabook.jpashop.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class ItemService {

    private final ItemRepository itemRepository;


//=================================================================================================================


    //[ '상품 서비스 개발'강. 00:00~ ]

    //< DB에 1개의 Item 객체를 저장시킴 >
    @Transactional
    public void saveItem(Item item){

        itemRepository.save(item);

    }


//=================================================================================================================


    //< 1개의 Item을 DB로부터 조회하기 >
    public Item findOne(Long itemId){


        Item savedItem = itemRepository.findOne(itemId);

        return savedItem;
    }


//=================================================================================================================


    //< DB에 저장되어 있는 모든 Item 객체들을 조회하기 >
    public List<Item> findItems(){

        List<Item> items = itemRepository.findAll();

        return items;
    }


//=================================================================================================================

    //< 1개의 Item 객체를 수정 >

    //1.DB에 저장되어 있는 수정 대상 Item 객체를 꺼내와서,
    //2.폼 화면(뷰 파일)으로 보내서 사용자에게 보내주어서,
    //3.사용자가 그 화면에 수정하고 싶은 내용을 입력하고,
    //4.사용자가 수정한 입력 내용을 받아와서, 다시 서버 DB에 저장함.


    @Transactional
    public void updateItem(Long itemId, String name, int price, int stockQuantity){ //사용자가 수정할 대상인 Book 객체를 받아옴.

        //1.DB에 저장되어 있는 '영속성 컨텍스트(이미 DB에 꽉 영속화되어 있는) 수정 대상 Item 객체를 꺼내와서,
        Item findItem = itemRepository.findOne(itemId);


        //4.사용자가 수정한 입력 내용을 다시 DB에 저장함.
        //- 그렇긴 한데, 아래처럼 findItem.setPrice(..), findItem.setName(..) 이렇게 setter 사용하는 건
        //  여기서처럼 아주 간단한 예제이고, 단발성이기 때문에 그냥 이렇게 간단히 한 것이고,
        //  실무에서는 '의미 있는 메소드'를 사용해서 만들어야 함.
        //  eg) findItem.change(price, name, stockQuantity) 또는
        //      findItem.addStock,
        //      ... 이런 것처럼 의미 있게 만들어야 함.
        findItem.setName(name);
        findItem.setPrice(price);
        findItem.setStockQuantity(stockQuantity); //여기까지만 써주면 '수정 update'는 끝이다!!!!

        //꺼내와서 수정한 Item 객체를 다시 여기서 다시 itemRepository.save(findItem); 라는 코드를 작성해서
        //DB에 저장해주는 과정 필요X
        //이것이 바로 '변경 감지 기능' 사용임.
        //*****중요*****
        //'pdf 실전! 스프링 부트와 JPA 활용 1 - 웹 어플리케이션 개발' p86~
        //


    }



//=================================================================================================================





}
