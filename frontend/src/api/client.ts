/**
 * APIクライアント
 * 
 * 自動車保険見積システムのバックエンドAPIと通信するためのクライアントモジュールです。
 * axiosを使用してHTTP通信を行い、見積作成・取得、管理者認証・管理機能を提供します。
 */
import axios from 'axios';
import { QuoteRequest, QuoteResponse, QuoteSummary } from '../types';

/** APIのベースURL。環境変数があればそれを使用し、なければlocalhost判定 */
const API_BASE = process.env.REACT_APP_API_URL
  || (window.location.hostname === 'localhost' ? 'http://localhost:8080/api' : '/api');

/** axiosインスタンス。共通のベースURL、ヘッダー、タイムアウトを設定 */
const api = axios.create({
  baseURL: API_BASE,
  headers: { 'Content-Type': 'application/json' },
  timeout: 30000, // タイムアウト30秒
});

/**
 * 見積を作成する
 * @param data 見積リクエストデータ
 * @returns 見積結果レスポンス
 */
export async function createQuote(data: QuoteRequest): Promise<QuoteResponse> {
  const res = await api.post<QuoteResponse>('/quotes', data);
  return res.data;
}

/**
 * 見積番号で見積を取得する
 * @param quoteNo 見積番号
 * @returns 見積内容
 */
export async function getQuote(quoteNo: string): Promise<QuoteResponse> {
  const res = await api.get<QuoteResponse>(`/quotes/${quoteNo}`);
  return res.data;
}

/**
 * 管理者ログイン
 * @param username 管理者ユーザー名
 * @param password パスワード
 * @returns ログイン結果（トークン含む）
 */
export async function adminLogin(username: string, password: string) {
  const res = await api.post('/admin/login', { username, password });
  return res.data;
}

/**
 * APIリクエストに認証トークンを設定する
 * @param token JWTトークン
 */
export function setAuthToken(token: string) {
  api.defaults.headers.common['Authorization'] = `Bearer ${token}`;
}

/**
 * 管理者向け見積一覧を取得する
 * @param token 認証トークン
 * @param params 検索パラメータ
 * @returns 見積一覧
 */
export async function getAdminQuotes(token: string, params?: Record<string, string>): Promise<QuoteSummary[]> {
  const res = await api.get('/admin/quotes', {
    headers: { Authorization: `Bearer ${token}` },
    params,
  });
  return res.data;
}

/**
 * 管理者向け見積詳細を取得する
 * @param quoteNo 見積番号
 * @param token 認証トークン
 * @returns 見積詳細
 */
export async function getAdminQuoteDetail(quoteNo: string, token: string): Promise<QuoteResponse> {
  const res = await api.get(`/admin/quotes/${quoteNo}`, {
    headers: { Authorization: `Bearer ${token}` },
  });
  return res.data;
}

/**
 * 管理者向け見積データをCSV形式でダウンロードする
 * @param token 認証トークン
 * @param params 検索パラメータ
 */
export async function getAdminQuotesCsv(token: string, params?: Record<string, string>): Promise<void> {
  // 検索パラメータをクエリ文字列に変換
  const paramStr = params ? '?' + new URLSearchParams(params).toString() : '';
  // fetch APIを使用してCSVファイルを取得
  const res = await fetch(`${API_BASE}/admin/quotes.csv${paramStr}`, {
    headers: { Authorization: `Bearer ${token}` },
  });
  if (!res.ok) throw new Error('CSV export failed');
  // Blobとして取得し、ダウンロードリンクを生成して自動ダウンロードを実行
  const blob = await res.blob();
  const url = URL.createObjectURL(blob);
  const a = document.createElement('a');
  a.href = url;
  a.download = 'quotes.csv';
  document.body.appendChild(a);
  a.click();
  document.body.removeChild(a);
  URL.revokeObjectURL(url);
}

/**
 * 管理者情報を取得する
 * @param token 認証トークン
 * @returns 管理者ユーザー名
 */
export async function getAdminInfo(token: string): Promise<{ username: string }> {
  const res = await api.get('/admin/me', {
    headers: { Authorization: `Bearer ${token}` },
  });
  return res.data;
}
