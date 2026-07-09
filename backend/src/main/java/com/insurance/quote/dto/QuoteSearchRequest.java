package com.insurance.quote.dto;

import java.time.LocalDate;

/**
 * 見積検索リクエストを表す DTO クラス。
 * <p>
 * 見積番号、車両情報、作成日付範囲などを条件として
 * 見積一覧を絞り込む際に使用する。
 */
public class QuoteSearchRequest {
    private String quoteNo; // 見積番号（部分一致）
    private String maker; // 車両メーカー名
    private String carName; // 車名
    private String vehicleType; // 車種区分
    private LocalDate dateFrom; // 検索期間（開始日）
    private LocalDate dateTo; // 検索期間（終了日）

    /** @return 見積番号 */
    public String getQuoteNo() { return quoteNo; }
    /** @param quoteNo 見積番号 */
    public void setQuoteNo(String quoteNo) { this.quoteNo = quoteNo; }
    /** @return 車両メーカー名 */
    public String getMaker() { return maker; }
    /** @param maker 車両メーカー名 */
    public void setMaker(String maker) { this.maker = maker; }
    /** @return 車名 */
    public String getCarName() { return carName; }
    /** @param carName 車名 */
    public void setCarName(String carName) { this.carName = carName; }
    /** @return 車種区分 */
    public String getVehicleType() { return vehicleType; }
    /** @param vehicleType 車種区分 */
    public void setVehicleType(String vehicleType) { this.vehicleType = vehicleType; }
    /** @return 検索期間（開始日） */
    public LocalDate getDateFrom() { return dateFrom; }
    /** @param dateFrom 検索期間（開始日） */
    public void setDateFrom(LocalDate dateFrom) { this.dateFrom = dateFrom; }
    /** @return 検索期間（終了日） */
    public LocalDate getDateTo() { return dateTo; }
    /** @param dateTo 検索期間（終了日） */
    public void setDateTo(LocalDate dateTo) { this.dateTo = dateTo; }
}
