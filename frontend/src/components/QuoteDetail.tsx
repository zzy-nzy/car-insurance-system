/**
 * 見積詳細コンポーネント
 *
 * 管理画面で選択された見積もりの詳細情報を表示するコンポーネント。
 * 見積番号、年間保険料、月額保険料、作成日時、および保険料計算の内訳をテーブル形式で表示する。
 * 管理者認証トークンを使用してAPIから見積詳細データを取得する。
 */
import React, { useEffect, useState } from 'react';
import { QuoteResponse } from '../types';
import { getAdminQuoteDetail } from '../api/client';

/** QuoteDetailコンポーネントのProps */
interface Props { token: string; quoteNo: string; onBack: () => void; }

/**
 * 見積詳細を描画するコンポーネント
 *
 * @param token - 管理者認証トークン
 * @param quoteNo - 表示対象の見積番号
 * @param onBack - 「一覧に戻る」ボタン押下時のコールバック
 */
export default function QuoteDetail({ token, quoteNo, onBack }: Props) {
  // 見積詳細データのState
  const [data, setData] = useState<QuoteResponse | null>(null);
  // データ取得中のローディングState
  const [loading, setLoading] = useState(true);

  // 見積番号が変更された際にAPIから見積詳細を取得
  useEffect(() => {
    getAdminQuoteDetail(quoteNo, token).then(setData).finally(() => setLoading(false));
  }, [quoteNo]);

  // ローディング中の表示
  if (loading) return <div className="page-card"><p>読み込み中...</p></div>;
  // データが取得できなかった場合の表示
  if (!data) return <div className="page-card"><p>データが見つかりません</p></div>;

  return (
    <div className="page-card" style={{ maxWidth: 720, margin: '0 auto' }}>
      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: 16 }}>
        <h2 style={{ margin: 0 }}>見積詳細</h2>
        <button className="btn-secondary" onClick={onBack}>一覧に戻る</button>
      </div>

      {/* 見積サマリー情報の表示 */}
      <div style={{ background: '#f9fafb', padding: 16, borderRadius: 8, marginBottom: 16 }}>
        <div style={{ display: 'flex', gap: 32, flexWrap: 'wrap' }}>
          <div><strong>見積番号:</strong> {data.quoteNo}</div>
          <div><strong>年間保険料:</strong> {data.annualPremium.toLocaleString()}円</div>
          <div><strong>月額保険料:</strong> {data.monthlyPremium.toLocaleString()}円</div>
          {/* 作成日時を日本語ロケールで表示 */}
          <div><strong>作成日時:</strong> {new Date(data.createdAt).toLocaleString('ja-JP')}</div>
        </div>
      </div>

      <h3 style={{ margin: '16px 0 8px' }}>内訳</h3>
      {/* 保険料計算内訳のテーブル */}
      <table style={{ width: '100%', borderCollapse: 'collapse' }}>
        <thead>
          <tr style={{ background: '#f3f4f6' }}>
            <th style={thStyle}>項目</th>
            <th style={thStyle}>料率</th>
            <th style={thStyle}>金額</th>
          </tr>
        </thead>
        <tbody>
          {/* 内訳データを1行ずつ描画 */}
          {data.breakdowns.map((b, i) => (
            <tr key={i} style={{ borderBottom: '1px solid #e5e7eb' }}>
              <td style={tdStyle}>{b.itemName}</td>
              {/* 料率が存在する場合は表示、ない場合は「-」を表示 */}
              <td style={tdStyle}>{b.rate != null ? b.rate : '-'}</td>
              {/* 金額が存在する場合はカンマ区切りで表示、ない場合は「-」を表示 */}
              <td style={tdStyle}>{b.amount != null ? `${b.amount.toLocaleString()}円` : '-'}</td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
}

// テーブルヘッダーセルのスタイル定義
const thStyle: React.CSSProperties = { padding: '8px 12px', textAlign: 'left', fontWeight: 600, borderBottom: '2px solid #d1d5db' };
// テーブルデータセルのスタイル定義
const tdStyle: React.CSSProperties = { padding: '8px 12px' };
