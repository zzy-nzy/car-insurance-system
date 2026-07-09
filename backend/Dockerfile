# ===================================================
# 自動車保険見積システム バックエンド Docker イメージ
# マルチステージビルド構成:
#   ステージ1: Maven + JDK 17 でソースコードをビルド
#   ステージ2: JRE 17 のみで軽量な実行イメージを作成
# ===================================================

# ---- ビルドステージ ----
# Maven 3.9 + JDK 17 のビルド用ベースイメージ
FROM maven:3.9-eclipse-temurin-17 AS build
# 作業ディレクトリを /app に設定
WORKDIR /app
# 依存関係解決のため pom.xml を先にコピー
COPY pom.xml .
# 依存関係を事前ダウンロードしてキャッシュ（ビルド高速化）
RUN mvn dependency:go-offline -B
# ソースコードをコピー
COPY src ./src
# テストをスキップして jar をパッケージング
RUN mvn package -DskipTests -B

# ---- 実行ステージ ----
# JRE 17 のみの軽量ベースイメージ
FROM eclipse-temurin:17-jre
# 作業ディレクトリを /app に設定
WORKDIR /app
# ビルドステージから jar をコピー
COPY --from=build /app/target/*.jar app.jar
# アプリケーションの待受ポートを公開
EXPOSE 8080
# Java アプリケーションを起動
ENTRYPOINT ["java", "-jar", "app.jar"]
