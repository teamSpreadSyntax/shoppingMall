package home.project.controller;


import home.project.domain.Member;
import home.project.repository.MemberRepository;
import home.project.service.MemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
public class MemberController {
    private final MemberService memberService;

    @Autowired
    public MemberController(MemberService memberService) {
        this.memberService = memberService;
    }

    @PostMapping("/")
    public ResponseEntity<Member> createMember(@RequestBody Member member) {
        memberService.join(member);
        return ResponseEntity.ok(member);
    }

    @GetMapping("/get")
    public ResponseEntity<List<Member>> getMember() {
        List<Member> memberList = memberService.findAll();
        return ResponseEntity.ok(memberList);
    }

    @GetMapping("findMember")
    public  ResponseEntity<Optional<Member>> findById(@RequestParam("memberId") Long memberId){
        Optional<Member> member =  memberService.findById(memberId);
        return ResponseEntity.ok(member);
    }
}