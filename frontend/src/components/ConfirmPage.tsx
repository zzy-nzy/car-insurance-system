/**
 * 入力確認ページコンポーネント
 *
 * ユーザーが入力した自動車保険見積情報を一覧表示し、確認させるページ。
 * ユーザーが「見積を作成する」ボタンを押すと、入力データをAPIリクエスト形式に変換し、
 * 見積作成APIを呼び出して結果を親コンポーネントに渡す。
 */
import React, { useState } from 'react';
import { FormData, QuoteRequest, QuoteResponse, PersonalInjuryAmount } from '../types';
import { createQuote } from '../api/client';

/** ConfirmPageコンポーネントのProps */
interface Props { data: FormData; onBack: () => void; onComplete: (res: QuoteResponse) => void; }

/**
 * 入力確認ページを描画するコンポーネント
 *
 * @param data - ユーザーが入力したフォームデータ
 * @param onBack - 「戻る」ボタン押下時のコールバック
 * @param onComplete - 見積作成成功時に呼び出されるコールバック。見積結果を引数に取る。
 */
export default function ConfirmPage({ data, onBack, onComplete }: Props) {
  // API通信中かどうかを示すローディングState
  const [loading, setLoading] = useState(false);

  /**
   * 見積作成ボタン押下時のハンドラ
   * フォームデータをAPIリクエスト形式に変換し、見積作成APIを呼び出す。
   * エラー発生時はエラーの種類に応じて適切なメッセージを表示する。
   */
  const handleSubmit = async () => {
    setLoading(true);
    try {
      // フォームデータをAPIリクエスト形式に変換
      const req: QuoteRequest = {
        driverAge: +data.driverAge,
        licenseColor: data.licenseColor as QuoteRequest['licenseColor'],
        usageType: data.usageType as QuoteRequest['usageType'],
        annualMileage: +data.annualMileage,
        driverRange: data.driverRange as QuoteRequest['driverRange'],
        hasCurrentInsurance: data.hasCurrentInsurance,
        // 現在保険に加入している場合のみ等級と事故有係数期間を送信
        grade: data.hasCurrentInsurance && data.grade ? +data.grade : undefined,
        accidentTerm: data.hasCurrentInsurance && data.accidentTerm !== '' ? +data.accidentTerm : undefined,
        maker: data.maker,
        carName: data.carName,
        firstRegistrationYearMonth: data.firstRegistrationYearMonth,
        vehicleType: data.vehicleType as QuoteRequest['vehicleType'],
        vehicleInsurance: data.vehicleInsurance!,
        propertyDamageLimit: data.propertyDamageLimit as QuoteRequest['propertyDamageLimit'],
        personalInjuryAmount: data.personalInjuryAmount as QuoteRequest['personalInjuryAmount'],
        lawyerOption: data.lawyerOption!,
        roadService: data.roadService!,
      };
      // 見積作成APIを呼び出し
      const res = await createQuote(req);
      // 見積結果を親コンポーネントに渡す
      onComplete(res);
    } catch (e: any) {
      console.error('Quote creation failed:', e);
      // サーバーからのレスポンスがある場合（バリデーションエラー等）
      if (e?.response) {
        const err = e.response.data;
        // バリデーションエラーのフィールド詳細を改行区切りで結合
        const fields = err.errors ? Object.entries(err.errors).map(([k, v]) => `${k}: ${v}`).join('\n') : '';
        alert(`[${err.code || 'ERROR'}] ${err.message || 'エラーが発生しました'}\n${fields}`);
      } else if (e?.request) {
        // リクエストは送信したがレスポンスがない場合（サーバー未起動等）
        alert('サーバーに接続できません。Backend が起動しているか確認してください。\n' + e.message);
      } else {
        // その他のエラー
        alert('エラーが発生しました: ' + e.message);
      }
    } finally {
      setLoading(false);
    }
  };

  /**
   * 確認画面の1行（ラベルと値）を描画するサブコンポーネント
   *
   * @param label - 表示するラベル名
   * @param value - 表示する値
   */
  const Row = ({ label, value }: { label: string; value: string }) => (
    <div className="confirm-row"><span className="confirm-label">{label}</span><span className="confirm-value">{value}</span></div>
  );

  return (
    <div className="page-card">
      <h2>入力確認</h2>
      <Row label="運転者年齢" value={data.driverAge} />
      <Row label="免許証色" value={data.licenseColor} />
      <Row label="使用目的" value={data.usageType} />
      <Row label="年間走行距離" value={`${data.annualMileage} km`} />
      <Row label="運転者範囲" value={data.driverRange} />
      <Row label="現在加入" value={data.hasCurrentInsurance ? 'あり' : 'なし'} />
      {/* 現在保険に加入している場合のみ等級と事故有係数期間を表示 */}
      {data.hasCurrentInsurance && <Row label="等級" value={data.grade} />}
      {data.hasCurrentInsurance && <Row label="事故有係数期間" value={data.accidentTerm} />}
      <Row label="メーカー" value={data.maker} />
      <Row label="車名" value={data.carName} />
      <Row label="初度登録年月" value={data.firstRegistrationYearMonth} />
      <Row label="車両タイプ" value={data.vehicleType} />
      <Row label="車両保険" value={data.vehicleInsurance === null ? '未選択' : data.vehicleInsurance ? 'あり' : 'なし'} />
      {/* 対物補償の制限額を表示名に変換 */}
      <Row label="対物補償" value={data.propertyDamageLimit === 'UNLIMITED' ? '無制限' : '3,000万円'} />
      {/* 人身傷害の金額を表示名に変換 */}
      <Row label="人身傷害" value={(() => { const labels: Record<string, string> = { 'THIRTY_MILLION': '3,000万円', 'FIFTY_MILLION': '5,000万円', 'UNLIMITED': '無制限' }; return labels[data.personalInjuryAmount as string] || ''; })()} />
      <Row label="弁護士特約" value={data.lawyerOption === null ? '未選択' : data.lawyerOption ? 'あり' : 'なし'} />
      <Row label="ロードサービス" value={data.roadService === null ? '未選択' : data.roadService ? 'あり' : 'なし'} />
      <div className="button-group">
        <button className="btn-secondary" onClick={onBack} disabled={loading}>戻る</button>
        <button className="btn-primary" onClick={handleSubmit} disabled={loading}>{loading ? '作成中...' : '見積を作成する'}</button>
      </div>
    </div>
  );
}
