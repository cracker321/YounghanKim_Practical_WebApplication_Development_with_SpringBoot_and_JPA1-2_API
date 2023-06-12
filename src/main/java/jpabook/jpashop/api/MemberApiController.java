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
import java.util.stream.Collectors;

import static org.springframework.data.jpa.domain.AbstractPersistable_.id;

@AllArgsConstructor
@Data
@RestController //@Responsebody와 @Controller가 합쳐진 어노테이션임.
public class MemberApiController {

    private final MemberService memberService;



//========================================================================================================

    //[ 회원 조회 API. 00:00~ ] 실전! 스프링 부트와 JPA 활용2 - API 개발과 성능 최적화


    //-------------------------------------------------------------------------------------------------

    //< API v1: 좋지 않은 방식 >. pdf p8.

    //- 엔티티의 '모든 속성(필드)이 전부 노출'되어 전달되기 때문.
    //- 어느 API에서는 '회원 엔티티 Member 객체'의 '모든 필드(속성) 정보들(회원명, 회원주소, 회원주문내역)'을 다 요구하기도 하지만,
    //  어떤 API에서는 '회원 엔티티 Member 객체'의 '회원명' 속성만 요구할 수도 있는 등,
    //  실무에서는 정말 다양한 API에서 다양하게 '회원 Member 객체의 다양한 속성들'을 '일부 도는 전부 취사선택 해야 하기 때문'에,
    //  이렇게 v1처럼 '회원 Member 객체의 모든 속성(필드)들'을 다 가져와서 노출시키는 것은 좋지 않다.
    //  (만약, 극단적으로 '회원 엔티티 Member'에 '비밀번호 속성(필드)'를 입력하는 경우도 있을 수 있기 때문)
    //  물론, API의 개수 다양성이 적거나 하는 경우에는, 내가 노출시키고 싶지 않은 '회원 Member 객체의 일부 속성(필드)'의
    //  위에 '@JsonIgnore'를 작성하면, 프론트단에서 포스트맨으로 통신해서 회원 조회할 때, 전부 또는 그 특정 회원의
    //  그 어노테이션이 적용된 필드는 화면에 보이지 않지만,
    //  실무는 엄청 API가 다양하고 '회원 엔티티 Member 객체'가 다양한 곳에서 사용되기 때문에, 그렇게 하면 안된다!


    @GetMapping("/api/v1/members")
    public List<Member> memberV1(){


        //- 'List<Member>': '전체 회원의 모든 속성을 다 보여주는 전체 회원 목록'

        List<Member> member = memberService.findMembers();
        //또는 그냥 아래 return문이랑 합쳐서
        //'return memberService.findMembers();'라고 작성해도 됨.
        //번외) 만약, 여기 메소드에서 매개변수로 'Member member'을 받았다면,
        //    여기서처럼 이렇게 List<Member> member = memberService.findMembers()를 통해
        //    새롭게 '변수 member'를 절대로 정의할 수 없다.
        //    즉, '매개변수 Member member'로 들어왔으면, 그 들어온 매개변수 member를 이 안에서 다시 재정의할 수 없다!

        return member;

    }


    //-------------------------------------------------------------------------------------------------


    //< API v2: 좋은 방식 >. pdf p9.

