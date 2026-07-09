package com.insurance.quote.service;

import com.insurance.quote.dto.*;
import com.insurance.quote.entity.Quote;
import com.insurance.quote.entity.QuoteBreakdown;
import com.insurance.quote.exception.NotFoundException;
import com.insurance.quote.repository.QuoteBreakdownRepository;
import com.insurance.quote.repository.QuoteRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 保険見積を管理するサービスクラス。
 * 見積の作成、照会、一覧検索、CSVエクスポートを担当します。
 * 見積作成時には保険料計算サービスと連携して保険料を算出します。
 */
@Service
public class QuoteService {

    /** 見積データのリポジトリ */
    private final QuoteRepository quoteRepository;
    /** 見積内訳データのリポジトリ */
    private final QuoteBreakdownRepository breakdownRepository;
    /** 保険料計算サービス */
    private final PremiumCalculationService calculationService;

    /**
     * コンストラクタ。各種リポジトリとサービスを注入します。
     *
     * @param quoteRepository      見積リポジトリ
     * @param breakdownRepository  見積内訳リポジトリ
     * @param calculationService   保険料計算サービス
     */
    public QuoteService(QuoteRepository quoteRepository,
                        QuoteBreakdownRepository breakdownRepository,
                        PremiumCalculationService calculationService) {
        this.quoteRepository = quoteRepository;
        this.breakdownRepository = breakdownRepository;
        this.calculationService = calculationService;
    }

    /**
     * 新しい見積を作成し、保険料を計算します。
     * 条件付き必須項目のバリデーションを行った後、
     * 各リスク要素に基づいて保険料を計算し、見積番号を発行して保存します。
     *
     * @param request 見積作成リクエスト
     * @return 作成された見積のレスポンス（保険料、内訳を含む）
     */
    @Transactional
    public QuoteResponse createQuote(QuoteRequest request) {
        // 条件付き必須項目のバリデーション
        validateConditionalFields(request);

        List<QuoteBreakdownItem> breakdownItems = new ArrayList<>();

        // 各リスク要素を区分値に変換（年齢区分、走行距離区分、等級区分）
        String ageRange = calculationService.resolveAgeRange(request.getDriverAge());
        String mileageRange = calculationService.resolveMileageRange(request.getAnnualMileage());
        String gradeRange = request.getGrade() != null
                ? calculationService.resolveGradeRange(request.getGrade())
                : null;

        // 年間保険料の計算（各リスク要素に基づく掛け率適用）
        int annualPremium = calculationService.calculateAnnualPremium(
                ageRange, request.getLicenseColor(), request.getUsageType(),
                mileageRange, request.getDriverRange(), gradeRange,
                hasAccidentTerm(request), request.getVehicleType(),
                request.getVehicleInsurance(), request.getPropertyDamageLimit(),
                request.getPersonalInjuryAmount(), request.getLawyerOption(),
                request.getRoadService(), breakdownItems
        );

        // 月額保険料 = 年額 / 12 を10円単位で丸め
        int monthlyPremium = calculationService.roundToTen(annualPremium / 12);

        // 見積エンティティの構築と保存
        Quote quote = new Quote();
        quote.setQuoteNo(generateQuoteNo());
        quote.setDriverAge(request.getDriverAge());
        quote.setLicenseColor(request.getLicenseColor());
        quote.setUsageType(request.getUsageType());
        quote.setAnnualMileage(request.getAnnualMileage());
        quote.setDriverRange(request.getDriverRange());
        quote.setHasCurrentInsurance(request.getHasCurrentInsurance());
        quote.setGrade(request.getGrade());
        quote.setAccidentTerm(request.getAccidentTerm());
        quote.setMaker(request.getMaker());
        quote.setCarName(request.getCarName());
        quote.setFirstRegistrationYm(request.getFirstRegistrationYearMonth());
        quote.setVehicleType(request.getVehicleType());
        quote.setVehicleInsurance(request.getVehicleInsurance());
        quote.setAnnualPremium(annualPremium);
        quote.setMonthlyPremium(monthlyPremium);

        quote = quoteRepository.save(quote);

        // 保険料内訳の保存
        for (QuoteBreakdownItem item : breakdownItems) {
            QuoteBreakdown bd = new QuoteBreakdown();
            bd.setQuoteId(quote.getId());
            bd.setItemCode(item.getItemCode());
            bd.setItemName(item.getItemName());
            bd.setRate(item.getRate());
            bd.setAmount(item.getAmount());
            bd.setDisplayOrder(item.getDisplayOrder());
            breakdownRepository.save(bd);
        }

        // レスポンスの構築
        QuoteResponse response = new QuoteResponse();
        response.setQuoteNo(quote.getQuoteNo());
        response.setAnnualPremium(annualPremium);
        response.setMonthlyPremium(monthlyPremium);
        response.setBreakdowns(breakdownItems);
        response.setCreatedAt(quote.getCreatedAt());
        return response;
    }

