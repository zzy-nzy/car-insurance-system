/**
 * 見積結果表示コンポーネント
 *
 * 見積作成APIから返却された結果をユーザーに表示するコンポーネント。
 * 見積番号、年間保険料、月額保険料をハイライト表示し、計算内訳をテーブル形式で表示する。
 */
import React from 'react';
import { QuoteResponse } from '../types';

/** QuoteResultコンポーネントのProps */
interface Props { data: QuoteResponse; onReset: () => void; }

/**
 * 見積結果を描画するコンポーネント
 *
 * @param data - 見積作成APIのレスポンスデータ
 * @param onReset - 「最初に戻る」ボタン押下時のコールバック
 */
export default function QuoteResult({ data, onReset }: Props) {
  return (
    <div className="page-card">
      <h2>見積結果</h2>
      {/* 見積結果サマリー */}
      <div className="result-box">
        <div className="result-item">
          <span className="result-label">見積番号</span>
          <span className="result-value">{data.quoteNo}</span>
        </div>
        {/* 年間保険料をハイライト表示 */}
        <div className="result-item highlight">
          <span className="result-label">年間保険料</span>
          <span className="result-value">{data.annualPremium.toLocaleString()} 円</span>
        </div>
        {/* 月額保険料をハイライト表示 */}
        <div className="result-item highlight">
          <span className="result-label">月額保険料</span>
          <span className="result-value">{data.monthlyPremium.toLocaleString()} 円</span>
        </div>
      </div>
      <h3>計算内訳</h3>
      {/* 保険料計算内訳のテーブル */}
      <table className="breakdown-table">
        <thead>
          <tr>
            <th>項目</th>
            <th>係数</th>
            <th>加算額</th>
          </tr>
        </thead>
        <tbody>
          {/* 内訳データを1行ずつ描画 */}
          {data.breakdowns.map((b, i) => (
            <tr key={i}>
              <td>{b.itemName}</td>
              {/* 係数が存在する場合は「×」付きで表示、ない場合は「-」を表示 */}
              <td>{b.rate != null ? `× ${b.rate}` : '-'}</td>
              {/* 加算額が存在する場合は「+」付きでカンマ区切り表示、ない場合は「-」を表示 */}
              <td>{b.amount != null ? `+ ${b.amount.toLocaleString()} 円` : '-'}</td>
            </tr>
          ))}
        </tbody>
      </table>
      <button className="btn-primary" onClick={onReset}>最初に戻る</button>
    </div>
  );
}
