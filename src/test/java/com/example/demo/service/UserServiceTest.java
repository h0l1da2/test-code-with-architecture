package com.example.demo.service;

import com.example.demo.exception.CertificationCodeNotMatchedException;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.model.UserStatus;
import com.example.demo.model.dto.UserCreateDto;
import com.example.demo.model.dto.UserUpdateDto;
import com.example.demo.repository.UserEntity;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlGroup;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;

@SpringBootTest
@SqlGroup({
        @Sql(value = "/sql/user-service-test-data.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD),
        @Sql(value = "/sql/delete-all-data.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
})
public class UserServiceTest {

    @Autowired
    private UserService userService;
    @MockBean
    private JavaMailSender mailSender;

    @Test
    void getByEmail은ACTIVE상태인유저만_가져옴() {
        // given
        String email = "aaaa@naver.com";

        // when
        UserEntity result = userService.getByEmail(email);

        // then
        assertThat(result.getNickname()).isEqualTo("aaaa");
    }

    @Test
    void getByEmail은PENDING상태인유저는_못가져옴() {
        // given
        String email = "bbbb@naver.com";

        // when

        // then
        assertThatThrownBy(() -> userService.getByEmail(email)).isInstanceOf(ResourceNotFoundException.class);;
    }

    @Test
    void getById은ACTIVE상태인유저만_가져옴() {
        // given
        // when
        UserEntity result = userService.getById(1);

        // then
        assertThat(result.getNickname()).isEqualTo("aaaa");
    }

    @Test
    void getById은PENDING상태인유저는_못가져옴() {
        // given
        // when

        // then
        assertThatThrownBy(() -> userService.getById(2)).isInstanceOf(ResourceNotFoundException.class);;
    }


    @Test
    void userCreateDto를_이용하여_유저생성_가능() {
        // given
        UserCreateDto userCreateDto = UserCreateDto.builder()
                .email("cccc@naver.com")
                .nickname("cccc")
                .address("Hansan")
                .build();

        BDDMockito.doNothing().when(mailSender).send(any(SimpleMailMessage.class));

        // when
        UserEntity result = userService.create(userCreateDto);

        // then
        assertThat(result.getId()).isNotNull();
        assertThat(result.getStatus()).isEqualTo(UserStatus.PENDING);
//        assertThat(result.getCertificationCode()).isEqualTo("T_T");
    }

    @Test
    void userUpdateDto를_이용하여_유저업데이트_가능() {
        // given
        UserUpdateDto userUpdateDto = UserUpdateDto.builder()
                .nickname("acacac")
                .address("Seoul")
                .build();

        // when
        UserEntity result = userService.update(1, userUpdateDto);

        // then
        UserEntity userEntity = userService.getById(1);
        assertThat(userEntity.getId()).isNotNull();
        assertThat(userEntity.getAddress()).isEqualTo("Seoul");
        assertThat(userEntity.getNickname()).isEqualTo("acacac");
//        assertThat(result.getCertificationCode()).isEqualTo("T_T"); // FIXME
    }

    @Test
    void login_가능() {
        // given
        // when
        userService.login(1);

        // then
        UserEntity userEntity = userService.getById(1);
        assertThat(userEntity.getLastLoginAt()).isGreaterThan(0L);
//        assertThat(userEntity.getLastLoginAt()).isEqualTo("T_T"); // FIXME
    }

    @Test
    void PENDING상태의_사용자는_인증코드로_ACTIVE_가능() {
        // given
        // when
        userService.verifyEmail(2, "bbbbbbbbb");

        // then
        UserEntity userEntity = userService.getById(2);
        assertThat(userEntity.getStatus()).isEqualTo(UserStatus.ACTIVE);
//        assertThat(userEntity.getLastLoginAt()).isEqualTo("T_T"); // FIXME
    }

    @Test
    void PENDING상태의_사용자는_인증코드가다르면_실패() {
        // given
        // when
        // then
        assertThatThrownBy(() -> {
            userService.verifyEmail(2, "bbbbbbbbbccc");
        }).isInstanceOf(CertificationCodeNotMatchedException.class);

    }


}