    /**
     * 見積番号で見積の詳細を取得します。
     * 見積基本情報と保険料内訳を返します。
     *
     * @param quoteNo 見積番号
     * @return 見積詳細レスポンス
     * @throws NotFoundException 指定された見積番号が存在しない場合
     */
    public QuoteResponse getQuote(String quoteNo) {
        Quote quote = quoteRepository.findByQuoteNo(quoteNo)
                .orElseThrow(() -> new NotFoundException("見積番号が存在しません: " + quoteNo));

        // 表示順でソートされた内訳を取得し、DTOに変換
        List<QuoteBreakdown> breakdowns = breakdownRepository.findByQuoteIdOrderByDisplayOrderAsc(quote.getId());
        List<QuoteBreakdownItem> items = breakdowns.stream().map(bd -> {
            QuoteBreakdownItem item = new QuoteBreakdownItem();
            item.setItemCode(bd.getItemCode());
            item.setItemName(bd.getItemName());
            item.setRate(bd.getRate());
            item.setAmount(bd.getAmount());
            item.setDisplayOrder(bd.getDisplayOrder());
            return item;
        }).collect(Collectors.toList());

        QuoteResponse response = new QuoteResponse();
        response.setQuoteNo(quote.getQuoteNo());
        response.setAnnualPremium(quote.getAnnualPremium());
        response.setMonthlyPremium(quote.getMonthlyPremium());
        response.setBreakdowns(items);
        response.setCreatedAt(quote.getCreatedAt());
        return response;
    }

    /**
     * 条件に合致する見積の概要一覧を取得します。
     * 検索条件がすべて空の場合は全件を返し、条件が指定されている場合はフィルタリングします。
     *
     * @param search 検索条件（見積番号、メーカー、車名、車種、日付範囲）
     * @return 見積概要のリスト
     */
    public List<QuoteSummaryResponse> listQuotes(QuoteSearchRequest search) {
        List<Quote> quotes;
        // 検索条件がすべてnullの場合は全件取得、それ以外は条件検索
        if (search == null || Stream.of(search.getQuoteNo(), search.getMaker(), search.getCarName(),
                search.getVehicleType(), search.getDateFrom(), search.getDateTo()).allMatch(java.util.Objects::isNull)) {
            quotes = quoteRepository.findAllByOrderByCreatedAtDesc();
        } else {
            // 日付範囲をLocalDateTimeに変換（開始日は00:00:00、終了日は23:59:59.999...）
            LocalDateTime dateFrom = search.getDateFrom() != null ? search.getDateFrom().atStartOfDay() : null;
            LocalDateTime dateTo = search.getDateTo() != null ? search.getDateTo().atTime(LocalTime.MAX) : null;
            quotes = quoteRepository.searchQuotes(
                    search.getQuoteNo(), search.getMaker(), search.getCarName(),
                    search.getVehicleType(), dateFrom, dateTo);
        }
        return quotes.stream().map(this::toSummary).collect(Collectors.toList());
    }

