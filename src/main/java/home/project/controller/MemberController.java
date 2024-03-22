package home.project.controller;


//import home.project.domain.LoginDto;


import home.project.domain.*;
//import home.project.domain.TokenDto;
import home.project.service.MemberService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
//import io.swagger.v3.oas.models.PathItem;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
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
//@RequestMapping(path = "/api/member")
@RequestMapping(path = "/api/member")
@ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "successful operation", content = @Content(schema = @Schema(implementation = Member.class))),
        @ApiResponse(responseCode = "400", description = "bad request operation", content = @Content(schema = @Schema(implementation = Member.class))),
        @ApiResponse(responseCode = "403", description = "접근이 금지되었습니다.", content = @Content(schema = @Schema(implementation = Member.class))),
        @ApiResponse(responseCode = "404", description = "요청한 리소스를 찾을 수 없습니다.", content = @Content(schema = @Schema(implementation = Member.class)))
})
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
            Map<String, String> responseMap = new HashMap<>();
            responseMap.put("회원가입완료", member.getEmail()+"로 가입되었습니다");
            return new ResponseEntity<Map<String, String>>(responseMap, HttpStatus.OK);
        } catch (DataIntegrityViolationException e) {
            Map<String, String> responseMap = new HashMap<>();
            responseMap.put("중복된 값이 입력되었습니다. 이메일 또는 전화번호로 이미 가입되어있습니다", e.getMessage()+"--->위 로그중 Duplicate entry '?'에서 ?는 이미 있는값입니다()");
            return new ResponseEntity<Map<String, String>>(responseMap, HttpStatus.BAD_REQUEST);
        }
    }

    @Operation(summary = "이메일로회원조회 메서드", description = "이메일로회원조회 메서드입니다.")
    @GetMapping("FindByEmail")
    public CustomOptionalMemberResponseEntity<Optional<Member>> findMember(@RequestParam("MemberEmail") @Valid String email) {
        if (email == null || email.isEmpty()) {
            throw new IllegalStateException("이메일이 입력되지 않았습니다.");
        }
        try {
            Optional<Member> memberOptional = memberService.findByEmail(email);
            String successMessage = email+"로 가입된 회원정보입니다";
            CustomOptionalMemberResponseBody<Optional<Member>> responseBody = new CustomOptionalMemberResponseBody<>(memberOptional, successMessage);
            return new CustomOptionalMemberResponseEntity<>(responseBody, HttpStatus.OK);
        } catch (DataIntegrityViolationException e) {
            CustomOptionalMemberResponseBody<Optional<Member>> errorBody = new CustomOptionalMemberResponseBody<>(null, "Validation failed");
            return new CustomOptionalMemberResponseEntity<>(errorBody, HttpStatus.BAD_REQUEST);
        }
    }

    @Operation(summary = "전체회원조회 메서드", description = "전체회원조회 메서드입니다.")
    @GetMapping("FindAllMember")
    public CustomListMemberResponseEntity<List<Member>> findAllMember() {
            try {
                List<Member> memberList = memberService.findAll();
                String successMessage = "전체회원 입니다";
                CustomListMemberResponseBody<List<Member>> responseBody = new CustomListMemberResponseBody<>(memberList, successMessage);
                return new CustomListMemberResponseEntity<>(responseBody, HttpStatus.OK);
            } catch (DataIntegrityViolationException e) {
                CustomListMemberResponseBody<List<Member>> errorBody = new CustomListMemberResponseBody<>(null, "Validation failed");
                return new CustomListMemberResponseEntity<>(errorBody, HttpStatus.BAD_REQUEST);
            }
        }


    @Operation(summary = "회원정보업데이트(수정) 메서드", description = "회원정보업데이트(수정) 메서드입니다.")
    @PutMapping("UpdateMember")
    public ResponseEntity<?> updateMember(@RequestBody @Valid Member member, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            Map<String, String> errorMap = new HashMap<>();
            for (FieldError error : bindingResult.getFieldErrors()) {
                errorMap.put(error.getField(), error.getDefaultMessage());
            }
            return new ResponseEntity<Map<String, String>>(errorMap, HttpStatus.BAD_REQUEST);
        }
        try {
            Optional<Member> memberOptional = memberService.update(member);
            String successMessage = "정보가 수정되었습니다";
            CustomOptionalMemberResponseBody<Optional<Member>> responseBody = new CustomOptionalMemberResponseBody<>(memberOptional, successMessage);
            return new CustomOptionalMemberResponseEntity<>(responseBody, HttpStatus.OK);
        } catch (DataIntegrityViolationException e) {
            CustomOptionalMemberResponseBody<Optional<Member>> errorBody = new CustomOptionalMemberResponseBody<>(null, "Validation failed");
            return new CustomOptionalMemberResponseEntity<>(errorBody, HttpStatus.BAD_REQUEST);
        }
    }


    @Transactional
    @Operation(summary = "멤버 삭제 메서드", description = "멤버를 삭제하는 메서드입니다.")
    @DeleteMapping("DeleteMember") // 멤버 삭제
    public ResponseEntity<?> deleteMember(@RequestParam("memberEmail") String email) {
        try {
            memberService.deleteMember(email);
            Map<String, String> responseMap = new HashMap<>();
            responseMap.put("이용해주셔서 감사합니다", email+"님의 계정이 삭제되었습니다");
            return new ResponseEntity<Map<String, String>>(responseMap, HttpStatus.OK);
        } catch (DataIntegrityViolationException e) {
            Map<String, String> responseMap = new HashMap<>();
            responseMap.put(email+"로 가입되어있는 계정이 없습니다", e.getMessage());
            return new ResponseEntity<Map<String, String>>(responseMap, HttpStatus.BAD_REQUEST);
        }
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<Map<String, String>> handleIllegalStateException(IllegalStateException e) {
        Map<String, String> errorMap = new HashMap<>();
        errorMap.put("errorMessage", e.getMessage());
        return new ResponseEntity<>(errorMap, HttpStatus.BAD_REQUEST);
    }

}