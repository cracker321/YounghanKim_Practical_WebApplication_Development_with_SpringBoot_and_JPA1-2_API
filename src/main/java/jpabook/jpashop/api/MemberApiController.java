package jpabook.jpashop.api;


import jpabook.jpashop.domain.Member;
import jpabook.jpashop.service.MemberService;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;

import java.util.List;

import static org.springframework.data.jpa.domain.AbstractPersistable_.id;

@AllArgsConstructor
@Data
@RestController //@Responsebody와 @Controller가 합쳐진 어노테이션임.
public class MemberApiController {

    private final MemberService memberService;



//========================================================================================================

    //[ 회원 조회 API. 00:00~ ] 실전! 스프링 부트와 JPA 활용2 - API 개발과 성능 최적화


    //-------------------------------------------------------------------------------------------------

    //< API v1: 엔티티를 그대로 컨트롤러에서 사용하는 아주 위험한 방법을 사용하는 경우임 >


    @GetMapping("/api/v1/members")
    public List<Member> memberV1(){

        List<Member> member = memberService.findMembers();
        //또는 그냥 아래 return문이랑 합쳐서
        //'return memberService.findMembers();'라고 작성해도 됨.

        return member;

    }






//========================================================================================================

    //[ 회원 등록 API. 00:00~ ]. 실전! 스프링 부트와 JPA 활용2 - API 개발과 성능 최적화



    //-------------------------------------------------------------------------------------------------

    //< API v1: 엔티티를 그대로 컨트롤러에서 사용하는 아주 위험한 방법을 사용하는 경우임 >

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

    //< API v2: 컨트롤러에서 엔티티를 직접 사용하는 것이 아니고, 그 엔티티를 감싸고 있는 dto를 사용하는 경우를 보여주는 것임 >

