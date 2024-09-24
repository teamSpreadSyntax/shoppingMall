package home.project.controller;


import home.project.domain.*;
import home.project.dto.*;
import home.project.response.CustomListResponseEntity;
import home.project.response.CustomOptionalResponseEntity;
import home.project.service.JwtTokenProvider;
import home.project.service.MemberService;
import home.project.service.RoleService;
import home.project.util.ValidationCheck;
import io.jsonwebtoken.JwtException;
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
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Tag(name = "회원", description = "회원관련 API입니다")
@RequestMapping(path = "/api/member")
@ApiResponses(value = {
        @ApiResponse(responseCode = "500", description = "Internal server error",
                content = @Content(schema = @Schema(ref = "#/components/schemas/InternalServerErrorResponseSchema")))
})
@RestController
public class MemberController {

    private final MemberService memberService;
    private final JwtTokenProvider jwtTokenProvider;
    private final ValidationCheck validationCheck;
    private final RoleService roleService;
    private  final PasswordEncoder passwordEncoder;

    @Autowired
    public MemberController(MemberService memberService, JwtTokenProvider jwtTokenProvider, ValidationCheck validationCheck, RoleService roleService, PasswordEncoder passwordEncoder) {
        this.memberService = memberService;
        this.jwtTokenProvider = jwtTokenProvider;
        this.validationCheck = validationCheck;
        this.roleService = roleService;
        this.passwordEncoder = passwordEncoder;
    }

