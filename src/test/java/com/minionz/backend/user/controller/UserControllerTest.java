package com.minionz.backend.user.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.minionz.backend.ApiDocument;
import com.minionz.backend.common.domain.Address;
import com.minionz.backend.common.domain.Message;
import com.minionz.backend.common.exception.BadRequestException;
import com.minionz.backend.common.exception.NotFoundException;
import com.minionz.backend.user.controller.dto.UserJoinRequestDto;
import com.minionz.backend.user.controller.dto.UserLoginRequestDto;
import com.minionz.backend.user.controller.dto.UserPageResponseDto;
import com.minionz.backend.user.domain.User;
import com.minionz.backend.user.domain.UserRepository;
import com.minionz.backend.user.service.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.http.MediaType;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.willReturn;
import static org.mockito.BDDMockito.willThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
@MockBean(JpaMetamodelMappingContext.class)
@ExtendWith({RestDocumentationExtension.class, SpringExtension.class})
class UserControllerTest extends ApiDocument {

    @MockBean
    private UserService userService;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    @Autowired
    private UserRepository userRepository;

    @DisplayName("로그인 성공")
    @Test
    public void 유저로그인테스트_성공() throws Exception {
        final UserLoginRequestDto userLoginRequestDto = new UserLoginRequestDto("email", "password");
        Message message = new Message("로그인 성공");
        willReturn(message).given(userService).login(any(UserLoginRequestDto.class));
        final ResultActions resultActions = 유저_로그인_요청(userLoginRequestDto);
        유저_로그인_성공(message, resultActions);
    }

    @DisplayName("로그인 실패")
    @Test
    public void 유저로그인테스트_실패() throws Exception {
        final UserLoginRequestDto userLoginRequestDto = new UserLoginRequestDto("email1", "password");
        Message errorMessage = new Message("로그인 실패");
        willThrow(new NotFoundException("로그인 실패")).given(userService).login(any(UserLoginRequestDto.class));
        final ResultActions resultActions = 유저_로그인_요청(userLoginRequestDto);
        유저_로그인_실패(errorMessage, resultActions);
    }

    @DisplayName("로그아웃 성공")
    @Test
    public void 유저로그아웃테스트_성공() throws Exception {
        final String email = "email";
        Message message = new Message("로그아웃 성공");
        willReturn(message).given(userService).logout(any(String.class));
        final ResultActions resultActions = 유저_로그아웃_요청(email);
        유저_로그아웃_성공(resultActions);
    }

    @DisplayName("로그아웃 실패")
    @Test
    public void 유저로그아웃테스트_실패() throws Exception {
        final String email = "email";
        Message errorMessage = new Message("로그아웃 실패");
        willThrow(new NotFoundException("로그아웃 실패")).given(userService).logout(any(String.class));
        final ResultActions resultActions = 유저_로그아웃_요청(email);
        유저_로그아웃_실패(errorMessage, resultActions);
    }

    @DisplayName("회원가입 성공")
    @Test
    void user_sign_up_success() throws Exception {
        Address address = new Address("안산시", "상록구", "성포동");
        UserJoinRequestDto signUpRequest = new UserJoinRequestDto("정재욱", "wodnr@naver.com", "라이언", "010-9969-9776", "111", address);
        Message message = new Message("회원가입 성공");
        willReturn(message).given(userService).signUp(any(UserJoinRequestDto.class));
        final ResultActions response = 유저_회원가입_요청(signUpRequest);
        유저_회원가입_성공(message, response);
    }

    @DisplayName("회원가입 실패")
    @Test
    void user_sign_up_fail() throws Exception {
        final Address address = new Address("안산시", "상록구", "성포동");
        UserJoinRequestDto signUpRequest = new UserJoinRequestDto("정재욱", "wodnr@naver.com", "라이언", "010-9969-9776", "12345", address);
        Message errorMessage = new Message("회원가입 실패");
        willThrow(new BadRequestException("회원가입 실패")).given(userService).signUp(any(UserJoinRequestDto.class));
        final ResultActions response = 유저_회원가입_요청(signUpRequest);
        유저_회원가입_실패(errorMessage, response);
    }

    @DisplayName("회원탈퇴 성공")
    @Test
    void user_withdraw_success() throws Exception {
        final String email = "email";
        Message message = new Message("회원탈퇴 성공");
        willReturn(message).given(userService).withdraw(any(String.class));
        final ResultActions response = 유저_회원탈퇴_요청(email);
        유저_회원탈퇴_성공(response);
    }

    @DisplayName("회원탈퇴 실패")
    @Test
    void user_withdraw_fail() throws Exception {
        final String email = "email";
        Message errorMessage = new Message("회원탈퇴 실패");
        willThrow(new NotFoundException("회원탈퇴 실패")).given(userService).withdraw(any(String.class));
        final ResultActions response = 유저_회원탈퇴_요청(email);
        유저_회원탈퇴_실패(errorMessage, response);
    }

