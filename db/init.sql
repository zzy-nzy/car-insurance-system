-- ===================================================
-- 自動車保険見積システム DDL（PostgreSQL 16）
-- Docker 起動時に自動実行される初期化スクリプト
-- ===================================================

-- ---------------------------------------------------
-- quotes（見積ヘッダーテーブル）
-- 見積の基本情報と計算結果を保持する
-- ---------------------------------------------------
CREATE TABLE IF NOT EXISTS quotes (
    id BIGSERIAL PRIMARY KEY,                         -- 主キー（自動採番）
    quote_no VARCHAR(20) NOT NULL UNIQUE,             -- 見積番号（一意）
    driver_age INTEGER NOT NULL,                      -- 運転者年齢
    license_color VARCHAR(20) NOT NULL,               -- 免許証の色（GOLD/BLUE/GREEN）
    usage_type VARCHAR(20) NOT NULL,                  -- 使用目的（PRIVATE/COMMUTE/BUSINESS）
    annual_mileage INTEGER NOT NULL,                  -- 年間走行距離（km）
    driver_range VARCHAR(20) NOT NULL,                -- 運転者範囲（SELF/COUPLE/FAMILY/ANYONE）
    has_current_insurance BOOLEAN NOT NULL,           -- 現在保険加入有無
    grade INTEGER,                                    -- 等級（1〜20）
    accident_term INTEGER,                            -- 事故有係数期間
    maker VARCHAR(50) NOT NULL,                       -- メーカー名
    car_name VARCHAR(50) NOT NULL,                    -- 車名
    first_registration_ym CHAR(7) NOT NULL,           -- 初度登録年月（YYYY-MM）
    vehicle_type VARCHAR(20) NOT NULL,                -- 車両タイプ（KEI/COMPACT/SEDAN/MINIVAN/SUV）
    vehicle_insurance BOOLEAN NOT NULL,               -- 車両保険有無
    annual_premium INTEGER NOT NULL,                  -- 年間保険料（円）
    monthly_premium INTEGER NOT NULL,                 -- 月額保険料（円）
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),      -- 作成日時
    updated_at TIMESTAMP NOT NULL DEFAULT NOW()       -- 更新日時
);

-- ---------------------------------------------------
-- quote_breakdowns（見積計算内訳テーブル）
-- 保険料計算の内訳（基本料・各種割増引）を保持する
-- ---------------------------------------------------
CREATE TABLE IF NOT EXISTS quote_breakdowns (
    id BIGSERIAL PRIMARY KEY,                         -- 主キー（自動採番）
    quote_id BIGINT NOT NULL REFERENCES quotes(id),   -- 紐づく見積ID（外部キー）
    item_code VARCHAR(50) NOT NULL,                   -- 内訳項目コード（BASE/AGE/LICENSE など）
    item_name VARCHAR(100) NOT NULL,                  -- 内訳項目名
    rate NUMERIC(6,3),                                -- 適用料率
    amount INTEGER,                                   -- 金額（円）
    display_order INTEGER NOT NULL                    -- 表示順序
);

-- ---------------------------------------------------
-- rate_masters（料率マスタテーブル）
-- 保険料算出に使用する各種料率を定義するマスタデータ
-- ---------------------------------------------------
CREATE TABLE IF NOT EXISTS rate_masters (
    id BIGSERIAL PRIMARY KEY,                         -- 主キー（自動採番）
    category VARCHAR(50) NOT NULL,                    -- 料率カテゴリ（AGE/LICENSE/USAGE/MILEAGE/DRIVER_RANGE/GRADE/ACCIDENT_TERM/VEHICLE_TYPE）
    item_code VARCHAR(50) NOT NULL,                   -- アイテムコード
    item_name VARCHAR(100) NOT NULL,                  -- アイテム名（表示用）
    rate NUMERIC(6,3),                                -- 料率（乗算係数）
    amount INTEGER,                                   -- 固定金額（円）
    active BOOLEAN NOT NULL DEFAULT TRUE              -- 有効フラグ
);

-- ---------------------------------------------------
-- admin_users（管理者ユーザーテーブル）
-- 管理画面ログイン用の管理者アカウントを保持する
-- ---------------------------------------------------
CREATE TABLE IF NOT EXISTS admin_users (
    id BIGSERIAL PRIMARY KEY,                         -- 主キー（自動採番）
    username VARCHAR(50) NOT NULL UNIQUE,             -- ユーザー名（一意）
    password VARCHAR(255) NOT NULL,                   -- パスワード（BCryptハッシュ）
    display_name VARCHAR(100),                        -- 表示名
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),      -- 作成日時
    updated_at TIMESTAMP NOT NULL DEFAULT NOW()       -- 更新日時
);

