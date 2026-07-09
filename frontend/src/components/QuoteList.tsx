/**
 * 見積一覧コンポーネント（管理画面）
 *
 * 管理画面で作成済みの見積もり一覧を表示・検索するコンポーネント。
 * 見積番号、メーカー、車名、車種での絞り込み検索が可能。
 * また、CSVエクスポート機能も提供する。
 * 行クリックで見積詳細画面へ遷移する。
 */
import React, { useEffect, useState } from 'react';
import { QuoteSummary } from '../types';
import { getAdminQuotes, getAdminQuotesCsv, getAdminInfo } from '../api/client';

/** QuoteListコンポーネントのProps */
interface Props { token: string; onLogout: () => void; onSelectQuote: (quoteNo: string) => void; }

/**
 * 見積一覧を描画するコンポーネント
 *
 * @param token - 管理者認証トークン
 * @param onLogout - 「ログアウト」ボタン押下時のコールバック
 * @param onSelectQuote - 見積行クリック時のコールバック。選択された見積番号を引数に取る。
 */
export default function QuoteList({ token, onLogout, onSelectQuote }: Props) {
  // 見積一覧データのState
  const [quotes, setQuotes] = useState<QuoteSummary[]>([]);
  // ログイン中の管理者ユーザー名のState
  const [username, setUsername] = useState('');
  // データ取得中のローディングState
  const [loading, setLoading] = useState(true);
  // 検索条件：見積番号
  const [quoteNo, setQuoteNo] = useState('');
  // 検索条件：メーカー
  const [maker, setMaker] = useState('');
  // 検索条件：車名
  const [carName, setCarName] = useState('');
  // 検索条件：車両タイプ
  const [vehicleType, setVehicleType] = useState('');

  // コンポーネントマウント時に管理者情報の取得と見積一覧の読み込みを実行
  useEffect(() => {
    getAdminInfo(token).then(r => setUsername(r.username)).catch(() => {});
    loadQuotes();
  }, []);

  /**
   * 見積一覧をAPIから取得する関数
   * 検索パラメータが指定されている場合はその条件で絞り込む。
   *
   * @param params - 検索条件のキー・バリューペア（省略可）
   */
  const loadQuotes = async (params?: Record<string, string>) => {
    setLoading(true);
    try {
      const result = await getAdminQuotes(token, params);
      setQuotes(result);
    } finally {
      setLoading(false);
    }
  };

  /**
   * 検索ボタン押下時のハンドラ
   * 入力された検索条件をパラメータにまとめて見積一覧を再取得する。
   */
  const handleSearch = () => {
    const params: Record<string, string> = {};
    // 入力されている検索条件のみパラメータに追加
    if (quoteNo) params.quoteNo = quoteNo;
    if (maker) params.maker = maker;
    if (carName) params.carName = carName;
    if (vehicleType) params.vehicleType = vehicleType;
    // 検索条件が1つでもあればパラメータ付きで取得、なければ全件取得
    loadQuotes(Object.keys(params).length ? params : undefined);
  };

  /**
   * リセットボタン押下時のハンドラ
   * 検索条件をクリアし、全件の見積一覧を再取得する。
   */
  const handleReset = () => {
    setQuoteNo(''); setMaker(''); setCarName(''); setVehicleType('');
    loadQuotes();
  };

  // APIの enum 値を日本語表示名に変換するマッピングテーブル
  const labelMap: Record<string, string> = {
    GOLD: 'ゴールド', BLUE: 'ブルー', GREEN: 'グリーン',
    PRIVATE: '通勤・業務以外', COMMUTE: '通勤', BUSINESS: '業務',
    COMPACT: 'コンパクト', SEDAN: 'セダン', MINIVAN: 'ミニバン', SUV: 'SUV', KEI: '軽自動車',
  };

  return (
    <div className="page-card" style={{ maxWidth: 960, margin: '0 auto' }}>
      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: 16 }}>
        <h2 style={{ margin: 0 }}>見積一覧</h2>
        <div style={{ display: 'flex', alignItems: 'center', gap: 12 }}>
          <span style={{ color: '#666' }}>ログイン中: {username || '管理者'}</span>
          <button className="btn-secondary" onClick={onLogout}>ログアウト</button>
        </div>
      </div>

      {/* 検索条件入力エリア */}
      <div style={{ display: 'flex', gap: 8, marginBottom: 12, flexWrap: 'wrap', alignItems: 'flex-end' }}>
        <div>
          <label style={{ fontSize: '0.8rem' }}>見積番号</label>
          <input type="text" value={quoteNo} onChange={e => setQuoteNo(e.target.value)}
            style={{ width: 120, padding: '6px 8px', border: '1px solid #d1d5db', borderRadius: 4 }} />
        </div>
        <div>
          <label style={{ fontSize: '0.8rem' }}>メーカー</label>
          <input type="text" value={maker} onChange={e => setMaker(e.target.value)}
            style={{ width: 100, padding: '6px 8px', border: '1px solid #d1d5db', borderRadius: 4 }} />
        </div>
        <div>
          <label style={{ fontSize: '0.8rem' }}>車名</label>
          <input type="text" value={carName} onChange={e => setCarName(e.target.value)}
            style={{ width: 100, padding: '6px 8px', border: '1px solid #d1d5db', borderRadius: 4 }} />
        </div>
        <div>
          <label style={{ fontSize: '0.8rem' }}>車種</label>
          <select value={vehicleType} onChange={e => setVehicleType(e.target.value)}
            style={{ width: 110, padding: '6px 8px', border: '1px solid #d1d5db', borderRadius: 4, background: '#fff' }}>
            <option value="">すべて</option>
            <option value="COMPACT">コンパクト</option>
            <option value="SEDAN">セダン</option>
            <option value="MINIVAN">ミニバン</option>
            <option value="SUV">SUV</option>
            <option value="KEI">軽自動車</option>
          </select>
        </div>
        <button onClick={handleSearch} style={{ padding: '6px 16px', background: '#1a365d', color: '#fff', border: 'none', borderRadius: 4, cursor: 'pointer' }}>検索</button>
        <button onClick={handleReset} style={{ padding: '6px 16px', background: '#6b7280', color: '#fff', border: 'none', borderRadius: 4, cursor: 'pointer' }}>リセット</button>
        {/* CSVエクスポートボタン（右端に配置） — 現在の検索条件を反映して出力 */}
        <button onClick={() => {
          const params: Record<string, string> = {};
          if (quoteNo) params.quoteNo = quoteNo;
          if (maker) params.maker = maker;
          if (carName) params.carName = carName;
          if (vehicleType) params.vehicleType = vehicleType;
          getAdminQuotesCsv(token, Object.keys(params).length ? params : undefined);
        }} style={{ padding: '6px 16px', background: '#047857', color: '#fff', border: 'none', borderRadius: 4, cursor: 'pointer', marginLeft: 'auto' }}>CSVエクスポート</button>
      </div>

      {/* ローディング中・データなし・一覧テーブルの切り替え表示 */}
      {loading ? <p>読み込み中...</p> : quotes.length === 0 ? <p>見積データがありません</p> : (
        <table style={{ width: '100%', borderCollapse: 'collapse', fontSize: '0.9rem' }}>
          <thead>
            <tr style={{ background: '#f3f4f6' }}>
              <th style={thStyle}>見積番号</th>
              <th style={thStyle}>運転者年齢</th>
              <th style={thStyle}>免許色</th>
              <th style={thStyle}>メーカー</th>
              <th style={thStyle}>車名</th>
              <th style={thStyle}>車種</th>
              <th style={thStyle}>年間保険料</th>
              <th style={thStyle}>月額保険料</th>
              <th style={thStyle}>作成日時</th>
            </tr>
          </thead>
          <tbody>
            {/* 見積データを1行ずつ描画。行クリックで見積詳細へ遷移 */}
            {quotes.map(q => (
              <tr key={q.quoteNo} onClick={() => onSelectQuote(q.quoteNo)}
                style={{ cursor: 'pointer', borderBottom: '1px solid #e5e7eb' }}
                onMouseEnter={e => (e.currentTarget.style.background = '#f9fafb')}
                onMouseLeave={e => (e.currentTarget.style.background = '')}>
                <td style={tdStyle}>{q.quoteNo}</td>
                <td style={tdStyle}>{q.driverAge}</td>
                {/* 免許色を日本語表示名に変換 */}
                <td style={tdStyle}>{labelMap[q.licenseColor] || q.licenseColor}</td>
                <td style={tdStyle}>{q.maker}</td>
                <td style={tdStyle}>{q.carName}</td>
                {/* 車種を日本語表示名に変換 */}
                <td style={tdStyle}>{labelMap[q.vehicleType] || q.vehicleType}</td>
                {/* 年間保険料をカンマ区切りで表示 */}
                <td style={tdStyle}>{q.annualPremium.toLocaleString()}円</td>
                {/* 月額保険料をカンマ区切りで表示 */}
                <td style={tdStyle}>{q.monthlyPremium.toLocaleString()}円</td>
                {/* 作成日時を日本語ロケールで表示 */}
                <td style={tdStyle}>{new Date(q.createdAt).toLocaleString('ja-JP')}</td>
              </tr>
            ))}
          </tbody>
        </table>
      )}
    </div>
  );
}

// テーブルヘッダーセルのスタイル定義
const thStyle: React.CSSProperties = { padding: '8px 6px', textAlign: 'left', fontWeight: 600, borderBottom: '2px solid #d1d5db' };
// テーブルデータセルのスタイル定義
const tdStyle: React.CSSProperties = { padding: '8px 6px' };
