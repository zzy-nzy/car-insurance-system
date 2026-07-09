package com.insurance.quote.controller;

import com.insurance.quote.dto.QuoteRequest;
import com.insurance.quote.dto.QuoteResponse;
import com.insurance.quote.service.QuoteService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * 保険見積の作成・照会を提供するRESTコントローラ。
 * 見積の新規作成、見積番号による照会APIを提供します。
 */
@RestController
@RequestMapping("/api/quotes")
public class QuoteController {

    /** 見積サービス */
    private final QuoteService quoteService;

    /**
     * コンストラクタ。見積サービスを注入します。
     *
     * @param quoteService 見積サービス
     */
    public QuoteController(QuoteService quoteService) {
        this.quoteService = quoteService;
    }

    /**
     * 新しい見積を作成します。
     * リクエストパラメータに基づいて保険料を計算し、見積を保存します。
     *
     * @param request 見積作成リクエスト（運転者情報、車両情報、保険条件などを含む）
     * @return 作成された見積のレスポンス（保険料、内訳情報を含む）
     */
    @PostMapping
    public ResponseEntity<QuoteResponse> createQuote(@Valid @RequestBody QuoteRequest request) {
        QuoteResponse response = quoteService.createQuote(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * 見積番号で見積を照会します。
     *
     * @param quoteNo 見積番号
     * @return 見積の詳細情報
     */
    @GetMapping("/{quoteNo}")
    public ResponseEntity<QuoteResponse> getQuote(@PathVariable String quoteNo) {
        QuoteResponse response = quoteService.getQuote(quoteNo);
        return ResponseEntity.ok(response);
    }
}
