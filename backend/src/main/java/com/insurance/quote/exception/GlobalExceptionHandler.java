package com.insurance.quote.exception;

import com.insurance.quote.dto.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

/**
 * アプリケーション全体の例外をハンドリングするグローバル例外ハンドラクラス。
 * <p>
 * バリデーションエラー（400）、不正引数エラー（400）、リソース未検出エラー（404）、
 * アクセス権限不足エラー（403）、およびその他のシステムエラー（500）を
 * 統一的な形式（ErrorResponse）でクライアントに返却する。
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * バリデーションエラー（@Valid 失敗）を処理し、フィールド別エラー詳細を返す。
     *
     * @param ex バリデーション例外
     * @return HTTP 400 とフィールド別エラー詳細を含む ErrorResponse
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        // 各フィールドのエラーメッセージをマップに格納
        for (FieldError fe : ex.getBindingResult().getFieldErrors()) {
            errors.put(fe.getField(), fe.getDefaultMessage());
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponse("VALIDATION_ERROR", "入力チェックエラー", errors));
    }

    /**
     * 不正引数例外を処理し、エラーメッセージを返す。
     *
     * @param ex 不正引数例外
     * @return HTTP 400 とエラー詳細を含む ErrorResponse
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgument(IllegalArgumentException ex) {
        Map<String, String> errors = new HashMap<>();
        errors.put("message", ex.getMessage()); // エラーメッセージを格納
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponse("VALIDATION_ERROR", ex.getMessage(), errors));
    }

    /**
     * リソース未検出例外を処理し、404 エラーを返す。
     *
     * @param ex リソース未検出例外
     * @return HTTP 404 とエラーメッセージを含む ErrorResponse
     */
    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(NotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ErrorResponse("NOT_FOUND", ex.getMessage()));
    }

    /**
     * アクセス権限不足例外（カスタム）を処理し、403 エラーを返す。
     * <p>
     * 認証済みだが該当リソースへのアクセス権限がない場合に発生する。
     *
     * @param ex 権限不足例外
     * @return HTTP 403 とエラーメッセージを含む ErrorResponse
     */
    @ExceptionHandler(ForbiddenException.class)
    public ResponseEntity<ErrorResponse> handleForbidden(ForbiddenException ex) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(new ErrorResponse("FORBIDDEN", ex.getMessage()));
    }

    /**
     * Spring Security のアクセス拒否例外を処理し、403 エラーを返す。
     * <p>
     * Spring Security の認可フィルタでアクセスが拒否された場合に発生する。
     *
     * @param ex アクセス拒否例外
     * @return HTTP 403 とエラーメッセージを含む ErrorResponse
     */
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAccessDenied(AccessDeniedException ex) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(new ErrorResponse("FORBIDDEN", "アクセス権限がありません"));
    }

    /**
     * 上記以外の未処理例外を処理し、500 エラーを返す。
     *
     * @param ex 未処理例外
     * @return HTTP 500 とシステムエラーメッセージを含む ErrorResponse
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneral(Exception ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse("SYSTEM_ERROR", "システムエラーが発生しました"));
    }
}
