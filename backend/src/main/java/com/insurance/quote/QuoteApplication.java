package com.insurance.quote;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 自動車保険見積システムの Spring Boot アプリケーション起動クラス。
 * <p>
 * アプリケーションのエントリポイントであり、Spring Boot の自動設定・
 * コンポーネントスキャン・コンフィギュレーションを有効化する。
 */
@SpringBootApplication
public class QuoteApplication {
    /**
     * アプリケーションを起動する。
     * @param args コマンドライン引数
     */
    public static void main(String[] args) {
        SpringApplication.run(QuoteApplication.class, args);
    }
}
