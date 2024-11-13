package home.project.controller.common;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin(origins = "http://localhost:5173")
@RestController
public class HomeController {

    @GetMapping("/home")
    public String home() {
        return "재고관리 프로젝트입니다";
    }

}