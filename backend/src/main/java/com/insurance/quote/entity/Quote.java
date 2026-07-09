package com.insurance.quote.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * 見積情報を表す JPA エンティティクラス。
 * <p>
 * 保険見積の入力条件（ドライバー情報・車両情報・補償内容）と
 * 計算結果（年額・月額保険料）を保持する。
 * 作成日時・更新日時はライフサイクルコールバックで自動設定される。
 */
@Entity
@Table(name = "quotes")
public class Quote {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // 主キー（自動採番）

    @Column(name = "quote_no", length = 20, nullable = false, unique = true)
    private String quoteNo; // 見積番号（一意）

    @Column(name = "driver_age", nullable = false)
    private Integer driverAge; // ドライバー年齢

    @Column(name = "license_color", length = 20, nullable = false)
    private String licenseColor; // 免許証の色

    @Column(name = "usage_type", length = 20, nullable = false)
    private String usageType; // 使用用途

    @Column(name = "annual_mileage", nullable = false)
    private Integer annualMileage; // 年間走行距離

    @Column(name = "driver_range", length = 20, nullable = false)
    private String driverRange; // 運転者範囲

    @Column(name = "has_current_insurance", nullable = false)
    private Boolean hasCurrentInsurance; // 現在保険加入有無

    @Column(name = "grade")
    private Integer grade; // 等級

    @Column(name = "accident_term")
    private Integer accidentTerm; // 事故あり期間

    @Column(name = "maker", length = 50, nullable = false)
    private String maker; // 車両メーカー名

    @Column(name = "car_name", length = 50, nullable = false)
    private String carName; // 車名

    @Column(name = "first_registration_ym", length = 7, nullable = false)
    private String firstRegistrationYm; // 初度登録年月

    @Column(name = "vehicle_type", length = 20, nullable = false)
    private String vehicleType; // 車種区分

    @Column(name = "vehicle_insurance", nullable = false)
    private Boolean vehicleInsurance; // 車両保険の有無

    @Column(name = "annual_premium", nullable = false)
    private Integer annualPremium; // 年額保険料

    @Column(name = "monthly_premium", nullable = false)
    private Integer monthlyPremium; // 月額保険料

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt; // 作成日時

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt; // 更新日時

    /**
     * エンティティ保存前に作成日時・更新日時を現在日時で設定する。
     */
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    /**
     * エンティティ更新前に更新日時を現在日時で設定する。
     */
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    /** @return 主キーID */
    public Long getId() { return id; }
    /** @param id 主キーID */
    public void setId(Long id) { this.id = id; }
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
    /** @return 年間走行距離 */
    public Integer getAnnualMileage() { return annualMileage; }
    /** @param annualMileage 年間走行距離 */
    public void setAnnualMileage(Integer annualMileage) { this.annualMileage = annualMileage; }
    /** @return 運転者範囲 */
    public String getDriverRange() { return driverRange; }
    /** @param driverRange 運転者範囲 */
    public void setDriverRange(String driverRange) { this.driverRange = driverRange; }
    /** @return 現在保険加入有無 */
    public Boolean getHasCurrentInsurance() { return hasCurrentInsurance; }
    /** @param hasCurrentInsurance 現在保険加入有無 */
    public void setHasCurrentInsurance(Boolean hasCurrentInsurance) { this.hasCurrentInsurance = hasCurrentInsurance; }
    /** @return 等級 */
    public Integer getGrade() { return grade; }
    /** @param grade 等級 */
    public void setGrade(Integer grade) { this.grade = grade; }
    /** @return 事故あり期間 */
    public Integer getAccidentTerm() { return accidentTerm; }
    /** @param accidentTerm 事故あり期間 */
    public void setAccidentTerm(Integer accidentTerm) { this.accidentTerm = accidentTerm; }
    /** @return 車両メーカー名 */
    public String getMaker() { return maker; }
    /** @param maker 車両メーカー名 */
    public void setMaker(String maker) { this.maker = maker; }
    /** @return 車名 */
    public String getCarName() { return carName; }
    /** @param carName 車名 */
    public void setCarName(String carName) { this.carName = carName; }
    /** @return 初度登録年月 */
    public String getFirstRegistrationYm() { return firstRegistrationYm; }
    /** @param firstRegistrationYm 初度登録年月 */
    public void setFirstRegistrationYm(String firstRegistrationYm) { this.firstRegistrationYm = firstRegistrationYm; }
    /** @return 車種区分 */
    public String getVehicleType() { return vehicleType; }
    /** @param vehicleType 車種区分 */
    public void setVehicleType(String vehicleType) { this.vehicleType = vehicleType; }
    /** @return 車両保険の有無 */
    public Boolean getVehicleInsurance() { return vehicleInsurance; }
    /** @param vehicleInsurance 車両保険の有無 */
    public void setVehicleInsurance(Boolean vehicleInsurance) { this.vehicleInsurance = vehicleInsurance; }
    /** @return 年額保険料 */
    public Integer getAnnualPremium() { return annualPremium; }
    /** @param annualPremium 年額保険料 */
    public void setAnnualPremium(Integer annualPremium) { this.annualPremium = annualPremium; }
    /** @return 月額保険料 */
    public Integer getMonthlyPremium() { return monthlyPremium; }
    /** @param monthlyPremium 月額保険料 */
    public void setMonthlyPremium(Integer monthlyPremium) { this.monthlyPremium = monthlyPremium; }
    /** @return 作成日時 */
    public LocalDateTime getCreatedAt() { return createdAt; }
    /** @param createdAt 作成日時 */
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    /** @return 更新日時 */
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    /** @param updatedAt 更新日時 */
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
