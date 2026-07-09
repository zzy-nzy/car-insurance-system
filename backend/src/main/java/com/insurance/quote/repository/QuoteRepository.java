package com.insurance.quote.repository;

import com.insurance.quote.entity.Quote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * 見積データのデータアクセスを担う JPA リポジトリインターフェース。
 * <p>
 * 見積番号による検索、見積番号プレフィックスの件数カウント、
 * 全件取得（降順）、および複数条件による絞り込み検索を提供する。
 */
@Repository
public interface QuoteRepository extends JpaRepository<Quote, Long> {
    /**
     * 見積番号で見積を検索する。
     * @param quoteNo 見積番号
     * @return 見つかった場合は Optional に包んだ Quote、見つからない場合は空の Optional
     */
    Optional<Quote> findByQuoteNo(String quoteNo);

    /**
     * 指定プレフィックスで始まる見積番号の件数を取得する。
     * @param prefix 見積番号のプレフィックス
     * @return 該当件数
     */
    long countByQuoteNoStartingWith(String prefix);

    /**
     * 全見積を作成日時の降順で取得する。
     * @return 作成日時降順の見積リスト
     */
    List<Quote> findAllByOrderByCreatedAtDesc();

    /**
     * 複数条件（見積番号・メーカー・車名・車種・期間）で見積を絞り込み検索する。
     * 各条件は null の場合は無視され、作成日時降順で返却される。
     *
     * @param quoteNo 見積番号（部分一致、null可）
     * @param maker 車両メーカー名（部分一致、null可）
     * @param carName 車名（部分一致、null可）
     * @param vehicleType 車種区分（完全一致、null可）
     * @param dateFrom 検索期間開始日時（null可）
     * @param dateTo 検索期間終了日時（null可）
     * @return 条件に合致する見積リスト（作成日時降順）
     */
    @Query("SELECT q FROM Quote q WHERE " +
           "(:quoteNo IS NULL OR q.quoteNo LIKE %:quoteNo%) AND " +
           "(:maker IS NULL OR q.maker LIKE %:maker%) AND " +
           "(:carName IS NULL OR q.carName LIKE %:carName%) AND " +
           "(:vehicleType IS NULL OR q.vehicleType = :vehicleType) AND " +
           "(:dateFrom IS NULL OR q.createdAt >= :dateFrom) AND " +
           "(:dateTo IS NULL OR q.createdAt <= :dateTo) " +
           "ORDER BY q.createdAt DESC")
    List<Quote> searchQuotes(@Param("quoteNo") String quoteNo,
                             @Param("maker") String maker,
                             @Param("carName") String carName,
                             @Param("vehicleType") String vehicleType,
                             @Param("dateFrom") LocalDateTime dateFrom,
                             @Param("dateTo") LocalDateTime dateTo);
}
