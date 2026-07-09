package com.insurance.quote.service;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

/**
 * JWT（JSON Web Token）を管理するサービスクラス。
 * トークンの生成、ユーザー名の抽出、トークンの検証を担当します。
 */
@Service
public class JwtService {

    /** JWT署名用の秘密鍵 */
    private final SecretKey key;
    /** トークンの有効期限（ミリ秒） */
    private final long expirationMs;

    /**
     * コンストラクタ。設定ファイルからJWTシークレットと有効期限を注入します。
     *
     * @param secret       JWT署名用のシークレット文字列
     * @param expirationMs トークンの有効期限（ミリ秒）
     */
    public JwtService(@Value("${app.jwt.secret}") String secret,
                      @Value("${app.jwt.expiration-ms}") long expirationMs) {
        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.expirationMs = expirationMs;
    }

    /**
     * 指定されたユーザー名に対してJWTトークンを生成します。
     * トークンにはユーザー名（subject）、発行日時、有効期限が含まれます。
     *
     * @param username トークンに埋め込むユーザー名
     * @return 生成されたJWTトークン文字列
     */
    public String generateToken(String username) {
        Date now = new Date();
        return Jwts.builder()
                .subject(username)
                .issuedAt(now)
                .expiration(new Date(now.getTime() + expirationMs))
                .signWith(key)
                .compact();
    }

    /**
     * JWTトークンからユーザー名（subject）を抽出します。
     *
     * @param token JWTトークン文字列
     * @return トークンに含まれるユーザー名
     */
    public String extractUsername(String token) {
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();
    }

    /**
     * JWTトークンの有効性を検証します。
     * 署名の検証、有効期限の確認などを行い、問題がなければtrueを返します。
     *
     * @param token JWTトークン文字列
     * @return トークンが有効な場合はtrue、無効な場合はfalse
     */
    public boolean validateToken(String token) {
        try {
            Jwts.parser().verifyWith(key).build().parseSignedClaims(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }
}
