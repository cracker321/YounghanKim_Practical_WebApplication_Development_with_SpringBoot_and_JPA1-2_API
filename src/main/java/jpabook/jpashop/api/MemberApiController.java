package jpabook.jpashop.api;


import jpabook.jpashop.domain.Member;
import jpabook.jpashop.service.MemberService;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;


@RequiredArgsConstructor
@Data
@RestController //@Responsebody와 @Controller가 합쳐진 어노테이션임.
public class MemberApiController {

    private final MemberService memberService;

//========================================================================================================

    //[ 회원 등록 API. 00:00~ ]. 실전! 스프링 부트와 JPA 활용2 - API 개발과 성능 최적화


    //-------------------------------------------------------------------------------------------------


    //< API v1 >
    @PostMapping("/api/v1/members")
    public CreateMemberResponse saveMemberV1(@RequestBody @Valid Member member){
        //그런데, '컨트롤러'에서 '엔티티 Member'를 이렇게 매개변수로 바로 받아오면 큰 일 나는 것.
        //반드시, dto를 톻해 엔티티를 가져와서 사용해야 함.
        //이거는 예시 v1 이기 때문에, 일단 이렇게 먼저 해본 것인 뿐임.


        //< @Validation 유효성 검사 >
        //'Member 객체의 속성들 일부 또는 전부'에 '유효성 검사 Validation'을 걸어놓음.
        //'클래스 Member'에 들어가보면, '필드 name' 위에 '@NotEmpty'로 유효성 조건 걸어둠.
        //따라서, 만약 신규 회원 가입하고자 할 때, 그 신규 회원정보 입력할 때, '이름 name'을 공백으로 입력하면,
        //유효성 검사에 걸린다!

        Long id = memberService.join(member);

        return new CreateMemberResponse(id);
    }


    //- '@RequestBody': '클라이언트가 추가하고 싶은 사용자 정보'는 당연히 'Header(HTTP메시지의 요약본)'가 아닌
    //                  'Body(HTTP메시지의 본문)'에 들어있기에,
    //                  '@RequestHeader'가 아닌 '@RequestBody'에 담겨져 서버로 오는 것이다!




    //-------------------------------------------------------------------------------------------------

    //< API v2 >
    //- 여기서는 임시 dto 와 같이 만들어서 진행함.
    @PostMapping("/api/v2/members")
    public CreateMemberResponse saveMemberV2(@RequestBody @Valid CreateMemberRequest request){


        //1.사용자는 'Request 요청의 RequestBody 바디'에 '회원의 이름' 데이터를 담아 Json으로 POST요청을 보냄.
        //2.'클라이언트'로부터 '스프링프레임워크'로 전달된 데이터를 가장 최초로 넘겨받은 'CreateMemberRequest 객체'로 변환하여,
        //3.'메소드 saveMemberV2'의 '매개변수 request'에 전달함.


        Member member = new Member(); //신규회원 정보를 새롭게 담을 새로운 Member 객체를 만들고,

        //그 새로운 Member 객체의 '이름 필드(속성)'을
        //'사용자로부터 받아온 신규회원 정보 데이터를 담고 있는 CreateMemberRequest 객체'의 '이름'으로 설정함.
        //(즉, 'request.getName()'을 통해 'CreateMemberRequest 객체'에서 '회원 이름'을 가져옴.)
        member.setName(request.getName());


        Long id = memberService.join(member); //사용자가 전달한 '신규 회원정보'를 이제 '서버 DB에' 저장하고,
                                              //'그 회원 id'를 가져와서

        return new CreateMemberResponse(id); //이제 다시 이 dto 객체에, 서버에 저장된 그 회원의 id 정보를 담아서
                                             //이 dto 객체를 사용자에게 다시 반환함.

    }


    //-------------------------------------------------------------------------------------------------

    //< saveMemberV1 용으로 만든 것 >

    //- 클라이언트로부터 들어오는 요청(=받은 데이터)을 최초로 가장 먼저 넘겨받아서 신규회원 정보를 임시 저장하고 처리할 목적으로 만든 클래스.
    @Data
    @NoArgsConstructor
    @AllArgsConstructor //필드가 다 차 있는 생성자를 생성해야, 저 위에서 'return new CreateMemberResponse(id)'에서
                        //'새로운 CreateMemberResponse 객체의 인자로 id를 넣어줄 수 있는 것임)
                        //왜냐하면, 이렇게 사용자 임의 생성자를 생성하면, 자바에서는 이 경우 '기본 생성자'가 자동으로 추가되지 않고
                        //내가 직접 다시 기본 생성자(=NoArgsConstructor)를 생성해줘야 한다.
    static class CreateMemberResponse{

        private Long id;

    }

    //-------------------------------------------------------------------------------------------------

    //< saveMemberV2 용으로 만든 것 >
    //- 클라이언트로부터 들어오는 요청(=받은 데이터)을 임시 저장하고 처리할 목적으로 만든 클래스.
    //  즉, 일단 임시적인 DTO와 비슷한 것이라 생각하면 됨.
    //  즉, 사용자로부터 들어온 회원 생성 요청 데이터를 담는 용도로 만들어짐.
    //  HTTP POST 요청의 바디에 담긴 데이터를 전달하고 처리하기 위한 임시적인 데이터 객체임.
    //1.사용자는 '요청의 바디'에 '회원의 이름' 데이터를 담아 POST요청을 보냄.
    //2.스프링프레임워크로 전달된 데이터를 'CreateMemberRequest 객체'로 변환하여,
    //3.'메소드 saveMemberV2'의 '매개변수 request'에 전달함.
    //4.'request.getName()'을 통해 'CreateMemberRequest 객체'에서 '회원 이름'을 가져옴.

    //- static: static클래스는 여러 인스턴스(객체)들이 공유하여 사용할 수 있음.
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    static class CreateMemberRequest{
        private String name;
    }



//========================================================================================================



//========================================================================================================



//========================================================================================================



//========================================================================================================



}
