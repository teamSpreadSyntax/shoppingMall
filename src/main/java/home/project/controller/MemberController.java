package home.project.controller;


import home.project.domain.Member;
import home.project.service.MemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class MemberController {
    private final MemberService memberService;

    @Autowired
    public MemberController(MemberService memberService) {
        this.memberService = memberService;
    }

    @PostMapping("join")
    public ResponseEntity<Member> createMember(@RequestBody Member member) {
        memberService.join(member);
        return ResponseEntity.ok(member);
    }



}