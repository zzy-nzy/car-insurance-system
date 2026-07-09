package com.insurance.quote.controller;

import com.insurance.quote.entity.RateMaster;
import com.insurance.quote.repository.RateMasterRepository;
import com.insurance.quote.service.JwtService;
import com.insurance.quote.dto.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * マスターデータを提供するRESTコントローラ。
 * 保険料率マスタの参照APIを提供します。
 * 詳細設計書のAPI-007認証要件に従い、JWT認証を必須とします。
 */
@RestController
@RequestMapping("/api/master")
public class MasterController {

    /** 料率マスタのリポジトリ */
    private final RateMasterRepository rateMasterRepository;
    /** JWT認証サービス */
    private final JwtService jwtService;

    /**
     * コンストラクタ。料率マスタリポジトリとJWTサービスを注入します。
     *
     * @param rateMasterRepository 料率マスタリポジトリ
     * @param jwtService JWT認証サービス
     */
    public MasterController(RateMasterRepository rateMasterRepository, JwtService jwtService) {
        this.rateMasterRepository = rateMasterRepository;
        this.jwtService = jwtService;
    }

    /**
     * 有効な料率マスタ一覧を取得します。
     * カテゴリ、IDの昇順でソートされた料率情報を返します。
     * 詳細設計書API-007の認証要件に従い、JWTトークンの検証を行います。
     *
     * @param authHeader Authorization ヘッダー（Bearer トークン）
     * @return 有効な料率マスタのリスト（認証失敗時は401）
     */
    @GetMapping("/rates")
    public ResponseEntity<?> getRates(@RequestHeader(value = "Authorization", required = false) String authHeader) {
        // 認証ヘッダーの存在と形式チェック
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ErrorResponse("UNAUTHORIZED", "認証が必要です"));
        }
        // JWTトークンの検証
        String token = authHeader.substring(7);
        if (!jwtService.validateToken(token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ErrorResponse("UNAUTHORIZED", "無効なトークンです"));
        }
        // 認証成功：料率マスタを返却
        return ResponseEntity.ok(rateMasterRepository.findByActiveTrueOrderByCategoryAscIdAsc());
    }
}
