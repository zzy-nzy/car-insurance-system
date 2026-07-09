/**
 * 管理者ログインコンポーネント
 *
 * 自動車保険見積システムの管理画面にアクセスするためのログインフォームを提供する。
 * 管理者ユーザー名とパスワードを入力し、認証APIを呼び出してトークンを取得する。
 * 認証成功時は親コンポーネントにトークンを渡し、管理画面へ遷移する。
 */
import React, { useState } from 'react';
import { adminLogin, setAuthToken } from '../api/client';

/** AdminLoginコンポーネントのProps */
interface Props { onLogin: (token: string) => void; }

/**
 * 管理者ログインフォームを描画するコンポーネント
 *
 * @param onLogin - 認証成功時に呼び出されるコールバック。取得したトークンを引数に取る。
 */
export default function AdminLogin({ onLogin }: Props) {
  // ユーザー名入力値のState
  const [username, setUsername] = useState('');
  // パスワード入力値のState
  const [password, setPassword] = useState('');
  // ログインエラーメッセージのState
  const [error, setError] = useState('');

  /**
   * ログインボタン押下時のハンドラ
   * 認証APIを呼び出し、成功時にトークンを保存して親に通知する。
   * 失敗時はエラーメッセージを表示する。
   */
  const handleLogin = async () => {
    try {
      // 認証APIを呼び出してトークンを取得
      const res = await adminLogin(username, password);
      // 取得したトークンをAPIクライアントに設定（以降のAPIリクエストで使用）
      setAuthToken(res.token);
      // 親コンポーネントにログイン成功を通知
      onLogin(res.token);
    } catch { setError('ログインに失敗しました'); }
  };

  return (
    <div className="page-card">
      <h2>管理者ログイン</h2>
      {/* ログインエラーがある場合はエラーメッセージを表示 */}
      {error && <p style={{ color: 'red' }}>{error}</p>}
      <div style={{ display: 'flex', flexDirection: 'column', gap: 12 }}>
        <div>
          <label>ユーザー名</label>
          <input type="text" value={username} onChange={e => setUsername(e.target.value)}
            style={{ width: '100%', padding: '10px 12px', border: '1px solid #d1d5db', borderRadius: 6, fontSize: '1rem', boxSizing: 'border-box' }} />
        </div>
        <div>
          <label>パスワード</label>
          <input type="password" value={password} onChange={e => setPassword(e.target.value)}
            style={{ width: '100%', padding: '10px 12px', border: '1px solid #d1d5db', borderRadius: 6, fontSize: '1rem', boxSizing: 'border-box' }} />
        </div>
        <button onClick={handleLogin}
          style={{ width: '100%', padding: '10px 12px', border: 'none', borderRadius: 6, fontSize: '1rem', fontWeight: 600,
                   background: '#1a365d', color: '#fff', cursor: 'pointer', boxSizing: 'border-box' }}>ログイン</button>
      </div>
    </div>
  );
}
