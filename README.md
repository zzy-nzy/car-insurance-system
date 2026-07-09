# 自動車保険見積サイト

Insurance Quote System — Spring Boot 3 + React 18 + PostgreSQL 16

## 起動方法

### Docker Compose 一発起動（推奨）

全てコンテナで実行、ローカルに Java / Node / PostgreSQL のインストール不要。

**事前前提**: PC に Docker Desktop がインストール済み、Docker サービスが起動していること

```bash
# ビルド＆起動（初回はイメージダウンロードに数分かかる）
docker compose up --build -d

# 起動確認
docker compose ps
# → insurance-db / insurance-backend / insurance-frontend 全てが Up と表示されること
```

### アクセス一覧

| サービス | URL |
|---------|-----|
| フロント画面 | http://localhost:3000 |
| Backend API | http://localhost:8080 |
| Swagger UI | http://localhost:8080/swagger-ui.html |

### 停止 / 再起動 / リセット

```bash
docker compose down          # 停止（データは保持）
docker compose down -v       # 停止して DB データも削除（初期状態に戻る）
docker compose up -d         # 再開
docker compose up --build -d # 再ビルドして起動（コード変更時）
```

---

## 管理者アカウント

| 項目 | 値 |
|------|-----|
| ユーザー名 | **admin** |
| パスワード | **admin123** |

初回起動時に DataInitializer が自動で作成します。

---

## テスト実行方法

### Backend テスト（JUnit 5）

```bash
cd backend
mvn test
```

**テスト内容**（全 25 件・全 PASS）:

| テスト | 件数 | 内容 |
|--------|------|------|
| PremiumCalculationServiceTest | 10 | 年齢/距離/等級の範囲解決、丸め処理、3つの計算例（標準/高リスク/ミドル）、料率欠落時の挙動、内訳保存件数、事故有係数 |
| AdminServiceTest | 5 | ログイン成功・パスワード誤り・ユーザー不在・isValidAdmin(true/false) |
| QuoteServiceTest | 2 | 見積取得成功・存在しない見積番号 |
| QuoteControllerTest | 2 | 正常作成(201)・バリデーションエラー(400) |
| AdminControllerTest | 6 | ログイン成功・見積詳細(認証あり/なし/ヘッダなし)・一覧取得・権限なし(403) |

### Frontend テスト（React Testing Library）

```bash
cd frontend
npm test
```

**テスト内容**（全 9 件・全 PASS）:
| テスト | 件数 | 内容 |
|--------|------|------|
| TopPage.test.tsx | 3 | 2つのエントリーポイント表示、各ボタンクリック |
| UserInfoForm.test.tsx | 6 | 全フィールド表示、年齢未入力/境界超過、免許色未選択、走行距離超過、全項目正常 |

> **テスト実測結果**: Backend 25 件 + Frontend 9 件 = 計 34 件すべて PASS。
> API 結合テスト（IT-001〜IT-008）は Docker(PostgreSQL 16) 実起動で全件 OK を確認。
> 詳細は `test_result.docx` を参照。

---

## 技術スタック

| 分類 | 技術 |
|------|------|
| Backend | Java 17 + Spring Boot 3.2.5 + Spring Data JPA |
| Frontend | React 18 + TypeScript |
| Database | PostgreSQL 16（Docker Compose） |
| Container | Docker / Docker Compose |
| API 仕様 | OpenAPI / Swagger UI（springdoc-openapi） |
| 認証 | JWT（jjwt 0.12.5） |
| パスワード | BCrypt |
| テスト(Backend) | JUnit 5 + Mockito |
| テスト(Frontend) | React Testing Library + Jest |

---

## プロジェクト構成

```
car_insurance_system/
├── backend/                    # Spring Boot アプリケーション
│   ├── src/main/java/com/insurance/quote/
│   │   ├── config/             # Security, Swagger, DataInitializer
│   │   ├── controller/         # REST コントローラ
│   │   ├── service/            # ビジネスロジック（計算、認証）
│   │   ├── repository/         # データアクセス（JPA）
│   │   ├── entity/             # JPA エンティティ
│   │   ├── dto/                # リクエスト/レスポンスDTO
│   │   └── exception/          # 例外ハンドリング
│   ├── Dockerfile
│   └── pom.xml
├── frontend/                   # React アプリケーション
│   ├── src/
│   │   ├── components/         # 9画面コンポーネント
│   │   ├── api/                # Axios API クライアント
│   │   ├── types/              # TypeScript 型定義
│   │   └── styles/             # CSS
│   ├── Dockerfile
│   └── package.json
├── db/
│   └── init.sql                # DDL + 27件の料率マスタ初期データ
├── docker-compose.yml          # 3サービス（frontend/backend/db）
├── test_result.docx  # テスト結果報告書（36件記入済み）
└── README.md
```

