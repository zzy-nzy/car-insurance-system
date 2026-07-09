package com.insurance.quote.dto;

/**
 * 管理者情報のレスポンスを表す DTO クラス。
 * <p>
 * ログイン中の管理者ユーザー名をフロントエンドに返す際に使用する。
 */
public class AdminInfoResponse {
    private String username; // 管理者ユーザー名

    public AdminInfoResponse() {}
    public AdminInfoResponse(String username) { this.username = username; }

    /**
     * ユーザー名を取得する。
     * @return ユーザー名
     */
    public String getUsername() { return username; }
    /**
     * ユーザー名を設定する。
     * @param username ユーザー名
     */
    public void setUsername(String username) { this.username = username; }
}
