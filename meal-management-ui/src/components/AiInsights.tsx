import React, { useState } from 'react';
import api from '../api/axiosInstance';
import {
  Sparkles,
  BrainCircuit,
  Loader2,
  MessageSquareQuote,
  RefreshCw,
  Wallet,
  Utensils,
  Users,
  TrendingUp
} from 'lucide-react';
import ReactMarkdown, { type Components } from 'react-markdown';

interface Props {
  mealHistory: any[];
  deposits: any[];
  expenses: any[];
  members: any[];
}

export const AiInsights = ({
  mealHistory,
  deposits,
  expenses,
  members
}: Props) => {
  const [insight, setInsight] = useState<string | null>(null);
  const [loading, setLoading] = useState(false);

  const totalMeals = mealHistory.reduce(
    (sum, meal) => sum + Number(meal.mealCount || 0),
    0
  );

  const totalDeposits = deposits.reduce(
    (sum, deposit) => sum + Number(deposit.amount || 0),
    0
  );

  const totalExpenses = expenses.reduce(
    (sum, expense) => sum + Number(expense.amount || expense.cost || 0),
    0
  );

  const memberCount = members.length;

  const mealRate =
    totalMeals > 0 ? Number((totalExpenses / totalMeals).toFixed(2)) : 0;

  const balance = totalDeposits - totalExpenses;

  const generateAiReport = async () => {
    setLoading(true);

    try {
      const dataSummary = {
        totalMeals,
        totalDeposits,
        totalExpenses,
        memberCount,
        mealRate,
        balance
      };

      const response = await api.post('/ai/analyze', {
        context: dataSummary
      });

      setInsight(response.data.analysis);
    } catch (err: any) {
      const errorMsg =
        err.response?.data?.analysis || 'Failed to connect to AI Assistant.';

      setInsight(`❌ **Error:** ${errorMsg}`);
    } finally {
      setLoading(false);
    }
  };

  const MarkdownComponents: Components = {
    h1: ({ children }) => (
      <h1 className="text-xl font-black text-amber-300 mt-4 mb-3">
        {children}
      </h1>
    ),

    h2: ({ children }) => (
      <h2 className="text-lg font-black text-amber-300 mt-4 mb-2">
        {children}
      </h2>
    ),

    h3: ({ children }) => (
      <h3 className="text-base font-bold text-amber-300 mt-4 mb-2">
        {children}
      </h3>
    ),

    strong: ({ children }) => (
      <strong className="text-white font-extrabold">
        {children}
      </strong>
    ),

    p: ({ children }) => (
      <p className="text-slate-200 leading-relaxed mb-3">
        {children}
      </p>
    ),

    ul: ({ children }) => (
      <ul className="list-disc list-inside space-y-1 my-3 text-slate-300">
        {children}
      </ul>
    ),

    ol: ({ children }) => (
      <ol className="list-decimal list-inside space-y-1 my-3 text-slate-300">
        {children}
      </ol>
    ),

    li: ({ children }) => (
      <li className="ml-2 leading-relaxed">
        {children}
      </li>
    ),

    hr: () => (
      <hr className="border-white/10 my-4" />
    )
  };

  return (
    <div className="bg-gradient-to-br from-indigo-950 via-slate-950 to-purple-950 rounded-3xl p-6 text-white shadow-xl relative overflow-hidden border border-white/10 transition-all duration-500">
      <div className="absolute top-0 right-0 p-4 opacity-5 rotate-12 pointer-events-none">
        <BrainCircuit size={150} />
      </div>

      <div className="absolute -bottom-20 -left-20 w-56 h-56 bg-indigo-500/20 rounded-full blur-3xl pointer-events-none" />
      <div className="absolute -top-20 -right-20 w-56 h-56 bg-purple-500/20 rounded-full blur-3xl pointer-events-none" />

      <div className="relative z-10">
        <div className="flex flex-col lg:flex-row lg:items-start lg:justify-between gap-5 mb-6">
          <div className="flex items-center gap-3">
            <div className="p-3 bg-white/10 rounded-2xl backdrop-blur-md border border-white/10">
              <Sparkles className="text-amber-400" size={26} />
            </div>

            <div>
              <h2 className="text-xl font-black tracking-tight">
                AI Mess Strategist
              </h2>

              <p className="text-indigo-200/60 text-[10px] uppercase font-bold tracking-widest">
                Powered by Gemini 2.5 Flash
              </p>
            </div>
          </div>

          <div className="grid grid-cols-2 md:grid-cols-4 gap-3">
            <div className="bg-white/5 border border-white/10 rounded-2xl p-3">
              <div className="flex items-center gap-2 text-indigo-200 text-[10px] font-bold uppercase">
                <Utensils size={13} />
                Meals
              </div>
              <p className="text-lg font-black mt-1">{totalMeals}</p>
            </div>

            <div className="bg-white/5 border border-white/10 rounded-2xl p-3">
              <div className="flex items-center gap-2 text-emerald-200 text-[10px] font-bold uppercase">
                <Wallet size={13} />
                Deposits
              </div>
              <p className="text-lg font-black mt-1">৳{totalDeposits}</p>
            </div>

            <div className="bg-white/5 border border-white/10 rounded-2xl p-3">
              <div className="flex items-center gap-2 text-red-200 text-[10px] font-bold uppercase">
                <TrendingUp size={13} />
                Expenses
              </div>
              <p className="text-lg font-black mt-1">৳{totalExpenses}</p>
            </div>

            <div className="bg-white/5 border border-white/10 rounded-2xl p-3">
              <div className="flex items-center gap-2 text-amber-200 text-[10px] font-bold uppercase">
                <Users size={13} />
                Members
              </div>
              <p className="text-lg font-black mt-1">{memberCount}</p>
            </div>
          </div>
        </div>

        {!insight && !loading && (
          <div className="flex flex-col md:flex-row md:items-center justify-between gap-6">
            <div>
              <p className="text-slate-300 text-sm max-w-2xl leading-relaxed">
                Analyze your current monthly meals, deposits, expenses, members,
                meal rate, and balance to get a smart budget health report.
              </p>

              <div className="flex flex-wrap gap-2 mt-4">
                <span className="px-3 py-1 rounded-full bg-white/10 border border-white/10 text-xs font-bold text-slate-200">
                  Meal Rate: ৳{mealRate}
                </span>

                <span
                  className={`px-3 py-1 rounded-full border text-xs font-bold ${
                    balance >= 0
                      ? 'bg-emerald-500/10 border-emerald-400/20 text-emerald-300'
                      : 'bg-red-500/10 border-red-400/20 text-red-300'
                  }`}
                >
                  Balance: ৳{balance}
                </span>
              </div>
            </div>

            <button
              onClick={generateAiReport}
              className="bg-white text-indigo-950 hover:bg-indigo-50 px-6 py-3 rounded-2xl font-black transition-all flex items-center justify-center gap-2 shadow-lg active:scale-95"
            >
              <Sparkles size={18} />
              Analyze Status
            </button>
          </div>
        )}

        {loading && (
          <div className="flex flex-col items-center py-10 gap-3">
            <Loader2 className="animate-spin text-amber-400" size={42} />

            <p className="text-indigo-200 text-sm font-medium animate-pulse">
              Consulting the Mess Strategist...
            </p>

            <p className="text-white/40 text-xs">
              Checking meals, deposits, expenses, and monthly balance.
            </p>
          </div>
        )}

        {insight && (
          <div className="animate-in fade-in zoom-in-95 duration-500">
            <div className="bg-white/5 border border-white/10 p-5 rounded-2xl backdrop-blur-sm max-h-[430px] overflow-y-auto custom-scrollbar">
              <div className="flex items-start gap-3">
                <MessageSquareQuote
                  className="text-amber-400 shrink-0 mt-1"
                  size={22}
                />

                <div className="text-sm text-slate-100 leading-relaxed prose prose-invert prose-sm max-w-none">
                  <ReactMarkdown components={MarkdownComponents}>
                    {insight}
                  </ReactMarkdown>
                </div>
              </div>
            </div>

            <div className="flex flex-col sm:flex-row sm:justify-between sm:items-center gap-3 mt-4">
              <button
                onClick={() => setInsight(null)}
                className="text-xs text-indigo-300 hover:text-white transition-colors font-bold underline underline-offset-4 w-fit"
              >
                Dismiss Analysis
              </button>

              <button
                onClick={generateAiReport}
                disabled={loading}
                className="text-xs text-white bg-white/10 hover:bg-white/15 px-4 py-2 rounded-xl font-bold transition-all flex items-center gap-2 w-fit"
              >
                <RefreshCw size={13} />
                Regenerate
              </button>
            </div>
          </div>
        )}
      </div>
    </div>
  );
};