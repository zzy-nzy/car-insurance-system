package com.insurance.quote.exception;

/**
 * アクセス権限が不足している場合にスローされるカスタム例外クラス。
 * <p>
 * 認証済み（JWTトークンが有効）だが、該当リソースへのアクセス権限がない場合に使用され、
 * グローバル例外ハンドラにより HTTP 403 FORBIDDEN に変換される。
 */
public class ForbiddenException extends RuntimeException {
    /**
     * エラーメッセージを指定してインスタンスを生成する。
     * @param message エラーメッセージ
     */
    public ForbiddenException(String message) {
        super(message);
    }
}
