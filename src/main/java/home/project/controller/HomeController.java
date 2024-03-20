package home.project.controller;


import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
public class HomeController {

    @GetMapping("/home")
    public String home() {
        return "재고관리 프로그램"; // 뷰 이름을 반환합니다. 이 경우 "index.html"로 연결됩니다.
    }
}