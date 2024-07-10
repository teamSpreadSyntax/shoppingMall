package home.project.controller;

import home.project.domain.*;
import home.project.service.JwtTokenProvider;
import home.project.service.MemberService;
import home.project.service.RoleService;
import home.project.service.ValidationCheck;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
public class MemberControllerTest {

    @Mock
    private MemberService memberService;

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @Mock
    private ValidationCheck validationCheck;

    @Mock
    private RoleService roleService;


    @InjectMocks
    private MemberController memberController;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testCreateMember_Success() {
        MemberDTOWithoutId memberDTO = new MemberDTOWithoutId();
        memberDTO.setEmail("test@example.com");
        memberDTO.setPassword("123456");
        memberDTO.setName("강민서");
        memberDTO.setPhone("010-1234-5678");
        BindingResult bindingResult = mock(BindingResult.class);
        Member member = new Member();
        member.setId(1L);
        member.setEmail(memberDTO.getEmail());
        member.setPassword(memberDTO.getPassword());
        member.setName(memberDTO.getName());
        member.setPhone(memberDTO.getPhone());
        Role role = new Role();
        role.setId(1L);
        role.setRole("user");


        when(validationCheck.validationChecks(bindingResult)).thenReturn(null);
        when(memberService.findByEmail(memberDTO.getEmail())).thenReturn(Optional.of(member));
        when(roleService.findById(member.getId())).thenReturn(Optional.of(role));
        when(jwtTokenProvider.generateToken(any())).thenReturn(new TokenDto("bearer","accessToken", "refreshToken"));

        CustomOptionalResponseEntity<?> responseEntity = memberController.createMember(memberDTO, bindingResult);

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
/*
        assertTrue(responseEntity.getBody().result.isPresent());
*/
    }

   /* @Test
    public void testFindMemberById_Success() {
        Long memberId = 1L;
        Member member = new Member();
        member.setId(memberId);
        when(memberService.findById(memberId)).thenReturn(Optional.of(member));

        CustomOptionalResponseEntity<Optional<Member>> responseEntity = memberController.findMemberById(memberId);

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertTrue(responseEntity.getBody().getResult().isPresent());
        assertEquals(memberId, ((Member) responseEntity.getBody().getResult().get()).getId());
    }

    @Test
    public void testFindAllMember_Success() {
        Pageable pageable = PageRequest.of(0, 5);
        Member member = new Member();
        member.setId(1L);
        Page<Member> memberPage = new PageImpl<>(Collections.singletonList(member));

        when(memberService.findAll(pageable)).thenReturn(memberPage);

        ResponseEntity<?> responseEntity = memberController.findAllMember(pageable);

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    }

    @Test
    public void testSearchMembers_Success() {
        Pageable pageable = PageRequest.of(0, 5);
        Member member = new Member();
        member.setId(1L);
        Page<Member> memberPage = new PageImpl<>(Collections.singletonList(member));

        when(memberService.findMembers(null, null, null, null, pageable)).thenReturn(memberPage);

        ResponseEntity<?> responseEntity = memberController.searchMembers(null, null, null, null, pageable);

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    }

    @Test
    public void testUpdateMember_Success() {
        Member member = new Member();
        member.setId(1L);
        member.setEmail("updated@example.com");
        member.setPassword("updatedPassword");
        member.setName("Updated User");
        member.setPhone("010-9876-5432");
        BindingResult bindingResult = mock(BindingResult.class);

        when(validationCheck.validationChecks(bindingResult)).thenReturn(null);
        when(memberService.update(member)).thenReturn(Optional.of(member));

        CustomOptionalResponseEntity<?> responseEntity = memberController.updateMember(member, bindingResult);

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertTrue(responseEntity.getBody().getResult().isPresent());
        assertEquals(member.getEmail(), ((Member) responseEntity.getBody().getResult().get()).getEmail());
    }

    @Test
    public void testDeleteMember_Success() {
        Long memberId = 1L;

        CustomOptionalResponseEntity<Optional<Member>> responseEntity = memberController.deleteMember(memberId);

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    }*/
}
