/**
 * アプリケーションエントリーポイント
 * 
 * Reactアプリケーションの起動ファイルです。
 * ReactDOM.createRootを使用してルートコンポーネントをマウントし、
 * StrictModeでラップして開発時の追加チェックを有効にします。
 */
import React from 'react';
import ReactDOM from 'react-dom/client';
import App from './App';
/** グローバルスタイルシート */
import './styles/App.css';

// DOM要素'root'を取得し、Reactルートを作成してレンダリング
const root = ReactDOM.createRoot(document.getElementById('root') as HTMLElement);
root.render(<React.StrictMode><App /></React.StrictMode>);
