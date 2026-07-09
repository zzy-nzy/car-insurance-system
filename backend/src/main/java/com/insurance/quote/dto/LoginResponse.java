package com.insurance.quote.dto;

/**
 * ログインレスポンスを表す DTO クラス。
 * <p>
 * 認証成功時にトークンと管理者の表示名をクライアントに返す。
 */
public class LoginResponse {

    private String token; // 認証トークン
    private String displayName; // 管理者の表示名

    public LoginResponse() {}

    /**
     * トークンと表示名を指定してインスタンスを生成する。
     * @param token 認証トークン
     * @param displayName 管理者の表示名
     */
    public LoginResponse(String token, String displayName) {
        this.token = token;
        this.displayName = displayName;
    }

    /**
     * トークンを取得する。
     * @return 認証トークン
     */
    public String getToken() { return token; }
    /**
     * トークンを設定する。
     * @param token 認証トークン
     */
    public void setToken(String token) { this.token = token; }
    /**
     * 表示名を取得する。
     * @return 管理者の表示名
     */
    public String getDisplayName() { return displayName; }
    /**
     * 表示名を設定する。
     * @param displayName 管理者の表示名
     */
    public void setDisplayName(String displayName) { this.displayName = displayName; }
}
