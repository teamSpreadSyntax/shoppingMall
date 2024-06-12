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
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.SortDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

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
    public ResponseEntity<?> createMember(@RequestBody @Valid MemberDTOWithoutId memberDTO, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            Map<String, String> responseMap = new HashMap<>();
            for (FieldError error : bindingResult.getFieldErrors()) {
                responseMap.put(error.getField(), error.getDefaultMessage());
            }
            CustomOptionalResponseBody<Optional<Member>> errorBody = new CustomOptionalResponseBody<>(Optional.ofNullable(responseMap), "Validation failed", HttpStatus.BAD_REQUEST.value());
            return new CustomOptionalResponseEntity<>(errorBody, HttpStatus.BAD_REQUEST);
        }

        Member member = new Member();
        member.setEmail(memberDTO.getEmail());
        member.setPassword(memberDTO.getPassword());
        member.setName(memberDTO.getName());
        member.setPhone(memberDTO.getPhone());
        memberService.join(member);

        Map<String, String> responseMap = new HashMap<>();
        responseMap.put("회원가입완료", member.getEmail()+"로 가입되었습니다");
        CustomOptionalResponseBody<Optional<Member>> responseBody = new CustomOptionalResponseBody<>(Optional.of(responseMap), "회원가입 성공", HttpStatus.OK.value());
        return new CustomOptionalResponseEntity<>(responseBody, HttpStatus.OK);
    }


    @Operation(summary = "이메일로회원조회 메서드", description = "이메일로회원조회 메서드입니다.")
    @GetMapping("FindByEmail")
    public CustomOptionalResponseEntity<Optional<Member>> findMemberByEmail(@RequestParam("MemberEmail") @Valid String email) {
        if (email == null || email.isEmpty()) {
            throw new IllegalStateException("이메일이 입력되지 않았습니다.");
        }
        if (!email.matches("^[0-9a-zA-Z]([-_.]?[0-9a-zA-Z])*@[0-9a-zA-Z]([-_.]?[0-9a-zA-Z])+[.][a-zA-Z]{2,3}$")) {
            throw new IllegalArgumentException("이메일 형식이 올바르지 않습니다.");
        }
            Optional<Member> memberOptional = memberService.findByEmail(email);
            String successMessage = email+"로 가입된 회원정보입니다";
            return new CustomOptionalResponseEntity<>(Optional.ofNullable(memberOptional), successMessage, HttpStatus.OK);
    }

    @Operation(summary = "ID로 회원조회 메서드", description = "ID로 회원조회 메서드입니다.")
    @GetMapping("findMemberById")
    public CustomOptionalResponseEntity<Optional<Member>> findMemberById(@RequestParam("MemberId") Long ID) {
        if (ID == null) {
            throw new IllegalStateException("id가 입력되지 않았습니다.");
        }
            Optional<Member> memberOptional = memberService.findById(ID);
            String successMessage = ID+"으로 가입된 회원정보입니다";
            return new CustomOptionalResponseEntity<>(Optional.ofNullable(memberOptional), successMessage, HttpStatus.OK);
    }

    @Operation(summary = "전체회원조회 메서드", description = "전체회원조회 메서드입니다.")
    @GetMapping("FindAllMember")
    public CustomListResponseEntity<MemberDTOWithoutPw> findAllMember(
            @PageableDefault(page = 0, size = 5)
            @SortDefault.SortDefaults({
                    @SortDefault(sort = "id", direction = Sort.Direction.ASC)
            }) @ParameterObject Pageable pageable) {
        Page<Member> memberPage = memberService.findAll(pageable);
        Page<MemberDTOWithoutPw> memberDtoPage = memberPage.map(member ->
                new MemberDTOWithoutPw(member.getId(), member.getEmail(), member.getName(), member.getPhone()));
        String successMessage = "전체 회원입니다";
        long totalCount = memberPage.getTotalElements();
        int page = memberPage.getNumber();

        return new CustomListResponseEntity<>(memberDtoPage.getContent(), successMessage, HttpStatus.OK, totalCount, page);
    }

    @Operation(summary = "회원정보업데이트(수정) 메서드", description = "회원정보업데이트(수정) 메서드입니다.")
    @PutMapping("UpdateMember")
    public ResponseEntity<?> updateMember(@RequestBody @Valid Member member, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            Map<String, String> responseMap = new HashMap<>();
            for (FieldError error : bindingResult.getFieldErrors()) {
                responseMap.put(error.getField(), error.getDefaultMessage());
            }
            CustomOptionalResponseBody<Optional<Product>> errorBody = new CustomOptionalResponseBody<>(Optional.ofNullable(responseMap), "Validation failed", HttpStatus.BAD_REQUEST.value());
            return new CustomOptionalResponseEntity<>(errorBody, HttpStatus.BAD_REQUEST);
        }
        try {
            Optional<Member> memberOptional = memberService.update(member);
            String successMessage = "정보가 수정되었습니다";
            return new CustomOptionalResponseEntity<>(Optional.ofNullable(memberOptional), successMessage, HttpStatus.OK);
        } catch (DataIntegrityViolationException e) {
            Map<String, String> responseMap = new HashMap<>();
            responseMap.put("중복된 값이 입력되었습니다. 해당 이메일 또는 전화번호는 이미 등록되어있습니다", e.getMessage() + "--->위 로그중 Duplicate entry '?'에서 ?는 이미 있는값입니다()");
            CustomOptionalResponseBody<Optional<Member>> errorBody = new CustomOptionalResponseBody<>(Optional.ofNullable(responseMap), "이메일 또는 전화번호 중복", HttpStatus.CONFLICT.value());
            return new CustomOptionalResponseEntity<>(errorBody, HttpStatus.BAD_REQUEST);
        }
    }


    @Transactional
    @Operation(summary = "멤버 삭제 메서드", description = "멤버를 삭제하는 메서드입니다.")
    @DeleteMapping("DeleteMember") // 멤버 삭제
    public ResponseEntity<?> deleteMember(@RequestParam("memberId") Long memberId) {
        try {
            memberService.deleteMember(memberId);
            Map<String, String> responseMap = new HashMap<>();
            responseMap.put("이용해주셔서 감사합니다", memberId+"님의 계정이 삭제되었습니다");
            CustomOptionalResponseBody responseBody = new CustomOptionalResponseBody<>(Optional.ofNullable(responseMap),"회원삭제 성공", HttpStatus.OK.value());
            return new CustomOptionalResponseEntity<>(responseBody, HttpStatus.OK);
        } catch (DataIntegrityViolationException e) {
            Map<String, String> responseMap = new HashMap<>();
            responseMap.put(memberId+"로 가입되어있는 계정이 없습니다", e.getMessage());
            CustomOptionalResponseBody responseBody = new CustomOptionalResponseBody<>(Optional.ofNullable(responseMap),"회원삭제 실패", HttpStatus.NO_CONTENT.value());
            return new CustomOptionalResponseEntity<>(responseBody, HttpStatus.NO_CONTENT);
        }
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<?> handleIllegalArgumentException(IllegalArgumentException e) {
        Map<String, String> responseMap = new HashMap<>();
        responseMap.put("errorMessage", e.getMessage());
        CustomOptionalResponseBody<Optional<Member>> errorBody = new CustomOptionalResponseBody<>(Optional.ofNullable(responseMap), "해당회원이 존재하지 않습니다.", HttpStatus.CONFLICT.value());
        return new CustomOptionalResponseEntity<>(errorBody, HttpStatus.BAD_REQUEST);
    }
}