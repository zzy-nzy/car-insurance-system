/**
 * 補償条件入力フォームコンポーネント
 *
 * ユーザーが自動車保険の補償条件を選択するためのフォームを提供する。
 * 対物補償、人身傷害、弁護士特約、ロードサービスの有無・金額を選択する。
 * 対人補償は「無制限」で固定とする。
 */
import React from 'react';
import { FormData, PropertyDamageLimit, PersonalInjuryAmount } from '../types';

/** CoverageFormコンポーネントのProps */
interface Props { data: FormData; onUpdate: (d: Partial<FormData>) => void; onNext: () => void; onBack: () => void; }

/**
 * 補償条件入力フォームを描画するコンポーネント
 *
 * @param data - 現在のフォームデータ
 * @param onUpdate - フォームデータ更新時のコールバック。更新された部分データを引数に取る。
 * @param onNext - 「次へ」ボタン押下時のコールバック
 * @param onBack - 「戻る」ボタン押下時のコールバック
 */
export default function CoverageForm({ data, onUpdate, onNext, onBack }: Props) {
  /**
   * 次へボタン押下時のハンドラ
   * 対物補償と人身傷害の選択を必須チェックし、未選択場合はアラートを表示する。
   * バリデーション通過後に次のステップへ進む。
   */
  const handleNext = () => {
    // 対物補償の選択チェック
    if (!data.propertyDamageLimit) { alert('対物補償を選択してください'); return; }
    // 人身傷害の選択チェック
    if (!data.personalInjuryAmount) { alert('人身傷害を選択してください'); return; }
    // 弁護士特約の選択チェック（null=未選択）
    if (data.lawyerOption === null) { alert('弁護士特約の有無を選択してください'); return; }
    // ロードサービスの選択チェック（null=未選択）
    if (data.roadService === null) { alert('ロードサービスの有無を選択してください'); return; }
    onNext();
  };

  return (
    <div className="page-card">
      <h2>補償条件</h2>
      {/* 対人補償は無制限で固定 */}
      <p>対人補償: 無制限（固定）</p>
      <label>対物補償</label>
      <select value={data.propertyDamageLimit} onChange={e => onUpdate({ propertyDamageLimit: e.target.value as PropertyDamageLimit })}>
        <option value="">選択してください</option>
        <option value="UNLIMITED">無制限</option>
        <option value="THIRTY_MILLION">3,000万円</option>
      </select>
      <label>人身傷害</label>
      <select value={data.personalInjuryAmount} onChange={e => onUpdate({ personalInjuryAmount: e.target.value as PersonalInjuryAmount })}>
        <option value="">選択してください</option>
        <option value="THIRTY_MILLION">3,000万円</option>
        <option value="FIFTY_MILLION">5,000万円</option>
        <option value="UNLIMITED">無制限</option>
      </select>
      <label>弁護士特約</label>
      <div className="radio-group">
        <label><input type="radio" name="lawyerOption" checked={data.lawyerOption === true} onChange={() => onUpdate({ lawyerOption: true })} /> あり</label>
        <label><input type="radio" name="lawyerOption" checked={data.lawyerOption === false} onChange={() => onUpdate({ lawyerOption: false })} /> なし</label>
      </div>
      <label>ロードサービス</label>
      <div className="radio-group">
        <label><input type="radio" name="roadService" checked={data.roadService === true} onChange={() => onUpdate({ roadService: true })} /> あり</label>
        <label><input type="radio" name="roadService" checked={data.roadService === false} onChange={() => onUpdate({ roadService: false })} /> なし</label>
      </div>
      <div className="button-group">
        <button className="btn-secondary" onClick={onBack}>戻る</button>
        <button className="btn-primary" onClick={handleNext}>次へ</button>
      </div>
    </div>
  );
}
