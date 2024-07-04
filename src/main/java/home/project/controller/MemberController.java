package home.project.controller;


import home.project.domain.*;
import home.project.service.JwtTokenProvider;
import home.project.service.MemberService;
import home.project.service.RoleService;
import home.project.service.ValidationCheck;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.SortDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Tag(name = "회원", description = "회원관련 API입니다")
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
    private final JwtTokenProvider jwtTokenProvider;
    private final ValidationCheck validationCheck;
    private final RoleService roleService;

    @Autowired
    public MemberController(MemberService memberService, JwtTokenProvider jwtTokenProvider, ValidationCheck validationCheck, RoleService roleService) {
        this.memberService = memberService;
        this.jwtTokenProvider = jwtTokenProvider;
        this.validationCheck = validationCheck;
        this.roleService = roleService;
    }

    @Operation(summary = "회원가입 메서드", description = "회원가입 메서드입니다.")
    @PostMapping("join")
    public CustomOptionalResponseEntity<?> createMember(@RequestBody @Valid MemberDTOWithoutId memberDTO, BindingResult bindingResult) {
        CustomOptionalResponseEntity<?> validationResponse = validationCheck.validationChecks(bindingResult);
        if (validationResponse != null) return validationResponse;
        Member member = new Member();
        member.setEmail(memberDTO.getEmail());
        member.setPassword(memberDTO.getPassword());
        member.setName(memberDTO.getName());
        member.setPhone(memberDTO.getPhone());
        memberService.join(member);
        Optional<Member> memberForAddRole = memberService.findByEmail(member.getEmail());
        Role role = new Role();
        role.setId(memberForAddRole.get().getId());
        roleService.join(role);
        TokenDto tokenDto = jwtTokenProvider.generateToken(new UsernamePasswordAuthenticationToken(member.getEmail(), member.getPassword()));
        Map<String, String> responseMap = new HashMap<>();
        responseMap.put("accessToken", tokenDto.getAccessToken());
        responseMap.put("refreshToken", tokenDto.getRefreshToken());
        responseMap.put("message", "회원가입이 성공적으로 완료되었습니다.");
        CustomOptionalResponseBody<Optional<Map<String, String>>> responseBody = new CustomOptionalResponseBody<>(Optional.of(responseMap), "회원가입 성공", HttpStatus.OK.value());
        return new CustomOptionalResponseEntity<>(responseBody, HttpStatus.OK);
    }

    @Operation(summary = "ID로 회원 조회 메서드", description = "ID로 회원 조회 메서드입니다.")
    @GetMapping("member")
    public CustomOptionalResponseEntity<Optional<Member>> findMemberById(@RequestParam("memberId") Long memberId) {
        if (memberId == null) {
            throw new IllegalStateException("id가 입력되지 않았습니다.");
        }
        Optional<Member> memberOptional = memberService.findById(memberId);
        String successMessage = memberId + "로 가입된 회원정보입니다";
        return new CustomOptionalResponseEntity<>(Optional.ofNullable(memberOptional), successMessage, HttpStatus.OK);
    }

    @Operation(summary = "전체 회원 조회 메서드", description = "전체 회원 조회 메서드입니다.")
    @GetMapping("members")
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> findAllMember(
            @PageableDefault(page = 1, size = 5)
            @SortDefault.SortDefaults({
                    @SortDefault(sort = "id", direction = Sort.Direction.ASC)
            }) @ParameterObject Pageable pageable) {
        try {
            pageable = PageRequest.of(pageable.getPageNumber() - 1, pageable.getPageSize());
            Page<Member> memberPage = memberService.findAll(pageable);
            Page<MemberDTOWithoutPw> memberDtoPage = memberPage.map(member -> new MemberDTOWithoutPw(member.getId(), member.getEmail(), member.getName(), member.getPhone()));
            String successMessage = "전체 회원입니다";
            long totalCount = memberPage.getTotalElements();
            int page = memberPage.getNumber();
            return new CustomListResponseEntity<>(memberDtoPage.getContent(), successMessage, HttpStatus.OK, totalCount, page);
        } catch (AccessDeniedException e) {
            String errorMessage = "접근 권한이 없습니다";
            return new CustomOptionalResponseEntity<>(Optional.ofNullable(e.getMessage()), errorMessage, HttpStatus.BAD_REQUEST);
        }
    }

    @Operation(summary = "회원 통합 조회 메서드", description = "이름, 이메일, 전화번호 및 일반 검색어로 회원을 조회합니다. 모든 조건을 만족하는 회원을 조회합니다. 검색어가 없으면 전체 회원을 조회합니다.")
    @GetMapping("search")
    public ResponseEntity<?> searchMembers(
            @RequestParam(value = "name", required = false) String name,
            @RequestParam(value = "email", required = false) String email,
            @RequestParam(value = "phone", required = false) String phone,
            @RequestParam(value = "content", required = false) String content,
            @PageableDefault(page = 1, size = 5)
            @SortDefault.SortDefaults({
                    @SortDefault(sort = "name", direction = Sort.Direction.ASC)
            }) @ParameterObject Pageable pageable) {
        pageable = PageRequest.of(pageable.getPageNumber() - 1, pageable.getPageSize());
        Page<Member> memberPage = memberService.findMembers(name, email, phone, content, pageable);
        String successMessage = "검색 결과입니다";
        long totalCount = memberPage.getTotalElements();
        int page = memberPage.getNumber();
        return new CustomListResponseEntity<>(memberPage.getContent(), successMessage, HttpStatus.OK, totalCount, page);
    }

    @Operation(summary = "회원 정보 업데이트(수정) 메서드", description = "회원 정보 업데이트(수정) 메서드입니다.")
    @PutMapping("update")
    public CustomOptionalResponseEntity<?> updateMember(@RequestBody @Valid Member member, BindingResult bindingResult) {
        CustomOptionalResponseEntity<?> validationResponse = validationCheck.validationChecks(bindingResult);
        if (validationResponse != null) return validationResponse;
        Optional<Member> memberOptional = memberService.update(member);
        String successMessage = "회원 정보가 수정되었습니다";
        return new CustomOptionalResponseEntity<>(Optional.ofNullable(memberOptional), successMessage, HttpStatus.OK);
    }

    @Transactional
    @Operation(summary = "멤버 삭제 메서드", description = "멤버 삭제 메서드입니다.")
    @DeleteMapping("delete")
    public CustomOptionalResponseEntity<Optional<Member>> deleteMember(@RequestParam("memberId") Long memberId) {
        memberService.deleteById(memberId);
        Map<String, String> responseMap = new HashMap<>();
        responseMap.put("이용해주셔서 감사합니다", memberId + "님의 계정이 삭제되었습니다");
        CustomOptionalResponseBody responseBody = new CustomOptionalResponseBody<>(Optional.ofNullable(responseMap), "회원 삭제 성공", HttpStatus.OK.value());
        return new CustomOptionalResponseEntity<>(responseBody, HttpStatus.OK);
    }



}