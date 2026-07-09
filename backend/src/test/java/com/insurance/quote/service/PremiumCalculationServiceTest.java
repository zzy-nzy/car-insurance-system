package com.insurance.quote.service;

import com.insurance.quote.dto.QuoteBreakdownItem;
import com.insurance.quote.entity.RateMaster;
import com.insurance.quote.repository.RateMasterRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;

/**
 * PremiumCalculationService のユニットテストクラス。
 * 保険料計算ロジック、範囲解決、端数処理、各種料率シナリオを包括的に検証する。
 */
@ExtendWith(MockitoExtension.class)
class PremiumCalculationServiceTest {

    @Mock
    private RateMasterRepository rateMasterRepository;

    private PremiumCalculationService service;

    /**
     * テスト前に PremiumCalculationService のインスタンスを手動で生成する。
     */
    @BeforeEach
    void setUp() {
        service = new PremiumCalculationService(rateMasterRepository);
    }

    /**
     * 指定されたカテゴリとアイテムコードの料率マスターをモックするヘルパーメソッド。
     *
     * @param category 料率カテゴリ（AGE, LICENSE, USAGE など）
     * @param itemCode アイテムコード
     * @param rate     料率値
     */
    private void mockRate(String category, String itemCode, double rate) {
        RateMaster rm = new RateMaster();
        rm.setCategory(category);
        rm.setItemCode(itemCode);
        rm.setItemName(itemCode);
        rm.setRate(BigDecimal.valueOf(rate));
        rm.setActive(true);
        lenient().when(rateMasterRepository.findByCategoryAndItemCodeAndActiveTrue(category, itemCode))
                .thenReturn(Optional.of(rm));
    }

    /**
     * 未設定のモック呼び出しに対して空の Optional を返すよう設定する。
     */
    @BeforeEach
    void setupMocks() {
        lenient().when(rateMasterRepository.findByCategoryAndItemCodeAndActiveTrue(anyString(), ArgumentMatchers.isNull()))
                .thenReturn(Optional.empty());
    }

    /**
     * 年齢に応じた料率範囲コードが正しく解決されることをテストする。
     * 境界値テストを含む（18,25,26,34,35,59,60,80）。
     */
    @Test
    void testResolveAgeRange() {
        // 18〜25歳の範囲
        assertEquals("AGE_18_25", service.resolveAgeRange(18));
        assertEquals("AGE_18_25", service.resolveAgeRange(25));
        // 26〜34歳の範囲
        assertEquals("AGE_26_34", service.resolveAgeRange(26));
        assertEquals("AGE_26_34", service.resolveAgeRange(34));
        // 35〜59歳の範囲
        assertEquals("AGE_35_59", service.resolveAgeRange(35));
        assertEquals("AGE_35_59", service.resolveAgeRange(59));
        // 60歳以上の範囲
        assertEquals("AGE_60_PLUS", service.resolveAgeRange(60));
        assertEquals("AGE_60_PLUS", service.resolveAgeRange(80));
    }

    /**
     * 年間走行距離に応じた料率範囲コードが正しく解決されることをテストする。
     * 境界値テストを含む。
     */
    @Test
    void testResolveMileageRange() {
        // 0〜5,000km
        assertEquals("MILEAGE_0_5000", service.resolveMileageRange(0));
        assertEquals("MILEAGE_0_5000", service.resolveMileageRange(5000));
        // 5,001〜10,000km
        assertEquals("MILEAGE_5001_10000", service.resolveMileageRange(5001));
        assertEquals("MILEAGE_5001_10000", service.resolveMileageRange(10000));
        // 10,001km以上
        assertEquals("MILEAGE_10001_PLUS", service.resolveMileageRange(10001));
        assertEquals("MILEAGE_10001_PLUS", service.resolveMileageRange(99999));
    }

