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


    @PostMapping("/")
    public String create(OrderForm form) { //- '변수 form': 사용자가 'OrderForm 객체'의 필드 속성들 형식에 맞추어서
                                           //              폼 화면에 입력한 정보.
                                           //              그리고, 그 정보를 submit 했으니, 이제 그것을 여기서 받아와서,
                                           //              서버 DB에 저장하는 과정인 것임.

        OrderForm order = new OrderForm();

        order.setMember(form.getMember());
        order.setDelivery(form.getDelivery());
        order.setOrderDate(form.getOrderDate());
        order.setOrderItems(form.getOrderItems());
        order.setStatus(form.getStatus());


        return "redirect:/order";
    }


//========================================================================================================


//========================================================================================================


//========================================================================================================

}