    @GetMapping("api/v2/members")
    public Result memberV2(){

        List<Member> findMembers = memberService.findMembers();

        //# 'List<Member>'를 'List<MemberDto>'로 바꾸는 과정
        //- 'List<Member>': '회원 엔티티 Member'의 '모든 속성(필드) 정보'가 다 담겨져 있는
        //                  '전체 회원의 모든 속성을 다 보여주는 전체 회원 목록'.
        //                  즉, '회원 엔티티 Member'의 속성으로 설정되어 있는 '회원명', '회원주소', '회원주문내역' 모두를
        //                  클라이언트단에 보여주는 것.
        //- 'List<MemberDto>': '전체 회원의 속성 중 '회원명 name' 속성만 뽑아서(꺼내서) 보여주는 전체 회원 목록'
        //                     '전체 회원의 모든 속성을 다 보여주는 전체 회원 목록'을 보여주는 'List<Member>'에서
        //                     '내부클래스 MemberDto'를 사용하여
        //                     '전체 회원의 속성 중 '회원명 name' 속성만 뽑아서(꺼내서) 보여주는 전체 회원 목록'을
        //                     클라이언트단에 보여주는 것.
        //                     stream 반복문을 사용하여(for문 또는 for-each문 사용해도 됨. java8 이상부터는 stream 지원함)
        //                     'List<Member>'에서 '이름 name 속성'만 쏙 '꺼내오는(=반복문 사용)' 것임.
        List<MemberDto> collect = findMembers.stream()
                .map(m -> new MemberDto(m.getName())) //여기서 'm'은
                                                      //'리스트 컬렉션 클래스 findMembers의 내부 각각의 개별 회원 리스트 객체'를
                                                      //의미하는 것임!!
                .collect(Collectors.toList());
        //1.'findMembers.stream()':
        //- '전체 회원의 모든 속성을 다 보여주는 전체 회원 목록 findMembers'에서 '스트림을 생성'함.
        //  스트림 API를 사용하기 위해 이 목록에서 'Stream<Member> 객체'를 생성함.
        //  이 스트림은 각 회원 객체에 의한 참조를 포함함.
        //- 이 스트림 연산의 최종 결과는 '전체 회원(엔티티 Member)의 '이름 name' 속성만 포함하는 'MemberDto 객체'를 담는 리스트.
        //- 스트림은 반복 및 변환 작업을 보다 쉽게 수행할 수 있게 해줌.
        //2.'map(m -> new MemberDto(m.getName()))':
        //- '각각의 회원 엔티티 Member 객체'에서 '이름 name' 속성만 추출하여, 'MemberDto 객체'에 '인자'로 넣어
        //  'MemberDto 스트림 객체('Stream<MemberDto> 객체')'로 변환시킴.
        //- 여기서 'm'은 '리스트 컬렉션 클래스 findMembers의 내부 각각의 원소인 '개별 회원 엔티티 Member 객체''를 의미하는 것임!!
        //3.collect(Collectors.toList)
        //- 위 2번에서의 'MemberDto 스트림 객체(Stream<MemberDto> 객체)'를 다시 'MemberDto 리스트 객체'로 변환시키는 과정임.
        //  이 과정을 통해 'List<MemberDto> 객체'로 변환시킬 수 있음.
        //- 작동과정
        //  (1)'MemberDto 스트림 객체(Stream<MemberDto> 객체)'를 '비어있는 List 객체'로 초기화, 변경시킴.
        //  (2)기존 스트림객체의 각 원소들을 새롭게 만든 '비어있는 List 객체'의 내부에 그 원소 객체로 넣음.
        //  (3)원소를 넣는 과정을 반복한 후, 최종 리스트 객체를 반환함.
        //     즉, 이제 'Stream<MemberDto> 객체'는 'List<MemberDto> 객체'로 변환되어 '변수 collect에 할당됨'.

        return new Result(collect, collect.size());
        //저 아래에서 생성된 '클래스 Result<T>'가,
        //여기서의 '변수 collect(=List<MemberDto> 객체)'를 '감싸서' 'API 응답 객체를 생성'하고, 이를 클라이언트에 전달하는 역할을 수행함.
        //이렇게 감싸지 않으면, 리스트는 리스트 객체인데, json은 배열 타입(?)이기 때문에 유연성이 확 떨어짐.

        //만약,
    }