---

## API 一覧

| メソッド | URL | 概要 | 認証 | ステータス |
|---------|-----|------|------|-----------|
| POST | `/api/quotes` | 見積作成 | 不要 | ✅ |
| GET | `/api/quotes/{quoteNo}` | 見積結果取得 | 不要 | ✅ |
| POST | `/api/admin/login` | 管理者ログイン（JWT発行） | 不要 | ✅ |
| GET | `/api/admin/me` | ログイン管理者情報 | 要 | ✅ |
| GET | `/api/admin/quotes` | 見積一覧検索（条件指定可） | 要 | ✅ |
| GET | `/api/admin/quotes/{quoteNo}` | 見積詳細（内訳含む） | 要 | ✅ |
| GET | `/api/admin/quotes.csv` | CSV出力（UTF-8 BOM付き） | 要 | ✅ |
| GET | `/api/master/rates` | 料率マスタ参照 | 要 | ✅ |

---

## 画面一覧（ウィザード形式）

| # | 画面 | 主なバリデーション |
|---|------|-------------------|
| 0 | トップページ | 2つのエントリ（ユーザー / 管理者） |
| 1 | 使用者情報 | 年齢18-100、免許色/目的/範囲の必須選択、距離0-30000 |
| 2 | 契約中保険 | 加入ありの場合：等級1-20必須、事故有係数0-6必須 |
| 3 | 車両情報 | メーカー/車名50文字以内、YYYY-MM形式、未来年月禁止 |
| 4 | 補償条件 | 対人無制限固定、対物/人身/弁護士/ロードサービス必須選択 |
| 5 | 入力確認 | 全項目確認 + 概算保険料表示 |
| 6 | 見積結果 | 見積番号、年額/月額、計算内訳表示 |
| 7 | 管理者ログイン | ユーザー名/パスワード認証 |
| 8 | 見積一覧 | 条件検索、CSVエクスポート、行クリックで詳細 |

---

## 見積計算ロジック

`PremiumCalculationService` に集約。詳細設計書に基づく簡易ロジック。

1. **基本保険料**: 50,000円（全件一律）
2. **乗算型係数**を順次適用（年齢 → 免許色 → 使用目的 → 走行距離 → 運転者範囲 → 等級 → 事故有係数 → 車両タイプ）
3. **加算型金額**を追加（車両保険 +30,000、対物無制限 +5,000、人身傷害 5,000万 +3,000/無制限 +7,000、弁護士特約 +2,000、ロードサービス +1,500）
4. **年間保険料**: 10円未満四捨五入
5. **月額保険料**: 年額 ÷ 12 → 10円未満四捨五入
6. 適用した係数・加算額は `quote_breakdowns` に保存

### テストデータ例

| ケース | 年齢 | 免許色 | 走行距離 | 範囲 | 車種 | オプション | 年額 |
|--------|------|--------|---------|------|------|-----------|------|
| 標準 | 30 / BLUE | 10,000km | SELF | SEDAN | 車両保険+対物無制限+人身無制限+弁護士+ロード | **90,500円** |
| 高リスク | 18 / GREEN | 15,000km | ANYONE | SUV | 同＋等級1-5＋事故有 | **317,830円** |
| ミドル | 28 / GOLD | 5,000km | COUPLE | COMPACT | 車両保険+対物無制限+人身5,000万+ロード | **92,550円** |

---

## 設計上の補足

- **責務分離**: Controller → Service → Repository / Entity → DTO の層を厳守
- **バリデーション**: フロント（alert）＋ バックエンド（@Valid + GlobalExceptionHandler）の二重チェック
- **認証**: JWT、Spring Security は CORS + permitAll 設定のみ、認証ロジックは Controller 内で JwtService を使用
- **パスワード**: BCrypt ハッシュ化（DataInitializer で admin 作成時）
- **CSV**: UTF-8 BOM（\uFEFF）付きで出力、Excel 直接開いても文字化けしない
- **データベース**: PostgreSQL 16 のみを使用（Docker Compose で起動）

## 既知の制限

- 管理画面の一覧検索は基本的な部分一致のみ（日付範囲検索等はクエリパラメータ対応済み）
- フロントエンドの画面テストは主要画面のみ（TopPage, UserInfoForm）
- 実際の保険料率ではなく課題用簡易ロジックを使用
- `/api/master/rates` は詳細設計書 API-007 に従い JWT 認証が必要（MasterController でトークン検証を実施）
