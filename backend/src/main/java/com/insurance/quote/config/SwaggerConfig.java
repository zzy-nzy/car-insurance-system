package com.insurance.quote.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Swagger / OpenAPI ドキュメントの設定クラス。
 * <p>
 * API仕様書のタイトル・バージョン・説明文を定義する。
 */
@Configuration
public class SwaggerConfig {

    /**
     * OpenAPI 仕様のメタ情報をカスタマイズしたインスタンスを生成する。
     *
     * @return カスタマイズ済みの OpenAPI インスタンス
     */
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("自動車保険見積サイト API") // API仕様書のタイトル
                        .version("1.0.0") // APIのバージョン
                        .description("自動車保険見積サイトのREST API仕様書")); // API仕様書の説明
    }
}
