package com.insurance.quote.dto;

import java.math.BigDecimal;

/**
 * 見積内訳項目を表す DTO クラス。
 * <p>
 * 保険料の内訳として、項目コード・項目名・料率・金額・表示順を保持する。
 */
public class QuoteBreakdownItem {

    private String itemCode; // 項目コード
    private String itemName; // 項目名
    private BigDecimal rate; // 適用料率
    private Integer amount; // 計算金額
    private Integer displayOrder; // 表示順

    /**
     * 項目コードを取得する。
     * @return 項目コード
     */
    public String getItemCode() { return itemCode; }
    /**
     * 項目コードを設定する。
     * @param itemCode 項目コード
     */
    public void setItemCode(String itemCode) { this.itemCode = itemCode; }
    /**
     * 項目名を取得する。
     * @return 項目名
     */
    public String getItemName() { return itemName; }
    /**
     * 項目名を設定する。
     * @param itemName 項目名
     */
    public void setItemName(String itemName) { this.itemName = itemName; }
    /**
     * 料率を取得する。
     * @return 適用料率
     */
    public BigDecimal getRate() { return rate; }
    /**
     * 料率を設定する。
     * @param rate 適用料率
     */
    public void setRate(BigDecimal rate) { this.rate = rate; }
    /**
     * 金額を取得する。
     * @return 計算金額
     */
    public Integer getAmount() { return amount; }
    /**
     * 金額を設定する。
     * @param amount 計算金額
     */
    public void setAmount(Integer amount) { this.amount = amount; }
    /**
     * 表示順を取得する。
     * @return 表示順
     */
    public Integer getDisplayOrder() { return displayOrder; }
    /**
     * 表示順を設定する。
     * @param displayOrder 表示順
     */
    public void setDisplayOrder(Integer displayOrder) { this.displayOrder = displayOrder; }
}
