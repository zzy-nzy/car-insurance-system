package com.insurance.quote.service;

import com.insurance.quote.dto.LoginRequest;
import com.insurance.quote.dto.LoginResponse;
import com.insurance.quote.entity.AdminUser;
import com.insurance.quote.repository.AdminUserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCrypt;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

/**
 * AdminService のユニットテストクラス。
 * 管理者ログイン処理の正常系・異常系（パスワード不一致、ユーザー不在）を検証する。
 */
@ExtendWith(MockitoExtension.class)
class AdminServiceTest {

    @Mock
    private AdminUserRepository adminUserRepository;

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private AdminService adminService;

    /**
     * 正しいユーザー名とパスワードでログインし、JWTトークンと表示名が返却されることをテストする。
     */
    @Test
    void loginSuccess() {
        // given: パスワードはBCryptでハッシュ化した管理者ユーザーを準備
        AdminUser user = new AdminUser();
        user.setUsername("admin");
        user.setPassword(BCrypt.hashpw("admin123", BCrypt.gensalt()));
        user.setDisplayName("管理者");

        // ユーザー検索とトークン生成のモックを設定
        when(adminUserRepository.findByUsername("admin")).thenReturn(Optional.of(user));
        when(jwtService.generateToken("admin")).thenReturn("test-jwt-token");

        // ログインリクエストを作成
        LoginRequest request = new LoginRequest();
        request.setUsername("admin");
        request.setPassword("admin123");

        // when: ログイン処理を実行
        LoginResponse response = adminService.login(request);

        // then: レスポンスにトークンと表示名が含まれることを検証
        assertNotNull(response); // レスポンスがnullでないこと
        assertEquals("test-jwt-token", response.getToken()); // JWTトークンの検証
        assertEquals("管理者", response.getDisplayName()); // 表示名の検証
    }

    /**
     * 誤ったパスワードでログインした場合に RuntimeException がスローされることをテストする。
     */
    @Test
    void loginWrongPassword() {
        // given: 正しいパスワードでハッシュ化された管理者ユーザーを準備
        AdminUser user = new AdminUser();
        user.setUsername("admin");
        user.setPassword(BCrypt.hashpw("admin123", BCrypt.gensalt()));

        // ユーザーは存在するがパスワードが異なる
        when(adminUserRepository.findByUsername("admin")).thenReturn(Optional.of(user));

        // 誤ったパスワードでログインリクエストを作成
        LoginRequest request = new LoginRequest();
        request.setUsername("admin");
        request.setPassword("wrongpass");

        // when+then: パスワード不一致により例外がスローされることを検証
        assertThrows(RuntimeException.class, () -> adminService.login(request));
    }

    /**
     * 存在しないユーザー名でログインした場合に RuntimeException がスローされることをテストする。
     */
    @Test
    void loginUserNotFound() {
        // given: ユーザーが存在しない場合のモック設定
        when(adminUserRepository.findByUsername("nonexistent")).thenReturn(Optional.empty());

        // 存在しないユーザー名でログインリクエストを作成
        LoginRequest request = new LoginRequest();
        request.setUsername("nonexistent");
        request.setPassword("pass");

        // when+then: ユーザー不在により例外がスローされることを検証
        assertThrows(RuntimeException.class, () -> adminService.login(request));
    }

    /**
     * 存在するユーザー名で isValidAdmin が true を返却することをテストする。
     */
    @Test
    void isValidAdminReturnsTrue() {
        // given: ユーザーが存在する場合のモック設定
        AdminUser user = new AdminUser();
        user.setUsername("admin");
        when(adminUserRepository.findByUsername("admin")).thenReturn(Optional.of(user));

        // when+then: ユーザーが存在するため true が返却されることを検証
        assertTrue(adminService.isValidAdmin("admin"));
    }

    /**
     * 存在しないユーザー名で isValidAdmin が false を返却することをテストする。
     */
    @Test
    void isValidAdminReturnsFalse() {
        // given: ユーザーが存在しない場合のモック設定
        when(adminUserRepository.findByUsername("unknown")).thenReturn(Optional.empty());

        // when+then: ユーザーが存在しないため false が返却されることを検証
        assertFalse(adminService.isValidAdmin("unknown"));
    }
}
