package home.project.controller;


import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;



@Controller
public class HomeController {

    @GetMapping("/")
    public String home() {
        return "최영원 바보"; // 뷰 이름을 반환합니다. 이 경우 "index.html"로 연결됩니다.
    }
}