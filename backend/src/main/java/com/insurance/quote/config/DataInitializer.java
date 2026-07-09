package com.insurance.quote.config;

import com.insurance.quote.entity.AdminUser;
import com.insurance.quote.repository.AdminUserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCrypt;

/**
 * アプリケーション起動時の初期データ登録を行う設定クラス。
 * <p>
 * データベースに管理者アカウントが存在しない場合、
 * デフォルトの管理者情報を自動生成して登録する。
 */
@Configuration
public class DataInitializer {

    /**
     * アプリケーション起動時に初期管理者アカウントを生成する CommandLineRunner を定義する。
     * <p>
     * ユーザー名 "admin" のアカウントが未登録の場合のみ、
     * パスワードを BCrypt でハッシュ化して登録する。
     *
     * @param adminUserRepository 管理者ユーザーのリポジトリ
     * @return 初期化処理を行う CommandLineRunner
     */
    @Bean
    CommandLineRunner initAdmin(AdminUserRepository adminUserRepository) {
        return args -> {
            // 管理者アカウントが未存在か確認
            if (adminUserRepository.findByUsername("admin").isEmpty()) {
                AdminUser user = new AdminUser();
                user.setUsername("admin"); // ユーザー名を設定
                user.setPassword(BCrypt.hashpw("admin123", BCrypt.gensalt())); // パスワードをハッシュ化して設定
                user.setDisplayName("管理者"); // 表示名を設定
                adminUserRepository.save(user); // 管理者ユーザーを保存
                System.out.println("初期管理者アカウントを作成しました: admin / admin123");
            }
        };
    }
}
