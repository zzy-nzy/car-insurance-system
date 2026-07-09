package com.insurance.quote.exception;

/**
 * リソースが見つからない場合にスローされるカスタム例外クラス。
 * <p>
 * 指定された見積やマスタデータが存在しない場合などに使用され、
 * グローバル例外ハンドラにより HTTP 404 に変換される。
 */
public class NotFoundException extends RuntimeException {
    /**
     * エラーメッセージを指定してインスタンスを生成する。
     * @param message エラーメッセージ
     */
    public NotFoundException(String message) {
        super(message);
    }
}
