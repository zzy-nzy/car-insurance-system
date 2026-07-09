/**
 * トップページコンポーネント
 *
 * 自動車保険見積サイトのエントリーポイントとなるページ。
 * ユーザーに「見積作成」と「管理者ログイン」の2つの選択肢を提供する。
 */
import React from 'react';

/** TopPageコンポーネントのProps */
interface Props { onStart: () => void; onAdmin: () => void; }

/**
 * トップページを描画するコンポーネント
 *
 * @param onStart - 「ユーザー（見積作成）」ボタン押下時のコールバック
 * @param onAdmin - 「管理者ログイン」ボタン押下時のコールバック
 */
export default function TopPage({ onStart, onAdmin }: Props) {
  return (
    <div className="page-card">
      <h2>自動車保険見積サイト</h2>
      <p style={{ marginBottom: 32 }}>見積もりを作成するか、管理者ログインを選択してください。</p>
      <div style={{ display: 'flex', flexDirection: 'column', gap: 16 }}>
        {/* ユーザー向け：見積作成フローへ遷移 */}
        <button className="btn-primary" onClick={onStart} style={{ padding: '20px 24px', fontSize: '1.2rem' }}>
          ユーザー（見積作成）
        </button>
        {/* 管理者向け：管理者ログイン画面へ遷移 */}
        <button className="btn-secondary" onClick={onAdmin} style={{ padding: '20px 24px', fontSize: '1.2rem' }}>
          管理者ログイン
        </button>
      </div>
    </div>
  );
}
