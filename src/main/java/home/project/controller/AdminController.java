package home.project.controller;

import home.project.domain.Member;
import home.project.service.AdminService;
import home.project.service.MemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

@RestController
public class AdminController {

    private final AdminService adminService;

    @Autowired
    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    @GetMapping("findMember")
    public ResponseEntity<Optional<Member>> findMember(@RequestParam("memberId") Long memberId){
        Optional<Member> member =  adminService.findById(memberId);
        return ResponseEntity.ok(member);
    }

    @GetMapping("findAllMember")
    public ResponseEntity<List<Member>> findAllMember() {
        List<Member> memberList = adminService.findAll();
        return ResponseEntity.ok(memberList);
    }

}