    @DisplayName("마이페이지 조회 성공")
    @Test
    void user_view_page_success() throws Exception {
        Address address = new Address("a", "b", "C");
        User user = User.builder()
                .nickName("asd")
                .telNumber("111")
                .address(address)
                .build();
        Long id = 1L;
        UserPageResponseDto userPageResponseDto = new UserPageResponseDto(user);
        willReturn(userPageResponseDto).given(userService).viewMypage(any(Long.class));
        final ResultActions response = 유저_마이페이지_요청(id);
        유저_마이페이지_성공(response, userPageResponseDto);
    }

    @DisplayName("마이페이지 조회 실패")
    @Test
    void user_view_page_fail() throws Exception {
        Long id = 2L;
        Message errorMessage = new Message("마이페이지 조회 실패");
        willThrow(new NotFoundException("마이페이지 조회 실패")).given(userService).viewMypage(any(Long.class));
        final ResultActions response = 유저_마이페이지_요청(id);
        유저_마이페이지_실패(response, errorMessage);
    }

    private ResultActions 유저_마이페이지_요청(Long id) throws Exception {
        return mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/users/page/" + id));
    }

    private ResultActions 유저_회원탈퇴_요청(String email) throws Exception {
        return mockMvc.perform(delete("/api/v1/users/withdraw/" + email)
                .contentType(MediaType.APPLICATION_JSON)
                .content(email));
    }

    private void 유저_회원가입_성공(Message message, ResultActions response) throws Exception {
        response.andExpect(status().isCreated())
                .andExpect(content().json(toJson(message)))
                .andDo(print())
                .andDo(toDocument("user-signup-success"));
    }

    private void 유저_회원가입_실패(Message errorMessage, ResultActions response) throws Exception {
        response.andExpect(status().isBadRequest())
                .andExpect(content().json(toJson(errorMessage)))
                .andDo(print())
                .andDo(toDocument("user-signup-fail"));
    }

    private void 유저_회원탈퇴_성공(ResultActions response) throws Exception {
        response.andExpect(status().isNoContent())
                .andDo(print())
                .andDo(toDocument("user-withdraw-success"));
    }

    private void 유저_회원탈퇴_실패(Message errorMessage, ResultActions response) throws Exception {
        response.andExpect(status().isNotFound())
                .andExpect(content().json(toJson(errorMessage)))
                .andDo(print())
                .andDo(toDocument("user-withdraw-fail"));
    }

    private void 유저_로그아웃_실패(Message errorMessage, ResultActions resultActions) throws Exception {
        resultActions.andExpect(status().isNotFound())
                .andExpect(content().json(toJson(errorMessage)))
                .andDo(print())
                .andDo(toDocument("user-logout-fail"));
    }

    private void 유저_로그아웃_성공(ResultActions resultActions) throws Exception {
        resultActions.andExpect(status().isNoContent())
                .andDo(print())
                .andDo(toDocument("user-logout-success"));
    }

    private ResultActions 유저_로그아웃_요청(String email) throws Exception {
        return mockMvc.perform(get("/api/v1/users/logout/" + email)
                .contentType(MediaType.APPLICATION_JSON)
                .content(email));
    }

    private void 유저_로그인_실패(Message errorMessage, ResultActions resultActions) throws Exception {
        resultActions.andExpect(status().isNotFound())
                .andExpect(content().json(toJson(errorMessage)))
                .andDo(print())
                .andDo(toDocument("user-login-fail"));
    }

    private void 유저_로그인_성공(Message message, ResultActions resultActions) throws Exception {
        resultActions.andExpect(status().isOk())
                .andExpect(content().json(toJson(message)))
                .andDo(print())
                .andDo(toDocument("user-login-success"));
    }

    private void 유저_마이페이지_성공(ResultActions response, UserPageResponseDto userPageResponseDto) throws Exception {
        response.andExpect(status().isOk())
                .andExpect(content().json(toJson(userPageResponseDto)))
                .andDo(print())
                .andDo(toDocument("user_view_page_success"));

    }

    private void 유저_마이페이지_실패(ResultActions response, Message errorMessage) throws Exception {
        response.andExpect(status().isNotFound())
                .andExpect(content().json(toJson(errorMessage)))
                .andDo(print())
                .andDo(toDocument("user_view_page_fail"));
    }

    private ResultActions 유저_로그인_요청(UserLoginRequestDto userLoginRequestDto) throws Exception {
        String content = objectMapper.writeValueAsString(userLoginRequestDto);
        return mockMvc.perform(post("/api/v1/users/login")
                .content(content)
                .contentType(MediaType.APPLICATION_JSON));
    }

    private ResultActions 유저_회원가입_요청(UserJoinRequestDto signUpRequest) throws Exception {
        return mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/users/join")
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(signUpRequest)));
    }
}
