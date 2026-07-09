package com.insurance.quote.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;

/**
 * 料率マスタを表す JPA エンティティクラス。
 * <p>
 * 保険料計算の基礎となる料率・金額をカテゴリ・項目コードごとに保持する。
 * 有効フラグ（active）により使用可否を制御する。
 */
@Entity
@Table(name = "rate_masters")
public class RateMaster {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // 主キー（自動採番）

    @Column(name = "category", length = 50, nullable = false)
    private String category; // 料率カテゴリ

    @Column(name = "item_code", length = 50, nullable = false)
    private String itemCode; // 項目コード

    @Column(name = "item_name", length = 100, nullable = false)
    private String itemName; // 項目名

    @Column(name = "rate", precision = 6, scale = 3)
    private BigDecimal rate; // 料率

    @Column(name = "amount")
    private Integer amount; // 固定金額

    @Column(name = "active", nullable = false)
    private Boolean active; // 有効フラグ

    /** @return 主キーID */
    public Long getId() { return id; }
    /** @param id 主キーID */
    public void setId(Long id) { this.id = id; }
    /** @return 料率カテゴリ */
    public String getCategory() { return category; }
    /** @param category 料率カテゴリ */
    public void setCategory(String category) { this.category = category; }
    /** @return 項目コード */
    public String getItemCode() { return itemCode; }
    /** @param itemCode 項目コード */
    public void setItemCode(String itemCode) { this.itemCode = itemCode; }
    /** @return 項目名 */
    public String getItemName() { return itemName; }
    /** @param itemName 項目名 */
    public void setItemName(String itemName) { this.itemName = itemName; }
    /** @return 料率 */
    public BigDecimal getRate() { return rate; }
    /** @param rate 料率 */
    public void setRate(BigDecimal rate) { this.rate = rate; }
    /** @return 固定金額 */
    public Integer getAmount() { return amount; }
    /** @param amount 固定金額 */
    public void setAmount(Integer amount) { this.amount = amount; }
    /** @return 有効フラグ */
    public Boolean getActive() { return active; }
    /** @param active 有効フラグ */
    public void setActive(Boolean active) { this.active = active; }
}
