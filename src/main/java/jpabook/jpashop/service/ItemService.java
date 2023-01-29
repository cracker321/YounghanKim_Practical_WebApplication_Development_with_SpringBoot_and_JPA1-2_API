package jpabook.jpashop.service;


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
    public Item findItem(Long itemId){


        Item savedItem = itemRepository.findOne(itemId);

        return savedItem;
    }


//=================================================================================================================


    //< DB에 저장되어 있는 모든 Item 객체들을 조회하기 >
    public List<Item> findAllItems(){

        List<Item> items = itemRepository.findAll();

        return items;
    }


//=================================================================================================================






//=================================================================================================================





}
