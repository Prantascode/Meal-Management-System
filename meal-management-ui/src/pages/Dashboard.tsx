import React from 'react';
import { Users, Utensils, Wallet, Landmark, FileBarChart, ArrowRight } from 'lucide-react';
import { Link } from 'react-router-dom';

export const Dashboard = () => {
  const userEmail = localStorage.getItem('email');
  const userRole = localStorage.getItem('role')?.replace('ROLE_', '') || 'USER';

  const stats = [
    { title: 'Total Members', icon: <Users />, link: '/members', color: 'bg-blue-500', desc: 'Manage mess members' },
    { title: 'Meal Entries', icon: <Utensils />, link: '/meals', color: 'bg-green-500', desc: 'Track daily meals' },
    { title: 'Member Deposits', icon: <Landmark />, link: '/deposits', color: 'bg-indigo-600', desc: 'View cash deposits' },
    { title: 'Add Expense', icon: <Wallet />, link: '/expenses', color: 'bg-orange-500', desc: 'Log bazaar costs' },
    { title: 'Monthly Reports', icon: <FileBarChart />, link: '/reports', color: 'bg-purple-600', desc: 'Financial summaries' },
  ];

  return (
    <div className="p-6 max-w-7xl mx-auto">
      <header className="mb-10 flex flex-col md:flex-row md:items-center justify-between gap-4">
        <div>
          <h1 className="text-3xl font-bold text-slate-800 tracking-tight">
            Welcome back, <span className="text-purple-600">{userEmail?.split('@')[0]}</span>
          </h1>
          <div className="flex items-center gap-2 mt-1">
            <span className="px-2 py-0.5 bg-slate-100 text-slate-600 text-xs font-bold rounded uppercase tracking-wider">
              Access Level: {userRole}
            </span>
          </div>
        </div>
        
        <Link 
          to="/reports" 
          className="flex items-center gap-2 text-sm font-bold text-purple-600 hover:text-purple-700 bg-purple-50 px-4 py-2 rounded-lg transition-colors"
        >
          View Latest Report <ArrowRight size={16} />
        </Link>
      </header>

      {/* Main Grid*/}
      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 xl:grid-cols-5 gap-6">
        {stats.map((item, index) => (
          <Link 
            key={index} 
            to={item.link} 
            className={`${item.color} p-6 rounded-2xl text-white shadow-lg shadow-gray-200 hover:scale-[1.03] active:scale-95 transition-all duration-200 group relative overflow-hidden`}
          >
            {/* Background Decorative Icon */}
            <div className="absolute -right-4 -bottom-4 opacity-10 group-hover:scale-125 transition-transform duration-500 text-white [&>svg]:w-24 [&>svg]:h-24">
              {item.icon}
            </div>

            <div className="relative z-10">
              <div className="p-3 bg-white bg-opacity-20 rounded-xl w-fit mb-4">
                {item.icon}
              </div>
              <div>
                <p className="text-sm font-medium opacity-90">{item.title}</p>
                <p className="text-xl font-bold mt-1">Manage</p>
                <p className="text-[10px] mt-2 opacity-70 uppercase font-bold tracking-widest">{item.desc}</p>
              </div>
            </div>
          </Link>
        ))}
      </div>

      {/*Add a "Quick Status" section below */}
      <div className="mt-12 grid grid-cols-1 lg:grid-cols-2 gap-8">
        <div className="bg-white p-6 rounded-2xl border border-slate-100 shadow-sm">
          <h2 className="text-lg font-bold text-slate-800 mb-4">Current Month Overview</h2>
          <p className="text-slate-500 text-sm">
            Head over to the <Link to="/reports" className="text-purple-600 font-bold hover:underline">Reports Page</Link> to generate the official balance sheet for {new Date().toLocaleString('default', { month: 'long' })}.
          </p>
        </div>
      </div>
    </div>
  );
};