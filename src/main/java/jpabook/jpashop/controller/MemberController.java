package jpabook.jpashop.controller;


import jpabook.jpashop.domain.Address;
import jpabook.jpashop.domain.Member;
import jpabook.jpashop.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

@Controller // 여기서는 thymeleaf를 사용하기 때문에, '@RestController'를 사용하면 안된다!
@RequiredArgsConstructor
public class MemberController {

    // 여기서 왜 'final' 쓰는지 확인하기.
    private final MemberService memberService;


    //[ '회원 등록'강 00:00~ ]. '실전! 스프링 부트와 JPA 활용1 - 웹 애플리케이션 개발'

    //< 신규 회원 등록하는 '폼 페이지(=뷰 createMemberForm.html)'로 이동시키는 메소드 >
    //'서버 ---> 화면'의 과정
    @GetMapping("/members/new")
    public String createForm(Model model) { //이렇게 메소드의 매개변수로 Model 객체가 오는 경우는,
                                            //브라우저 화면에 데이터를 표시하기 위해 '뷰'에 보낼 때 사용되는 메소드라 생각하면 됨!

        model.addAttribute("memberForm", new MemberForm());
        //'변수명 memberForm'에 '실제 데이터인 new MemberForm() 객체'를 담아서 보냄.

        return "members/createMemberForm";
        //서버로부터 가져온 데이터(다만, 여기서는 그냥 신규회원 등록하는 페이지를 보여주기 위한 목적이기에,
        //여기서는 따로 DB로부터 데이터를 가져오는 로직은 없음)를
        //'URL링크 members/memberList'로 보내고,
        //'뷰 createMemberForm'에서 그 'URL 링크 members/createMemberForm'과 연결해서 화면에 데이터를 띄워줌.
    }

        //'뷰 members/createMemberForm'으로 찾아감.
        //'templates/members/createMemberForm.html'을 찾아가서, 그 내용을 브라우저로 보냄.
        //화면에 실제 표시되는 것 --> '뷰 members/createMemberForm' 내부의 로직이 화면에 표시됨.
//=======================================================================================


//    [ addAttribute 관련 설명 ]

//    @GetMapping("/hi") //'컨트롤러'에서 받은 URL요청이 'hi'라면, 아래 절차 거친 후, '뷰 greetings'로 찾아감
//    public String niceToMeetYou(Model model){ //'모델 객체'를 이용하여 'greetings.mustache의 변수 uesername'에
//        //데이터 전달해주기 위해, '메소드 niceToMeetYou'의 '매개변수'로 '모델 객체' 삽입
//        model.addAttribute("username", "yujong");
//        //'변수 username'에 주입해줄 정보(값) 'yujong'을 '모델 객체'에 실어넣어서 보내줌
//
//        return "greetings";
//        //'뷰 greetings'를 찾아감
//        //'templates/greetings.mustache'를 알아서 찾은 후, 브라우저로 전송해주는 역할
//        //화면에 실제 표시되는 것 --> '뷰 greetings'내부의 로직이 화면에 표시됨
//
//    }


//========================================================================================================



    //< 신규 회원 등록하는 '폼 페이지(=뷰 createMemberForm.html)'에 신규 회원이 입력한 본인 회원정보를
    // 이제 DB에 '등록 post'시키는 메소드 >

