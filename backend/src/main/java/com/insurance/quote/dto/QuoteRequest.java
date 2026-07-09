package com.insurance.quote.dto;

import jakarta.validation.constraints.*;

/**
 * 見積作成リクエストを表す DTO クラス。
 * <p>
 * ドライバー情報、車両情報、補償内容など、
 * 保険料計算に必要な全入力項目を保持する。
 * 各フィールドにはバリデーション制約が設定されている。
 */
public class QuoteRequest {

    @NotNull @Min(18) @Max(100)
    private Integer driverAge; // ドライバー年齢（18〜100歳）

    @NotBlank @Pattern(regexp = "GOLD|BLUE|GREEN", message = "免許証色はGOLD, BLUE, GREENのいずれかを指定してください")
    private String licenseColor; // 免許証の色（GOLD/BLUE/GREEN）

    @NotBlank @Pattern(regexp = "PRIVATE|COMMUTE|BUSINESS", message = "使用目的はPRIVATE, COMMUTE, BUSINESSのいずれかを指定してください")
    private String usageType; // 使用用途（PRIVATE/COMMUTE/BUSINESS）

    @NotNull @Min(0) @Max(30000)
    private Integer annualMileage; // 年間走行距離（0〜30000km）

    @NotBlank @Pattern(regexp = "SELF|COUPLE|FAMILY|ANYONE", message = "運転者範囲はSELF, COUPLE, FAMILY, ANYONEのいずれかを指定してください")
    private String driverRange; // 運転者範囲（SELF/COUPLE/FAMILY/ANYONE）

    @NotNull
    private Boolean hasCurrentInsurance; // 現在保険に加入しているか

    @Min(1) @Max(20)
    private Integer grade; // 等級（1〜20、現在加入ありの場合必須）

    @Min(0) @Max(6)
    private Integer accidentTerm; // 事故あり期間（0〜6、現在加入ありの場合必須）

    @NotBlank @Size(max = 50)
    private String maker; // 車両メーカー名

    @NotBlank @Size(max = 50)
    private String carName; // 車名

    @NotBlank @Pattern(regexp = "\\d{4}-(0[1-9]|1[0-2])", message = "初度登録年月はYYYY-MM形式（月は01〜12）で指定してください")
    private String firstRegistrationYearMonth; // 初度登録年月（YYYY-MM形式）

    @NotBlank @Pattern(regexp = "COMPACT|SEDAN|MINIVAN|SUV|KEI", message = "車両タイプはCOMPACT, SEDAN, MINIVAN, SUV, KEIのいずれかを指定してください")
    private String vehicleType; // 車種区分（COMPACT/SEDAN/MINIVAN/SUV/KEI）

    @NotNull
    private Boolean vehicleInsurance; // 車両保険の有無

    @NotBlank @Pattern(regexp = "UNLIMITED|THIRTY_MILLION", message = "対物賠償限度額はUNLIMITEDまたはTHIRTY_MILLIONを指定してください")
    private String propertyDamageLimit; // 対物賠償限度額（UNLIMITED/THIRTY_MILLION）

    @NotBlank @Pattern(regexp = "THIRTY_MILLION|FIFTY_MILLION|UNLIMITED", message = "人身傷害保険金額はTHIRTY_MILLION, FIFTY_MILLION, UNLIMITEDのいずれかを指定してください")
    private String personalInjuryAmount; // 人身傷害保険金額（THIRTY_MILLION/FIFTY_MILLION/UNLIMITED）

    @NotNull
    private Boolean lawyerOption; // 弁護士費用特約の有無

    @NotNull
    private Boolean roadService; // ロードサービスの有無

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
    public String getFirstRegistrationYearMonth() { return firstRegistrationYearMonth; }
    /** @param firstRegistrationYearMonth 初度登録年月 */
    public void setFirstRegistrationYearMonth(String firstRegistrationYearMonth) { this.firstRegistrationYearMonth = firstRegistrationYearMonth; }
    /** @return 車種区分 */
    public String getVehicleType() { return vehicleType; }
    /** @param vehicleType 車種区分 */
    public void setVehicleType(String vehicleType) { this.vehicleType = vehicleType; }
    /** @return 車両保険の有無 */
    public Boolean getVehicleInsurance() { return vehicleInsurance; }
    /** @param vehicleInsurance 車両保険の有無 */
    public void setVehicleInsurance(Boolean vehicleInsurance) { this.vehicleInsurance = vehicleInsurance; }
    /** @return 対物賠償限度額 */
    public String getPropertyDamageLimit() { return propertyDamageLimit; }
    /** @param propertyDamageLimit 対物賠償限度額 */
    public void setPropertyDamageLimit(String propertyDamageLimit) { this.propertyDamageLimit = propertyDamageLimit; }
    /** @return 人身傷害保険金額 */
    public String getPersonalInjuryAmount() { return personalInjuryAmount; }
    /** @param personalInjuryAmount 人身傷害保険金額 */
    public void setPersonalInjuryAmount(String personalInjuryAmount) { this.personalInjuryAmount = personalInjuryAmount; }
    /** @return 弁護士費用特約の有無 */
    public Boolean getLawyerOption() { return lawyerOption; }
    /** @param lawyerOption 弁護士費用特約の有無 */
    public void setLawyerOption(Boolean lawyerOption) { this.lawyerOption = lawyerOption; }
    /** @return ロードサービスの有無 */
    public Boolean getRoadService() { return roadService; }
    /** @param roadService ロードサービスの有無 */
    public void setRoadService(Boolean roadService) { this.roadService = roadService; }
}
