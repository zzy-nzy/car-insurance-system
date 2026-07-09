package com.insurance.quote.controller;

import com.insurance.quote.config.SecurityConfig;
import com.insurance.quote.dto.LoginResponse;
import com.insurance.quote.dto.QuoteResponse;
import com.insurance.quote.service.AdminService;
import com.insurance.quote.service.JwtService;
import com.insurance.quote.service.QuoteService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * AdminController の Web 層テストクラス。
 * 管理者ログインと認証付き見積照会APIの正常系・異常系を検証する。
 */
@WebMvcTest(AdminController.class)
@Import(SecurityConfig.class)
class AdminControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AdminService adminService;

    @MockBean
    private QuoteService quoteService;

    @MockBean
    private JwtService jwtService;

    /**
     * 正しい管理者認証情報でログインAPIが 200 OK と JWTトークン・表示名を返却することをテストする。
     */
    @Test
    void loginSuccess() throws Exception {
        // given: モックのログインレスポンスを定義
        when(adminService.login(any())).thenReturn(new LoginResponse("token123", "管理者"));

        // when+then: ログインPOSTリクエストを実行し、トークンと表示名を検証
        mockMvc.perform(post("/api/admin/login")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"admin\",\"password\":\"admin123\"}"))
                .andExpect(status().isOk()) // HTTP 200 を期待
                .andExpect(jsonPath("$.token").value("token123")) // トークンの検証
                .andExpect(jsonPath("$.displayName").value("管理者")); // 表示名の検証
    }

    /**
     * 有効なトークンで見積詳細APIが 200 OK と見積番号を返却することをテストする。
     */
    @Test
    void getQuoteDetailWithAuth() throws Exception {
        // given: トークン検証が成功し、管理者ユーザーが有効なモック設定
        when(jwtService.validateToken("valid-token")).thenReturn(true);
        when(jwtService.extractUsername("valid-token")).thenReturn("admin");
        when(adminService.isValidAdmin("admin")).thenReturn(true);

        // モックの見積レスポンス
        QuoteResponse response = new QuoteResponse();
        response.setQuoteNo("EST001");
        response.setAnnualPremium(45000);
        response.setMonthlyPremium(3750);
        response.setBreakdowns(List.of());
        response.setCreatedAt(LocalDateTime.now());

        when(quoteService.getQuote("EST001")).thenReturn(response);

        // when+then: 認証付きGETリクエストで見積詳細を取得
        mockMvc.perform(get("/api/admin/quotes/EST001")
                        .with(csrf())
                        .header("Authorization", "Bearer valid-token")) // 有効な認証ヘッダー
                .andExpect(status().isOk()) // HTTP 200 を期待
                .andExpect(jsonPath("$.quoteNo").value("EST001")); // 見積番号の検証
    }

    /**
     * 無効なトークンで見積詳細APIが 401 Unauthorized を返却することをテストする。
     */
    @Test
    void getQuoteDetailWithoutAuthReturns401() throws Exception {
        // given: トークン検証が失敗するモック設定
        when(jwtService.validateToken("bad-token")).thenReturn(false);

        // when+then: 無効なトークンでリクエストし、401エラーを期待
        mockMvc.perform(get("/api/admin/quotes/EST001")
                        .with(csrf())
                        .header("Authorization", "Bearer bad-token")) // 無効な認証ヘッダー
                .andExpect(status().isUnauthorized()); // HTTP 401 を期待
    }

    /**
     * 有効なトークンで見積一覧APIが 200 OK を返却することをテストする。
     */
    @Test
    void listQuotesWithAuth() throws Exception {
        // given: トークン検証が成功し、管理者ユーザーが有効、空の一覧を返すモック設定
        when(jwtService.validateToken("valid-token")).thenReturn(true);
        when(jwtService.extractUsername("valid-token")).thenReturn("admin");
        when(adminService.isValidAdmin("admin")).thenReturn(true);
        when(quoteService.listQuotes(any())).thenReturn(List.of());

        // when+then: 認証付きGETリクエストで見積一覧を取得
        mockMvc.perform(get("/api/admin/quotes")
                        .with(csrf())
                        .header("Authorization", "Bearer valid-token")) // 有効な認証ヘッダー
                .andExpect(status().isOk()); // HTTP 200 を期待
    }

    /**
     * Authorizationヘッダーなしで見積詳細APIが 401 Unauthorized を返却することをテストする。
     */
    @Test
    void getQuoteDetailWithoutHeaderReturns401() throws Exception {
        // when+then: Authorizationヘッダーなしでリクエストし、401エラーを期待
        mockMvc.perform(get("/api/admin/quotes/EST001")
                        .with(csrf())) // 認証ヘッダーなし
                .andExpect(status().isUnauthorized()); // HTTP 401 を期待
    }

    /**
     * 有効なトークンだが管理者ユーザーが存在しない場合、403 Forbidden を返却することをテストする。
     * <p>
     * JWTトークンの署名検証は成功するが、トークンに含まれるユーザー名が
     * 管理者テーブルに存在しない場合、権限不足として403エラーとなる。
     */
    @Test
    void getQuoteDetailWithValidTokenButNotAdminReturns403() throws Exception {
        // given: トークン検証は成功するが、管理者ユーザーが存在しないモック設定
        when(jwtService.validateToken("valid-token")).thenReturn(true);
        when(jwtService.extractUsername("valid-token")).thenReturn("hacker");
        when(adminService.isValidAdmin("hacker")).thenReturn(false);

        // when+then: 有効なトークンだが非管理者ユーザーでリクエストし、403エラーを期待
        mockMvc.perform(get("/api/admin/quotes/EST001")
                        .with(csrf())
                        .header("Authorization", "Bearer valid-token")) // 有効なトークンだが権限なし
                .andExpect(status().isForbidden()) // HTTP 403 を期待
                .andExpect(jsonPath("$.code").value("FORBIDDEN")); // エラーコードの検証
    }
}
