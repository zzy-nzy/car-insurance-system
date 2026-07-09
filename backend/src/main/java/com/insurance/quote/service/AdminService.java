package com.insurance.quote.service;

import com.insurance.quote.dto.LoginRequest;
import com.insurance.quote.dto.LoginResponse;
import com.insurance.quote.entity.AdminUser;
import com.insurance.quote.repository.AdminUserRepository;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;

/**
 * 管理者認証を担当するサービスクラス。
 * ログイン処理、パスワード検証、JWTトークン発行を行います。
 */
@Service
public class AdminService {

    /** 管理者ユーザー情報のリポジトリ */
    private final AdminUserRepository adminUserRepository;
    /** JWTトークン管理サービス */
    private final JwtService jwtService;

    /**
     * コンストラクタ。管理者リポジトリとJWTサービスを注入します。
     *
     * @param adminUserRepository 管理者ユーザーリポジトリ
     * @param jwtService          JWTトークンサービス
     */
    public AdminService(AdminUserRepository adminUserRepository, JwtService jwtService) {
        this.adminUserRepository = adminUserRepository;
        this.jwtService = jwtService;
    }

    /**
     * 管理者ログインを処理します。
     * ユーザー名でユーザーを検索し、BCryptでパスワードを検証し、
     * 認証成功時はJWTトークンを発行して返します。
     *
     * @param request ログインリクエスト（ユーザー名、パスワードを含む）
     * @return ログイン成功時のレスポンス（JWTトークンと表示名）
     * @throws RuntimeException ユーザー名またはパスワードが一致しない場合
     */
    public LoginResponse login(LoginRequest request) {
        AdminUser user = adminUserRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new RuntimeException("ユーザー名またはパスワードが正しくありません"));

        // BCryptでパスワードの一致を検証
        if (!BCrypt.checkpw(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("ユーザー名またはパスワードが正しくありません");
        }

        // 認証成功：JWTトークンを生成して返却
        String token = jwtService.generateToken(user.getUsername());
        return new LoginResponse(token, user.getDisplayName());
    }

    /**
     * 指定されたユーザー名が管理者ユーザーとして存在するかを検証する。
     * <p>
     * JWTトークンは有効（署名検証OK・期限内有効）だが、
     * トークンに含まれるユーザー名が管理者テーブルに存在しない場合、
     * 認証は成功しているがアクセス権限がない（403 FORBIDDEN）と判定するために使用する。
     *
     * @param username 検証対象のユーザー名
     * @return 管理者ユーザーが存在する場合はtrue、存在しない場合はfalse
     */
    public boolean isValidAdmin(String username) {
        return adminUserRepository.findByUsername(username).isPresent();
    }
}
