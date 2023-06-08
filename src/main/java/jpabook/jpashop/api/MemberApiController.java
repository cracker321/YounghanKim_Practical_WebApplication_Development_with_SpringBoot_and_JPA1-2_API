package jpabook.jpashop.api;


import jpabook.jpashop.service.MemberService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;


@RequiredArgsConstructor
@Data
@RestController //@Responsebody와 @Controller가 합쳐진 어노테이션임.
public class MemberApiController {

    private final MemberService memberService;

//========================================================================================================

    //[ 회원 등록 API. 00:00~ ]. 실전! 스프링 부트와 JPA 활용2 - API 개발과 성능 최적화

    //

    //public CreateMemberResponse saveMemberV1(@RequestBody)


    //- '@RequestBody': '클라이언트가 추가하고 싶은 사용자 정보'는 당연히 'Header(HTTP메시지의 요약본)'가 아닌
    //                  'Body(HTTP메시지의 본문)'에 들어있기에,
    //                  '@RequestHeader'가 아닌 '@RequestBody'에 담겨져 서버로 오는 것이다!



}