    /**
     * 見積データをCSV形式の文字列として生成します。
     * BOM（\uFEFF）を先頭に付与し、Excelで開いた際の文字化けを防止します。
     * カンマ、ダブルクォーテーション、改行を含むフィールドは適切にエスケープ処理されます。
     *
     * @param search 検索条件
     * @return CSV形式の文字列
     */
    public String generateCsv(QuoteSearchRequest search) {
        List<Quote> quotes;
        if (search == null || Stream.of(search.getQuoteNo(), search.getMaker(), search.getCarName(),
                search.getVehicleType(), search.getDateFrom(), search.getDateTo()).allMatch(java.util.Objects::isNull)) {
            quotes = quoteRepository.findAllByOrderByCreatedAtDesc();
        } else {
            LocalDateTime dateFrom = search.getDateFrom() != null ? search.getDateFrom().atStartOfDay() : null;
            LocalDateTime dateTo = search.getDateTo() != null ? search.getDateTo().atTime(LocalTime.MAX) : null;
            quotes = quoteRepository.searchQuotes(
                    search.getQuoteNo(), search.getMaker(), search.getCarName(),
                    search.getVehicleType(), dateFrom, dateTo);
        }

        StringBuilder sb = new StringBuilder();
        // BOM付与：ExcelでのUTF-8文字化け防止
        sb.append('\uFEFF'); // BOM for Excel UTF-8
        sb.append("見積番号,運転者年齢,免許色,使用目的,年間走行距離,運転者範囲,メーカー,車名,車種,年間保険料,月額保険料,作成日時\n");
        for (Quote q : quotes) {
            sb.append(escapeCsv(q.getQuoteNo())).append(",");
            sb.append(q.getDriverAge()).append(",");
            sb.append(escapeCsv(q.getLicenseColor())).append(",");
            sb.append(escapeCsv(q.getUsageType())).append(",");
            sb.append(q.getAnnualMileage()).append(",");
            sb.append(escapeCsv(q.getDriverRange())).append(",");
            sb.append(escapeCsv(q.getMaker())).append(",");
            sb.append(escapeCsv(q.getCarName())).append(",");
            sb.append(escapeCsv(q.getVehicleType())).append(",");
            sb.append(q.getAnnualPremium()).append(",");
            sb.append(q.getMonthlyPremium()).append(",");
            sb.append(q.getCreatedAt().format(DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss"))).append("\n");
        }
        return sb.toString();
    }

    /**
     * CSVフィールド値のエスケープ処理を行います。
     * カンマ、ダブルクォーテーション、改行が含まれる場合は、
     * フィールド全体をダブルクォーテーションで囲み、内部のダブルクォーテーションは2重化します。
     *
     * @param value エスケープ前の文字列
     * @return エスケープ済みの文字列
     */
    private String escapeCsv(String value) {
        if (value == null) return "";
        if (value.contains(",") || value.contains("\"") || value.contains("\n")) {
            return "\"" + value.replace("\"", "\"\"") + "\"";
        }
        return value;
    }

    /**
     * QuoteエンティティをQuoteSummaryResponse DTOに変換します。
     *
     * @param q Quoteエンティティ
     * @return 見積概要DTO
     */
    private QuoteSummaryResponse toSummary(Quote q) {
        QuoteSummaryResponse r = new QuoteSummaryResponse();
        r.setQuoteNo(q.getQuoteNo());
        r.setDriverAge(q.getDriverAge());
        r.setLicenseColor(q.getLicenseColor());
        r.setUsageType(q.getUsageType());
        r.setMaker(q.getMaker());
        r.setCarName(q.getCarName());
        r.setVehicleType(q.getVehicleType());
        r.setAnnualPremium(q.getAnnualPremium());
        r.setMonthlyPremium(q.getMonthlyPremium());
        r.setCreatedAt(q.getCreatedAt());
        return r;
    }

    /**
     * 見積番号を生成します。
     * 形式: "EST" + yyyyMMdd + 4桁の連番（例: EST202607090001）
     * 同一日付の接頭辞を持つ既存データの件数をカウントし、次の連番を採番します。
     *
     * @return 生成された見積番号
     */
    private String generateQuoteNo() {
        String datePart = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String prefix = "EST" + datePart;
        long count = quoteRepository.countByQuoteNoStartingWith(prefix);
        return prefix + String.format("%04d", count + 1);
    }

    /**
     * 事故有係数の適用有無を判定します。
     * 現在の保険加入があり、かつ事故有係数期間が1年以上の場合に事故有と判定します。
     *
     * @param request 見積リクエスト
     * @return 事故有係数が適用される場合はtrue
     */
    private boolean hasAccidentTerm(QuoteRequest request) {
        return request.getHasCurrentInsurance()
                && request.getAccidentTerm() != null
                && request.getAccidentTerm() >= 1;
    }

    /**
     * 条件付き必須項目のバリデーションを行います。
     * 現在の保険加入がある場合、等級と事故有係数期間が必須となります。
     *
     * @param request 見積リクエスト
     * @throws IllegalArgumentException 必須項目が不足している場合
     */
    private void validateConditionalFields(QuoteRequest request) {
        if (Boolean.TRUE.equals(request.getHasCurrentInsurance())) {
            if (request.getGrade() == null) {
                throw new IllegalArgumentException("現在加入ありの場合、等級は必須です");
            }
            if (request.getAccidentTerm() == null) {
                throw new IllegalArgumentException("現在加入ありの場合、事故有係数期間は必須です");
            }
        }
    }
}
