package com.insurance.quote.service;

import com.insurance.quote.dto.*;
import com.insurance.quote.entity.Quote;
import com.insurance.quote.entity.QuoteBreakdown;
import com.insurance.quote.exception.NotFoundException;
import com.insurance.quote.repository.QuoteBreakdownRepository;
import com.insurance.quote.repository.QuoteRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

/**
 * QuoteService のユニットテストクラス。
 * 見積番号による見積照会の正常系・異常系（見つからない場合）を検証する。
 */
@ExtendWith(MockitoExtension.class)
class QuoteServiceTest {

    @Mock
    private QuoteRepository quoteRepository;

    @Mock
    private QuoteBreakdownRepository breakdownRepository;

    @Mock
    private PremiumCalculationService calculationService;

    @InjectMocks
    private QuoteService quoteService;

    /**
     * 有効な見積番号で見積詳細と内訳が正しく取得できることをテストする。
     */
    @Test
    void getQuoteSuccess() {
        // given: 見積ヘッダーエンティティを準備
        Quote quote = new Quote();
        quote.setId(1L);
        quote.setQuoteNo("EST202607070001");
        quote.setAnnualPremium(45000);
        quote.setMonthlyPremium(3750);
        quote.setCreatedAt(LocalDateTime.now());

        // 見積内訳エンティティを準備
        QuoteBreakdown bd = new QuoteBreakdown();
        bd.setItemCode("BASE");
        bd.setItemName("基本保険料");
        bd.setAmount(50000);
        bd.setDisplayOrder(1);

        // リポジトリのモック設定
        when(quoteRepository.findByQuoteNo("EST202607070001")).thenReturn(Optional.of(quote));
        when(breakdownRepository.findByQuoteIdOrderByDisplayOrderAsc(1L)).thenReturn(List.of(bd));

        // when: 見積番号で照会
        QuoteResponse response = quoteService.getQuote("EST202607070001");

        // then: レスポンスの各項目を検証
        assertNotNull(response); // レスポンスがnullでないこと
        assertEquals("EST202607070001", response.getQuoteNo()); // 見積番号の検証
        assertEquals(45000, response.getAnnualPremium()); // 年額保険料の検証
        assertEquals(1, response.getBreakdowns().size()); // 内訳が1件であること
        assertEquals("基本保険料", response.getBreakdowns().get(0).getItemName()); // 内訳項目名の検証
    }

    /**
     * 存在しない見積番号で照会した場合に NotFoundException がスローされることをテストする。
     */
    @Test
    void getQuoteNotFound() {
        // given: 見つからない見積番号のモック設定
        when(quoteRepository.findByQuoteNo("NOT_EXIST")).thenReturn(Optional.empty());

        // when+then: NotFoundException がスローされることを検証
        assertThrows(NotFoundException.class, () -> quoteService.getQuote("NOT_EXIST"));
    }
}