-- ===================================================
-- 料率マスタ 初期データ
-- ===================================================
INSERT INTO rate_masters (category, item_code, item_name, rate, amount, active) VALUES
-- 年齢カテゴリ（運転者の年齢に応じた料率）
('AGE', 'AGE_18_25', '18〜25歳', 1.60, NULL, TRUE),       -- 若年層は割高（1.60倍）
('AGE', 'AGE_26_34', '26〜34歳', 1.25, NULL, TRUE),       -- やや割高（1.25倍）
('AGE', 'AGE_35_59', '35〜59歳', 1.00, NULL, TRUE),       -- 基準値（1.00倍）
('AGE', 'AGE_60_PLUS', '60歳以上', 1.20, NULL, TRUE),     -- 高齢層はやや割高（1.20倍）
-- 免許証色カテゴリ
('LICENSE', 'GOLD', 'GOLD', 0.90, NULL, TRUE),             -- ゴールド免許は割引（0.90倍）
('LICENSE', 'BLUE', 'BLUE', 1.00, NULL, TRUE),             -- ブルー免許は基準値（1.00倍）
('LICENSE', 'GREEN', 'GREEN', 1.10, NULL, TRUE),           -- グリーン免許は割高（1.10倍）
-- 使用目的カテゴリ
('USAGE', 'PRIVATE', 'PRIVATE', 1.00, NULL, TRUE),         -- 自家用は基準値（1.00倍）
('USAGE', 'COMMUTE', 'COMMUTE', 1.10, NULL, TRUE),         -- 通勤はやや割高（1.10倍）
('USAGE', 'BUSINESS', 'BUSINESS', 1.25, NULL, TRUE),       -- 業務用は割高（1.25倍）
-- 走行距離カテゴリ
('MILEAGE', 'MILEAGE_0_5000', '0〜5,000km', 0.95, NULL, TRUE),         -- 短距離は割引（0.95倍）
('MILEAGE', 'MILEAGE_5001_10000', '5,001〜10,000km', 1.00, NULL, TRUE), -- 中距離は基準値（1.00倍）
('MILEAGE', 'MILEAGE_10001_PLUS', '10,001km以上', 1.15, NULL, TRUE),    -- 長距離は割高（1.15倍）
-- 運転者範囲カテゴリ
('DRIVER_RANGE', 'SELF', 'SELF', 0.90, NULL, TRUE),       -- 本人のみは割引（0.90倍）
('DRIVER_RANGE', 'COUPLE', 'COUPLE', 0.95, NULL, TRUE),   -- 夫婦限定はやや割引（0.95倍）
('DRIVER_RANGE', 'FAMILY', 'FAMILY', 1.05, NULL, TRUE),   -- 家族限定はやや割高（1.05倍）
('DRIVER_RANGE', 'ANYONE', 'ANYONE', 1.20, NULL, TRUE),   -- 誰でも運転は割高（1.20倍）
-- 等級カテゴリ
('GRADE', 'GRADE_1_5', '1〜5等級', 1.30, NULL, TRUE),     -- 低等級は割高（1.30倍）
('GRADE', 'GRADE_6_10', '6〜10等級', 1.10, NULL, TRUE),   -- 中低等級はやや割高（1.10倍）
('GRADE', 'GRADE_11_15', '11〜15等級', 0.95, NULL, TRUE), -- 中高等級はやや割引（0.95倍）
('GRADE', 'GRADE_16_20', '16〜20等級', 0.80, NULL, TRUE), -- 高等級は割引（0.80倍）
-- 事故有係数期間カテゴリ
('ACCIDENT_TERM', 'HAS_ACCIDENT', '事故有係数期間1年以上', 1.20, NULL, TRUE), -- 事故ありは割高（1.20倍）
-- 車両タイプカテゴリ
('VEHICLE_TYPE', 'KEI', 'KEI', 0.90, NULL, TRUE),         -- 軽自動車は割引（0.90倍）
('VEHICLE_TYPE', 'COMPACT', 'COMPACT', 0.95, NULL, TRUE), -- コンパクトカーはやや割引（0.95倍）
('VEHICLE_TYPE', 'SEDAN', 'SEDAN', 1.00, NULL, TRUE),     -- セダンは基準値（1.00倍）
('VEHICLE_TYPE', 'MINIVAN', 'MINIVAN', 1.10, NULL, TRUE), -- ミニバンはやや割高（1.10倍）
('VEHICLE_TYPE', 'SUV', 'SUV', 1.15, NULL, TRUE);         -- SUVは割高（1.15倍）
