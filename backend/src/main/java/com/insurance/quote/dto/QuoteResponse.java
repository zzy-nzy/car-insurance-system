package com.insurance.quote.dto;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 見積作成レスポンスを表す DTO クラス。
 * <p>
 * 見積番号、年額・月額保険料、内訳明細、作成日時をクライアントに返す。
 */
public class QuoteResponse {

    private String quoteNo; // 見積番号
    private Integer annualPremium; // 年額保険料
    private Integer monthlyPremium; // 月額保険料
    private List<QuoteBreakdownItem> breakdowns; // 保険料内訳明細リスト
    private LocalDateTime createdAt; // 見積作成日時

    /** @return 見積番号 */
    public String getQuoteNo() { return quoteNo; }
    /** @param quoteNo 見積番号 */
    public void setQuoteNo(String quoteNo) { this.quoteNo = quoteNo; }
    /** @return 年額保険料 */
    public Integer getAnnualPremium() { return annualPremium; }
    /** @param annualPremium 年額保険料 */
    public void setAnnualPremium(Integer annualPremium) { this.annualPremium = annualPremium; }
    /** @return 月額保険料 */
    public Integer getMonthlyPremium() { return monthlyPremium; }
    /** @param monthlyPremium 月額保険料 */
    public void setMonthlyPremium(Integer monthlyPremium) { this.monthlyPremium = monthlyPremium; }
    /** @return 保険料内訳明細リスト */
    public List<QuoteBreakdownItem> getBreakdowns() { return breakdowns; }
    /** @param breakdowns 保険料内訳明細リスト */
    public void setBreakdowns(List<QuoteBreakdownItem> breakdowns) { this.breakdowns = breakdowns; }
    /** @return 見積作成日時 */
    public LocalDateTime getCreatedAt() { return createdAt; }
    /** @param createdAt 見積作成日時 */
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
