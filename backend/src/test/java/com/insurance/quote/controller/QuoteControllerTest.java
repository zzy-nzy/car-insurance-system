package com.insurance.quote.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.insurance.quote.config.SecurityConfig;
import com.insurance.quote.dto.QuoteRequest;
import com.insurance.quote.dto.QuoteResponse;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * QuoteController の Web 層テストクラス。
 * 見積作成APIの正常系・異常系のリクエスト/レスポンスを検証する。
 */
@WebMvcTest(QuoteController.class)
@Import(SecurityConfig.class)
class QuoteControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private QuoteService quoteService;

    /**
     * 正常な見積リクエストに対して 201 Created と見積番号・保険料が返却されることをテストする。
     */
    @Test
    void createQuoteReturns201() throws Exception {
        // given: 有効な見積リクエストを作成
        QuoteRequest request = new QuoteRequest();
        request.setDriverAge(30);
        request.setLicenseColor("BLUE");
        request.setUsageType("PRIVATE");
        request.setAnnualMileage(10000);
        request.setDriverRange("SELF");
        request.setHasCurrentInsurance(false);
        request.setMaker("トヨタ");
        request.setCarName("カローラ");
        request.setFirstRegistrationYearMonth("2020-04");
        request.setVehicleType("SEDAN");
        request.setVehicleInsurance(false);
        request.setPropertyDamageLimit("UNLIMITED");
        request.setPersonalInjuryAmount("UNLIMITED");
        request.setLawyerOption(false);
        request.setRoadService(false);

        // モックのレスポンスを定義
        QuoteResponse response = new QuoteResponse();
        response.setQuoteNo("EST202607070001");
        response.setAnnualPremium(45000);
        response.setMonthlyPremium(3750);
        response.setBreakdowns(List.of());
        response.setCreatedAt(LocalDateTime.now());

        // when: サービスが呼ばれたらモックレスポンスを返すように設定
        when(quoteService.createQuote(any())).thenReturn(response);

        // when+then: POSTリクエストを実行し、レスポンスを検証
        mockMvc.perform(post("/api/quotes")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated()) // HTTP 201 を期待
                .andExpect(jsonPath("$.quoteNo").value("EST202607070001")) // 見積番号の検証
                .andExpect(jsonPath("$.annualPremium").value(45000)); // 年額保険料の検証
    }

    /**
     * 無効なデータ（運転者年齢が負の値）のリクエストに対して 400 Bad Request が返却されることをテストする。
     */
    @Test
    void createQuoteWithInvalidDataReturns400() throws Exception {
        // given: 運転者年齢が -1 の無効なリクエスト
        QuoteRequest request = new QuoteRequest();
        request.setDriverAge(-1);

        // when+then: POSTリクエストを実行し、400エラーを期待
        mockMvc.perform(post("/api/quotes")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest()); // バリデーションエラー 400 を期待
    }
}
