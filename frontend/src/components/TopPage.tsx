import React from 'react';

interface Props { onStart: () => void; onAdmin: () => void; }

export default function TopPage({ onStart, onAdmin }: Props) {
  return (
    <div className="page-card top-page">
      <div className="site-icon">🛡️</div>
      <h2>自動車保険見積サイト</h2>
      <p>
        簡単な質問に答えるだけで、自動車保険の<br />
        見積もりをすぐに作成できます。
      </p>
      <p>実際の保険料率ではなく、課題用の簡易ロジックを使用しています。</p>
      <div className="top-buttons">
        <button className="btn-user" onClick={onStart}>
          ユーザー（見積作成）
        </button>
        <button className="btn-admin" onClick={onAdmin}>
          管理者ログイン
        </button>
      </div>
    </div>
  );
}
