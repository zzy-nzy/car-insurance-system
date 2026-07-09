package com.insurance.quote.repository;

import com.insurance.quote.entity.QuoteBreakdown;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 見積内訳明細のデータアクセスを担う JPA リポジトリインターフェース。
 * <p>
 * 見積IDに紐付く内訳明細を表示順で取得するクエリメソッドを提供する。
 */
@Repository
public interface QuoteBreakdownRepository extends JpaRepository<QuoteBreakdown, Long> {
    /**
     * 指定した見積IDの内訳明細を表示順の昇順で取得する。
     * @param quoteId 見積ID
     * @return 表示順昇順の内訳明細リスト
     */
    List<QuoteBreakdown> findByQuoteIdOrderByDisplayOrderAsc(Long quoteId);
}