    /**
     * 等級に応じた料率範囲コードが正しく解決されることをテストする。
     * 境界値テストを含む。
     */
    @Test
    void testResolveGradeRange() {
        // 1〜5等級
        assertEquals("GRADE_1_5", service.resolveGradeRange(1));
        assertEquals("GRADE_1_5", service.resolveGradeRange(5));
        // 6〜10等級
        assertEquals("GRADE_6_10", service.resolveGradeRange(6));
        assertEquals("GRADE_6_10", service.resolveGradeRange(10));
        // 11〜15等級
        assertEquals("GRADE_11_15", service.resolveGradeRange(11));
        assertEquals("GRADE_11_15", service.resolveGradeRange(15));
        // 16〜20等級
        assertEquals("GRADE_16_20", service.resolveGradeRange(16));
        assertEquals("GRADE_16_20", service.resolveGradeRange(20));
    }

    /**
     * 保険料の10円単位端数処理（四捨五入）が正しく動作することをテストする。
     */
    @Test
    void testRoundToTen() {
        assertEquals(0, service.roundToTen(0)); // 0はそのまま0
        assertEquals(10, service.roundToTen(9)); // 9 → 10に切り上げ
        assertEquals(10, service.roundToTen(10)); // 10はそのまま
        assertEquals(20, service.roundToTen(15)); // 15 → 20に切り上げ
        assertEquals(317830, service.roundToTen(317830)); // 10の倍数はそのまま
    }

    /**
     * 標準的な条件（35〜59歳、ブルー免許、自家用、自損のみ）で保険料が正しく計算されることをテストする。
     * 期待値: 90,500円
     */
    @Test
    void testStandardCaseExpected45000() {
        // given: 標準的な料率をモック
        mockRate("AGE", "AGE_35_59", 1.00);
        mockRate("LICENSE", "BLUE", 1.00);
        mockRate("USAGE", "PRIVATE", 1.00);
        mockRate("MILEAGE", "MILEAGE_5001_10000", 1.00);
        mockRate("DRIVER_RANGE", "SELF", 0.90);
        mockRate("VEHICLE_TYPE", "SEDAN", 1.00);

        // when: 保険料計算を実行
        List<QuoteBreakdownItem> breakdowns = new ArrayList<>();
        int result = service.calculateAnnualPremium(
                "AGE_35_59", "BLUE", "PRIVATE", "MILEAGE_5001_10000", "SELF", null,
                false, "SEDAN", true, "UNLIMITED", "UNLIMITED", true, true, breakdowns);

        // then: 期待される保険料額と一致すること
        assertEquals(90500, result);
    }

    /**
     * 高リスク条件（若年・グリーン免許・業務用・長距離・誰でも運転・低等級・事故あり・SUV）で
     * 保険料が正しく計算されることをテストする。
     * 期待値: 317,830円
     */
    @Test
    void testHighRiskCaseExpected317830() {
        // given: 各カテゴリの最高リスク料率をモック
        mockRate("AGE", "AGE_18_25", 1.60);
        mockRate("LICENSE", "GREEN", 1.10);
        mockRate("USAGE", "BUSINESS", 1.25);
        mockRate("MILEAGE", "MILEAGE_10001_PLUS", 1.15);
        mockRate("DRIVER_RANGE", "ANYONE", 1.20);
        mockRate("GRADE", "GRADE_1_5", 1.30);
        mockRate("ACCIDENT_TERM", "HAS_ACCIDENT", 1.20);
        mockRate("VEHICLE_TYPE", "SUV", 1.15);

        // when: 保険料計算を実行
        List<QuoteBreakdownItem> breakdowns = new ArrayList<>();
        int result = service.calculateAnnualPremium(
                "AGE_18_25", "GREEN", "BUSINESS", "MILEAGE_10001_PLUS", "ANYONE",
                "GRADE_1_5", true, "SUV",
                true, "UNLIMITED", "UNLIMITED", true, true, breakdowns);

        // then: 期待される保険料額と一致すること
        assertEquals(317830, result);
    }

    /**
     * 中間的な条件（26〜34歳・ゴールド免許・通勤・短距離・夫婦限定・コンパクトカー）で
     * 保険料が正しく計算されることをテストする。
     * 期待値: 92,550円
     */
    @Test
    void testMediumCase() {
        // given: 中間的な料率をモック
        mockRate("AGE", "AGE_26_34", 1.25);
        mockRate("LICENSE", "GOLD", 0.90);
        mockRate("USAGE", "COMMUTE", 1.10);
        mockRate("MILEAGE", "MILEAGE_0_5000", 0.95);
        mockRate("DRIVER_RANGE", "COUPLE", 0.95);
        mockRate("VEHICLE_TYPE", "COMPACT", 0.95);

        // when: 保険料計算を実行（対人賠償5,000万円、弁護士特約なし）
        List<QuoteBreakdownItem> breakdowns = new ArrayList<>();
        int result = service.calculateAnnualPremium(
                "AGE_26_34", "GOLD", "COMMUTE", "MILEAGE_0_5000", "COUPLE", null,
                false, "COMPACT", true, "UNLIMITED", "FIFTY_MILLION", false, true, breakdowns);

        // then: 期待される保険料額と一致すること
        assertEquals(92550, result);
    }

