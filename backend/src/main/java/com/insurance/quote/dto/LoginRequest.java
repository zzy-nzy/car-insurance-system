package com.insurance.quote.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * ログインリクエストを表す DTO クラス。
 * <p>
 * 管理者ログイン時に送信されるユーザー名とパスワードを保持する。
 * 各フィールドは必須入力である。
 */
public class LoginRequest {

    @NotBlank
    private String username; // ユーザー名（必須）

    @NotBlank
    private String password; // パスワード（必須）

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
    /**
     * パスワードを取得する。
     * @return パスワード
     */
    public String getPassword() { return password; }
    /**
     * パスワードを設定する。
     * @param password パスワード
     */
    public void setPassword(String password) { this.password = password; }
}
