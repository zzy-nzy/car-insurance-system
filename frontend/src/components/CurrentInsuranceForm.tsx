/**
 * 契約中保険入力フォームコンポーネント
 *
 * ユーザーが現在加入している自動車保険の情報を入力するためのフォームを提供する。
 * 現在保険に加入しているかどうかの選択、および加入している場合の等級・事故有係数期間を入力する。
 */
import React from 'react';
import { FormData } from '../types';

/** CurrentInsuranceFormコンポーネントのProps */
interface Props { data: FormData; onUpdate: (d: Partial<FormData>) => void; onNext: () => void; onBack: () => void; }

/**
 * 契約中保険入力フォームを描画するコンポーネント
 *
 * @param data - 現在のフォームデータ
 * @param onUpdate - フォームデータ更新時のコールバック。更新された部分データを引数に取る。
 * @param onNext - 「次へ」ボタン押下時のコールバック
 * @param onBack - 「戻る」ボタン押下時のコールバック
 */
export default function CurrentInsuranceForm({ data, onUpdate, onNext, onBack }: Props) {
  /**
   * 次へボタン押下時のハンドラ
   * 現在保険に加入している場合、等級（1〜20）と事故有係数期間（0〜6）のバリデーションを実施する。
   * バリデーション通過後に次のステップへ進む。
   */
  const handleNext = () => {
    // 現在保険に加入している場合のみ等級・事故有係数期間をチェック
    if (data.hasCurrentInsurance) {
      // 等級の範囲チェック（1〜20）
      if (!data.grade || +data.grade < 1 || +data.grade > 20) { alert('等級は1〜20を入力してください'); return; }
      // 事故有係数期間の範囲チェック（0〜6）
      if (data.accidentTerm === '' || +data.accidentTerm < 0 || +data.accidentTerm > 6) { alert('事故有係数期間は0〜6を入力してください'); return; }
    }
    onNext();
  };

  return (
    <div className="page-card">
      <h2>契約中保険</h2>
      <label>現在自動車保険に加入していますか？</label>
      <div className="radio-group">
        {/* 「はい」選択時は等級・事故有係数期間を空にリセット */}
        <label><input type="radio" name="hasInsurance" checked={data.hasCurrentInsurance === true} onChange={() => onUpdate({ hasCurrentInsurance: true, grade: '', accidentTerm: '' })} /> はい</label>
        {/* 「いいえ」選択時も等級・事故有係数期間を空にリセット */}
        <label><input type="radio" name="hasInsurance" checked={data.hasCurrentInsurance === false} onChange={() => onUpdate({ hasCurrentInsurance: false, grade: '', accidentTerm: '' })} /> いいえ</label>
      </div>
      {/* 現在保険に加入している場合のみ等級・事故有係数期間の入力欄を表示 */}
      {data.hasCurrentInsurance && (
        <>
          <label>等級（1〜20）</label>
          <input type="number" min={1} max={20} value={data.grade} onChange={e => onUpdate({ grade: e.target.value })} />
          <label>事故有係数期間（0〜6）</label>
          <input type="number" min={0} max={6} value={data.accidentTerm} onChange={e => onUpdate({ accidentTerm: e.target.value })} />
        </>
      )}
      <div className="button-group">
        <button className="btn-secondary" onClick={onBack}>戻る</button>
        <button className="btn-primary" onClick={handleNext}>次へ</button>
      </div>
    </div>
  );
}