    /**
     * 料率マスターが存在しない場合、基本保険料（50,000円）が適用されることをテストする。
     */
    @Test
    void testMissingRateDefaultsToBase() {
        // when: モックなし（すべての料率が未定義）の状態で計算
        List<QuoteBreakdownItem> breakdowns = new ArrayList<>();
        int result = service.calculateAnnualPremium(
                "AGE_35_59", "BLUE", "PRIVATE", "MILEAGE_5001_10000", "SELF", null,
                false, "SEDAN", false, null, null, false, false, breakdowns);

        // then: 基本保険料 50,000円 になること
        assertEquals(50000, result);
    }

    /**
     * 内訳リストに5件以上の項目が含まれ、最初の項目が基本保険料（BASE）であることをテストする。
     */
    @Test
    void testBreakdownItemsCount() {
        // given: 複数カテゴリの料率をモック
        mockRate("AGE", "AGE_35_59", 1.00);
        mockRate("LICENSE", "GREEN", 1.10);
        mockRate("USAGE", "PRIVATE", 1.00);
        mockRate("MILEAGE", "MILEAGE_5001_10000", 1.00);
        mockRate("DRIVER_RANGE", "FAMILY", 1.05);
        mockRate("VEHICLE_TYPE", "MINIVAN", 1.10);

        // when: 保険料計算を実行
        List<QuoteBreakdownItem> breakdowns = new ArrayList<>();
        service.calculateAnnualPremium(
                "AGE_35_59", "GREEN", "PRIVATE", "MILEAGE_5001_10000", "FAMILY", null,
                false, "MINIVAN", true, "UNLIMITED", "UNLIMITED", true, true, breakdowns);

        // then: 内訳が6件以上あり、最初の項目が BASE であること
        assertTrue(breakdowns.size() > 5);
        assertEquals("BASE", breakdowns.get(0).getItemCode());
    }

    /**
     * 事故有係数が適用される場合、保険料が増加することをテストする。
     * 事故ありの場合の保険料が事故なしの場合より高いことを検証する。
     */
    @Test
    void testAccidentTermApplied() {
        // given: 事故有係数を除く共通の料率をモック
        mockRate("AGE", "AGE_35_59", 1.00);
        mockRate("LICENSE", "BLUE", 1.00);
        mockRate("USAGE", "PRIVATE", 1.00);
        mockRate("MILEAGE", "MILEAGE_5001_10000", 1.00);
        mockRate("DRIVER_RANGE", "SELF", 0.90);
        mockRate("GRADE", "GRADE_16_20", 0.80);
        mockRate("ACCIDENT_TERM", "HAS_ACCIDENT", 1.20);
        mockRate("VEHICLE_TYPE", "KEI", 0.90);

        // when: 事故ありで保険料を計算
        List<QuoteBreakdownItem> withAccident = new ArrayList<>();
        int withAccidentResult = service.calculateAnnualPremium(
                "AGE_35_59", "BLUE", "PRIVATE", "MILEAGE_5001_10000", "SELF",
                "GRADE_16_20", true, "KEI", false, null, null, false, false, withAccident);

        // when: 事故なしで保険料を計算
        List<QuoteBreakdownItem> withoutAccident = new ArrayList<>();
        int withoutAccidentResult = service.calculateAnnualPremium(
                "AGE_35_59", "BLUE", "PRIVATE", "MILEAGE_5001_10000", "SELF",
                "GRADE_16_20", false, "KEI", false, null, null, false, false, withoutAccident);

        // then: 事故ありの保険料が事故なしより高いこと
        assertTrue(withAccidentResult > withoutAccidentResult);
    }
}
