package com.insurance.quote.dto;

import java.util.Map;

/**
 * エラーレスポンスを表す DTO クラス。
 * <p>
 * API でエラーが発生した際、エラーコード・メッセージ・
 * フィールドごとのエラー詳細をクライアントに返す。
 */
public class ErrorResponse {

    private String code; // エラーコード
    private String message; // エラーメッセージ
    private Map<String, String> errors; // フィールドごとのエラー詳細

    public ErrorResponse() {}

    /**
     * エラーコードとメッセージを指定してインスタンスを生成する。
     * @param code エラーコード
     * @param message エラーメッセージ
     */
    public ErrorResponse(String code, String message) {
        this.code = code;
        this.message = message;
    }

    /**
     * エラーコード・メッセージ・フィールド別エラー詳細を指定してインスタンスを生成する。
     * @param code エラーコード
     * @param message エラーメッセージ
     * @param errors フィールドごとのエラー詳細マップ
     */
    public ErrorResponse(String code, String message, Map<String, String> errors) {
        this.code = code;
        this.message = message;
        this.errors = errors;
    }

    /**
     * エラーコードを取得する。
     * @return エラーコード
     */
    public String getCode() { return code; }
    /**
     * エラーコードを設定する。
     * @param code エラーコード
     */
    public void setCode(String code) { this.code = code; }
    /**
     * エラーメッセージを取得する。
     * @return エラーメッセージ
     */
    public String getMessage() { return message; }
    /**
     * エラーメッセージを設定する。
     * @param message エラーメッセージ
     */
    public void setMessage(String message) { this.message = message; }
    /**
     * フィールド別エラー詳細マップを取得する。
     * @return フィールド別エラー詳細
     */
    public Map<String, String> getErrors() { return errors; }
    /**
     * フィールド別エラー詳細マップを設定する。
     * @param errors フィールド別エラー詳細
     */
    public void setErrors(Map<String, String> errors) { this.errors = errors; }
}
