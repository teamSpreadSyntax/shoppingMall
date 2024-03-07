package home.project.controller;


import home.project.domain.Member;
import home.project.service.MemberService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Tag(name = "회원", description = "회원관련 API 입니다")
@RequestMapping(path = "/api/member")
@RestController
public class MemberController {
    private final MemberService memberService;

    @Autowired
    public MemberController(MemberService memberService) {
        this.memberService = memberService;
    }

    @Operation(summary = "회원가입 메서드", description = "회원가입 메서드입니다.")
    @PostMapping("join")
//    @ApiResponses(value = {
//            @ApiResponse(responseCode = "200", description = "successful operation", content = @Content(schema = @Schema(implementation = LoginResponse.class))),
//            @ApiResponse(responseCode = "400", description = "bad request operation", content = @Content(schema = @Schema(implementation = LoginResponse.class)))
//    })
    public ResponseEntity<?> createMember(@RequestBody Member member, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            Map<String, String> errorMap = new HashMap<>();
            for (FieldError error : bindingResult.getFieldErrors()) {
                errorMap.put(error.getField(), error.getDefaultMessage());
            }
            return new ResponseEntity<Map<String, String>>(errorMap, HttpStatus.BAD_REQUEST);
        }try {
            memberService.join(member);
            return ResponseEntity.ok(member);
        }catch (DataIntegrityViolationException e){
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @Operation(summary = "로그인 메서드", description = "로그인 메서드입니다.")
    @PostMapping("login")
    public ResponseEntity<Member> login(@RequestBody Member member) {
        memberService.login(member);
        return ResponseEntity.ok(member);
    }

    @Operation(summary = "특정회원조회 메서드", description = "특정회원조회 메서드입니다.")
    @GetMapping("findMember")
    public ResponseEntity<Optional<Member>> findMember(@RequestParam("memberId") Long memberId){
        Optional<Member> member =  memberService.findById(memberId);
        return ResponseEntity.ok(member);
    }

    @Operation(summary = "전체회원조회 메서드", description = "특정회원조회 메서드입니다.")
    @GetMapping("findAllMember")
    public ResponseEntity<List<Member>> findAllMember() {
        List<Member> memberList = memberService.findAll();
        return ResponseEntity.ok(memberList);
    }

}