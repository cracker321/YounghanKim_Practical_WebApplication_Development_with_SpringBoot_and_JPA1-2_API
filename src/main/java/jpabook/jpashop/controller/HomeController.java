package jpabook.jpashop.controller;


import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@Slf4j
public class HomeController {


    //[ 실전! 스프링 부트와 JPA 활용1 - 웹 애플리케이션 개발 ]
    //[ '홈 화면과 레이아웃'강. 00:00~ ]
    @RequestMapping("/")
    public String home(){

        log.info("home controller");
        return "home"; //'home.html'로 들어가게 되는 경로임.


    }
}
