/**
 * UserInfoFormコンポーネントのテストファイル
 *
 * 使用者情報入力フォームの各フィールドが正しく表示されること、
 * およびバリデーションエラー時に適切なアラートが表示されることを検証する。
 * また、全フィールドが有効な場合にonNextコールバックが呼び出されることも確認する。
 */
import React from 'react';
import { render, screen } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import UserInfoForm from './UserInfoForm';
import { FormData } from '../types';

// テスト用の空のフォームデータ（全フィールド初期値）
const emptyForm: FormData = {
  driverAge: '', licenseColor: '', usageType: '', annualMileage: '',
  driverRange: '', hasCurrentInsurance: false, grade: '', accidentTerm: '',
  maker: '', carName: '', firstRegistrationYearMonth: '', vehicleType: '',
  vehicleInsurance: null, propertyDamageLimit: '', personalInjuryAmount: '',
  lawyerOption: null, roadService: null,
};

// 全フィールドが画面に表示されることを検証
test('renders all fields', () => {
  render(<UserInfoForm data={emptyForm} onUpdate={() => {}} onNext={() => {}} />);
  expect(screen.getByText('運転者年齢（18〜100）')).toBeInTheDocument();
  expect(screen.getByText('免許証色')).toBeInTheDocument();
  expect(screen.getByText('使用目的')).toBeInTheDocument();
  expect(screen.getByText('年間走行距離（km）')).toBeInTheDocument();
  expect(screen.getByText('運転者範囲')).toBeInTheDocument();
  expect(screen.getByText('次へ')).toBeInTheDocument();
});

// 運転者年齢が空の場合にアラートが表示されることを検証
test('shows alert when age is empty', async () => {
  const alertMock = jest.spyOn(window, 'alert').mockImplementation();
  render(<UserInfoForm data={emptyForm} onUpdate={() => {}} onNext={() => {}} />);
  await userEvent.click(screen.getByText('次へ'));
  expect(alertMock).toHaveBeenCalledWith('運転者年齢は18〜100を入力してください');
  alertMock.mockRestore();
});

// 運転者年齢が18未満の場合にアラートが表示されることを検証
test('shows alert when age is under 18', async () => {
  const alertMock = jest.spyOn(window, 'alert').mockImplementation();
  render(<UserInfoForm data={{ ...emptyForm, driverAge: '17' }} onUpdate={() => {}} onNext={() => {}} />);
  await userEvent.click(screen.getByText('次へ'));
  expect(alertMock).toHaveBeenCalledWith('運転者年齢は18〜100を入力してください');
  alertMock.mockRestore();
});

// 免許証色が未選択の場合にアラートが表示されることを検証
test('shows alert when license color not selected', async () => {
  const alertMock = jest.spyOn(window, 'alert').mockImplementation();
  render(<UserInfoForm data={{ ...emptyForm, driverAge: '30' }} onUpdate={() => {}} onNext={() => {}} />);
  await userEvent.click(screen.getByText('次へ'));
  expect(alertMock).toHaveBeenCalledWith('免許証色を選択してください');
  alertMock.mockRestore();
});

// 年間走行距離が30000を超える場合にアラートが表示されることを検証
test('shows alert when mileage exceeds 30000', async () => {
  const alertMock = jest.spyOn(window, 'alert').mockImplementation();
  render(<UserInfoForm data={{ ...emptyForm, driverAge: '30', licenseColor: 'BLUE', usageType: 'PRIVATE', annualMileage: '99999' }} onUpdate={() => {}} onNext={() => {}} />);
  await userEvent.click(screen.getByText('次へ'));
  expect(alertMock).toHaveBeenCalledWith('年間走行距離は0〜30000を入力してください');
  alertMock.mockRestore();
});

// 全フィールドが有効な場合にonNextが呼び出されることを検証
test('calls onNext when all fields valid', async () => {
  const onNext = jest.fn();
  render(<UserInfoForm data={{
    ...emptyForm,
    driverAge: '30', licenseColor: 'BLUE', usageType: 'PRIVATE',
    annualMileage: '10000', driverRange: 'SELF',
  }} onUpdate={() => {}} onNext={onNext} />);
  await userEvent.click(screen.getByText('次へ'));
  expect(onNext).toHaveBeenCalledTimes(1);
});
