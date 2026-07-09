package com.insurance.quote.service;

import com.insurance.quote.dto.QuoteBreakdownItem;
import com.insurance.quote.entity.RateMaster;
import com.insurance.quote.repository.RateMasterRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

/**
 * 自動車保険の保険料を計算するサービスクラス。
 * <p>
 * 保険料計算の流れ:
 * <ol>
 *   <li>基本保険料（50,000円）を起点とする</li>
 *   <li>以下のリスク要素ごとに料率マスタから掛け率を取得し、保険料に乗算する:
 *     <ul>
 *       <li>運転者年齢区分 (AGE)</li>
 *       <li>免許証の色 (LICENSE)</li>
 *       <li>使用目的 (USAGE)</li>
 *       <li>年間走行距離区分 (MILEAGE)</li>
 *       <li>運転者範囲 (DRIVER_RANGE)</li>
 *       <li>等級区分 (GRADE)</li>
 *       <li>事故有係数 (ACCIDENT_TERM) — 条件付き</li>
 *       <li>車種区分 (VEHICLE_TYPE)</li>
 *     </ul>
 *   </li>
 *   <li>以下のオプション特約がある場合、定額を加算する:
 *     <ul>
 *       <li>車両保険: +30,000円</li>
 *       <li>対物補償（無制限）: +5,000円</li>
 *       <li>人身傷害（5,000万円）: +3,000円 / 人身傷害（無制限）: +7,000円</li>
 *       <li>弁護士特約: +2,000円</li>
 *       <li>ロードサービス: +1,500円</li>
 *     </ul>
 *   </li>
 *   <li>最終金額を10円単位に丸める</li>
 * </ol>
 */
@Service
public class PremiumCalculationService {

    /** 基本保険料（円） */
    private static final int BASE_PREMIUM = 50000;

    /** 料率マスタリポジトリ */
    private final RateMasterRepository rateMasterRepository;

    /**
     * コンストラクタ。料率マスタリポジトリを注入します。
     *
     * @param rateMasterRepository 料率マスタリポジトリ
     */
    public PremiumCalculationService(RateMasterRepository rateMasterRepository) {
        this.rateMasterRepository = rateMasterRepository;
    }

