import React, { useEffect, useState } from 'react';
import api from '../api/axiosInstance';
import {
  CalendarDays,
  Check,
  CheckCircle2,
  Clock,
  Loader2,
  MessageSquare,
  Send,
  ShieldCheck,
  UtensilsCrossed,
  X,
  XCircle,
  AlertCircle,
  RefreshCw
} from 'lucide-react';

type MealType = 'BREAKFAST' | 'LUNCH' | 'DINNER';
type RequestStatus = 'PENDING' | 'APPROVED' | 'REJECTED';

interface MealRequest {
  id: number;
  memberId: number;
  memberName: string;
  messId: number;
  date: string;
  mealType: MealType;
  mealCount: number;
  status: RequestStatus;
  note?: string;
  requestedAt?: string;
  reviewedAt?: string;
  reviewedByName?: string;
}

export const MealRequests = () => {
  const [formData, setFormData] = useState({
    date: new Date().toISOString().split('T')[0],
    mealType: 'LUNCH' as MealType,
    mealCount: 1,
    note: ''
  });

  const [myRequests, setMyRequests] = useState<MealRequest[]>([]);
  const [pendingRequests, setPendingRequests] = useState<MealRequest[]>([]);

  const [loading, setLoading] = useState(true);
  const [submitLoading, setSubmitLoading] = useState(false);
  const [reviewLoadingId, setReviewLoadingId] = useState<number | null>(null);

  const [notification, setNotification] = useState<{
    type: 'success' | 'error';
    title: string;
    message: string;
  } | null>(null);

  const rawRole = localStorage.getItem('role') || '';
  const userRole = rawRole.replace('ROLE_', '').toUpperCase();

  const isAdmin = userRole === 'ADMIN';
  const isManager = userRole === 'MANAGER';
  const canReview = isAdmin || isManager;

  useEffect(() => {
    fetchData();
  }, []);

  const showNotification = (
    type: 'success' | 'error',
    title: string,
    message: string
  ) => {
    setNotification({ type, title, message });

    setTimeout(() => {
      setNotification(null);
    }, 3000);
  };

  const getErrorMessage = (err: any, fallback: string) => {
    if (typeof err.response?.data === 'string') {
      return err.response.data;
    }

    if (err.response?.data?.message) {
      return err.response.data.message;
    }

    return fallback;
  };

  const fetchData = async () => {
    try {
      setLoading(true);

      const myRes = await api.get('/meal-requests/my');
      setMyRequests(Array.isArray(myRes.data) ? myRes.data : []);

      if (canReview) {
        const pendingRes = await api.get('/meal-requests/pending');
        setPendingRequests(Array.isArray(pendingRes.data) ? pendingRes.data : []);
      }
    } catch (err: any) {
      showNotification(
        'error',
        'Failed to Load',
        getErrorMessage(err, 'Could not load meal requests.')
      );
    } finally {
      setLoading(false);
    }
  };

  const handleSubmitRequest = async (e: React.FormEvent) => {
    e.preventDefault();

    if (!formData.date) {
      showNotification('error', 'Date Required', 'Please select a meal date.');
      return;
    }

    if (formData.mealCount < 1) {
      showNotification('error', 'Invalid Meal Count', 'Meal count must be at least 1.');
      return;
    }

    try {
      setSubmitLoading(true);

      const payload = {
        date: formData.date,
        mealType: formData.mealType,
        mealCount: formData.mealCount,
        note: formData.note
      };

      await api.post('/meal-requests', payload);

      setFormData({
        date: new Date().toISOString().split('T')[0],
        mealType: 'LUNCH',
        mealCount: 1,
        note: ''
      });

      await fetchData();

      showNotification(
        'success',
        'Request Submitted',
        'Your meal request has been sent for approval.'
      );
    } catch (err: any) {
      showNotification(
        'error',
        'Submit Failed',
        getErrorMessage(err, 'Could not submit meal request.')
      );
    } finally {
      setSubmitLoading(false);
    }
  };

  const handleApprove = async (id: number) => {
    try {
      setReviewLoadingId(id);

      await api.put(`/meal-requests/${id}/approve`);

      await fetchData();

      showNotification(
        'success',
        'Request Approved',
        'The meal has been added to final meal entries.'
      );
    } catch (err: any) {
      showNotification(
        'error',
        'Approve Failed',
        getErrorMessage(err, 'Could not approve this request.')
      );
    } finally {
      setReviewLoadingId(null);
    }
  };

  const handleReject = async (id: number) => {
    try {
      setReviewLoadingId(id);

      await api.put(`/meal-requests/${id}/reject`);

      await fetchData();

      showNotification(
        'success',
        'Request Rejected',
        'The meal request has been rejected.'
      );
    } catch (err: any) {
      showNotification(
        'error',
        'Reject Failed',
        getErrorMessage(err, 'Could not reject this request.')
      );
    } finally {
      setReviewLoadingId(null);
    }
  };

  const getStatusStyle = (status: RequestStatus) => {
    if (status === 'APPROVED') {
      return 'bg-emerald-50 text-emerald-700 border-emerald-200';
    }

    if (status === 'REJECTED') {
      return 'bg-red-50 text-red-700 border-red-200';
    }

    return 'bg-amber-50 text-amber-700 border-amber-200';
  };

  const getMealTypeStyle = (type: MealType) => {
    if (type === 'BREAKFAST') {
      return 'bg-orange-50 text-orange-700 border-orange-200';
    }

    if (type === 'LUNCH') {
      return 'bg-blue-50 text-blue-700 border-blue-200';
    }

    return 'bg-purple-50 text-purple-700 border-purple-200';
  };

  if (loading) {
    return (
      <div className="flex h-screen items-center justify-center bg-slate-50">
        <Loader2 className="animate-spin text-indigo-600" size={48} />
      </div>
    );
  }

  return (
    <div className="p-6 max-w-7xl mx-auto space-y-8 animate-in fade-in duration-500">
      {notification && (
        <div className="fixed top-6 right-6 z-[9999] w-full max-w-sm animate-in slide-in-from-right duration-300">
          <div
            className={`flex items-start gap-4 rounded-2xl border p-4 shadow-xl ${
              notification.type === 'success'
                ? 'bg-emerald-50 border-emerald-200 text-emerald-800'
                : 'bg-red-50 border-red-200 text-red-800'
            }`}
          >
            <div
              className={`mt-0.5 rounded-full p-2 ${
                notification.type === 'success'
                  ? 'bg-emerald-100 text-emerald-600'
                  : 'bg-red-100 text-red-600'
              }`}
            >
              {notification.type === 'success' ? (
                <CheckCircle2 size={22} />
              ) : (
                <AlertCircle size={22} />
              )}
            </div>

            <div className="flex-1">
              <h3 className="font-bold">{notification.title}</h3>
              <p className="text-sm opacity-90">{notification.message}</p>
            </div>

            <button
              onClick={() => setNotification(null)}
              className="rounded-lg p-1 hover:bg-white/60 transition"
            >
              <X size={18} />
            </button>
          </div>
        </div>
      )}

      {/* Header */}
      <div className="flex flex-col md:flex-row justify-between items-start md:items-center gap-4">
        <div>
          <div className="flex items-center gap-3 mb-1">
            <div className="p-2 bg-indigo-100 rounded-xl text-indigo-700">
              <UtensilsCrossed size={26} />
            </div>

            <div>
              <h1 className="text-2xl font-black text-slate-800">
                Meal Requests
              </h1>
              <p className="text-sm text-slate-500 font-medium">
                Submit your meal request and wait for admin/manager approval.
              </p>
            </div>
          </div>
        </div>

        <button
          onClick={fetchData}
          className="flex items-center gap-2 rounded-xl border border-slate-200 bg-white px-4 py-2 text-sm font-bold text-slate-600 hover:bg-slate-50 transition"
        >
          <RefreshCw size={16} />
          Refresh
        </button>
      </div>

      {/* Top Summary Cards */}
      <div className="grid grid-cols-1 md:grid-cols-3 gap-5">
        <div className="rounded-2xl bg-gradient-to-br from-indigo-600 to-blue-700 p-6 text-white shadow-lg">
          <Clock className="mb-3 opacity-80" size={26} />
          <p className="text-xs font-black uppercase text-indigo-100">
            My Pending
          </p>
          <h2 className="text-3xl font-black">
            {myRequests.filter(r => r.status === 'PENDING').length}
          </h2>
        </div>

        <div className="rounded-2xl bg-gradient-to-br from-emerald-500 to-teal-700 p-6 text-white shadow-lg">
          <CheckCircle2 className="mb-3 opacity-80" size={26} />
          <p className="text-xs font-black uppercase text-emerald-100">
            My Approved
          </p>
          <h2 className="text-3xl font-black">
            {myRequests.filter(r => r.status === 'APPROVED').length}
          </h2>
        </div>

        <div className="rounded-2xl bg-gradient-to-br from-amber-500 to-orange-600 p-6 text-white shadow-lg">
          <ShieldCheck className="mb-3 opacity-80" size={26} />
          <p className="text-xs font-black uppercase text-amber-100">
            Pending Review
          </p>
          <h2 className="text-3xl font-black">
            {canReview ? pendingRequests.length : 0}
          </h2>
        </div>
      </div>

      <div className="grid grid-cols-1 lg:grid-cols-3 gap-8">
        {/* Submit Request Form */}
        <div className="lg:col-span-1">
          <div className="bg-white rounded-2xl border border-slate-100 shadow-xl p-6 sticky top-6">
            <div className="flex items-center gap-3 mb-6">
              <div className="p-2 bg-blue-100 rounded-xl text-blue-600">
                <Send size={20} />
              </div>

              <div>
                <h2 className="text-xl font-black text-slate-800">
                  Submit Meal
                </h2>
                <p className="text-xs text-slate-400 font-bold">
                  This will go to pending request.
                </p>
              </div>
            </div>

            <form onSubmit={handleSubmitRequest} className="space-y-5">
              {/* Date */}
              <div className="space-y-2">
                <label className="text-xs font-black text-slate-500 uppercase tracking-widest">
                  Meal Date
                </label>

                <div className="relative">
                  <CalendarDays
                    size={18}
                    className="absolute left-4 top-1/2 -translate-y-1/2 text-slate-400"
                  />

                  <input
                    type="date"
                    className="w-full rounded-2xl border border-slate-200 bg-slate-50 py-3.5 pl-12 pr-4 text-sm font-bold text-slate-700 outline-none transition focus:border-indigo-500 focus:ring-4 focus:ring-indigo-100"
                    value={formData.date}
                    onChange={e =>
                      setFormData({ ...formData, date: e.target.value })
                    }
                  />
                </div>
              </div>

              {/* Meal Type */}
              <div className="space-y-2">
                <label className="text-xs font-black text-slate-500 uppercase tracking-widest">
                  Meal Type
                </label>

                <div className="grid grid-cols-3 gap-2">
                  {(['BREAKFAST', 'LUNCH', 'DINNER'] as MealType[]).map(type => (
                    <button
                      key={type}
                      type="button"
                      onClick={() => setFormData({ ...formData, mealType: type })}
                      className={`rounded-2xl border py-3 text-[10px] font-black uppercase transition-all ${
                        formData.mealType === type
                          ? 'bg-indigo-600 text-white border-indigo-600 shadow-lg shadow-indigo-200'
                          : 'bg-slate-50 text-slate-500 border-slate-200 hover:bg-indigo-50 hover:text-indigo-600'
                      }`}
                    >
                      {type === 'BREAKFAST'
                        ? 'Breakfast'
                        : type === 'LUNCH'
                        ? 'Lunch'
                        : 'Dinner'}
                    </button>
                  ))}
                </div>
              </div>

              {/* Meal Count */}
              <div className="space-y-2">
                <label className="text-xs font-black text-slate-500 uppercase tracking-widest">
                  Meal Count
                </label>

                <div className="w-full max-w-full overflow-hidden flex items-center justify-between gap-2 bg-slate-50 border border-slate-200 rounded-2xl p-2 shadow-sm">
                  <button
                    type="button"
                    onClick={() =>
                      setFormData({
                        ...formData,
                        mealCount: Math.max(1, formData.mealCount - 1)
                      })
                    }
                    className="flex-shrink-0 w-10 h-10 rounded-xl bg-white border border-slate-200 text-slate-600 font-black text-xl hover:bg-red-50 hover:text-red-600 hover:border-red-200 transition"
                  >
                    -
                  </button>

                  <input
                    type="number"
                    min="1"
                    className="min-w-0 flex-1 w-full bg-transparent text-center text-2xl font-black text-slate-800 outline-none"
                    value={formData.mealCount}
                    onChange={e =>
                      setFormData({
                        ...formData,
                        mealCount: Math.max(1, parseInt(e.target.value) || 1)
                      })
                    }
                  />

                  <button
                    type="button"
                    onClick={() =>
                      setFormData({
                        ...formData,
                        mealCount: formData.mealCount + 1
                      })
                    }
                    className="flex-shrink-0 w-10 h-10 rounded-xl bg-white border border-slate-200 text-slate-600 font-black text-xl hover:bg-emerald-50 hover:text-emerald-600 hover:border-emerald-200 transition"
                  >
                    +
                  </button>
                </div>
              </div>

              {/* Note */}
              <div className="space-y-2">
                <label className="text-xs font-black text-slate-500 uppercase tracking-widest">
                  Note
                </label>

                <div className="relative">
                  <MessageSquare
                    size={18}
                    className="absolute left-4 top-4 text-slate-400"
                  />

                  <textarea
                    rows={3}
                    placeholder="Optional note..."
                    className="w-full resize-none rounded-2xl border border-slate-200 bg-slate-50 py-3.5 pl-12 pr-4 text-sm font-medium text-slate-700 outline-none transition focus:border-indigo-500 focus:ring-4 focus:ring-indigo-100"
                    value={formData.note}
                    onChange={e =>
                      setFormData({ ...formData, note: e.target.value })
                    }
                  />
                </div>
              </div>

              <button
                disabled={submitLoading}
                className="w-full rounded-2xl bg-gradient-to-r from-indigo-600 to-blue-600 py-4 text-white font-black shadow-lg shadow-indigo-200 transition hover:from-indigo-700 hover:to-blue-700 hover:shadow-xl active:scale-[0.98] disabled:from-slate-400 disabled:to-slate-500 flex items-center justify-center gap-2"
              >
                {submitLoading ? (
                  <>
                    <Loader2 className="animate-spin" size={20} />
                    Submitting...
                  </>
                ) : (
                  <>
                    <Send size={20} />
                    Submit Request
                  </>
                )}
              </button>
            </form>
          </div>
        </div>

        {/* My Requests */}
        <div className="lg:col-span-2">
          <div className="bg-white rounded-2xl border border-slate-100 shadow-sm overflow-hidden">
            <div className="p-6 border-b border-slate-100">
              <h2 className="text-lg font-black text-slate-800">
                My Meal Requests
              </h2>
              <p className="text-sm text-slate-500">
                Your submitted requests and review status.
              </p>
            </div>

            <div className="overflow-x-auto">
              <table className="w-full text-left">
                <thead className="bg-slate-50 text-slate-400 text-[10px] font-black uppercase tracking-widest">
                  <tr>
                    <th className="p-5">Date</th>
                    <th className="p-5">Type</th>
                    <th className="p-5 text-center">Qty</th>
                    <th className="p-5">Status</th>
                    <th className="p-5">Note</th>
                  </tr>
                </thead>

                <tbody className="divide-y divide-slate-100">
                  {myRequests.length === 0 ? (
                    <tr>
                      <td
                        colSpan={5}
                        className="p-8 text-center text-slate-500 font-medium"
                      >
                        No meal requests found.
                      </td>
                    </tr>
                  ) : (
                    myRequests.map(request => (
                      <tr key={request.id} className="hover:bg-slate-50 transition">
                        <td className="p-5 text-sm font-bold text-slate-600">
                          {request.date}
                        </td>

                        <td className="p-5">
                          <span
                            className={`inline-flex rounded-full border px-3 py-1 text-[10px] font-black ${getMealTypeStyle(
                              request.mealType
                            )}`}
                          >
                            {request.mealType}
                          </span>
                        </td>

                        <td className="p-5 text-center font-black text-slate-700">
                          {request.mealCount}
                        </td>

                        <td className="p-5">
                          <span
                            className={`inline-flex rounded-full border px-3 py-1 text-[10px] font-black ${getStatusStyle(
                              request.status
                            )}`}
                          >
                            {request.status}
                          </span>
                        </td>

                        <td className="p-5 text-sm text-slate-500 max-w-xs">
                          {request.note || 'N/A'}
                        </td>
                      </tr>
                    ))
                  )}
                </tbody>
              </table>
            </div>
          </div>
        </div>
      </div>

      {/* Admin/Manager Pending Review */}
      {canReview && (
        <div className="bg-white rounded-2xl border border-slate-100 shadow-sm overflow-hidden">
          <div className="p-6 border-b border-slate-100 flex items-center justify-between">
            <div>
              <h2 className="text-lg font-black text-slate-800">
                Pending Review
              </h2>
              <p className="text-sm text-slate-500">
                Approve requests to add them into the final meal entries.
              </p>
            </div>

            <div className="hidden md:flex items-center gap-2 rounded-xl bg-amber-50 px-4 py-2 text-amber-700 font-black text-sm">
              <Clock size={18} />
              {pendingRequests.length} Pending
            </div>
          </div>

          <div className="grid grid-cols-1 md:grid-cols-2 xl:grid-cols-3 gap-5 p-6">
            {pendingRequests.length === 0 ? (
              <div className="col-span-full rounded-2xl border border-dashed border-slate-200 p-10 text-center text-slate-500 font-medium">
                No pending meal requests.
              </div>
            ) : (
              pendingRequests.map(request => (
                <div
                  key={request.id}
                  className="rounded-2xl border border-slate-100 bg-slate-50/50 p-5 shadow-sm hover:shadow-md transition"
                >
                  <div className="flex items-start justify-between gap-3 mb-4">
                    <div>
                      <h3 className="font-black text-slate-800">
                        {request.memberName}
                      </h3>

                      <p className="text-xs text-slate-400 font-bold">
                        Requested for {request.date}
                      </p>
                    </div>

                    <span className="rounded-full bg-amber-50 border border-amber-200 px-3 py-1 text-[10px] font-black text-amber-700">
                      PENDING
                    </span>
                  </div>

                  <div className="space-y-3 mb-5">
                    <div className="flex justify-between text-sm">
                      <span className="text-slate-500 font-medium">Meal Type</span>
                      <span
                        className={`rounded-full border px-3 py-1 text-[10px] font-black ${getMealTypeStyle(
                          request.mealType
                        )}`}
                      >
                        {request.mealType}
                      </span>
                    </div>

                    <div className="flex justify-between text-sm">
                      <span className="text-slate-500 font-medium">Quantity</span>
                      <span className="font-black text-slate-800">
                        {request.mealCount}
                      </span>
                    </div>

                    <div className="rounded-xl bg-white border border-slate-100 p-3">
                      <p className="text-[10px] uppercase font-black tracking-widest text-slate-400 mb-1">
                        Note
                      </p>
                      <p className="text-sm text-slate-600">
                        {request.note || 'No note provided.'}
                      </p>
                    </div>
                  </div>

                  <div className="grid grid-cols-2 gap-3">
                    <button
                      disabled={reviewLoadingId === request.id}
                      onClick={() => handleReject(request.id)}
                      className="rounded-xl border border-red-200 bg-red-50 py-2.5 text-sm font-black text-red-600 hover:bg-red-100 transition flex items-center justify-center gap-2 disabled:opacity-60"
                    >
                      {reviewLoadingId === request.id ? (
                        <Loader2 className="animate-spin" size={18} />
                      ) : (
                        <>
                          <XCircle size={18} />
                          Reject
                        </>
                      )}
                    </button>

                    <button
                      disabled={reviewLoadingId === request.id}
                      onClick={() => handleApprove(request.id)}
                      className="rounded-xl border border-emerald-200 bg-emerald-600 py-2.5 text-sm font-black text-white hover:bg-emerald-700 transition flex items-center justify-center gap-2 disabled:opacity-60"
                    >
                      {reviewLoadingId === request.id ? (
                        <Loader2 className="animate-spin" size={18} />
                      ) : (
                        <>
                          <Check size={18} />
                          Approve
                        </>
                      )}
                    </button>
                  </div>
                </div>
              ))
            )}
          </div>
        </div>
      )}
    </div>
  );
};