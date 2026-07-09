package com.insurance.quote.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

/**
 * Spring Security のセキュリティ設定クラス。
 * <p>
 * CORS 設定、CSRF 無効化、セッション管理（ステートレス）、
 * および URL パターンごとの認可ルールを定義する。
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    /**
     * HTTP セキュリティフィルタチェーンを構築・設定する。
     * <p>
     * CORS を有効化し、CSRF を無効化、セッションをステートレスに設定した上で、
     * 各エンドポイントの認可ルールを定義する。
     *
     * @param http HttpSecurity ビルダー
     * @return 構築済みの SecurityFilterChain
     * @throws Exception 設定中に例外が発生した場合
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .cors(cors -> cors.configurationSource(corsSource())) // CORS 設定を適用
            .csrf(csrf -> csrf.disable()) // API利用のためCSRFを無効化
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) // ステートレスセッション
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/quotes/**").permitAll() // 見積APIは全許可
                .requestMatchers("/api/admin/**").permitAll() // 管理者APIは全許可
                .requestMatchers("/api/master/**").permitAll() // マスターAPIはMasterControllerでJWT認証を実施
                .requestMatchers("/swagger-ui.html", "/swagger-ui/**", "/api-docs/**", "/v3/api-docs/**").permitAll() // Swagger UIを許可
                .anyRequest().authenticated() // それ以外は認証必須
            );
        return http.build();
    }

    /**
     * CORS 設定を提供する。
     * <p>
     * 全オリジンからのアクセスを許可し、主要な HTTP メソッドとヘッダーを許可する。
     *
     * @return CORS 設定ソース
     */
    @Bean
    public CorsConfigurationSource corsSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(List.of("*")); // 全オリジンを許可
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS")); // 主要メソッドを許可
        config.setAllowedHeaders(List.of("*")); // 全ヘッダーを許可
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config); // 全パスにCORS設定を適用
        return source;
    }
}