    /**
     * 年間保険料を計算します。
     * <p>
     * 計算ロジック:
     * <ol>
     *   <li>基本保険料 50,000円 から開始</li>
     *   <li>各リスク要素の料率を乗算（掛け率形式、例: 1.2 → 20%増）</li>
     *   <li>オプション特約の定額を加算</li>
     *   <li>最終金額を10円単位で丸め</li>
     * </ol>
     * 各計算ステップで内訳情報（breakdowns）が追加され、フロントエンドでの表示に利用されます。
     *
     * @param driverAgeRange       運転者年齢区分（AGE_18_25, AGE_26_34, AGE_35_59, AGE_60_PLUS）
     * @param licenseColor         免許証の色（GOLD / BLUE / GREEN）
     * @param usageType            使用目的（PRIVATE / COMMUTE / BUSINESS）
     * @param mileageRange         年間走行距離区分（MILEAGE_0_5000, MILEAGE_5001_10000, MILEAGE_10001_PLUS）
     * @param driverRange          運転者範囲（SELF / COUPLE / FAMILY / ANYONE）
     * @param gradeRange           等級区分（GRADE_1_5, GRADE_6_10, GRADE_11_15, GRADE_16_20）
     * @param hasAccidentTerm      事故有係数の適用有無
     * @param vehicleType          車種（COMPACT / SEDAN / MINIVAN / SUV / KEI）
     * @param vehicleInsurance     車両保険の付带有無
     * @param propertyDamageLimit  対物賠償限度額（UNLIMITED / THIRTY_MILLION）。UNLIMITED のみ加算対象
     * @param personalInjuryAmount 人身傷害補償額（THIRTY_MILLION / FIFTY_MILLION / UNLIMITED）。FIFTY_MILLION・UNLIMITED が加算対象
     * @param lawyerOption         弁護士特約の付带有無
     * @param roadService          ロードサービスの付带有無
     * @param breakdowns           保険料内訳リスト（計算結果が追加される）
     * @return 10円単位に丸められた年間保険料
     */
    public int calculateAnnualPremium(String driverAgeRange, String licenseColor, String usageType,
                                       String mileageRange, String driverRange, String gradeRange,
                                       boolean hasAccidentTerm, String vehicleType,
                                       boolean vehicleInsurance, String propertyDamageLimit,
                                       String personalInjuryAmount, boolean lawyerOption,
                                       boolean roadService, List<QuoteBreakdownItem> breakdowns) {

        // ----------------------------------------
        // ステップ1: 基本保険料から計算開始
        // ----------------------------------------
        BigDecimal premium = BigDecimal.valueOf(BASE_PREMIUM);
        addBreakdown(breakdowns, "BASE", "基本保険料", null, BASE_PREMIUM, 1);

        // ----------------------------------------
        // ステップ2: 各リスク要素の料率を乗算
        // 料率マスタから各カテゴリの掛け率を取得し、現在の保険料に乗算する
        // 掛け率は 1.0 を基準とし、1.0より大きければ割増、小さければ割引となる
        // ----------------------------------------
        premium = applyRate(premium, "AGE", driverAgeRange, breakdowns, 2);
        premium = applyRate(premium, "LICENSE", licenseColor, breakdowns, 3);
        premium = applyRate(premium, "USAGE", usageType, breakdowns, 4);
        premium = applyRate(premium, "MILEAGE", mileageRange, breakdowns, 5);
        premium = applyRate(premium, "DRIVER_RANGE", driverRange, breakdowns, 6);
        premium = applyRate(premium, "GRADE", gradeRange, breakdowns, 7);

        // 事故有係数は条件付き（現在の保険加入があり、事故有係数期間が1年以上の場合のみ適用）
        if (hasAccidentTerm) {
            premium = applyRate(premium, "ACCIDENT_TERM", "HAS_ACCIDENT", breakdowns, 8);
        }

        premium = applyRate(premium, "VEHICLE_TYPE", vehicleType, breakdowns, 9);

        // 乗算結果の端数を四捨五入して整数化
        int total = premium.setScale(0, RoundingMode.HALF_UP).intValue();

        // ----------------------------------------
        // ステップ3: オプション特約の定額加算
        // 各特約は保険料に対して定額で加算される
        // ----------------------------------------

        // 車両保険: 特約付加で +30,000円
        if (vehicleInsurance) {
            total += 30000;
            addBreakdown(breakdowns, "VEHICLE_INSURANCE", "車両保険", null, 30000, 10);
        }
        // 対物補償: 無制限選択時 +5,000円
        if ("UNLIMITED".equals(propertyDamageLimit)) {
            total += 5000;
            addBreakdown(breakdowns, "PROPERTY_DAMAGE", "対物補償（無制限）", null, 5000, 11);
        }
        // 人身傷害: 5,000万円で +3,000円 / 無制限で +7,000円
        if ("FIFTY_MILLION".equals(personalInjuryAmount)) {
            total += 3000;
            addBreakdown(breakdowns, "PERSONAL_INJURY", "人身傷害（5,000万円）", null, 3000, 12);
        } else if ("UNLIMITED".equals(personalInjuryAmount)) {
            total += 7000;
            addBreakdown(breakdowns, "PERSONAL_INJURY", "人身傷害（無制限）", null, 7000, 13);
        }
        // 弁護士特約: +2,000円
        if (lawyerOption) {
            total += 2000;
            addBreakdown(breakdowns, "LAWYER", "弁護士特約", null, 2000, 14);
        }
        // ロードサービス: +1,500円
        if (roadService) {
            total += 1500;
            addBreakdown(breakdowns, "ROAD_SERVICE", "ロードサービス", null, 1500, 15);
        }

        // ----------------------------------------
        // ステップ4: 10円単位に丸めて返却
        // ----------------------------------------
        return roundToTen(total);
    }

