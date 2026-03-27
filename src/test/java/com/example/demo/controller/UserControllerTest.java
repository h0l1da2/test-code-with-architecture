package com.example.demo.controller;

import com.example.demo.model.UserStatus;
import com.example.demo.model.dto.UserUpdateDto;
import com.example.demo.repository.UserEntity;
import com.example.demo.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlGroup;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureTestDatabase
@SqlGroup({
        @Sql(value = "/sql/user-controller-test-data.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD),
        @Sql(value = "/sql/delete-all-data.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
})
public class UserControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private UserRepository userRepository;
    private final ObjectMapper mapper = new ObjectMapper();

    @Test
    void 사용자는_특정유저의정보를_개인정보는소거된채_전달받을수있다() throws Exception {
        //given
        //when
        //then
        mockMvc.perform(get("/api/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.email").value("aaaa@naver.com"))
                .andExpect(jsonPath("$.nickname").value("aaaa"))
                .andExpect(jsonPath("$.address").doesNotExist())
                .andExpect(jsonPath("$.status").value("ACTIVE"));
    }

    @Test
    void 존재하지않는유저의아이디로호출될때_404응답을_받는다() throws Exception {
        //given
        //when
        //then
        mockMvc.perform(get("/api/users/1231234214"))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Users에서 ID 1231234214를 찾을 수 없습니다."));
    }

    @Test
    void 사용자는인증코드로계정을활성화시킬수있다() throws Exception {
        //given
        //when
        //then
        mockMvc.perform(get("/api/users/2/verify")
                        .queryParam("certificationCode", "bbbbbbbbb"))
                .andExpect(status().isFound());

        UserEntity userEntity = userRepository.findById(2L).get();
        assertThat(userEntity.getStatus()).isEqualTo(UserStatus.ACTIVE);
    }

    @Test
    void 사용자는내정보를불러올때개인정보인주소도갖고올수있다() throws Exception {
        //given
        //when
        //then
        mockMvc.perform(get("/api/users/me")
                        .header("EMAIL", "aaaa@naver.com"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.email").value("aaaa@naver.com"))
                .andExpect(jsonPath("$.nickname").value("aaaa"))
                .andExpect(jsonPath("$.address").value("Seoul"))
                .andExpect(jsonPath("$.status").value("ACTIVE"));

    }

    @Test
    void 사용자는내정보를수정할수있다() throws Exception {
        //given
        UserUpdateDto userUpdateDto = UserUpdateDto.builder()
                .nickname("caca")
                .address("Busan")
                .build();
        //when
        //then
        mockMvc.perform(put("/api/users/me")
                        .header("EMAIL", "aaaa@naver.com")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(userUpdateDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.email").value("aaaa@naver.com"))
                .andExpect(jsonPath("$.nickname").value("caca"))
                .andExpect(jsonPath("$.address").value("Busan"))
                .andExpect(jsonPath("$.status").value("ACTIVE"));

    }

}
