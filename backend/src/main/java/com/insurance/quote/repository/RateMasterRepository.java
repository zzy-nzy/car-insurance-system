package com.insurance.quote.repository;

import com.insurance.quote.entity.RateMaster;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 料率マスタのデータアクセスを担う JPA リポジトリインターフェース。
 * <p>
 * 有効な料率マスタの全件取得、およびカテゴリ・項目コードによる
 * 個別検索のクエリメソッドを提供する。
 */
@Repository
public interface RateMasterRepository extends JpaRepository<RateMaster, Long> {
    /**
     * 有効な料率マスタを全件取得する（カテゴリ昇順、ID昇順）。
     * @return 有効な料率マスタリスト
     */
    List<RateMaster> findByActiveTrueOrderByCategoryAscIdAsc();

    /**
     * カテゴリと項目コードで有効な料率マスタを検索する。
     * @param category 料率カテゴリ
     * @param itemCode 項目コード
     * @return 見つかった場合は Optional に包んだ RateMaster、見つからない場合は空の Optional
     */
    Optional<RateMaster> findByCategoryAndItemCodeAndActiveTrue(String category, String itemCode);
}
