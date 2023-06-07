package jpabook.jpashop.controller;


import jpabook.jpashop.domain.Item.Book;
import jpabook.jpashop.domain.Item.Item;
import jpabook.jpashop.service.ItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

@RequiredArgsConstructor
@Controller
public class ItemController {
    private final ItemService itemService;


//========================================================================================================


    //[ '상품 등록'강 00:00~ ]. '실전! 스프링 부트와 JPA 활용1 - 웹 애플리케이션 개발'

    //< 신규 '상품 등록'하는 '폼 페이지(=뷰 createItemForm.html)'로 이동시키는(사용자에게 해당 폼 페이지를 보여주기 위한) 메소드 >
    //'서버 ---> 화면'의 과정
    @GetMapping("/items/new")
    public String createForm(Model model) {

        //*****중요*****
        //폼 화면에 넘겨줄 새로운 객체(새로운 BookForm 객체)를 새롭게 생성하고, 이것을 'form'이라는 이름으로 모델에 추가함.
        //이렇게 폼 화면에 초기화된 객체를 전달하면, '뷰'에서 'form'이라는 이름으로 'BookForm  객체'를 사용할 수 있게 됨.
        //즉, 이를 통해 'BookForm 객체'를 사용하여 신규 상품을 등록할 수 있는 폼을 구성하게 됨.
        //개발자가 새로운 상품 정보를 등록하기 위해 폼에 직접 데이터를 입력하도록 구성되어 있는 것임.
        model.addAttribute("form", new BookForm());

        return "items/createItemForm"; //이를 통해,
    }


//========================================================================================================

    //< 신규 상품 등록 >

    //< 신규 상품 등록하는 '폼 페이지(=뷰 createItemForm.html)'에 사용자가 신규로 입력한 신규 '상품 정보'를
    //  여기 컨트롤러 메소드가 받아와서, 그 폼에 입력된 정보를 이제 DB에 '등록 post'시키는 메소드 >

    //- '화면(사용자가 입력한 데이터) ---> 서버'의 과정

    //사용자가 저~기 '뷰 createItemForm'에서 입력한 '신규 상품' 정보 입력 데이터를 method='post'를 통해 받아와서
    //아래에서 그 신규 상품 정보를 서버에 입력하는 기능을 수행하는 메소드임.


    //- '메소드 createForm'에서 '뷰 items/createItemForm'으로 보낼 때, 'new BookForm 객체'를 'Model 객체에 담아
    //  보냈기 때문'에,
    //  당연히 '뷰 ~'에서 처리되는 데이터도 'BookForm 객체'이고, 따라서, 여기에서 다시 그 뷰로부터 받아오는 데이터도
    //  당연히 'BookForm 객체'이기에, 여기 메소드의 매개변수에 'BookForm 객체'를 넣어주는 것임. 너무 당연함.
    //- 여기는 메소드의 매개변수로 'Model 객체'가 아닌 'BookForm 객체'가 왔기 때문에, 이건 데이터를 '뷰'로 보내는 것이 아닌,
    //  데이터를 '뷰'로부터 받아와서 서버에 저장하는 과정인 것이다!!


    //아래처럼 이렇게 setter 열어서 컨트롤러에서 이렇게 사용하는 건 좋지 못한 설계임.
    //이것보단, Member 객체를 여기 메소드의 매개변수로 받아와서 사용하는 것이 좋음.
    //근데 이건 그냥 간단한 예제이기에 아래처럼 setName, setAddress 이렇게 사용함.

    @PostMapping("/items/new")
    public String create(BookForm form) { //- '변수 form': 사용자가 'BookForm 객체'의 필드 속성들 형식에 맞추어서
                                          //              폼 화면에 입력한 정보.
                                          //              그리고, 그 정보를 submit 했으니, 이제 그것을 여기서 받아오는 것임.

        Book book = new Book();
        book.setName(form.getName());
        book.setPrice(form.getPrice());
        book.setStockQuantity(form.getStockQuantity());

        book.setAuthor(form.getAuthor());
        book.setIsbn(form.getIsbn());
        itemService.saveItem(book);

        return "redirect:/items"; //위에서 작업을 다 마쳤기 때문에, 그냥 뭐 리다이렉트로 ''로 화면을 넘겨버림 그냥. 별 이유 없음.
    }


//========================================================================================================


    //[ '상품 목록'강 00:00~ ]. '실전! 스프링 부트와 JPA 활용1 - 웹 애플리케이션 개발'

    //< 상품 목록 조회 >

    //현재 전체 상품 목록 조회
    //'서버(여기서는 DB)---> 화면'의 과정

    @GetMapping("/items")
    public String list(Model model) {

        //상품 전체 조회하는 findItems
        List<Item> items = itemService.findItems(); //'서비스'에서 '레퍼지토리'를 통해 DB로부터 데이터 가져오고
        //그 가져온 '서비스 데이터'를 다시 여기 '컨트롤러'에서
        //그 서비스를 호출해서 그 데이터를 가져옴.

        model.addAttribute("items", items); //서비스로부터 가져온 전체 상품 목록 데이터를

        return "items/itemList"; //'URL링크 items/memberList'로 보내고,
        //'뷰 itemList'에서 그 'URL 링크 items/itemList'와 연결해서 화면에 데이터를 띄워줌.
    }


//========================================================================================================


    //[ '상품 수정'강 00:00~ ]. '실전! 스프링 부트와 JPA 활용1 - 웹 애플리케이션 개발'

    //< '기존의 상품을 수정'하는 '폼 페이지(=뷰 updateItemForm.html)'로 이동시키는 메소드 >
    //'서버(여기서는 DB)---> 화면'의 과정


