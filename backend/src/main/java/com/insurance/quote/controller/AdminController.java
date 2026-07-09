package com.insurance.quote.controller;

import com.insurance.quote.dto.*;
import com.insurance.quote.exception.ForbiddenException;
import com.insurance.quote.service.AdminService;
import com.insurance.quote.service.JwtService;
import com.insurance.quote.service.QuoteService;
import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * 管理者向けRESTコントローラ。
 * 管理者ログイン、認証状態確認、見積一覧照会、見積詳細照会、CSVエクスポートのAPIを提供します。
 * ログイン以外のエンドポイントはJWTトークンによる認証が必要です。
 */
@RestController
@RequestMapping("/api/admin")
public class AdminController {

    /** 管理者認証サービス */
    private final AdminService adminService;
    /** 見積管理サービス */
    private final QuoteService quoteService;
    /** JWTトークン管理サービス */
    private final JwtService jwtService;

    /**
     * コンストラクタ。各種サービスを注入します。
     *
     * @param adminService 管理者認証サービス
     * @param quoteService 見積管理サービス
     * @param jwtService   JWTトークンサービス
     */
    public AdminController(AdminService adminService, QuoteService quoteService, JwtService jwtService) {
        this.adminService = adminService;
        this.quoteService = quoteService;
        this.jwtService = jwtService;
    }

    /**
     * 管理者ログインを処理します。
     * ユーザー名とパスワードを検証し、JWTトークンを返します。
     *
     * @param request ログインリクエスト（ユーザー名、パスワードを含む）
     * @return ログイン成功時のレスポンス（JWTトークンを含む）
     */
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        LoginResponse response = adminService.login(request);
        return ResponseEntity.ok(response);
    }

    /**
     * 現在ログイン中の管理者情報を返します。
     * AuthorizationヘッダーからJWTトークンを検証し、ユーザー名を返します。
     *
     * @param authHeader Authorizationリクエストヘッダー（Bearer トークン形式）
     * @return 管理者情報レスポンス、認証失敗時は401エラー、権限不足時は403エラー
     */
    @GetMapping("/me")
    public ResponseEntity<?> me(@RequestHeader(value = "Authorization", required = false) String authHeader) {
        String username = validateAndExtract(authHeader);
        if (username == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ErrorResponse("UNAUTHORIZED", "管理者認証が必要です"));
        }
        return ResponseEntity.ok(new AdminInfoResponse(username));
    }

    /**
     * 見積一覧を検索条件付きで取得します。
     * 管理者認証後に検索条件に合致する見積の概要リストを返します。
     *
     * @param authHeader Authorizationリクエストヘッダー（Bearer トークン形式）
     * @param search     検索条件（見積番号、メーカー、車名、車種、日付範囲）
     * @return 見積概要のリスト、認証失敗時は401エラー、権限不足時は403エラー
     */
    @GetMapping("/quotes")
    public ResponseEntity<?> listQuotes(@RequestHeader(value = "Authorization", required = false) String authHeader,
                                         @ModelAttribute QuoteSearchRequest search) {
        String username = validateAndExtract(authHeader);
        if (username == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ErrorResponse("UNAUTHORIZED", "管理者認証が必要です"));
        }
        List<QuoteSummaryResponse> quotes = quoteService.listQuotes(search);
        return ResponseEntity.ok(quotes);
    }

    /**
     * 見積番号で見積の詳細を取得します。
     *
     * @param quoteNo    見積番号
     * @param authHeader Authorizationリクエストヘッダー（Bearer トークン形式）
     * @return 見積詳細レスポンス、認証失敗時は401エラー、権限不足時は403エラー、見積不在時は404エラー
     */
    @GetMapping("/quotes/{quoteNo}")
    public ResponseEntity<?> getQuoteDetail(@PathVariable String quoteNo,
                                             @RequestHeader(value = "Authorization", required = false) String authHeader) {
        String username = validateAndExtract(authHeader);
        if (username == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ErrorResponse("UNAUTHORIZED", "管理者認証が必要です"));
        }
        QuoteResponse response = quoteService.getQuote(quoteNo);
        return ResponseEntity.ok(response);
    }

    /**
     * 見積データをCSV形式でエクスポートします。
     * 検索条件に合致する見積をCSVファイルとしてダウンロードします。
     * UTF-8エンコーディングでBOM付きのCSVを生成し、Excelでの文字化けを防止します。
     *
     * @param authHeader Authorizationリクエストヘッダー（Bearer トークン形式）
     * @param search     検索条件
     * @return CSVファイルのバイナリデータ、認証失敗時は401エラー、権限不足時は403エラー
     */
    @GetMapping(value = "/quotes.csv", produces = "text/csv; charset=UTF-8")
    public ResponseEntity<?> exportCsv(@RequestHeader(value = "Authorization", required = false) String authHeader,
                                        @ModelAttribute QuoteSearchRequest search) {
        String username = validateAndExtract(authHeader);
        if (username == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ErrorResponse("UNAUTHORIZED", "管理者認証が必要です"));
        }
        String csv = quoteService.generateCsv(search);
        byte[] bytes = csv.getBytes(StandardCharsets.UTF_8);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(new MediaType("text", "csv", StandardCharsets.UTF_8));
        headers.setContentDispositionFormData("attachment", "quotes.csv");
        return new ResponseEntity<>(bytes, headers, HttpStatus.OK);
    }

    /**
     * AuthorizationヘッダーからJWTトークンを検証し、ユーザー名を抽出します。
     * 複数のエンドポイントで共通的に使用される認証ヘルパーメソッドです。
     * <p>
     * 認証フロー：
     * <ol>
     *   <li>Authorizationヘッダー不在 or "Bearer "プレフィックスなし → null返却（呼び出し元で401を返す）</li>
     *   <li>JWTトークン無効（署名エラー・期限切れ） → null返却（呼び出し元で401を返す）</li>
     *   <li>JWTトークン有効だが管理者ユーザー不在 → ForbiddenExceptionスロー（403 FORBIDDEN）</li>
     *   <li>JWTトークン有効かつ管理者ユーザー存在 → ユーザー名返却（認証成功）</li>
     * </ol>
     *
     * @param authHeader Authorizationヘッダー値（"Bearer "プレフィックス付き）
     * @return 認証成功時はユーザー名、認証失敗（401）時はnull
     * @throws ForbiddenException トークンは有効だが管理者権限がない場合（403）
     */
    private String validateAndExtract(String authHeader) {
        // Bearer プレフィックスの有無を確認
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return null;
        }
        // "Bearer " の7文字をスキップしてトークン部分を取得
        String token = authHeader.substring(7);
        // トークンの有効性を検証
        if (!jwtService.validateToken(token)) {
            return null;
        }
        // トークンからユーザー名を抽出
        String username = jwtService.extractUsername(token);
        // トークンは有効だが、管理者ユーザーとして存在しない場合は権限不足（403）
        if (!adminService.isValidAdmin(username)) {
            throw new ForbiddenException("このリソースにアクセスする権限がありません");
        }
        // 認証成功：ユーザー名を返却
        return username;
    }
}
