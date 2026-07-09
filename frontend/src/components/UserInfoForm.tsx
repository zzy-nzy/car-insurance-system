/**
 * 使用者情報入力フォームコンポーネント
 *
 * 自動車保険見積の最初のステップで、運転者の基本情報を入力するフォームを提供する。
 * 運転者年齢、免許証色、使用目的、年間走行距離、運転者範囲を入力・選択する。
 */
import React from 'react';
import { FormData, LicenseColor, UsageType, DriverRange } from '../types';

/** UserInfoFormコンポーネントのProps */
interface Props { data: FormData; onUpdate: (d: Partial<FormData>) => void; onNext: () => void; }

/**
 * 使用者情報入力フォームを描画するコンポーネント
 *
 * @param data - 現在のフォームデータ
 * @param onUpdate - フォームデータ更新時のコールバック。更新された部分データを引数に取る。
 * @param onNext - 「次へ」ボタン押下時のコールバック
 */
export default function UserInfoForm({ data, onUpdate, onNext }: Props) {
  /**
   * 次へボタン押下時のハンドラ
   * 各入力項目のバリデーションを実施し、エラーがある場合はアラートを表示する。
   * 全てのバリデーションを通過した場合に次のステップへ進む。
   */
  const handleNext = () => {
    // 運転者年齢のバリデーション（18〜100歳）
    if (!data.driverAge || +data.driverAge < 18 || +data.driverAge > 100) { alert('運転者年齢は18〜100を入力してください'); return; }
    // 免許証色の選択チェック
    if (!data.licenseColor) { alert('免許証色を選択してください'); return; }
    // 使用目的の選択チェック
    if (!data.usageType) { alert('使用目的を選択してください'); return; }
    // 年間走行距離のバリデーション（0〜30000km）
    if (!data.annualMileage || +data.annualMileage < 0 || +data.annualMileage > 30000) { alert('年間走行距離は0〜30000を入力してください'); return; }
    // 運転者範囲の選択チェック
    if (!data.driverRange) { alert('運転者範囲を選択してください'); return; }
    onNext();
  };

  return (
    <div className="page-card">
      <h2>使用者情報</h2>
      <label>運転者年齢（18〜100）</label>
      <input type="number" min={18} max={100} value={data.driverAge} onChange={e => onUpdate({ driverAge: e.target.value })} />
      <label>免許証色</label>
      <select value={data.licenseColor} onChange={e => onUpdate({ licenseColor: e.target.value as LicenseColor })}>
        <option value="">選択してください</option>
        <option value="GOLD">GOLD</option>
        <option value="BLUE">BLUE</option>
        <option value="GREEN">GREEN</option>
      </select>
      <label>使用目的</label>
      <select value={data.usageType} onChange={e => onUpdate({ usageType: e.target.value as UsageType })}>
        <option value="">選択してください</option>
        <option value="PRIVATE">PRIVATE</option>
        <option value="COMMUTE">COMMUTE</option>
        <option value="BUSINESS">BUSINESS</option>
      </select>
      <label>年間走行距離（km）</label>
      <input type="number" min={0} max={30000} value={data.annualMileage} onChange={e => onUpdate({ annualMileage: e.target.value })} />
      <label>運転者範囲</label>
      <select value={data.driverRange} onChange={e => onUpdate({ driverRange: e.target.value as DriverRange })}>
        <option value="">選択してください</option>
        <option value="SELF">SELF</option>
        <option value="COUPLE">COUPLE</option>
        <option value="FAMILY">FAMILY</option>
        <option value="ANYONE">ANYONE</option>
      </select>
      <button className="btn-primary" onClick={handleNext}>次へ</button>
    </div>
  );
}
