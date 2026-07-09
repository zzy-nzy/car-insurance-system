/**
 * アプリケーションルートコンポーネント
 * 
 * 自動車保険見積システムのメインコンポーネントです。
 * step状態に応じて各画面（トップページ、ユーザー情報入力、保険情報入力、
 * 車両情報入力、補償内容選択、確認画面、見積結果、管理画面）を切り替えます。
 */
import React, { useState } from 'react';
import { FormData, QuoteResponse } from './types';
import TopPage from './components/TopPage';
import UserInfoForm from './components/UserInfoForm';
import CurrentInsuranceForm from './components/CurrentInsuranceForm';
import VehicleInfoForm from './components/VehicleInfoForm';
import CoverageForm from './components/CoverageForm';
import ConfirmPage from './components/ConfirmPage';
import QuoteResult from './components/QuoteResult';
import AdminLogin from './components/AdminLogin';
import QuoteList from './components/QuoteList';
import QuoteDetail from './components/QuoteDetail';

/** フォームの初期値 */
const emptyForm: FormData = {
  driverAge: '', licenseColor: '', usageType: '', annualMileage: '',
  driverRange: '', hasCurrentInsurance: false, grade: '', accidentTerm: '',
  maker: '', carName: '', firstRegistrationYearMonth: '', vehicleType: '',
  vehicleInsurance: null, propertyDamageLimit: '', personalInjuryAmount: '',
  lawyerOption: null, roadService: null,
};

/**
 * アプリケーションルートコンポーネント
 * step状態で画面遷移を管理します。
 */
export default function App() {
  /** 画面ステップ（0:トップ, 1～6:見積フロー, -1～-3:管理画面） */
  const [step, setStep] = useState(0);
  /** 見積入力フォームデータ */
  const [form, setForm] = useState<FormData>({ ...emptyForm });
  /** 見積結果 */
  const [result, setResult] = useState<QuoteResponse | null>(null);
  /** 管理者認証トークン */
  const [adminToken, setAdminToken] = useState<string | null>(null);
  /** 詳細表示する見積番号 */
  const [detailQuoteNo, setDetailQuoteNo] = useState<string>('');

  /** フォームデータを部分的に更新する */
  const updateForm = (data: Partial<FormData>) => setForm(prev => ({ ...prev, ...data }));
  /** フォームと結果をリセットして最初の画面に戻る */
  const reset = () => { setForm({ ...emptyForm }); setResult(null); setStep(0); };
  /** トップページに戻り、管理者状態をクリアする */
  const goHome = () => { setStep(0); setAdminToken(null); setDetailQuoteNo(''); };

  return (
    <div className="app-container">
      <header className="app-header" onClick={goHome} style={{ cursor: 'pointer' }}>
        <h1>自動車保険見積サイト</h1>
      </header>
      <main className="app-main">
        {/* 管理者ログイン画面（step=-1 かつ 未認証） */}
        {step === -1 && !adminToken && (
          <AdminLogin onLogin={(token) => { setAdminToken(token); setStep(-2); }} />
        )}
        {/* 管理者向け見積一覧（step=-2 かつ 認証済み） */}
        {step === -2 && adminToken && (
          <QuoteList token={adminToken} onLogout={() => { setAdminToken(null); setStep(0); }}
            onSelectQuote={(qno) => { setDetailQuoteNo(qno); setStep(-3); }} />
        )}
        {/* 管理者向け見積詳細（step=-3） */}
        {step === -3 && adminToken && detailQuoteNo && (
          <QuoteDetail token={adminToken} quoteNo={detailQuoteNo} onBack={() => setStep(-2)} />
        )}

        {/* ---- ユーザー向け見積フロー ---- */}

        {/* トップページ */}
        {step === 0 && (
          <TopPage
            onStart={() => setStep(1)}
            onAdmin={() => setStep(-1)}
          />
        )}
        {/* ステップ1: 運転者情報入力 */}
        {step === 1 && (
          <UserInfoForm
            data={form}
            onUpdate={updateForm}
            onNext={() => setStep(2)}
          />
        )}
        {/* ステップ2: 現在の保険情報 */}
        {step === 2 && (
          <CurrentInsuranceForm
            data={form}
            onUpdate={updateForm}
            onNext={() => setStep(3)}
            onBack={() => setStep(1)}
          />
        )}
        {/* ステップ3: 車両情報入力 */}
        {step === 3 && (
          <VehicleInfoForm
            data={form}
            onUpdate={updateForm}
            onNext={() => setStep(4)}
            onBack={() => setStep(2)}
          />
        )}
        {/* ステップ4: 補償内容選択 */}
        {step === 4 && (
          <CoverageForm
            data={form}
            onUpdate={updateForm}
            onNext={() => setStep(5)}
            onBack={() => setStep(3)}
          />
        )}
        {/* ステップ5: 入力内容確認 */}
        {step === 5 && (
          <ConfirmPage
            data={form}
            onBack={() => setStep(4)}
            onComplete={(res) => { setResult(res); setStep(6); }}
          />
        )}
        {/* ステップ6: 見積結果表示 */}
        {step === 6 && result && <QuoteResult data={result} onReset={reset} />}
      </main>
    </div>
  );
}