    //-------------------------------------------------------------------------------------------------


    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    static class Result<T>{
        //'제네릭타입 T'를 사용하여 다양한 데이터타입의 데이터를 저장하고 반환하는 API 응답 객체로 사용됨.
        //컨트롤러 메소드의 'public 자료형 메소드명(){}' 에서, '자료형'에 'List<Member>', 'List<Order>' 등등을 넣고,
        //그 컨트롤러 메소드의 반환값(API 응답 객체. 'return ~')으로 사용할 수 있음!


        //< Result<T> 객체로 데이터를 감싸는 이유 >
        //https://wonsjung.tistory.com/421      : 반드시 참고하기. 김영한님 강의 QnA에서 참고링크 준 것임.

        //1.표준화된 응답 형식 구현:
        //Result 객체를 사용하면 미리 정의된 응답 형식을 유지할 수 있습니다.
        //이를 통해 클라이언트는 구조화된 데이터를 기대할 수 있으며 API 사용이 더 쉬워집니다.

        //2.응답 메타데이터 추가:
        //Result 객체는 응답에 메타데이터를 추가할 수 있는 구조를 제공합니다.
        //예를 들어, 페이지 정보, 오류 메시지, 상태 코드 등과 관련된 정보를 포함할 수 있습니다.
        //이러한 메타데이터는 클라이언트에게 데이터 이외의 유용한 정보를 제공하며, 자세한 오류 추적 및 디버깅에 도움을 줍니다.

        //3.유지 보수성 향상:
        //코드에서 분리된 구조로 응답 형식을 정의함으로써 코드 유지 관리가 쉬워집니다.
        //응답 형식이나 로직을 변경하려면 Result 클래스만 수정하면 됩니다.
        //이에 따라 코드베이스 전체에서 일관된 응답을 유지할 수 있으며, 변경 시 재사용성이 향상됩니다.
        /*

        장점 1: 표준화된 객체 사용

클래스 Result를 사용하면 API 응답 객체의 구조를 일관되게 관리할 수 있습니다.
이로 인해 코드 유지보수가 용이해집니다.
모든 API 응답을 동일한 클래스로 처리할 수 있으므로 예외 상황 처리와 재활용성이 향상됩니다.
예시 1:
예를 들어, 다양한 API 서비스에서 얻어온 데이터를 통하여 사용하고자 할 때, 모든 API의 응답 객체를 클래스 Result로 변환함으로써
보다 쉽게 데이터를 처리할 수 있습니다.


장점 2: 데이터 속성 추출 용이

클래스 Result는 API 응답 객체의 속성을 추출하여 사용하기에 적합한 메서드를 제공합니다.
이를 통 필요한 데이터를 빠르게 검색하고 처리할 수 있습니다.
예시 2:
API 응답이 JSON 형식이고 Result 클래스의 from_json 메서드를 사용하면 JSON 데이터에서 원하는 속성을 쉽게 추출할 수 있습니다.

장점 3: 타입 안정성 제공
클래스 Result를 사용하면 API 응답 객체의 데이터 타입을 명확하게 할 수 있으며, 강타입 언어를 사용할 때 오류를 줄여줍니다.
예시 3:
데이터 베이스 API 에서 반환된 응답 데이터가 문자열, 숫자 또는 날짜 형식일 수 있는 경우,
클래스 Result를 사용하여 데이터 타입을 명시적으로 선언할 수 있습니다. 이를 통해 코드가 더 안정적이고 에러 케이스를 줄일 수 있습니다.


장점 4: 가독성 향상
클래스 Result를 사용하면 API 응답 객체를 직관적으로 파악할 수 있습니다.
복잡한 데이터 구조를 클래스를 통해 체계적으로 정리할 수 있어 가독성이 향상됩니다.
예시 4:
큰 JSON 응답 객체를 클래스 Result로 변환하여 사용하면 소스 코드 가독성이 향상됩니다. JSON 데이터를 일반적인 객체로 변환하여 사용하는 것보다 클래스 를 이용하여 파악할 수 있으므로 전체 코드의 이해도가 올라갑니다.

장점 5: 유닛 테스트 작성 용이
클래스 Result를 사용하면 API 응답 객체에 대한 유닛 테스트를 효과적으로 작성할 수 있습니다.
개별 메서드의 기능을 분리하여 테스트할 수 있어 테스트 커버리지가 향상됩니다.
예시 5:
API 응답 객체를 클래스 Result로 변환면 해당 객체의 동작을 검증할 유닛 테스트를 작성할 수 있습니다. 메서드 단위의 테스트가 가능해지므로 결과적으로 전체 어플리케이션의 안정성이 향상됩니다.

이러한 장점들은 클래스 Result를 사용할 때 얻을 수 있는 주요 이점입니다.
이를 통해 개발자들은 API 응답 객체를 효과적으로 관리하고 이용할 수 있습니다.
         */

        private T data; //포스트맨으로 json 데이터 보내서 확인해보면, 'data'가 저 위에 'dto 데이터'를 감싸서(포장해서)
                        //보여주고 있음.
                        //T 는 Object 타입인 것 같음(?). 'generic raw type'으로 검색해보기.
        private int count; //만약, '전체 회원의 숫자'를 알고 싶으면, 이렇게 '변수 count'를 넣어주고,
                           //컨트롤러 메소드 memberV2 의 응답 객체 속에 인자값으로 collect.size() 를 넣어주기만 하면 된다!

    }

    //-------------------------------------------------------------------------------------------------


    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    static class MemberDto{ //서버 db '회원(들)의 이름'만 가져와서 클라이언트단에 넘겨주기 위해, name만 dto에 넣었음.

        private String name; //내가 클라이언트에 넘겨주고 싶은(노출하고 싶은) '회원 엔티티 Member 객체의 특정 필드(속성)만'
                             //dto에 넣음.

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
