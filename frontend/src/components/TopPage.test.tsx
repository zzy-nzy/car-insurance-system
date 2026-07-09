/**
 * TopPageコンポーネントのテストファイル
 *
 * トップページに2つのエントリーポイント（ユーザー・管理者）が正しく表示されること、
 * および各ボタンクリック時に対応するコールバックが呼び出されることを検証する。
 */
import React from 'react';
import { render, screen } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import TopPage from './TopPage';

// テスト用のモックコールバック
test('renders two entry points', () => {
  const onStart = jest.fn();
  const onAdmin = jest.fn();
  render(<TopPage onStart={onStart} onAdmin={onAdmin} />);

  // ユーザー向けボタンと管理者向けボタンが表示されることを確認
  expect(screen.getByText('ユーザー（見積作成）')).toBeInTheDocument();
  expect(screen.getByText('管理者ログイン')).toBeInTheDocument();
});

// 「ユーザー（見積作成）」ボタンクリック時にonStartが呼ばれることを検証
test('calls onStart when user button clicked', async () => {
  const onStart = jest.fn();
  render(<TopPage onStart={onStart} onAdmin={() => {}} />);
  await userEvent.click(screen.getByText('ユーザー（見積作成）'));
  expect(onStart).toHaveBeenCalledTimes(1);
});

// 「管理者ログイン」ボタンクリック時にonAdminが呼ばれることを検証
test('calls onAdmin when admin button clicked', async () => {
  const onAdmin = jest.fn();
  render(<TopPage onStart={() => {}} onAdmin={onAdmin} />);
  await userEvent.click(screen.getByText('管理者ログイン'));
  expect(onAdmin).toHaveBeenCalledTimes(1);
});
