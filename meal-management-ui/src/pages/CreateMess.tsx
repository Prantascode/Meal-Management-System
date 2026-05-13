import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import api from '../api/axiosInstance';
import {
  Home,
  User,
  Mail,
  Lock,
  Loader2,
  CheckCircle2,
  AlertCircle,
  X
} from 'lucide-react';

export const CreateMess = () => {
  const [formData, setFormData] = useState({
    name: '',
    email: '',
    password: '',
    messName: ''
  });

  const [loading, setLoading] = useState(false);

  const [message, setMessage] = useState<{
    type: 'success' | 'error';
    title: string;
    text: string;
  } | null>(null);

  const navigate = useNavigate();

  const getErrorMessage = (err: any) => {
    if (typeof err.response?.data === 'string') {
      return err.response.data;
    }

    if (err.response?.data?.message) {
      return err.response.data.message;
    }

    return 'Registration failed. Check if email already exists.';
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();

    setLoading(true);
    setMessage(null);

    try {
      await api.post('/auth/register-admin', formData);

      setMessage({
        type: 'success',
        title: 'Mess Created Successfully',
        text: 'Your mess has been created. Please login as Admin, then connect Gmail to send member passwords.'
      });

      setTimeout(() => {
        navigate('/login');
      }, 1500);
    } catch (err: any) {
      setMessage({
        type: 'error',
        title: 'Registration Failed',
        text: getErrorMessage(err)
      });
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="min-h-screen bg-slate-900 flex items-center justify-center p-4 font-sans">

      {/* Popup Message */}
      {message && (
        <div className="fixed top-6 left-4 right-4 md:left-auto md:right-6 md:w-full md:max-w-sm z-[9999] animate-in slide-in-from-right duration-300">
          <div
            className={`flex items-start gap-4 rounded-2xl border p-4 shadow-xl ${
              message.type === 'success'
                ? 'bg-emerald-50 border-emerald-200 text-emerald-800'
                : 'bg-red-50 border-red-200 text-red-800'
            }`}
          >
            <div
              className={`mt-0.5 rounded-full p-2 ${
                message.type === 'success'
                  ? 'bg-emerald-100 text-emerald-600'
                  : 'bg-red-100 text-red-600'
              }`}
            >
              {message.type === 'success' ? (
                <CheckCircle2 size={22} />
              ) : (
                <AlertCircle size={22} />
              )}
            </div>

            <div className="flex-1">
              <h3 className="font-bold">{message.title}</h3>
              <p className="text-sm opacity-90 mt-1">{message.text}</p>
            </div>

            <button
              type="button"
              onClick={() => setMessage(null)}
              className="rounded-lg p-1 hover:bg-white/60 transition"
            >
              <X size={18} />
            </button>
          </div>
        </div>
      )}

      <form
        onSubmit={handleSubmit}
        className="bg-white p-8 rounded-2xl shadow-xl w-full max-w-md border border-gray-100"
      >
        <div className="flex flex-col items-center mb-8">
          <div className="bg-blue-100 p-3 rounded-full mb-3">
            <Home className="text-blue-600" size={32} />
          </div>

          <h2 className="text-2xl font-extrabold text-gray-800">
            Register New Mess
          </h2>

          <p className="text-gray-500 text-sm">
            Create a workspace for your meal system
          </p>
        </div>

        <div className="space-y-4">
          <div className="relative">
            <Home className="absolute left-3 top-3 text-gray-400" size={18} />

            <input
              type="text"
              placeholder="Mess Name"
              className="w-full pl-10 pr-4 py-2 border rounded-lg focus:ring-2 focus:ring-blue-500 outline-none"
              value={formData.messName}
              onChange={e =>
                setFormData({ ...formData, messName: e.target.value })
              }
              required
            />
          </div>

          <div className="relative">
            <User className="absolute left-3 top-3 text-gray-400" size={18} />

            <input
              type="text"
              placeholder="Admin Name"
              className="w-full pl-10 pr-4 py-2 border rounded-lg focus:ring-2 focus:ring-blue-500 outline-none"
              value={formData.name}
              onChange={e =>
                setFormData({ ...formData, name: e.target.value })
              }
              required
            />
          </div>

          <div className="relative">
            <Mail className="absolute left-3 top-3 text-gray-400" size={18} />

            <input
              type="email"
              placeholder="Admin Email"
              className="w-full pl-10 pr-4 py-2 border rounded-lg focus:ring-2 focus:ring-blue-500 outline-none"
              value={formData.email}
              onChange={e =>
                setFormData({ ...formData, email: e.target.value })
              }
              required
            />
          </div>

          <div className="relative">
            <Lock className="absolute left-3 top-3 text-gray-400" size={18} />

            <input
              type="password"
              placeholder="Admin Password"
              className="w-full pl-10 pr-4 py-2 border rounded-lg focus:ring-2 focus:ring-blue-500 outline-none"
              value={formData.password}
              onChange={e =>
                setFormData({ ...formData, password: e.target.value })
              }
              required
            />
          </div>
        </div>

        <button
          type="submit"
          disabled={loading}
          className="w-full bg-blue-600 hover:bg-blue-700 disabled:bg-blue-400 text-white py-3 rounded-lg font-bold mt-8 transition-all flex items-center justify-center gap-2 shadow-lg shadow-blue-500/40 hover:shadow-xl hover:shadow-blue-500/50 active:scale-[0.98]"
        >
          {loading ? (
            <>
              <Loader2 className="animate-spin" size={20} />
              Creating...
            </>
          ) : (
            'Establish Mess'
          )}
        </button>
      </form>
    </div>
  );
};