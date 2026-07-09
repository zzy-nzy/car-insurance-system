/**
 * 車両情報入力フォームコンポーネント
 *
 * 自動車保険見積のステップで、対象車両の情報を入力するフォームを提供する。
 * メーカー、車名、初度登録年月、車両タイプ、車両保険の有無を入力・選択する。
 */
import React from 'react';
import { FormData, VehicleType } from '../types';

/** VehicleInfoFormコンポーネントのProps */
interface Props { data: FormData; onUpdate: (d: Partial<FormData>) => void; onNext: () => void; onBack: () => void; }

/**
 * 車両情報入力フォームを描画するコンポーネント
 *
 * @param data - 現在のフォームデータ
 * @param onUpdate - フォームデータ更新時のコールバック。更新された部分データを引数に取る。
 * @param onNext - 「次へ」ボタン押下時のコールバック
 * @param onBack - 「戻る」ボタン押下時のコールバック
 */
export default function VehicleInfoForm({ data, onUpdate, onNext, onBack }: Props) {
  /**
   * 次へボタン押下時のハンドラ
   * 各入力項目のバリデーションを実施し、エラーがある場合はアラートを表示する。
   * メーカー・車名の文字数制限、初度登録年月の形式・未来日チェック、車両タイプ・車両保険の選択チェックを行う。
   * 全てのバリデーションを通過した場合に次のステップへ進む。
   */
  const handleNext = () => {
    // メーカーの入力チェック（50文字以内）
    if (!data.maker || data.maker.length > 50) { alert('メーカーを入力してください（50文字以内）'); return; }
    // 車名の入力チェック（50文字以内）
    if (!data.carName || data.carName.length > 50) { alert('車名を入力してください（50文字以内）'); return; }
    // 初度登録年月の形式チェック（YYYY-MM形式）
    if (!data.firstRegistrationYearMonth || !/^\d{4}-\d{2}$/.test(data.firstRegistrationYearMonth)) { alert('初度登録年月はYYYY-MM形式で入力してください'); return; }
    // 年月を数値に変換して未来日かどうかをチェック
    const [y, m] = data.firstRegistrationYearMonth.split('-').map(Number);
    const now = new Date(); const ym = y * 12 + m; const nowYm = now.getFullYear() * 12 + (now.getMonth() + 1);
    // 未来の年月は入力不可
    if (ym > nowYm) { alert('未来の年月は入力できません'); return; }
    // 車両タイプの選択チェック
    if (!data.vehicleType) { alert('車両タイプを選択してください'); return; }
    // 車両保険の有無の選択チェック（null=未選択）
    if (data.vehicleInsurance === null) { alert('車両保険の有無を選択してください'); return; }
    onNext();
  };

  return (
    <div className="page-card">
      <h2>車両情報</h2>
      <label>メーカー</label>
      <input type="text" maxLength={50} value={data.maker} onChange={e => onUpdate({ maker: e.target.value })} />
      <label>車名</label>
      <input type="text" maxLength={50} value={data.carName} onChange={e => onUpdate({ carName: e.target.value })} />
      <label>初度登録年月（YYYY-MM）</label>
      <input type="text" placeholder="2020-01" value={data.firstRegistrationYearMonth} onChange={e => onUpdate({ firstRegistrationYearMonth: e.target.value })} />
      <label>車両タイプ</label>
      <select value={data.vehicleType} onChange={e => onUpdate({ vehicleType: e.target.value as VehicleType })}>
        <option value="">選択してください</option>
        <option value="COMPACT">COMPACT</option>
        <option value="SEDAN">SEDAN</option>
        <option value="MINIVAN">MINIVAN</option>
        <option value="SUV">SUV</option>
        <option value="KEI">KEI</option>
      </select>
      <label>車両保険に加入しますか？</label>
      <div className="radio-group">
        <label><input type="radio" name="vehicleInsurance" checked={data.vehicleInsurance === true} onChange={() => onUpdate({ vehicleInsurance: true })} /> はい</label>
        <label><input type="radio" name="vehicleInsurance" checked={data.vehicleInsurance === false} onChange={() => onUpdate({ vehicleInsurance: false })} /> いいえ</label>
      </div>
      <div className="button-group">
        <button className="btn-secondary" onClick={onBack}>戻る</button>
        <button className="btn-primary" onClick={handleNext}>次へ</button>
      </div>
    </div>
  );
}