    //- 여기서는 임시 dto 와 같이 만들어서 진행함.
    @PostMapping("/api/v2/members")
    public CreateMemberResponse saveMemberV2(@RequestBody @Valid CreateMemberRequest request){
        //여기서의 @Valid: 'Member 엔티티'가 아니라, 'Member 엔티티를 감싸주고 있는 dto인 request'의 내부 필드들을 유효성 검사 해줌.


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


    //< 클라이언트로부터 들어온 요청 Request용 dto >

    //- 클라이언트--->서버 '요청 request용 dto'
    //- 클라이언트로부터 들어오는 요청(=받은 데이터)을 최초로 가장 먼저 넘겨받아서 신규회원 정보를 임시 저장하고 처리할 목적으로 만든 클래스.
    //  즉, 일단 임시적인 DTO와 비슷한 것이라 생각하면 됨.
    //  즉, 사용자로부터 들어온 회원 생성 요청 데이터를 담는 용도로 만들어짐.
    //  HTTP POST 요청의 바디에 담긴 데이터를 전달하고 처리하기 위한 임시적인 데이터 객체임.
    //1.사용자는 '요청의 바디'에 '회원의 이름' 데이터를 담아 POST요청을 보냄.
    //2.스프링프레임워크로 전달된 데이터를 'CreateMemberRequest 객체'로 변환하여,
    //3.'메소드 saveMemberV2'의 '매개변수 request'에 전달함.
    //4.'request.getName()'을 통해 'CreateMemberRequest 객체'에서 '회원 이름'을 가져옴.


    //< dto 사용의 장점 >
    //- 업무 과정에서, 다른 개발자가 엔티티의 내부 속성 필드명과 같은 정보들을 조금이라도 수정 변경할 경우,
    //  그 엔티티가 연결된 모든 곳에서 에러가 발생하나,
    //  이렇게 dto로 감싸서 할 경우, 해당 엔티티를 감싼 dto가 사용된 컨트롤러 메소드 등 그 위치에서만 변경해주면 됨.


    //- static: static클래스는 여러 인스턴스(객체)들이 공유하여 사용할 수 있음.

    //- dto에는 사실 롬복 이것저것 많이 다 사용해도 됨. 실용적인 관점에서.
    //- 다만, 엔티티에는 김영한님은 @AllArgsConstructor 정도 등 몇 개 제한적으로만 사용한다고 함.
    @Data
    static class CreateMemberRequest{ //static이라서, 생성자를

        @NotEmpty //'유효성 검사'도 'Member 엔티티'의 내부에 직접 넣어주지 않고,
                  //이렇게 여기 dto의 내부 필드에 넣어주는 것이 더 낫다(그런 듯?)
        private String name;
    }


    //-------------------------------------------------------------------------------------------------

    //< 클라이언트에게 전달해주는 응답 Response용 dto >

    //- 서버--->클라이언트 '응답 response용 dto'
    @Data
    @NoArgsConstructor
    @AllArgsConstructor //필드가 다 차 있는 생성자를 생성해야, 저 위에서 'return new CreateMemberResponse(id)'에서
                        //'새로운 CreateMemberResponse 객체의 인자로 id를 넣어줄 수 있는 것임)
                        //왜냐하면, 이렇게 사용자 임의 생성자를 생성하면, 자바에서는 이 경우 '기본 생성자'가 자동으로 추가되지 않고
                        //내가 직접 다시 기본 생성자(=NoArgsConstructor)를 생성해줘야 한다.
    static class CreateMemberResponse{

        private Long id;

    }



//========================================================================================================

    //[ 회원 수정 API. 00:00~ ]. 실전! 스프링 부트와 JPA 활용2 - API 개발과 성능 최적화

    @PutMapping("/api/v2/members/{id}")
    public UpdateMemberResponse updateMemberV2(
            @PathVariable("id") Long memberId,
            @RequestBody
            @Valid UpdateMemberRequest request){
        //클라이언트가 본인이 수정하길 희망하는 회원정보인 '이름' 정보를 새롭게 수정하여 화면에 입력했고,
        //그 데이터를 dto인 'UpdateMemberRequest 객체'가 받아서,
        //여기 컨트롤러 메소드의 매개변수로 들어감.



        //*****중요*****
        //'메소드 findMember'는 'Member 객체를 반환'하기 때문에,
        //아래에서 'Long id = memberService.findMember(...)' 이렇게 하면 안 되고,
        //아래처럼 'Member member = memberService.findMember(...)' 이렇게 해야 한다!

        //Member member = memberService.findMember(memberId);


        //1.사용자로부터 전달받은, 사용자가 수정하길 희망하는 새로운 ID(='memberId')와 새로운 이름(='request.getName()')을
        //그 정보를 '서비스 MemberService의 메소드 update'에 넘겨주는 단계.
        memberService.update(memberId, request.getName());


        //2.위에서 그렇게 넘겨줘서 서비스, 레퍼지토리 거쳐서 db에 저장된 이제 '수정 완료된 회원 정보'를
        //여기에서 이제 db로부터 '다시 조회'해서 가져옴.
        //(김영한님은 이렇게 1번과 2번을 분리해서 사용하는 스타일이라고 함. 1번과 2번을 합쳐서 할 수 도 있는 듯.
        // 이게 유지보수성 증대에 좋다고 함.)
        Member findMember = memberService.findOne(memberId);


        return new UpdateMemberResponse(memberId, request.getName());
        //저기 아래에서 생성된 dto인 UpdateMemberResponse 객체의 생성자는 AllArgs..로 생성했기 때문에, 이렇게 쓰는 것 가능함.


        //'새로운 UpdateMemberResponse 객체'를 '생성'하고 있기 때문에( (클 참 = ) 뉴 클 )
        //이 메소드 안에서 'Member 객체를 매개변수로 받는 새로운 사용자 생성자 UpdateMemberResponse'를 생성해줘야 함.
    }


    //-------------------------------------------------------------------------------------------------


    //< 클라이언트로부터 들어온 요청 Request용 dto >

    //- 사용자가 화면에서 본인이 수정하고자 해서 수정한 정보는 '이름 name' 하나임.
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    static class UpdateMemberRequest{

        private String name;
    }


    //-------------------------------------------------------------------------------------------------


    //< 클라이언트에게 전달해주는 응답 Response용 dto >

    //- 수정한 정보들을 db에 다 저장해서 다 과정 처리한 후에, 이제 사용자에게 다시 넘겨줘서 화면에 띄어주는 정보는
    //  '그 회원의 id'와, '그 회원의 이름'을 넘겨줌.

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    static class UpdateMemberResponse{

        private Long id;
        private String name;
    }


//========================================================================================================



//========================================================================================================



//========================================================================================================



}