    /**
     * 料率マスタから該当する掛け率を取得し、現在の保険料に乗算します。
     * マスタに該当する料率が存在しない場合、そのままの保険料を返します（乗算なし）。
     *
     * @param premium    乗算前の保険料
     * @param category   料率カテゴリ（AGE, LICENSE, USAGE など）
     * @param itemCode   料率アイテムコード（区分値）
     * @param breakdowns 内訳リスト（計算結果が追加される）
     * @param order      表示順
     * @return 乗算後の保険料
     */
    private BigDecimal applyRate(BigDecimal premium, String category, String itemCode,
                                  List<QuoteBreakdownItem> breakdowns, int order) {
        RateMaster rate = rateMasterRepository.findByCategoryAndItemCodeAndActiveTrue(category, itemCode).orElse(null);
        if (rate == null || rate.getRate() == null) {
            return premium;
        }
        BigDecimal result = premium.multiply(rate.getRate());
        addBreakdown(breakdowns, rate.getItemCode(), rate.getItemName(), rate.getRate(), null, order);
        return result;
    }

    /**
     * 保険料内訳項目をリストに追加します。
     * 掛け率による乗算項目の場合はrateに値が設定され、定額加算項目の場合はamountに値が設定されます。
     *
     * @param breakdowns 内訳リスト
     * @param code       項目コード
     * @param name       項目名
     * @param rate       掛け率（乗算項目の場合、定額項目の場合はnull）
     * @param amount     加算額（定額項目の場合、乗算項目の場合はnull）
     * @param order      表示順
     */
    private void addBreakdown(List<QuoteBreakdownItem> breakdowns, String code, String name,
                               BigDecimal rate, Integer amount, int order) {
        QuoteBreakdownItem item = new QuoteBreakdownItem();
        item.setItemCode(code);
        item.setItemName(name);
        item.setRate(rate);
        item.setAmount(amount);
        item.setDisplayOrder(order);
        breakdowns.add(item);
    }

    /**
     * 金額を10円単位に丸めます（四捨五入）。
     * 例: 52,345 → 52,350 / 52,344 → 52,340
     *
     * @param value 丸め前の金額
     * @return 10円単位に丸められた金額
     */
    public int roundToTen(int value) {
        return (int) (Math.round(value / 10.0) * 10);
    }

    /**
     * 運転者の年齢から該当する年齢区分コードを返します。
     * <ul>
     *   <li>18〜25歳: AGE_18_25</li>
     *   <li>26〜34歳: AGE_26_34</li>
     *   <li>35〜59歳: AGE_35_59</li>
     *   <li>60歳以上: AGE_60_PLUS</li>
     * </ul>
     *
     * @param age 運転者の年齢
     * @return 年齢区分コード
     */
    public String resolveAgeRange(int age) {
        if (age <= 25) return "AGE_18_25";
        if (age <= 34) return "AGE_26_34";
        if (age <= 59) return "AGE_35_59";
        return "AGE_60_PLUS";
    }

    /**
     * 年間走行距離から該当する走行距離区分コードを返します。
     * <ul>
     *   <li>0〜5,000km: MILEAGE_0_5000</li>
     *   <li>5,001〜10,000km: MILEAGE_5001_10000</li>
     *   <li>10,001km以上: MILEAGE_10001_PLUS</li>
     * </ul>
     *
     * @param mileage 年間走行距離（km）
     * @return 走行距離区分コード
     */
    public String resolveMileageRange(int mileage) {
        if (mileage <= 5000) return "MILEAGE_0_5000";
        if (mileage <= 10000) return "MILEAGE_5001_10000";
        return "MILEAGE_10001_PLUS";
    }

    /**
     * 等級から該当する等級区分コードを返します。
     * <ul>
     *   <li>1〜5等級: GRADE_1_5</li>
     *   <li>6〜10等級: GRADE_6_10</li>
     *   <li>11〜15等級: GRADE_11_15</li>
     *   <li>16〜20等級: GRADE_16_20</li>
     * </ul>
     *
     * @param grade 等級（1〜20）
     * @return 等級区分コード
     */
    public String resolveGradeRange(int grade) {
        if (grade <= 5) return "GRADE_1_5";
        if (grade <= 10) return "GRADE_6_10";
        if (grade <= 15) return "GRADE_11_15";
        return "GRADE_16_20";
    }
}
