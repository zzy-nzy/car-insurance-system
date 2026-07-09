/**
 * 型定義モジュール
 * 
 * 自動車保険見積システムで使用されるすべてのTypeScript型定義を集約します。
 * 共通の型（免許色、使用目的、運転者範囲、車両タイプなど）と
 * APIリクエスト/レスポンスのインターフェースを含みます。
 */

/** 免許の色区分 */
export type LicenseColor = 'GOLD' | 'BLUE' | 'GREEN';

/** 車の使用目的区分 */
export type UsageType = 'PRIVATE' | 'COMMUTE' | 'BUSINESS';

/** 運転者範囲区分 */
export type DriverRange = 'SELF' | 'COUPLE' | 'FAMILY' | 'ANYONE';

/** 車両区分 */
export type VehicleType = 'COMPACT' | 'SEDAN' | 'MINIVAN' | 'SUV' | 'KEI';

/** 対物賠償限度額区分 */
export type PropertyDamageLimit = 'UNLIMITED' | 'THIRTY_MILLION';

/** 人身傷害補償額区分 */
export type PersonalInjuryAmount = 'THIRTY_MILLION' | 'FIFTY_MILLION' | 'UNLIMITED';

/** 見積作成リクエスト */
export interface QuoteRequest {
  /** 運転者の年齢 */
  driverAge: number;
  /** 免許の色 */
  licenseColor: LicenseColor;
  /** 使用目的 */
  usageType: UsageType;
  /** 年間走行距離 */
  annualMileage: number;
  /** 運転者範囲 */
  driverRange: DriverRange;
  /** 現在加入中の保険の有無 */
  hasCurrentInsurance: boolean;
  /** 等級（保険加入中の場合） */
  grade?: number;
  /** 事故有無の期間（保険加入中の場合） */
  accidentTerm?: number;
  /** メーカー名 */
  maker: string;
  /** 車名 */
  carName: string;
  /** 初度登録年月（YYYY-MM形式） */
  firstRegistrationYearMonth: string;
  /** 車両区分 */
  vehicleType: VehicleType;
  /** 車両保険の有無 */
  vehicleInsurance: boolean;
  /** 対物賠償限度額 */
  propertyDamageLimit: PropertyDamageLimit;
  /** 人身傷害補償額 */
  personalInjuryAmount: PersonalInjuryAmount;
  /** 弁護士特約の有無 */
  lawyerOption: boolean;
  /** ロードサービスの有無 */
  roadService: boolean;
}

/** 見積内訳項目 */
export interface QuoteBreakdownItem {
  /** 項目コード */
  itemCode: string;
  /** 項目名 */
  itemName: string;
  /** 料率（該当なしの場合はnull） */
  rate: number | null;
  /** 金額（該当なしの場合はnull） */
  amount: number | null;
  /** 表示順序 */
  displayOrder: number;
}

/** 見積作成レスポンス */
export interface QuoteResponse {
  /** 見積番号 */
  quoteNo: string;
  /** 年額保険料 */
  annualPremium: number;
  /** 月額保険料 */
  monthlyPremium: number;
  /** 内訳一覧 */
  breakdowns: QuoteBreakdownItem[];
  /** 作成日時 */
  createdAt: string;
}

/** 見積一覧のサマリー情報 */
export interface QuoteSummary {
  /** 見積番号 */
  quoteNo: string;
  /** 運転者年齢 */
  driverAge: number;
  /** 免許色 */
  licenseColor: string;
  /** 使用目的 */
  usageType: string;
  /** メーカー */
  maker: string;
  /** 車名 */
  carName: string;
  /** 車両区分 */
  vehicleType: string;
  /** 年額保険料 */
  annualPremium: number;
  /** 月額保険料 */
  monthlyPremium: number;
  /** 作成日時 */
  createdAt: string;
}

/** フォーム入力データ。空文字列を許容し、任意の追加キーも受け入れる */
export interface FormData {
  /** 運転者年齢（文字列） */
  driverAge: string;
  /** 免許色 */
  licenseColor: LicenseColor | '';
  /** 使用目的 */
  usageType: UsageType | '';
  /** 年間走行距離（文字列） */
  annualMileage: string;
  /** 運転者範囲 */
  driverRange: DriverRange | '';
  /** 現在加入中の保険の有無 */
  hasCurrentInsurance: boolean;
  /** 等級（文字列） */
  grade: string;
  /** 事故有無の期間（文字列） */
  accidentTerm: string;
  /** メーカー名 */
  maker: string;
  /** 車名 */
  carName: string;
  /** 初度登録年月（YYYY-MM形式文字列） */
  firstRegistrationYearMonth: string;
  /** 車両区分 */
  vehicleType: VehicleType | '';
  /** 車両保険の有無（null=未選択） */
  vehicleInsurance: boolean | null;
  /** 対物賠償限度額 */
  propertyDamageLimit: PropertyDamageLimit | '';
  /** 対人賠償金額 */
  personalInjuryAmount: PersonalInjuryAmount | '';
  /** 弁護士特約の有無（null=未選択） */
  lawyerOption: boolean | null;
  /** ロードサービスの有無（null=未選択） */
  roadService: boolean | null;
  /** 任意の追加キーを受け入れるインデックスシグネチャ */
  [key: string]: any;
}
