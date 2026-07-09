package com.insurance.quote.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;

/**
 * 見積内訳明細を表す JPA エンティティクラス。
 * <p>
 * 各見積に対する保険料の内訳項目（項目コード・項目名・料率・金額・表示順）を保持する。
 * 見積エンティティ（Quote）に紐付く子テーブルとして機能する。
 */
@Entity
@Table(name = "quote_breakdowns")
public class QuoteBreakdown {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // 主キー（自動採番）

    @Column(name = "quote_id", nullable = false)
    private Long quoteId; // 紐付く見積ID

    @Column(name = "item_code", length = 50, nullable = false)
    private String itemCode; // 項目コード

    @Column(name = "item_name", length = 100, nullable = false)
    private String itemName; // 項目名

    @Column(name = "rate", precision = 6, scale = 3)
    private BigDecimal rate; // 適用料率

    @Column(name = "amount")
    private Integer amount; // 計算金額

    @Column(name = "display_order", nullable = false)
    private Integer displayOrder; // 表示順

    /** @return 主キーID */
    public Long getId() { return id; }
    /** @param id 主キーID */
    public void setId(Long id) { this.id = id; }
    /** @return 紐付く見積ID */
    public Long getQuoteId() { return quoteId; }
    /** @param quoteId 紐付く見積ID */
    public void setQuoteId(Long quoteId) { this.quoteId = quoteId; }
    /** @return 項目コード */
    public String getItemCode() { return itemCode; }
    /** @param itemCode 項目コード */
    public void setItemCode(String itemCode) { this.itemCode = itemCode; }
    /** @return 項目名 */
    public String getItemName() { return itemName; }
    /** @param itemName 項目名 */
    public void setItemName(String itemName) { this.itemName = itemName; }
    /** @return 適用料率 */
    public BigDecimal getRate() { return rate; }
    /** @param rate 適用料率 */
    public void setRate(BigDecimal rate) { this.rate = rate; }
    /** @return 計算金額 */
    public Integer getAmount() { return amount; }
    /** @param amount 計算金額 */
    public void setAmount(Integer amount) { this.amount = amount; }
    /** @return 表示順 */
    public Integer getDisplayOrder() { return displayOrder; }
    /** @param displayOrder 表示順 */
    public void setDisplayOrder(Integer displayOrder) { this.displayOrder = displayOrder; }
}