    //사용자가 저~기 '뷰 createMemberForm'에서 입력한 본인의 회원가입 정보 입력 데이터를 method='post'를 통해 받아와서
    //아래에서 그 신규 정보를 서버에 입력하는 기능을 수행하는 메소드임.
    //- '화면(사용자가 입력한 데이터) ---> 서버'의 과정
    @PostMapping("/members/new")
    public String create(@Valid MemberForm form, BindingResult result) {
        //- '변수 form': 사용자가 'MemberForm 객체'의 필드 속성들 형식에 맞추어서 신규 가입에 필요한 '본인의 정보'를 폼 화면에
        //              입력한 정보.
        //              그리고, 그 정보를 submit 했으니, 이제 그것을 여기서 받아오는 것임.
        //              이제 그것을 여기서 받아와서, 서버 DB에 저장하는 과정인 것임.


        //- '메소드 createForm'에서 '뷰 members/createMemberForm'로 보낼 때, 'new MemberForm 객체'를 'Model 객체에 담아
        //  보냈기 때문'에,
        //  당연히 '뷰 ~'에서 처리되는 데이터도 'MemberForm 객체'이고, 따라서, 여기에서 다시 그 뷰로부터 받아오는 데이터도
        //  당연히 'MemberForm 객체'이기에, 여기 메소드의 매개변수에 'MemberForm 객체'를 넣어주는 것임. 너무 당연함.
        //- 여기는 메소드의 매개변수로 'Model 객체'가 아닌 'MemberForm 객체'가 왔기 때문에, 이건 데이터를 '뷰'로 보내는 것이 아닌,
        //  데이터를 '뷰'로부터 받아와서 서버에 저장하는 과정인 것이다!!


        if (result.hasErrors()) { //만약, 사용자가 입력한 본인의 회원가입 정보가, 개발자가 작성한 'MemberForm 객체'의
            //입력 양식('MemberForm 객체의 필드 속성들')에 적합하지 않다는 것이
            //@Valid를 통해 확인된다면(=유효성 검사에서 Errors가 있다면(확인된다면))
            return "members/createMemberForm"; //1.화면단에서 바로 Whitelabel Error Page를 보여주는 것이 아니라,
            //  'members/createMemberForm'으로 다시 화면 넘겨버리고(새로고침 느낌),
            //2.'MemberForm 객체의 필드 name 위의 어노테이션
            //   @NotEmpty(message = "회원 이름은 필수입니다.")가 작동되어서,
            //   화면에서 해당 입력창 아래에 '회원 이름은 필수입니다'가 표시됨.
        }

        //- 뷰 createMemberForm'으로부터 'MemberForm 객체'가 넘어와서 그것을 받아옴.

        // < '@Valid' >
        //- 스프링에서 사용자가 '폼 form'을 통해 어떠한 데이터를 입력(회원가입에 필요한 정보 등)하면,
        //  그 입력된 데이터'가 '개발자가 작성한 해당되는 Bean 객체의 필드(속성)'에 적합한 형식 등으로 작성되었는지 등
        //  그 입력 데이터가 유효하게 작성되었는지 여부를 검증함.
        //- 1.'컨트롤러 클래스'나 '컨트롤러 내부의 해당 메소드'에 '@Valid'를 추가함.
        //- 2.검증의 대상이 되는 Bean 객체(여기서는 'MemberForm 객체' 내부에 '유효성 검사의 대상이 되는 어노테이션(저기서는
        //    @NotEmpty)을 추가함.

        Address address = new Address(form.getCity(), form.getStreet(), form.getZipcode());
        //'createMember.html'에서 '사용자가 입력한 city,street, zipcode'를 가져와서
        //그것으로 '새로운 Address 객체'를 만들어줌.


        //아래처럼 이렇게 setter 열어서 컨트롤러에서 이렇게 사용하는 건 좋지 못한 설계임. 
        //이것보단, Member 객체를 여기 메소드의 매개변수로 받아와서 사용하는 것이 좋음.
        //근데 이건 그냥 간단한 예제이기에 아래처럼 setName, setAddress 이렇게 사용함.

        Member member = new Member(); //'createMember.html'에서 사용자가 회원가입하려 입력한 데이터를 이제
        //'새로운 Member 객체'를 만들어서,
        member.setName(form.getName()); //각각 알맞게 여기에 넣어서 저장해줌
        member.setAddress(address); //각각 알맞게 여기에 넣어서 저장해줌


        memberService.join(member); //이제, 위에서 '새로운 Member 객체'에 저장한 사용자로부터 받아온 회원가입에 필요한 데이터를
        //MemberService의 해당되는 메소드를 통해 저장함.

        return "redirect:/"; //위에서 작업을 다 마쳤기 때문에, 그냥 뭐 리다이렉트로 '최초 페이지'로 화면을 넘겨버림 그냥. 별 이유 없음.


    }


//========================================================================================================

    //[ '회원 목록 조회'강. 00:00~ ]. '실전! 스프링 부트와 JPA 활용1 - 웹 애플리케이션 개발'
    //현재 전체 회원 조회
    //'서버 ---> 화면'의 과정
    @GetMapping("/members")
    public String list(Model model) { //이렇게 메소드의 매개변수로 Model 객체가 오는 경우는,
                                      //브라우저 화면에 데이터를 표시하기 위해 '뷰'에 보낼 때 사용되는 메소드라 생각하면 됨!


        List<Member> members = memberService.findMembers(); //'서비스'에서 '레퍼지토리'를 통해 DB로부터 데이터 가져오고
                                                            //그 가져온 '서비스 데이터'를 다시 여기 '컨트롤러'에서
                                                            //그 서비스를 호출해서 그 데이터를 가져옴.
        //cf) 근데 사실 이렇게 바로 엔티티를 화면에 건네주면 안되는 것임.
        //    이건 그냥 타임리프와 같은 '템플릿 엔진'을 사용하기에, 화면도 다 서버 사이드에서 처리되고 하고,
        //    또, 간단해서 바로 엔티티를 넘긴 것인데,
        //    백-프론트 분리해서 API 작성할 때는 반드시 DTO로 감싸서 넘겨줘야 함.

        model.addAttribute("members", members); //서비스로부터 가져온 전체 회원 목록 데이터를

        return "members/memberList"; //'URL링크 members/memberList'로 보내고,
                                     //'뷰 memberList'에서 그 'URL 링크 members/memberList'와 연결해서 화면에 데이터를 띄워줌.
    }


}
