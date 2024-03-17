package home.project.controller;


import home.project.domain.LoginDto;
import home.project.domain.Member;
import home.project.domain.Product;
import home.project.domain.TokenDto;
import home.project.service.MemberService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
//import io.swagger.v3.oas.models.PathItem;
import io.swagger.v3.oas.models.PathItem;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Tag(name = "회원", description = "회원관련 API 입니다")
//@RequestMapping(path = "/api/member")
@RequestMapping(path = "/api/member")
@RestController
public class MemberController {
    private final MemberService memberService;

    @Autowired
    public MemberController(MemberService memberService) {
        this.memberService = memberService;
    }

//    @Operation(summary = "로그인 메서드", description = "로그인 메서드입니다.")
//    @PostMapping("Login")
//    public ResponseEntity<?> login(@RequestBody @Valid LoginDto loginDto, BindingResult bindingResult) {
//        if (bindingResult.hasErrors()) {
//            Map<String, String> errorMap = new HashMap<>();
//            for (FieldError error : bindingResult.getFieldErrors()) {
//                errorMap.put(error.getField(), error.getDefaultMessage());
//            }
//            return new ResponseEntity<Map<String, String>>(errorMap, HttpStatus.BAD_REQUEST);
//        }
//        try {
//            memberService.login(loginDto);
//            return ResponseEntity.ok(loginDto);
//        } catch (DataIntegrityViolationException e) {
//            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
//        }
//
//    }

    @Operation(summary = "회원가입 메서드", description = "회원가입 메서드입니다.")
    @PostMapping("Join")
//    @ApiResponses(value = {
//            @ApiResponse(responseCode = "200", description = "successful operation", content = @Content(schema = @Schema(implementation = LoginResponse.class))),
//            @ApiResponse(responseCode = "400", description = "bad request operation", content = @Content(schema = @Schema(implementation = LoginResponse.class)))
//    })
    public ResponseEntity<?> createMember(@RequestBody @Valid Member member, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            Map<String, String> errorMap = new HashMap<>();
            for (FieldError error : bindingResult.getFieldErrors()) {
                errorMap.put(error.getField(), error.getDefaultMessage());
            }
            return new ResponseEntity<Map<String, String>>(errorMap, HttpStatus.BAD_REQUEST);
        }
        try {
            memberService.join(member);
            return ResponseEntity.ok(member);
        } catch (DataIntegrityViolationException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @Operation(summary = "이메일로회원조회 메서드", description = "이메일로회원조회 메서드입니다.")
    @GetMapping("FindByEmail")
    public ResponseEntity<Optional<Member>> findMember(@RequestParam("MemberEmail") String email) {
        Optional<Member> member = memberService.findByEmail(email);
        return ResponseEntity.ok(member);
    }

    @Operation(summary = "전체회원조회 메서드", description = "전체회원조회 메서드입니다.")
    @GetMapping("FindAllMember")
    public ResponseEntity<List<Member>> findAllMember() {
        List<Member> memberList = memberService.findAll();
        return ResponseEntity.ok(memberList);
    }

    @Operation(summary = "회원정보업데이트(수정) 메서드", description = "회원정보업데이트(수정) 메서드입니다.")
    @PutMapping("UpdateMember")
    public ResponseEntity<?> updateProduct(@RequestBody @Valid Member member, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            Map<String, String> errorMap = new HashMap<>();
            for (FieldError error : bindingResult.getFieldErrors()) {
                errorMap.put(error.getField(), error.getDefaultMessage());
            }
            return new ResponseEntity<Map<String, String>>(errorMap, HttpStatus.BAD_REQUEST);
        }
        memberService.update(member);
        return ResponseEntity.ok(member);
    }

    @Operation(summary = "멤버 삭제 메서드", description = "멤버를 삭제하는 메서드입니다.")
    @DeleteMapping("DeleteMember") // 멤버 삭제
    public ResponseEntity<?> deleteMember(@RequestParam("memberid") Member member) {
        memberService.deleteMember(member);
        return ResponseEntity.ok(member);

    }
}