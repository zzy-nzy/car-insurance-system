package com.insurance.quote.repository;

import com.insurance.quote.entity.AdminUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * 管理者ユーザーのデータアクセスを担う JPA リポジトリインターフェース。
 * <p>
 * ユーザー名による検索など、管理者認証に必要なクエリメソッドを提供する。
 */
@Repository
public interface AdminUserRepository extends JpaRepository<AdminUser, Long> {
    /**
     * ユーザー名で管理者ユーザーを検索する。
     * @param username ユーザー名
     * @return 見つかった場合は Optional に包んだ AdminUser、見つからない場合は空の Optional
     */
    Optional<AdminUser> findByUsername(String username);
}
