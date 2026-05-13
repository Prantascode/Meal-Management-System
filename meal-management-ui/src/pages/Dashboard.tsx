import React, { useState, useEffect } from 'react';
import {
  Users,
  Utensils,
  Wallet,
  Landmark,
  FileBarChart,
  ArrowRight,
  RefreshCw,
  CheckCircle2,
  XCircle,
  Mail,
  User,
  Lock,
  X,
  ShieldCheck,
  Phone,
  Home,
  ClipboardList
} from 'lucide-react';
import { Link } from 'react-router-dom';
import { motion, AnimatePresence } from 'framer-motion';
import { AiInsights } from '../components/AiInsights';
import api from '../api/axiosInstance';

type DashboardData = {
  meals: any[];
  deposits: any[];
  expenses: any[];
  members: any[];
};

export const Dashboard = () => {
  const [currentUser, setCurrentUser] = useState<any>(null);
  const [profileLoading, setProfileLoading] = useState(true);
  const [showProfile, setShowProfile] = useState(false);

  const [dashboardData, setDashboardData] = useState<DashboardData>({
    meals: [],
    deposits: [],
    expenses: [],
    members: []
  });

  const [dataStatus, setDataStatus] = useState<'loading' | 'success' | 'error'>(
    'loading'
  );

  const normalizedRole =
    currentUser?.role?.replace('ROLE_', '').toUpperCase() || 'USER';

  const isAdmin = normalizedRole === 'ADMIN';
  const isManager = normalizedRole === 'MANAGER';
  const isMember = normalizedRole === 'MEMBER';

  const canAccessAI = isAdmin || isManager;

  const canViewMembers = isAdmin || isManager || isMember;
  const canViewExpenses = isAdmin || isManager || isMember;

  const handleConnectGmail = async () => {
    try {
      const res = await api.get('/google/oauth/url');
      window.location.href = res.data;
    } catch (err: any) {
      console.error(
        'Failed to connect Gmail:',
        err.response?.status,
        err.response?.data
      );

      alert('Failed to connect Gmail. Please login again and try.');
    }
  };

  useEffect(() => {
    const fetchCurrentUser = async () => {
      try {
        const res = await api.get('/members/me');
        setCurrentUser(res.data);
      } catch (err: any) {
        console.error(
          'Failed to fetch profile:',
          err.response?.status,
          err.response?.data
        );
      } finally {
        setProfileLoading(false);
      }
    };

    fetchCurrentUser();
  }, []);

  useEffect(() => {
    const fetchContext = async () => {
      if (profileLoading) return;

      if (!canAccessAI) {
        setDataStatus('success');
        return;
      }

      setDataStatus('loading');

      try {
        const now = new Date();
        const year = now.getFullYear();
        const month = now.getMonth();

        const firstDay = new Date(year, month, 1)
          .toISOString()
          .split('T')[0];

        const lastDay = new Date(year, month + 1, 0)
          .toISOString()
          .split('T')[0];

        const startDateTime = `${firstDay}T00:00:00`;
        const endDateTime = `${lastDay}T23:59:59`;

        const [mealsRes, depositsRes, expensesRes, membersRes] =
          await Promise.all([
            api.get(`/meals?startDate=${firstDay}&endDate=${lastDay}`),

            api.get(
              `/deposit/total/range?startDate=${startDateTime}&endDate=${endDateTime}`
            ),

            api.get(`/expenses?startDate=${firstDay}&endDate=${lastDay}`),

            api.get('/members')
          ]);

        setDashboardData({
          meals: Array.isArray(mealsRes.data) ? mealsRes.data : [],
          deposits: Array.isArray(depositsRes.data) ? depositsRes.data : [],
          expenses: Array.isArray(expensesRes.data) ? expensesRes.data : [],
          members: Array.isArray(membersRes.data) ? membersRes.data : []
        });

        setDataStatus('success');
      } catch (err: any) {
        console.error(
          'Dashboard Sync Error:',
          err.response?.status,
          err.response?.data
        );

        setDataStatus('error');
      }
    };

    fetchContext();
  }, [profileLoading, canAccessAI]);

  const stats = [
    ...(canViewMembers
      ? [
          {
            title: 'Total Members',
            icon: <Users />,
            link: '/members',
            color: 'bg-blue-600',
            desc: isMember ? 'View mess members' : 'Manage mess members'
          }
        ]
      : []),

    {
      title: 'Meal Entries',
      icon: <Utensils />,
      link: '/meals',
      color: 'bg-green-600',
      desc: isMember ? 'View approved meals' : 'Manage final meal entries'
    },

    {
      title: 'Meal Requests',
      icon: <ClipboardList />,
      link: '/meal-requests',
      color: 'bg-cyan-600',
      desc: isMember
        ? 'Submit meal for approval'
        : 'Review member meal requests'
    },

    {
      title: 'Member Deposits',
      icon: <Landmark />,
      link: '/deposits',
      color: 'bg-indigo-600',
      desc: isMember ? 'View deposits' : 'Manage deposits'
    },

    ...(canViewExpenses
      ? [
          {
            title: 'Expenses',
            icon: <Wallet />,
            link: '/expenses',
            color: 'bg-orange-500',
            desc: isMember ? 'View expenses' : 'Log bazaar costs'
          }
        ]
      : []),

    {
      title: 'Monthly Reports',
      icon: <FileBarChart />,
      link: '/reports',
      color: 'bg-purple-600',
      desc: 'Financial summaries'
    }
  ];

  return (
    <div className="p-6 max-w-7xl mx-auto animate-in fade-in duration-500 relative">
      {/* My Profile Modal */}
      <AnimatePresence>
        {showProfile && (
          <motion.div
            className="fixed inset-0 z-[9999] bg-black/40 backdrop-blur-sm flex items-center justify-center p-4"
            initial={{ opacity: 0 }}
            animate={{ opacity: 1 }}
            exit={{ opacity: 0 }}
            transition={{ duration: 0.25 }}
            onClick={() => setShowProfile(false)}
          >
            <motion.div
              className="w-full max-w-md bg-white rounded-3xl shadow-2xl border border-slate-100 overflow-hidden"
              initial={{ opacity: 0, scale: 0.85, y: 40 }}
              animate={{ opacity: 1, scale: 1, y: 0 }}
              exit={{ opacity: 0, scale: 0.9, y: 30 }}
              transition={{ duration: 0.3, ease: 'easeOut' }}
              onClick={e => e.stopPropagation()}
            >
              <div className="bg-gradient-to-r from-indigo-600 to-blue-600 p-6 text-white relative">
                <button
                  type="button"
                  onClick={() => setShowProfile(false)}
                  className="absolute top-4 right-4 p-2 rounded-full bg-white/20 hover:bg-white/30 transition"
                >
                  <X size={18} />
                </button>

                <div className="flex items-center gap-4">
                  <motion.div
                    className="w-16 h-16 rounded-2xl bg-white/20 flex items-center justify-center border border-white/30"
                    initial={{ rotate: -12, scale: 0.8 }}
                    animate={{ rotate: 0, scale: 1 }}
                    transition={{ delay: 0.15, duration: 0.25 }}
                  >
                    <User size={32} />
                  </motion.div>

                  <div>
                    <h2 className="text-xl font-bold">
                      {currentUser?.name || 'User'}
                    </h2>

                    <p className="text-sm text-white/80">
                      {currentUser?.email || 'No email found'}
                    </p>

                    <span className="inline-flex mt-2 px-3 py-1 rounded-full bg-white/20 text-xs font-bold uppercase tracking-wider">
                      {normalizedRole}
                    </span>
                  </div>
                </div>
              </div>

              <div className="p-6 space-y-4">
                <div className="grid gap-3">
                  {[
                    {
                      icon: <Mail className="text-indigo-500" size={18} />,
                      label: 'Email',
                      value: currentUser?.email || 'N/A'
                    },
                    {
                      icon: <Phone className="text-green-500" size={18} />,
                      label: 'Phone',
                      value: currentUser?.phone || 'N/A'
                    },
                    {
                      icon: (
                        <ShieldCheck className="text-purple-500" size={18} />
                      ),
                      label: 'Role',
                      value: normalizedRole
                    },
                    {
                      icon: <Home className="text-orange-500" size={18} />,
                      label: 'Mess',
                      value: currentUser?.messName || 'N/A',
                      subValue: `Mess ID: ${currentUser?.messId || 'N/A'}`
                    }
                  ].map((item, index) => (
                    <motion.div
                      key={item.label}
                      className="flex items-center gap-3 p-3 rounded-xl bg-slate-50 border border-slate-100"
                      initial={{ opacity: 0, x: -20 }}
                      animate={{ opacity: 1, x: 0 }}
                      transition={{
                        delay: 0.08 * index,
                        duration: 0.25
                      }}
                    >
                      {item.icon}

                      <div>
                        <p className="text-xs text-slate-500 font-semibold">
                          {item.label}
                        </p>

                        <p className="text-sm font-bold text-slate-700 break-all">
                          {item.value}
                        </p>

                        {item.subValue && (
                          <p className="text-xs text-slate-400">
                            {item.subValue}
                          </p>
                        )}
                      </div>
                    </motion.div>
                  ))}
                </div>

                <motion.div
                  className="grid grid-cols-1 sm:grid-cols-2 gap-3 pt-2"
                  initial={{ opacity: 0, y: 16 }}
                  animate={{ opacity: 1, y: 0 }}
                  transition={{ delay: 0.25, duration: 0.25 }}
                >
                  <Link
                    to="/profile/update"
                    onClick={() => setShowProfile(false)}
                    className="flex items-center justify-center gap-2 bg-indigo-600 hover:bg-indigo-700 text-white font-bold py-3 rounded-xl transition"
                  >
                    <User size={17} />
                    Update Profile
                  </Link>

                  <Link
                    to="/password/update"
                    onClick={() => setShowProfile(false)}
                    className="flex items-center justify-center gap-2 bg-slate-800 hover:bg-slate-900 text-white font-bold py-3 rounded-xl transition"
                  >
                    <Lock size={17} />
                    Update Password
                  </Link>
                </motion.div>
              </div>
            </motion.div>
          </motion.div>
        )}
      </AnimatePresence>

      <header className="mb-8 flex flex-col md:flex-row md:items-center justify-between gap-4">
        <div>
          <h1 className="text-3xl font-bold text-slate-800 tracking-tight">
            Welcome back,{' '}
            <span className="text-indigo-600">
              {profileLoading ? 'Loading...' : currentUser?.name || 'User'}
            </span>
          </h1>

          <div className="flex items-center gap-3 mt-2">
            <span className="px-2 py-0.5 bg-slate-100 text-slate-600 text-[10px] font-bold rounded uppercase tracking-widest border border-slate-200">
              {profileLoading ? 'LOADING' : normalizedRole} Account
            </span>

            {isMember && (
              <span className="px-2 py-0.5 bg-blue-50 text-blue-600 text-[10px] font-bold rounded uppercase tracking-widest border border-blue-100">
                View Only
              </span>
            )}

            {canAccessAI && (
              <div className="flex items-center gap-1.5 text-[10px] font-bold uppercase tracking-wider">
                {dataStatus === 'loading' && (
                  <>
                    <RefreshCw
                      size={12}
                      className="animate-spin text-blue-500"
                    />
                    <span className="text-blue-500">Syncing Data...</span>
                  </>
                )}

                {dataStatus === 'success' && (
                  <>
                    <CheckCircle2 size={12} className="text-green-500" />
                    <span className="text-green-500">AI Context Ready</span>
                  </>
                )}

                {dataStatus === 'error' && (
                  <>
                    <XCircle size={12} className="text-red-500" />
                    <span className="text-red-500">Sync Failed</span>
                  </>
                )}
              </div>
            )}
          </div>
        </div>

        <div className="flex flex-wrap items-center gap-3">
          <button
            type="button"
            onClick={() => setShowProfile(true)}
            className="flex items-center gap-2 text-sm font-bold text-slate-700 hover:text-indigo-700 bg-white px-4 py-2.5 rounded-xl transition-all border border-slate-200 shadow-sm hover:shadow-md active:scale-95"
            title="My Profile"
          >
            <div className="w-8 h-8 rounded-full bg-indigo-600 text-white flex items-center justify-center">
              <User size={17} />
            </div>

            <span className="hidden sm:inline">
              {profileLoading ? 'Profile' : currentUser?.name || 'Profile'}
            </span>
          </button>

          {isAdmin && (
            <button
              type="button"
              onClick={handleConnectGmail}
              className="flex items-center gap-2 text-sm font-bold text-slate-700 hover:text-emerald-700 bg-white px-4 py-2.5 rounded-xl transition-all border border-slate-200 shadow-sm hover:shadow-md active:scale-95"
              title="Connect Gmail"
            >
              <div className="w-8 h-8 rounded-full bg-emerald-600 text-white flex items-center justify-center">
                <Mail size={17} />
              </div>

              <span className="hidden sm:inline">Connect Gmail</span>
            </button>
          )}

          <Link
            to="/reports"
            className="flex items-center gap-2 text-sm font-bold text-slate-700 hover:text-purple-700 bg-white px-4 py-2.5 rounded-xl transition-all border border-slate-200 shadow-sm hover:shadow-md active:scale-95"
            title="View Full Report"
          >
            <div className="w-8 h-8 rounded-full bg-purple-600 text-white flex items-center justify-center">
              <ArrowRight size={17} />
            </div>

            <span className="hidden sm:inline">View Report</span>
          </Link>
        </div>
      </header>

      {canAccessAI && (
        <div className="mb-10">
          <AiInsights
            mealHistory={dashboardData.meals}
            deposits={dashboardData.deposits}
            expenses={dashboardData.expenses}
            members={dashboardData.members}
          />

          {dataStatus === 'error' && (
            <div className="mt-4 p-4 bg-red-50 border border-red-100 rounded-xl">
              <p className="text-sm text-red-600 font-semibold flex items-center gap-2">
                <XCircle size={16} /> Data Synchronization Error
              </p>

              <p className="text-xs text-red-500 mt-1">
                The AI couldn't fetch current meal, deposit, expense, or member
                records. Check your backend API or re-login.
              </p>
            </div>
          )}
        </div>
      )}

      <div
        className={`grid grid-cols-1 md:grid-cols-2 gap-6 ${
          isMember ? 'lg:grid-cols-3' : 'lg:grid-cols-3 xl:grid-cols-6'
        }`}
      >
        {stats.map((item, index) => (
          <Link
            key={index}
            to={item.link}
            className={`${item.color} p-6 rounded-2xl text-white shadow-lg hover:scale-[1.03] transition-all duration-300 group relative overflow-hidden`}
          >
            <div className="relative z-10">
              <div className="p-3 bg-white/20 backdrop-blur-md rounded-xl w-fit mb-4">
                {item.icon}
              </div>

              <p className="text-sm font-medium opacity-90">{item.title}</p>

              <p className="text-xl font-bold mt-1">
                {isMember ? 'View' : 'Manage'}
              </p>

              <p className="text-[10px] mt-2 opacity-70 uppercase font-bold tracking-widest leading-tight">
                {item.desc}
              </p>
            </div>
          </Link>
        ))}
      </div>
    </div>
  );
};