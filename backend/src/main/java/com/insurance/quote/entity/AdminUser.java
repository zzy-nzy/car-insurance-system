package com.insurance.quote.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * 管理者ユーザーを表す JPA エンティティクラス。
 * <p>
 * 管理者アカウントの認証情報および表示名を保持する。
 * 作成日時・更新日時はライフサイクルコールバックで自動設定される。
 */
@Entity
@Table(name = "admin_users")
public class AdminUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // 主キー（自動採番）

    @Column(name = "username", length = 50, nullable = false, unique = true)
    private String username; // ユーザー名（一意）

    @Column(name = "password", length = 255, nullable = false)
    private String password; // パスワード（ハッシュ化）

    @Column(name = "display_name", length = 100)
    private String displayName; // 表示名

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt; // 作成日時

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt; // 更新日時

    /**
     * エンティティ保存前に作成日時・更新日時を現在日時で設定する。
     */
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    /**
     * エンティティ更新前に更新日時を現在日時で設定する。
     */
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    /** @return 主キーID */
    public Long getId() { return id; }
    /** @param id 主キーID */
    public void setId(Long id) { this.id = id; }
    /** @return ユーザー名 */
    public String getUsername() { return username; }
    /** @param username ユーザー名 */
    public void setUsername(String username) { this.username = username; }
    /** @return パスワード */
    public String getPassword() { return password; }
    /** @param password パスワード */
    public void setPassword(String password) { this.password = password; }
    /** @return 表示名 */
    public String getDisplayName() { return displayName; }
    /** @param displayName 表示名 */
    public void setDisplayName(String displayName) { this.displayName = displayName; }
    /** @return 作成日時 */
    public LocalDateTime getCreatedAt() { return createdAt; }
    /** @param createdAt 作成日時 */
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    /** @return 更新日時 */
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    /** @param updatedAt 更新日時 */
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
