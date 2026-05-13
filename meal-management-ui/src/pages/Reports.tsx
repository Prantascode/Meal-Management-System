import React, { useState, useEffect, useMemo } from 'react';
import api from '../api/axiosInstance';
import jsPDF from 'jspdf';
import autoTable from 'jspdf-autotable';
import { 
  FileText,
  Calculator,
  RefreshCw,
  Loader2,
  AlertCircle,
  CheckCircle2,
  TrendingUp,
  DollarSign,
  Users,
  Download,
  Info,
  X
} from 'lucide-react';

export const Reports = () => {
  const [reports, setReports] = useState<any[]>([]);
  const [month, setMonth] = useState(new Date().getMonth() + 1);
  const [year, setYear] = useState(new Date().getFullYear());
  const [loading, setLoading] = useState(false);

  const [notification, setNotification] = useState<{
    type: 'success' | 'error' | 'info';
    title: string;
    message: string;
  } | null>(null);

  const [showGenerateConfirm, setShowGenerateConfirm] = useState(false);

  const isAdminOrManager = (localStorage.getItem('role') || '').match(/ADMIN|MANAGER/);

  const summary = useMemo(() => {
    const data = Array.isArray(reports) ? reports : [];

    const totalMeals = data.reduce(
      (acc, curr) => acc + (Number(curr.totalMeals) || 0),
      0
    );

    const totalCost = data.reduce(
      (acc, curr) => acc + (Number(curr.totalExpense) || 0),
      0
    );

    const totalDeposit = data.reduce(
      (acc, curr) => acc + (Number(curr.totalDeposit) || 0),
      0
    );

    const rate = totalMeals > 0 ? totalCost / totalMeals : 0;

    return { totalMeals, totalCost, totalDeposit, rate };
  }, [reports]);

  const showNotification = (
    type: 'success' | 'error' | 'info',
    title: string,
    message: string
  ) => {
    setNotification({ type, title, message });

    setTimeout(() => {
      setNotification(null);
    }, 3000);
  };

  const fetchReport = async () => {
    try {
      setLoading(true);

      const res = await api.get(`/reports/${month}/${year}`);

      if (res.status === 204 || !res.data) {
        setReports([]);

        showNotification(
          'info',
          'No Report Found',
          'Report has not been generated for this month yet.'
        );
      } else {
        setReports(res.data);
      }
    } catch (err: any) {
      setReports([]);

      showNotification(
        'error',
        'Unable to Fetch Report',
        'The report may not exist yet or the server could not respond.'
      );
    } finally {
      setLoading(false);
    }
  };

  const handleGenerateReport = async () => {
    try {
      setLoading(true);
      setShowGenerateConfirm(false);

      const res = await api.post(`/reports/generate/${month}/${year}`);

      setReports(Array.isArray(res.data) ? res.data : []);

      showNotification(
        'success',
        reports.length > 0 ? 'Report Recalculated Successfully' : 'Report Generated Successfully',
        'The monthly report has been saved successfully.'
      );
    } catch (err: any) {
      showNotification(
        'error',
        'Report Generation Failed',
        'Failed to generate report. Ensure you have Admin or Manager permission.'
      );
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchReport();
  }, []);

  const downloadPDF = () => {
    const doc = new jsPDF();

    const monthName = new Date(0, month - 1).toLocaleString('en-US', {
      month: 'long'
    });

    doc.setFontSize(20);
    doc.setTextColor(40);
    doc.text('MESS MANAGEMENT SYSTEM', 14, 15);

    doc.setFontSize(12);
    doc.text(`Monthly Statement: ${monthName} ${year}`, 14, 22);

    autoTable(doc, {
      startY: 30,
      head: [['Total Meals', 'Total Expense', 'Meal Rate', 'Total Deposits']],
      body: [[
        summary.totalMeals,
        `Tk ${summary.totalCost.toFixed(2)}`,
        `Tk ${summary.rate.toFixed(4)}`,
        `Tk ${summary.totalDeposit.toFixed(2)}`
      ]],
      theme: 'grid',
      headStyles: { fillColor: [124, 58, 237] }
    });

    autoTable(doc, {
      startY: (doc as any).lastAutoTable.finalY + 10,
      head: [['Member Name', 'Meals', 'Cost', 'Deposit', 'Balance']],
      body: reports.map(r => [
        r.memberName,
        r.totalMeals,
        r.totalExpense.toFixed(2),
        r.totalDeposit.toFixed(2),
        {
          content: r.balance.toFixed(2),
          styles: {
            textColor: r.balance >= 0 ? [0, 128, 0] : [255, 0, 0]
          }
        }
      ])
    });

    doc.save(`Mess_Report_${monthName}_${year}.pdf`);

    showNotification(
      'success',
      'PDF Downloaded',
      `Mess report for ${monthName} ${year} has been downloaded.`
    );
  };

  const getNotificationStyle = () => {
    if (notification?.type === 'success') {
      return {
        wrapper: 'bg-emerald-50 border-emerald-200 text-emerald-800',
        iconBox: 'bg-emerald-100 text-emerald-600',
        icon: <CheckCircle2 size={22} />
      };
    }

    if (notification?.type === 'error') {
      return {
        wrapper: 'bg-red-50 border-red-200 text-red-800',
        iconBox: 'bg-red-100 text-red-600',
        icon: <AlertCircle size={22} />
      };
    }

    return {
      wrapper: 'bg-amber-50 border-amber-200 text-amber-800',
      iconBox: 'bg-amber-100 text-amber-600',
      icon: <Info size={22} />
    };
  };

  const notificationStyle = getNotificationStyle();

  return (
    <div className="p-6 max-w-7xl mx-auto space-y-6">

      {/* Popup Notification */}
      {notification && (
        <div className="fixed top-6 left-4 right-4 md:left-auto md:right-6 md:w-full md:max-w-sm z-[9999] animate-in slide-in-from-right duration-300">
          <div
            className={`flex items-start gap-4 rounded-2xl border p-4 shadow-xl ${notificationStyle.wrapper}`}
          >
            <div className={`mt-0.5 rounded-full p-2 ${notificationStyle.iconBox}`}>
              {notificationStyle.icon}
            </div>

            <div className="flex-1">
              <h3 className="font-bold">{notification.title}</h3>
              <p className="text-sm opacity-90 mt-1">{notification.message}</p>
            </div>

            <button
              type="button"
              onClick={() => setNotification(null)}
              className="rounded-lg p-1 hover:bg-white/60 transition"
            >
              <X size={18} />
            </button>
          </div>
        </div>
      )}

      {/* Header & Controls */}
      <div className="flex flex-col md:flex-row justify-between items-start md:items-center gap-4 bg-white p-4 rounded-2xl shadow-sm border border-slate-100">
        <div>
          <h1 className="text-2xl font-black flex items-center gap-2 text-slate-800">
            <FileText className="text-purple-600" />
            Monthly Audit
          </h1>

          <p className="text-slate-500 text-sm font-medium">
            Review and finalize mess accounts
          </p>
        </div>

        <div className="flex flex-wrap gap-2 items-center">
          <select
            value={month}
            onChange={e => setMonth(Number(e.target.value))}
            className="p-2.5 border rounded-xl bg-slate-50 font-bold text-slate-700 outline-none focus:ring-2 focus:ring-purple-500"
          >
            {Array.from({ length: 12 }, (_, i) => (
              <option key={i + 1} value={i + 1}>
                {new Date(0, i).toLocaleString('en-US', { month: 'long' })}
              </option>
            ))}
          </select>

          <input
            type="number"
            value={year}
            onChange={e => setYear(Number(e.target.value))}
            className="w-24 p-2.5 border rounded-xl bg-slate-50 font-bold text-slate-700 outline-none focus:ring-2 focus:ring-purple-500"
          />

          <button
            onClick={fetchReport}
            disabled={loading}
            className="p-2.5 hover:bg-slate-100 rounded-xl transition-colors text-slate-500 disabled:opacity-60"
          >
            <RefreshCw size={22} className={loading ? 'animate-spin' : ''} />
          </button>

          {isAdminOrManager && (
            <button
              onClick={() => setShowGenerateConfirm(true)}
              disabled={loading}
              className="bg-purple-600 hover:bg-purple-700 text-white px-5 py-2.5 rounded-xl font-bold flex items-center gap-2 shadow-lg shadow-purple-100 transition-all active:scale-95 disabled:opacity-50"
            >
              {loading ? (
                <Loader2 size={18} className="animate-spin" />
              ) : (
                <Calculator size={18} />
              )}

              {reports.length > 0 ? 'Recalculate' : 'Generate'}
            </button>
          )}

          {reports.length > 0 && (
            <button
              onClick={downloadPDF}
              className="bg-slate-800 hover:bg-slate-900 text-white px-5 py-2.5 rounded-xl font-bold flex items-center gap-2 transition-all"
            >
              <Download size={18} />
              Export
            </button>
          )}
        </div>
      </div>

      {/* Quick Summary Grid */}
      <div className="grid grid-cols-1 md:grid-cols-4 gap-4">
        <StatCard
          label="Meal Rate"
          value={`৳${summary.rate.toFixed(4)}`}
          icon={<TrendingUp />}
          color="text-purple-600"
          bg="bg-purple-50"
        />

        <StatCard
          label="Total Meals"
          value={summary.totalMeals}
          icon={<Users />}
          color="text-blue-600"
          bg="bg-blue-50"
        />

        <StatCard
          label="Total Cost"
          value={`৳${summary.totalCost.toLocaleString()}`}
          icon={<DollarSign />}
          color="text-rose-600"
          bg="bg-rose-50"
        />

        <StatCard
          label="Total Deposit"
          value={`৳${summary.totalDeposit.toLocaleString()}`}
          icon={<CheckCircle2 />}
          color="text-emerald-600"
          bg="bg-emerald-50"
        />
      </div>

      {/* Main Table */}
      <div className="bg-white rounded-2xl shadow-sm border border-slate-200 overflow-hidden">
        <div className="overflow-x-auto">
          <table className="w-full text-left">
            <thead className="bg-slate-50/50 border-b border-slate-100">
              <tr className="text-[10px] font-black text-slate-400 uppercase tracking-widest">
                <th className="px-6 py-4">Member</th>
                <th className="px-6 py-4">Meals</th>
                <th className="px-6 py-4">Individual Cost</th>
                <th className="px-6 py-4">Total Deposit</th>
                <th className="px-6 py-4 text-right">Final Balance</th>
              </tr>
            </thead>

            <tbody className="divide-y divide-slate-50">
              {reports.map((rep) => (
                <tr
                  key={rep.memberId}
                  className="hover:bg-slate-50/50 transition-colors"
                >
                  <td className="px-6 py-4">
                    <p className="font-bold text-slate-800">
                      {rep.memberName}
                    </p>
                    <p className="text-[10px] text-slate-400">
                      ID: #{rep.memberId}
                    </p>
                  </td>

                  <td className="px-6 py-4 font-medium text-slate-600">
                    {rep.totalMeals}
                  </td>

                  <td className="px-6 py-4 font-medium text-slate-600">
                    ৳{rep.totalExpense.toFixed(2)}
                  </td>

                  <td className="px-6 py-4 font-medium text-slate-600">
                    ৳{rep.totalDeposit.toFixed(2)}
                  </td>

                  <td className="px-6 py-4 text-right">
                    <span
                      className={`px-3 py-1.5 rounded-lg font-black text-sm ${
                        rep.balance >= 0
                          ? 'bg-emerald-50 text-emerald-600'
                          : 'bg-rose-50 text-rose-600'
                      }`}
                    >
                      {rep.balance >= 0 ? '+' : ''}
                      ৳{rep.balance.toFixed(2)}
                    </span>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>

        {reports.length === 0 && !loading && (
          <div className="p-20 text-center flex flex-col items-center gap-3">
            <div className="h-16 w-16 bg-slate-50 rounded-full flex items-center justify-center text-slate-300">
              <FileText size={32} />
            </div>

            <p className="text-slate-400 font-medium">
              No report generated for this month.
            </p>
          </div>
        )}

        {loading && (
          <div className="p-12 text-center flex flex-col items-center gap-3">
            <Loader2 className="animate-spin text-purple-600" size={36} />
            <p className="text-slate-400 font-medium">
              Loading report...
            </p>
          </div>
        )}
      </div>

      {/* Custom Generate/Recalculate Confirmation Modal */}
      {showGenerateConfirm && (
        <div className="fixed inset-0 z-[9998] flex items-center justify-center bg-black/40 px-4">
          <div className="w-full max-w-md rounded-2xl bg-white p-6 shadow-2xl border border-slate-200">
            <div className="flex items-center gap-3 mb-4">
              <div className="rounded-full bg-purple-100 p-3 text-purple-600">
                <Calculator size={24} />
              </div>

              <div>
                <h2 className="text-lg font-bold text-slate-800">
                  {reports.length > 0 ? 'Recalculate Report?' : 'Generate Report?'}
                </h2>

                <p className="text-sm text-slate-500">
                  This will calculate all member balances based on current meals and expenses.
                </p>
              </div>
            </div>

            <div className="rounded-xl bg-slate-50 border border-slate-100 p-4 mb-6">
              <p className="text-sm text-slate-500">Selected period:</p>

              <p className="font-bold text-slate-800">
                {new Date(0, month - 1).toLocaleString('en-US', { month: 'long' })} {year}
              </p>
            </div>

            <div className="flex justify-end gap-3">
              <button
                disabled={loading}
                onClick={() => setShowGenerateConfirm(false)}
                className="px-5 py-2.5 rounded-xl border border-slate-200 text-slate-600 font-bold hover:bg-slate-50 disabled:opacity-60 transition"
              >
                Cancel
              </button>

              <button
                disabled={loading}
                onClick={handleGenerateReport}
                className="px-5 py-2.5 rounded-xl bg-purple-600 text-white font-bold hover:bg-purple-700 disabled:bg-purple-400 transition flex items-center gap-2"
              >
                {loading ? (
                  <>
                    <Loader2 className="animate-spin" size={18} />
                    Processing...
                  </>
                ) : (
                  <>
                    <Calculator size={18} />
                    {reports.length > 0 ? 'Recalculate' : 'Generate'}
                  </>
                )}
              </button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
};

const StatCard = ({ label, value, icon, color, bg }: any) => (
  <div className="bg-white p-5 rounded-2xl border border-slate-100 shadow-sm flex items-center gap-4">
    <div className={`h-12 w-12 rounded-xl flex items-center justify-center ${bg} ${color}`}>
      {React.cloneElement(icon, { size: 24 })}
    </div>

    <div>
      <p className="text-slate-400 text-[10px] font-black uppercase tracking-wider">
        {label}
      </p>

      <h3 className="text-xl font-black text-slate-800">
        {value}
      </h3>
    </div>
  </div>
);