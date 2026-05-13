import React, { useState } from 'react';
import {
  Lock,
  Loader2,
  CheckCircle2,
  AlertCircle,
  ArrowLeft,
  Eye,
  EyeOff
} from 'lucide-react';
import { Link } from 'react-router-dom';
import api from '../api/axiosInstance';

export const UpdatePassword = () => {
  const [formData, setFormData] = useState({
    currentPassword: '',
    newPassword: ''
  });

  const [showCurrentPassword, setShowCurrentPassword] = useState(false);
  const [showNewPassword, setShowNewPassword] = useState(false);

  const [submitLoading, setSubmitLoading] = useState(false);

  const [message, setMessage] = useState<{
    type: 'success' | 'error';
    text: string;
  } | null>(null);

  const handleChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    setFormData(prev => ({
      ...prev,
      [e.target.name]: e.target.value
    }));
  };

  const getErrorMessage = (err: any) => {
    if (typeof err.response?.data === 'string') {
      return err.response.data;
    }

    if (err.response?.data?.message) {
      return err.response.data.message;
    }

    return 'Password update failed.';
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();

    setSubmitLoading(true);
    setMessage(null);

    try {
      await api.put('/members/me/password', formData);

      setFormData({
        currentPassword: '',
        newPassword: ''
      });

      setMessage({
        type: 'success',
        text: 'Password updated successfully.'
      });
    } catch (err: any) {
      setMessage({
        type: 'error',
        text: getErrorMessage(err)
      });
    } finally {
      setSubmitLoading(false);
    }
  };

  return (
    <div className="min-h-screen bg-slate-50 flex items-center justify-center p-4">
      <div className="w-full max-w-md bg-white rounded-3xl shadow-xl border border-slate-100 p-6">
        <div className="mb-6">
          <Link
            to="/dashboard"
            className="inline-flex items-center gap-2 text-sm font-bold text-slate-500 hover:text-indigo-600 mb-4"
          >
            <ArrowLeft size={17} />
            Back to Dashboard
          </Link>

          <div className="flex items-center gap-3">
            <div className="p-3 rounded-2xl bg-slate-800 text-white">
              <Lock size={28} />
            </div>

            <div>
              <h1 className="text-2xl font-bold text-slate-800">
                Update Password
              </h1>
              <p className="text-sm text-slate-500">
                Change your account password
              </p>
            </div>
          </div>
        </div>

        {message && (
          <div
            className={`mb-5 flex items-center gap-3 rounded-xl p-4 text-sm font-semibold ${
              message.type === 'success'
                ? 'bg-emerald-50 text-emerald-700 border border-emerald-100'
                : 'bg-red-50 text-red-700 border border-red-100'
            }`}
          >
            {message.type === 'success' ? (
              <CheckCircle2 size={20} />
            ) : (
              <AlertCircle size={20} />
            )}
            {message.text}
          </div>
        )}

        <form onSubmit={handleSubmit} className="space-y-4">
          <div>
            <label className="text-sm font-bold text-slate-600">
              Current Password
            </label>

            <div className="relative mt-2">
              <Lock
                size={18}
                className="absolute left-3 top-1/2 -translate-y-1/2 text-slate-400"
              />

              <input
                name="currentPassword"
                type={showCurrentPassword ? 'text' : 'password'}
                value={formData.currentPassword}
                onChange={handleChange}
                required
                placeholder="Enter current password"
                className="w-full border border-slate-200 rounded-xl py-3 pl-10 pr-11 outline-none focus:ring-2 focus:ring-indigo-500"
              />

              <button
                type="button"
                onClick={() => setShowCurrentPassword(prev => !prev)}
                className="absolute right-3 top-1/2 -translate-y-1/2 text-slate-400 hover:text-indigo-600"
              >
                {showCurrentPassword ? <EyeOff size={18} /> : <Eye size={18} />}
              </button>
            </div>
          </div>

          <div>
            <label className="text-sm font-bold text-slate-600">
              New Password
            </label>

            <div className="relative mt-2">
              <Lock
                size={18}
                className="absolute left-3 top-1/2 -translate-y-1/2 text-slate-400"
              />

              <input
                name="newPassword"
                type={showNewPassword ? 'text' : 'password'}
                value={formData.newPassword}
                onChange={handleChange}
                required
                minLength={6}
                placeholder="Enter new password"
                className="w-full border border-slate-200 rounded-xl py-3 pl-10 pr-11 outline-none focus:ring-2 focus:ring-indigo-500"
              />

              <button
                type="button"
                onClick={() => setShowNewPassword(prev => !prev)}
                className="absolute right-3 top-1/2 -translate-y-1/2 text-slate-400 hover:text-indigo-600"
              >
                {showNewPassword ? <EyeOff size={18} /> : <Eye size={18} />}
              </button>
            </div>
          </div>

          <button
            type="submit"
            disabled={submitLoading}
            className="w-full bg-slate-800 hover:bg-slate-900 disabled:bg-slate-500 text-white py-3 rounded-xl font-bold flex items-center justify-center gap-2 transition"
          >
            {submitLoading ? (
              <>
                <Loader2 className="animate-spin" size={20} />
                Updating...
              </>
            ) : (
              'Update Password'
            )}
          </button>
        </form>
      </div>
    </div>
  );
};