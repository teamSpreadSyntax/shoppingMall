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
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
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
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "successful operation", content = @Content(schema = @Schema(implementation = Member.class))),
            @ApiResponse(responseCode = "400", description = "bad request operation", content = @Content(schema = @Schema(implementation = Member.class))),
            @ApiResponse(responseCode = "403", description = "접근이 금지되었습니다.", content = @Content(schema = @Schema(implementation = Member.class))),
            @ApiResponse(responseCode = "404", description = "요청한 리소스를 찾을 수 없습니다.", content = @Content(schema = @Schema(implementation = Member.class)))
    })
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
    public CustomOptionalResponseEntity<Optional<Member>> findMember(@RequestParam("MemberEmail") String email) {
        try {
            Optional<Member> memberOptional = memberService.findByEmail(email);
            String successMessage = email+"로 회원 조회 성공";
            CustomOptionalResponseBody<Optional<Member>> responseBody = new CustomOptionalResponseBody<>(memberOptional, successMessage);
            return new CustomOptionalResponseEntity<>(responseBody, HttpStatus.OK);
        } catch (DataIntegrityViolationException e) {
            CustomOptionalResponseBody<Optional<Member>> errorBody = new CustomOptionalResponseBody<>(null, "Validation failed");
            return new CustomOptionalResponseEntity<>(errorBody, HttpStatus.BAD_REQUEST);
        }
    }

    @Operation(summary = "전체회원조회 메서드", description = "전체회원조회 메서드입니다.")
    @GetMapping("FindAllMember")
    public CustomListResponseEntity<List<Member>> findAllMember() {
            try {
                List<Member> memberList = memberService.findAll();
                String successMessage = "전체회원 조회 성공";
                CustomListResponseBody<List<Member>> responseBody = new CustomListResponseBody<>(memberList, successMessage);
                return new CustomListResponseEntity<>(responseBody, HttpStatus.OK);
            } catch (DataIntegrityViolationException e) {
                CustomListResponseBody<List<Member>> errorBody = new CustomListResponseBody<>(null, "Validation failed");
                return new CustomListResponseEntity<>(errorBody, HttpStatus.BAD_REQUEST);
            }
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

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<Map<String, String>> handleIllegalStateException(IllegalStateException e) {
        Map<String, String> errorMap = new HashMap<>();
        errorMap.put("errorMessage", e.getMessage());
        return new ResponseEntity<>(errorMap, HttpStatus.BAD_REQUEST);
    }

}