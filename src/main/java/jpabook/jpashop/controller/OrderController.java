package jpabook.jpashop.controller;


import jpabook.jpashop.domain.Item.Item;
import jpabook.jpashop.domain.Member;
import jpabook.jpashop.domain.Order;
import jpabook.jpashop.service.ItemService;
import jpabook.jpashop.service.MemberService;
import jpabook.jpashop.service.OrderService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Data
@RequiredArgsConstructor
@Controller
public class OrderController {

    private final OrderService orderService;
    private final MemberService memberService;
    private final ItemService itemService;

//========================================================================================================

    //[ '상품 주문'강. 00:00~ ]. 실전! 스프링 부트와 JPA 활용1 - 웹 애플리케이션 개발

    //< 상품 주문 >
    //< 사용자에게 신규 '상품 주문'하는 '폼 페이지(=뷰 orderForm.html)'로 이동시키는(사용자에게 해당 폼 페이지를 보여주기 위한) 메소드 >
    //'서버 ---> 화면'의 과정

    @GetMapping("/order")
    public String createForm(Model model) { //1.사용자에게 보여줄 폼 화면에 '회원 Member 객체'와 '상품 Item 객체' 정보를
                                            //  보여줘야 하기 때문에,
                                            //2.DB에서 '회원 Member 객체'와 '상품 Item 객체'를 꺼내와서,
                                            //  폼 화면에 뿌려줌.


        /*
        cf) < '컨트롤러 ItemController'에서의 'model.addAttribute("form", new BookForm());'와 같은
            새로운 폼 객체를 생성하지 않는 이유 >
            : '상품 주문을 희망하는 회원'은, 개발자가 이미 서버 DB에 저장된 '회원 정보'와 '상품 정보'를 DB로부터 꺼내와서
              해당 정보를 조회한 후, Model 객체에 저장하여 회원이 '뷰'에서 '폼 화면'을 통해 입력하여 사용할 수 있도록 하기 때문에,
              이 때는, 당연히 '새로운 객체'를 생성하지 않고, 기존 DB에 저장되어 있는 데이터를 활용한다.
         */
        List<Member> members = memberService.findMembers();
        List<Item> items = itemService.findItems();

        model.addAttribute("members", members);
        model.addAttribute("items", items);

        return "order/orderForm";

    }

//========================================================================================================

    //< 사용자가 직접 상품을 주문하는 기능 >

    //< 상품 주문하는 '폼 페이지(=뷰 OrdermForm.html)'에 사용자가 신규로 입력한 신규 '상품 정보'를,
    //  여기 컨트롤러 메소드가 받아와서, 그 폼에 입력된 정보를 이제 DB에 '등록 post'시키는 메소드 >

    //- '화면(사용자가 입력한 데이터) ---> 서버'의 과정

    //사용자가 저~기 '뷰 orderForm'에서 입력한 '상품 주문' 정보 입력 데이터를 method='post'를 통해 받아와서
    //아래에서 그 상품 주문 정보를 서버에 입력하는 기능을 수행하는 메소드임.


    //< '쿼리파라미터'와 '@RequestParam' >
    //여기서는 쿼리파라미터(주로 GET 조회할 때 사용됨. POST도 사용하긴 함)와 '@RequestParam'을 사용했기 때문에,
    //1.클라이언트가 URL 주소창에 'www.thekary.com/.../order?memberId=kuka92&itemId=4224323&count=2'와 같이 입력하고,
    //2.클라이언트가 URL을 다 입력하면, 웹 브라우저가 해당 URL로 HTTP GET 요청을 보내고,
    //3.이 요청은 '서버'로 전달되어, 스프링부트 어플리케이션은 해당 요청을 처리하기 위해 '매핑된 컨트롤러 메소드'를 찾고
    //  (여기서는 아래 create 메소드)
    //4.메소드에 있는 '@RequestParam 어노테이션'은 쿼리파라미터에서 'memberId', 'itemId', 'count'라는 이름의
    //  쿼리 파라미터 값을 추출함.
    //5.그리고 그 추출한 값을 각각의 매개변수 'Long membermemberId', 'Long itemmmmmId', 'int counttttt' 등등
    //  매개변수 이름은 자유롭게 하여 그 매개변수에 바인딩 시키는 것임
    //6.그리고 이제 그 파라미터 값을 그 메소드 안의 수행 로직에서 사용해서 실행되는 것임.


    @PostMapping("/order")
    public String create(@RequestParam("memberId") Long memberId, //여기서 매개변수명('memberId'..)는 내 맘대로 해도 됨
                         @RequestParam("itemId") Long itemId,
                         @RequestParam("count") int count
                         ) {


        orderService.order(memberId, itemId, count); //어떤 고객이, 어떤 상품을, 몇 개의 수량  으로 주문'했는지' 대한 정보를
                                                     //서버에 저장함

        return "redirect:/orders"; //주문 내역 목록
    }


//========================================================================================================


//========================================================================================================


//========================================================================================================

}