    @Operation(summary = "회원가입 메서드", description = "회원가입 메서드입니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful operation",
                    content = @Content(schema = @Schema(ref = "#/components/schemas/MemberJoinSuccessResponseSchema"))),
            @ApiResponse(responseCode = "400", description = "Bad Request",
                    content = @Content(schema = @Schema(ref = "#/components/schemas/MemberValidationFailedResponseSchema"))),
            @ApiResponse(responseCode = "409", description = "Conflict",
                    content = @Content(schema = @Schema(ref = "#/components/schemas/ConflictResponseSchema")))
    })
    @PostMapping("/join")
    public ResponseEntity<?> createMember(@RequestBody @Valid MemberDTOWithoutId memberDTO, BindingResult bindingResult) {
        CustomOptionalResponseEntity<Map<String, String>> validationResponse = validationCheck.validationChecks(bindingResult);
        if (validationResponse != null) return validationResponse;
        if(!memberDTO.getPassword().equals(memberDTO.getPasswordConfirm())){
            throw new IllegalStateException("비밀번호와 비밀번호 확인이 일치하지 않습니다.");
        }

        Optional<Map<String, String>> responseMap = memberService.join(memberDTO);

        return new CustomOptionalResponseEntity<>(Optional.of(responseMap), "회원가입 성공", HttpStatus.OK);
    }

    @Operation(summary = "id로 회원 조회 메서드", description = "id로 회원 조회 메서드입니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful operation",
                    content = @Content(schema = @Schema(ref = "#/components/schemas/MemberWithoutPasswordResponseSchema"))),
            @ApiResponse(responseCode = "403", description = "Forbidden",
                    content = @Content(schema = @Schema(ref = "#/components/schemas/ForbiddenResponseSchema"))),
            @ApiResponse(responseCode = "404", description = "Resource not found",
                    content = @Content(schema = @Schema(ref = "#/components/schemas/NotFoundResponseSchema")))
    })
    @GetMapping("/member")
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_CENTER')")
    public ResponseEntity<?> findMemberById() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        Long memberId = memberService.findByEmail(email).get().getId();
        Optional<Member> memberOptional = memberService.findById(memberId);
        String role = roleService.findById(memberId).get().getRole();
        Optional<MemberDTOWithoutPw> memberDTOWithoutPw = memberOptional.map(member -> new MemberDTOWithoutPw(member.getId(), member.getEmail(), member.getName(), member.getPhone(), role));
            String successMessage = memberId + "(으)로 가입된 회원정보입니다";
        return new CustomOptionalResponseEntity<>(memberDTOWithoutPw, successMessage, HttpStatus.OK);
    }

    @Operation(summary = "전체 회원 조회 메서드", description = "전체 회원 조회 메서드입니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful operation",
                    content = @Content(schema = @Schema(ref = "#/components/schemas/PagedMemberListResponseSchema"))),
            @ApiResponse(responseCode = "400", description = "Bad Request",
                    content = @Content(schema = @Schema(ref = "#/components/schemas/BadRequestResponseSchema"))),
            @ApiResponse(responseCode = "403", description = "Forbidden",
                    content = @Content(schema = @Schema(ref = "#/components/schemas/ForbiddenResponseSchema")))

    })
    @GetMapping("/members")
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_CENTER')")
    public ResponseEntity<?> findAllMember(
            @PageableDefault(page = 1, size = 5)
            @SortDefault.SortDefaults({
                    @SortDefault(sort = "name", direction = Sort.Direction.ASC)
            }) @ParameterObject Pageable pageable) {

            pageable = PageRequest.of(pageable.getPageNumber() - 1, pageable.getPageSize());
            Page<Member> memberPage = memberService.findAll(pageable);

            Page<MemberDTOWithoutPw> pagedMemberDTOWithoutPw = memberPage.map(member -> {
                Long roleId = member.getId();
                String roleName = "No Role";
                if (roleId != null) {
                    Optional<Role> role = roleService.findById(roleId);
                    roleName = role.get().getRole();
                }
                return new MemberDTOWithoutPw(member.getId(), member.getEmail(), member.getName(), member.getPhone(), roleName);
            });

            String successMessage = "전체 회원입니다.";
            long totalCount = pagedMemberDTOWithoutPw.getTotalElements();
            int page = pagedMemberDTOWithoutPw.getNumber();
            return new CustomListResponseEntity<>(pagedMemberDTOWithoutPw.getContent(), successMessage, HttpStatus.OK, totalCount, page);

    }

    @Operation(summary = "회원 통합 조회 메서드", description = "이름, 이메일, 전화번호 및 일반 검색어로 회원을 조회합니다. 모든 조건을 만족하는 회원을 조회합니다. 검색어가 없으면 전체 회원을 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful operation",
                    content = @Content(schema = @Schema(ref = "#/components/schemas/PagedMemberListResponseSchema"))),
            @ApiResponse(responseCode = "403", description = "Forbidden",
                    content = @Content(schema = @Schema(ref = "#/components/schemas/ForbiddenResponseSchema"))),
            @ApiResponse(responseCode = "400", description = "Bad Request",
                    content = @Content(schema = @Schema(ref = "#/components/schemas/BadRequestResponseSchema"))),
            @ApiResponse(responseCode = "404", description = "Resource not found",
                    content = @Content(schema = @Schema(ref = "#/components/schemas/NotFoundResponseSchema")))
    })
    @GetMapping("/search")
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_CENTER')")
    public ResponseEntity<?> searchMembers(
            @RequestParam(value = "name", required = false) String name,
            @RequestParam(value = "email", required = false) String email,
            @RequestParam(value = "phone", required = false) String phone,
            @RequestParam(value = "role", required = false) String role,
            @RequestParam(value = "content", required = false) String content,
            @PageableDefault(page = 1, size = 5)
            @SortDefault.SortDefaults({
                    @SortDefault(sort = "name", direction = Sort.Direction.ASC)
            }) @ParameterObject Pageable pageable) {
        pageable = PageRequest.of(pageable.getPageNumber() - 1, pageable.getPageSize());
        Page<Member> memberPage = memberService.findMembers(name, email, phone, role, content, pageable);
        Page<MemberDTOWithoutPw> pagedMemberDTOWithoutPw = memberPage.map(member -> {
            String roleName = Optional.ofNullable(member.getRole())
                    .map(Role::getRole)
                    .orElse("No Role");
            return new MemberDTOWithoutPw(member.getId(), member.getEmail(), member.getName(), member.getPhone(), roleName);
        });
            StringBuilder searchCriteria = new StringBuilder();
            if (name != null) searchCriteria.append(name).append(", ");
            if (email != null) searchCriteria.append(email).append(", ");
            if (phone != null) searchCriteria.append(phone).append(", ");
            if (role != null) searchCriteria.append(role).append(", ");
            if (content != null) searchCriteria.append(content).append(", ");

            String successMessage;
            if (!searchCriteria.isEmpty()) {
                searchCriteria.setLength(searchCriteria.length() - 2);
                successMessage = "검색 키워드 : " + searchCriteria;
            } else {
                successMessage = "전체 회원입니다.";
            }

        long totalCount = pagedMemberDTOWithoutPw.getTotalElements();
        int page = pagedMemberDTOWithoutPw.getNumber();

        if (totalCount == 0) {
            successMessage = "검색 결과가 없습니다. 검색 키워드 : " + searchCriteria;
        }

        return new CustomListResponseEntity<>(pagedMemberDTOWithoutPw.getContent(), successMessage, HttpStatus.OK, totalCount, page);
    }

    @Operation(summary = "본인확인 메서드", description = "본인확인 메서드입니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful operation",
                    content = @Content(schema = @Schema(ref = "#/components/schemas/VerifyResponseSchema"))),
            @ApiResponse(responseCode = "400", description = "Bad Request",
                    content = @Content(schema = @Schema(ref = "#/components/schemas/MemberValidationFailedResponseSchema"))),
            @ApiResponse(responseCode = "401", description = "Unauthorized",
                    content = @Content(schema = @Schema(ref = "#/components/schemas/UnauthorizedResponseSchema"))),
            @ApiResponse(responseCode = "404", description = "Resource not found",
                    content = @Content(schema = @Schema(ref = "#/components/schemas/NotFoundResponseSchema")))

    })
    @PostMapping("/verify")
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasAnyRole('ROLE_USER','ROLE_ADMIN','ROLE_CENTER')")
    public ResponseEntity<?> verifyUser(@RequestBody @Valid PasswordDTO password, BindingResult bindingResult) {


            CustomOptionalResponseEntity<?> validationResponse = validationCheck.validationChecks(bindingResult);
            if (validationResponse != null) {
                return validationResponse;
            }

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();

            Long id = memberService.findByEmail(email).get().getId();
          if (!passwordEncoder.matches(password.getPassword(), memberService.findByEmail(email).get().getPassword())){
                throw new BadCredentialsException("비밀번호가 일치하지 않습니다.");
            }

            String verificationToken = jwtTokenProvider.generateVerificationToken(email, id);

            Map<String, String> response = new HashMap<>();
            response.put("successMessage", "본인 확인이 완료되었습니다.");
            response.put("verificationToken", verificationToken);

            return new CustomOptionalResponseEntity<>(Optional.of(response), "본인 확인 성공", HttpStatus.OK);

    }

    @Transactional
    @Operation(summary = "회원 정보 업데이트(수정) 메서드", description = "회원 정보 업데이트(수정) 메서드입니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful operation",
                    content = @Content(schema = @Schema(ref = "#/components/schemas/MemberWithoutPasswordResponseSchema"))),
            @ApiResponse(responseCode = "204", description = "NO_CONTENT",
                    content = @Content(schema = @Schema(ref = "#/components/schemas/NochangeResponseSchema"))),
            @ApiResponse(responseCode = "400", description = "Bad Request",
                    content = @Content(schema = @Schema(ref = "#/components/schemas/MemberValidationFailedResponseSchema"))),
            @ApiResponse(responseCode = "409", description = "Conflict",
                    content = @Content(schema = @Schema(ref = "#/components/schemas/ConflictResponseSchema")))
    })
    @PutMapping("/update")
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasAnyRole('ROLE_USER','ROLE_ADMIN','ROLE_CENTER')")
    public ResponseEntity<?> updateMember(      @RequestBody @Valid MemberDTOWithPasswordConfirm memberDTOWithPasswordConfirm,
                                                BindingResult bindingResult,
                                                @RequestHeader("Verification_Token") String verificationToken) {
            CustomOptionalResponseEntity<?> validationResponse = validationCheck.validationChecks(bindingResult);
            if (validationResponse != null) {
                return validationResponse;
            }
            String email = jwtTokenProvider.getEmailFromToken(verificationToken);

            if (email == null) {
                throw new JwtException("유효하지 않은 본인인증 토큰입니다. 본인인증을 다시 진행해주세요.");
            }
        if(!memberDTOWithPasswordConfirm.getPassword().equals(memberDTOWithPasswordConfirm.getPasswordConfirm())){
            throw new IllegalStateException("비밀번호와 비밀번호 확인이 일치하지 않습니다.");
        }
        Long id = Long.parseLong(jwtTokenProvider.getIdFromVerificationToken(verificationToken));
        Member member = new Member();
        member.setId(id);
        member.setName(memberDTOWithPasswordConfirm.getName());
        member.setPhone(memberDTOWithPasswordConfirm.getPhone());
        member.setEmail(memberDTOWithPasswordConfirm.getEmail());
        member.setPassword(memberDTOWithPasswordConfirm.getPassword());
        member.setRole(roleService.findById(id).get());
        Optional<Member> memberOptional = memberService.update(member);
            Optional<MemberDTOWithoutPw> memberDTOWithoutPw = memberOptional.map(memberWithoutPw -> new MemberDTOWithoutPw(member.getId(), member.getEmail(), member.getName(), member.getPhone(), member.getRole().getRole()));
            String successMessage = "회원 정보가 수정되었습니다.";
        return new CustomOptionalResponseEntity<>(memberDTOWithoutPw, successMessage, HttpStatus.OK);
    }

    @Transactional
    @Operation(summary = "회원 삭제 메서드", description = "회원 삭제 메서드입니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful operation",
                    content = @Content(schema = @Schema(ref = "#/components/schemas/GeneralSuccessResponseSchema"))),
            @ApiResponse(responseCode = "403", description = "Forbidden",
                    content = @Content(schema = @Schema(ref = "#/components/schemas/ForbiddenResponseSchema"))),
            @ApiResponse(responseCode = "404", description = "Resource not found",
                    content = @Content(schema = @Schema(ref = "#/components/schemas/NotFoundResponseSchema")))
    })
    @DeleteMapping("/delete")
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_CENTER')")
    public ResponseEntity<?> deleteMember(@RequestParam("memberId") Long memberId) {

        String email = memberService.findById(memberId).get().getEmail();
        memberService.deleteById(memberId);
        Map<String, String> responseMap = new HashMap<>();
        responseMap.put("successMessage", email + "(id:" + memberId + ")님의 계정이 삭제되었습니다.");
        return new CustomOptionalResponseEntity<>(Optional.of(responseMap), "회원 삭제 성공", HttpStatus.OK);
        }

}