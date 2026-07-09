package com.insurance.quote.dto;

import java.time.LocalDateTime;

/**
 * 見積一覧のサマリーレスポンスを表す DTO クラス。
 * <p>
 * 見積一覧画面に表示する、各見積の概要情報を保持する。
 */
public class QuoteSummaryResponse {
    private String quoteNo; // 見積番号
    private Integer driverAge; // ドライバー年齢
    private String licenseColor; // 免許証の色
    private String usageType; // 使用用途
    private String maker; // 車両メーカー名
    private String carName; // 車名
    private String vehicleType; // 車種区分
    private Integer annualPremium; // 年額保険料
    private Integer monthlyPremium; // 月額保険料
    private LocalDateTime createdAt; // 見積作成日時

    /** @return 見積番号 */
    public String getQuoteNo() { return quoteNo; }
    /** @param quoteNo 見積番号 */
    public void setQuoteNo(String quoteNo) { this.quoteNo = quoteNo; }
    /** @return ドライバー年齢 */
    public Integer getDriverAge() { return driverAge; }
    /** @param driverAge ドライバー年齢 */
    public void setDriverAge(Integer driverAge) { this.driverAge = driverAge; }
    /** @return 免許証の色 */
    public String getLicenseColor() { return licenseColor; }
    /** @param licenseColor 免許証の色 */
    public void setLicenseColor(String licenseColor) { this.licenseColor = licenseColor; }
    /** @return 使用用途 */
    public String getUsageType() { return usageType; }
    /** @param usageType 使用用途 */
    public void setUsageType(String usageType) { this.usageType = usageType; }
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
    /** @return 年額保険料 */
    public Integer getAnnualPremium() { return annualPremium; }
    /** @param annualPremium 年額保険料 */
    public void setAnnualPremium(Integer annualPremium) { this.annualPremium = annualPremium; }
    /** @return 月額保険料 */
    public Integer getMonthlyPremium() { return monthlyPremium; }
    /** @param monthlyPremium 月額保険料 */
    public void setMonthlyPremium(Integer monthlyPremium) { this.monthlyPremium = monthlyPremium; }
    /** @return 見積作成日時 */
    public LocalDateTime getCreatedAt() { return createdAt; }
    /** @param createdAt 見積作成日時 */
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
