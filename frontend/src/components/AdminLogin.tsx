import React, { useState } from 'react';
import { adminLogin, setAuthToken } from '../api/client';

interface Props { onLogin: (token: string) => void; }

export default function AdminLogin({ onLogin }: Props) {
  const [username, setUsername] = useState('');
  const [password, setPassword] = useState('');
  const [error, setError] = useState('');

  const handleLogin = async () => {
    try {
      const res = await adminLogin(username, password);
      setAuthToken(res.token);
      onLogin(res.token);
    } catch { setError('ログインに失敗しました'); }
  };

  return (
    <div className="page-card">
      <h2>管理者ログイン</h2>
      {error && <p style={{ color: '#e53e3e', background: '#fff5f5', padding: '10px 14px', borderRadius: 6, marginBottom: 16 }}>{error}</p>}
      <label>ユーザー名</label>
      <input type="text" value={username} onChange={e => setUsername(e.target.value)} placeholder="ユーザー名を入力" />
      <label>パスワード</label>
      <input type="password" value={password} onChange={e => setPassword(e.target.value)} placeholder="パスワードを入力" />
      <div className="button-group" style={{ marginTop: 20 }}>
        <button className="btn-primary" onClick={handleLogin} style={{ width: '100%' }}>ログイン</button>
      </div>
    </div>
  );
}
