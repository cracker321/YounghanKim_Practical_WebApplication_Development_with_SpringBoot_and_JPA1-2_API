package jpabook.jpashop.controller;


import lombok.Data;

import javax.validation.constraints.NotEmpty;


//< 엔티티 객체 >
@Data
public class MemberForm {

    // < '@Valid' >
    //- 스프링에서 사용자가 '폼 form'을 통해 어떠한 데이터를 입력(회원가입에 필요한 정보 등)하면,
    //  그 입력된 데이터'가 '개발자가 작성한 해당되는 Bean 객체의 필드(속성)'에 적합한 형식 등으로 작성되었는지 등
    //  그 입력 데이터가 유효하게 작성되었는지 여부를 검증함.
    //- 1.'컨트롤러 클래스(여기서는 MemberController)'나 '컨트롤러 내부의 해당 메소드(메소드 create)'에 '@Valid'를 추가함.
    //- 2.검증의 대상이 되는 Bean 객체(여기서는 'MemberForm 객체' 내부에 '유효성 검사의 대상이 되는 어노테이션(저기서는
    //    @NotEmpty)을 추가함.
    @NotEmpty(message = "회원 이름은 필수입니다.") // 유효성 검사 어노테이션.
    private String name;
    private String city;
    private String street;
    private String zipcode;


}