    //1.사용자가 수정하길 희망하는 데이터를 아래에 '@GetMapping updateItemForm 메소드'로 그 데이터를 DB로부터 꺼내와서,
    //  그것을 '폼 페이지('뷰 updateItemFrorm')'에 넘겨서,
    //  사용자에게 화면을 보여주고 사용자가 그 화면을 통해 데이터를 다시 수정 입력하도록 한 후,
    //2.사용자가 수정 입력한 데이터를 다시 저 아래 '@PostMapping updateItem 메소드'로 그 데이터를 다시 서버 DB에 넣는 것임.


    @GetMapping("/items/{itemId}/edit") //-'itemId': URL경로의 일부인 '경로변수'
    //           반드시 '@PathVariable("itemId")의 itemId와 같은 글자여야 한다!!
    //1.이 'itemId'는 '@PathVariable("itemId")와 연결되어 서로 바인딩되고
    //2.메소드의 매개변수 itemitemId는 @PathVariable("itemId")를 통해 전달된
    //'경로변수 {itemId}'의 값을 담는 '메소드의 매개변수'가 되는 것이다!
    public String updateItemForm(@PathVariable("itemId") Long itemitemId, Model model) {
        //-'@PathVariable("itemId") 에서의 itemId': '@PathVariable 어노테이션'의 매개변수 이름
        //                                         반드시 'URL 경로변수 {itemId}의 itemId와 같은 글자여야 한다!!
        //-'itemitemId': '메소드 updateItemForm의 매개변수 이름'.
        //               그냥 정말 단순히, 이 메소드 내부 로직에서 itemitemId를 사용하기 위해 만든 매개변수임.'

        Book item = (Book) itemService.findOne(itemitemId); //DB로부터 현재 DB에 있는 수정을 희망하는 기존 상품을 하나를 불러와서


        //< 아래가 사실 entity를 dto로 만들어주는 원리와 같음 >
        //- https://www.inflearn.com/questions/356825/%EC%97%94%ED%8B%B0%ED%8B%B0-vs-%ED%8F%BC%EA%B0%9D%EC%B2%B4 참고

        BookForm form = new BookForm(); //새로운 책 객체를 만들고,
        //그 책 객체에 이제 아래의 새롭게 수정하는 내용을 집어넣음.

        //BookForm 객체에 사용자가 이제 새롭게 수정하는 내용들을 입력함. 그 입력을 여기서 받음.
        form.setId(item.getId());
        form.setName(item.getName());
        form.setPrice(item.getPrice());
        form.setStockQuantity(item.getStockQuantity());
        form.setAuthor(item.getAuthor());
        form.setIsbn(item.getIsbn());

        model.addAttribute("form", form);

        return "items/updateItemForm";

    }


//========================================================================================================


    //< 상품 수정하는 '폼 페이지(=뷰 updateItemForm.html)'에 사용자가 수정한 '상품 정보'를 이제 DB에 '등록 post'시키는 메소드 >

    //-'화면(사용자가 수정 입력한 데이터) ---> 서버'의 과정

    @PostMapping("/items/{itemId}/edit")
    public String updateItem(@PathVariable("itemId") Long itemId, //사실 여기서 '@PathVariable("itemId")'는 생략하고 안 써줘도 됨.
                             @ModelAttribute("form") BookForm form) { //'뷰 updateItemForm' 내부의
        //th:object="${form}" 의 'form'(=즉, 사용자가 폼 화면에서 이런저런 수정 입력한 정보들)이
        //여기 '@ModelAttribute("form")'을 통해 매개변수 'form'으로 넘어오게 되는 것임.
        //아 아님. 그냥 @ModelAttribute의 "form"(?)을 아무 이름으로나 해도 된다고 강의에서 그렇게 다시 말 수정함.

        Book book = new Book(); //새로운 Book 객체를 생성 후에

        //1.사용자가 수정하길 희망하는 데이터를 위에 '@GetMapping updateItemForm 메소드'로 그 데이터를 DB로부터 꺼내와서,
        //  그것을 '폼 페이지('뷰 updateItemFrorm')'에 넘겨서,
        //  사용자에게 화면을 보여주고 사용자가 그 화면을 통해 데이터를 다시 수정 입력하도록 한 후,
        //2.사용자가 수정 입력한 데이터를 바로 여기 '@PostMapping updateItem 메소드'로 그 데이터를 다시 서버 DB에 넣는 것임.


        //사용자가 BookForm 객체에 이제 새롭게 수정하는 내용들(변수 form에 담겨져 있음)을 이제 벗겨내서
        //그것을 DB에 저장하기 위해 Book 객체에 담음.


        //< 강의 중간에서 v1 >
//        book.setId(form.getId());
//        book.setName(form.getName());
//        book.setPrice(form.getPrice());
//        book.setStockQuantity(form.getStockQuantity());
//        book.setAuthor(form.getAuthor());
//        book.setIsbn(form.getIsbn());
//
//        itemService.saveItem(book); //'서비스 itemService의 메소드 saveItem'은 '매개변수로 Item 객체'만 허용하고 있는데,
//        //여기서 'Book 객체'가 들어갈 수 있는 이유는,
//        //'Book 객체'는 '클래스 Book에서 Book extends Item'으로 상속(?)하고 있기 때문이다!


        //< 강의 중간에서 v2 >
        //- v2가 훨씬 더 깔끔하다.
        itemService.updateItem(itemId, form.getName(), form.getPrice(), form.getStockQuantity());


        return "redirect:/items"; //'redirect:' 이렇게 쓰면, 이것은 '뷰 템플릿'을 요청하지 않음.
                                  //대신, 여기서는 '/items'라는 URL로 다시 요청하게 되는 것임.
    }

//========================================================================================================


}
