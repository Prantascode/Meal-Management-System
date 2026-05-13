import React, { useState, useEffect } from 'react';
import api from '../api/axiosInstance';
import { UtensilsCrossed, List, Calculator, CalendarDays, User, Trash2, Edit3, Check, X, Search, PieChart, Loader2 } from 'lucide-react';

export const Meals = () => {
  const [mealData, setMealData] = useState({
    memberId: '',
    date: new Date().toISOString().split('T')[0],
    mealType: 'LUNCH',
    mealCount: 1
  });
  
  const [members, setMembers] = useState<any[]>([]);
  const [mealHistory, setMealHistory] = useState<any[]>([]);
  const [loading, setLoading] = useState(true);
  const [submitting, setSubmitting] = useState(false);
  
  const [searchTerm, setSearchTerm] = useState('');
  const [editingId, setEditingId] = useState<number | null>(null);
  const [editFormData, setEditFormData] = useState<any>(null);

  // Unified Role Check
  const userRole = (localStorage.getItem('role') || '').replace('ROLE_', '');
  const isAdmin = userRole === 'ADMIN' || userRole === 'MANAGER';

  useEffect(() => {
    const init = async () => {
      setLoading(true);
      await Promise.all([fetchMembers(), fetchMealHistory()]);
      setLoading(false);
    };
    init();
  }, []);

  // --- CALCULATIONS ---
  const todayStr = new Date().toISOString().split('T')[0];
  const totalMeals = mealHistory.reduce((sum, meal) => sum + (meal.mealCount || 0), 0);
  const todayMeals = mealHistory
    .filter(m => m.date === todayStr)
    .reduce((sum, meal) => sum + (meal.mealCount || 0), 0);

  const filteredMeals = mealHistory.filter(meal => 
    (meal.memberName || '').toLowerCase().includes(searchTerm.toLowerCase())
  );

  const memberSummaries = mealHistory.reduce((acc: any, meal: any) => {
    const name = meal.memberName || `Unknown`;
    acc[name] = (acc[name] || 0) + (meal.mealCount || 0);
    return acc;
  }, {});

  const fetchMembers = async () => {
    try {
      const res = await api.get('/members');
      setMembers(Array.isArray(res.data) ? res.data : []);
    } catch (err) { console.error("Error fetching members:", err); }
  };

  const fetchMealHistory = async () => {
    try {
      const now = new Date();
      const firstDay = new Date(now.getFullYear(), now.getMonth(), 1).toISOString().split('T')[0];
      const lastDay = new Date(now.getFullYear(), now.getMonth() + 1, 0).toISOString().split('T')[0];
      const res = await api.get(`/meals?startDate=${firstDay}&endDate=${lastDay}`); 
      setMealHistory(Array.isArray(res.data) ? res.data : []);
    } catch (err) { console.error("Error fetching meals:", err); }
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!isAdmin) return;
    if (!mealData.memberId) return alert("Please select a member");

    setSubmitting(true);
    try {
      await api.post('/meals/add', mealData);
      setMealData(prev => ({ ...prev, memberId: '', mealCount: 1 }));
      fetchMealHistory(); 
    } catch (err: any) {
      alert(err.response?.data || "Error adding meal");
    } finally {
      setSubmitting(false);
    }
  };

  const handleDelete = async (id: number) => {
    if (!window.confirm("Delete this meal record?")) return;
    try {
      await api.delete(`/meals/${id}`);
      setMealHistory(prev => prev.filter(m => m.id !== id));
    } catch (err) { alert("Delete failed"); }
  };

  const startEditing = (meal: any) => {
    setEditingId(meal.id);
    setEditFormData({ ...meal });
  };

  const handleUpdate = async () => {
    try {
      await api.put(`/meals/${editingId}`, editFormData);
      setEditingId(null);
      fetchMealHistory();
    } catch (err) { alert("Update failed"); }
  };

  if (loading) return <div className="h-screen flex items-center justify-center"><Loader2 className="animate-spin text-blue-600" size={48} /></div>;

  return (
    <div className="p-6 max-w-7xl mx-auto space-y-8 animate-in fade-in duration-500">
      
      {/* 1. TOP CARDS */}
      <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
        <div className="bg-gradient-to-br from-blue-600 to-blue-800 p-6 rounded-2xl shadow-lg text-white">
          <Calculator className="mb-2 opacity-80" size={24} />
          <p className="text-blue-100 text-xs font-bold uppercase">Monthly Total</p>
          <h3 className="text-3xl font-black">{totalMeals}</h3>
        </div>
        <div className="bg-gradient-to-br from-emerald-500 to-teal-700 p-6 rounded-2xl shadow-lg text-white">
          <CalendarDays className="mb-2 opacity-80" size={24} />
          <p className="text-emerald-100 text-xs font-bold uppercase">Today's Consumption</p>
          <h3 className="text-3xl font-black">{todayMeals}</h3>
        </div>
        <div className="bg-gradient-to-br from-indigo-500 to-purple-700 p-6 rounded-2xl shadow-lg text-white">
          <PieChart className="mb-2 opacity-80" size={24} />
          <p className="text-indigo-100 text-xs font-bold uppercase">Active Members</p>
          <h3 className="text-3xl font-black">{members.length}</h3>
        </div>
      </div>

      {/* 2. MEMBER SUMMARIES */}
      <div className="bg-white p-6 rounded-2xl shadow-sm border border-gray-100">
        <h2 className="text-sm font-black text-gray-400 uppercase mb-4 tracking-widest">Monthly Member Breakdown</h2>
        <div className="flex flex-wrap gap-3">
          {Object.entries(memberSummaries).map(([name, count]: any) => (
            <div key={name} className="flex items-center gap-2 bg-gray-50 px-4 py-2 rounded-full border border-gray-100">
              <span className="text-xs font-bold text-gray-600">{name}:</span>
              <span className="text-sm font-black text-blue-600">{count}</span>
            </div>
          ))}
        </div>
      </div>

      <div className="grid grid-cols-1 lg:grid-cols-3 gap-8">
        {/* LOG FORM */}
        {isAdmin && (
          <div className="lg:col-span-1">
            <div className="bg-white p-6 rounded-2xl shadow-xl border border-gray-100 sticky top-6">
              <div className="flex items-center gap-3 mb-6">
                <div className="p-2 bg-blue-100 rounded-lg text-blue-600"><UtensilsCrossed size={20} /></div>
                <h2 className="text-xl font-bold text-gray-800">Add Meal</h2>
              </div>
              <form onSubmit={handleSubmit} className="space-y-5">

                {/* Select Member */}
                <div className="space-y-2">
                  <label className="text-xs font-black text-gray-500 uppercase tracking-widest">
                    Select Member
                  </label>

                  <div className="relative group">
                    <div className="absolute left-4 top-1/2 -translate-y-1/2 text-gray-400 group-focus-within:text-blue-600 transition-colors">
                      <User size={18} />
                    </div>

                    <select
                      className="
                        w-full appearance-none
                        border border-gray-200
                        bg-gradient-to-r from-gray-50 to-white
                        pl-12 pr-10 py-3.5
                        rounded-2xl
                        text-sm font-bold text-gray-700
                        shadow-sm
                        outline-none
                        cursor-pointer
                        transition-all duration-200
                        hover:border-blue-300 hover:shadow-md
                        focus:border-blue-500 focus:ring-4 focus:ring-blue-100 focus:bg-white
                      "
                      value={mealData.memberId}
                      onChange={e => setMealData({ ...mealData, memberId: e.target.value })}
                    >
                      <option value="">Choose a member</option>
                      {members.map((m: any) => (
                        <option key={m.id} value={m.id}>
                          {m.name}
                        </option>
                      ))}
                    </select>

                    <div className="pointer-events-none absolute right-4 top-1/2 -translate-y-1/2 text-gray-400 text-xs">
                      ▼
                    </div>
                  </div>
                </div>

                {/* Date */}
                <div className="space-y-2">
                  <label className="text-xs font-black text-gray-500 uppercase tracking-widest">
                    Meal Date
                  </label>

                  <div className="relative group">
                    <div className="absolute left-4 top-1/2 -translate-y-1/2 text-gray-400 group-focus-within:text-blue-600 transition-colors">
                      <CalendarDays size={18} />
                    </div>

                    <input
                      type="date"
                      className="
                        w-full
                        border border-gray-200
                        bg-gradient-to-r from-gray-50 to-white
                        pl-12 pr-4 py-3.5
                        rounded-2xl
                        text-sm font-bold text-gray-700
                        shadow-sm
                        outline-none
                        transition-all duration-200
                        hover:border-blue-300 hover:shadow-md
                        focus:border-blue-500 focus:ring-4 focus:ring-blue-100 focus:bg-white
                      "
                      value={mealData.date}
                      onChange={e => setMealData({ ...mealData, date: e.target.value })}
                    />
                  </div>
                </div>

                {/* Meal Type */}
                <div className="space-y-2">
                  <label className="text-xs font-black text-gray-500 uppercase tracking-widest">
                    Meal Type
                  </label>

                  <div className="grid grid-cols-3 gap-2">
                    {['BREAKFAST', 'LUNCH', 'DINNER'].map((type) => (
                      <button
                        key={type}
                        type="button"
                        onClick={() => setMealData({ ...mealData, mealType: type })}
                        className={`
                          py-3 rounded-2xl text-[11px] font-black uppercase transition-all duration-200 border
                          ${
                            mealData.mealType === type
                              ? 'bg-blue-600 text-white border-blue-600 shadow-lg shadow-blue-200 scale-[1.02]'
                              : 'bg-gray-50 text-gray-500 border-gray-200 hover:bg-blue-50 hover:text-blue-600 hover:border-blue-200'
                          }
                        `}
                      >
                        {type === 'BREAKFAST' ? 'Breakfast' : type === 'LUNCH' ? 'Lunch' : 'Dinner'}
                      </button>
                    ))}
                  </div>
                </div>

                {/* Meal Count */}
                <div className="space-y-2">
                  <label className="text-xs font-black text-gray-500 uppercase tracking-widest">
                    Meal Count
                  </label>

                  <div className="w-full max-w-full overflow-hidden flex items-center justify-between gap-2 bg-gradient-to-r from-gray-50 to-white border border-gray-200 rounded-2xl p-2 shadow-sm">
                    <button
                      type="button"
                      onClick={() =>
                        setMealData({
                          ...mealData,
                          mealCount: Math.max(1, mealData.mealCount - 1),
                        })
                      }
                      className="
                        flex-shrink-0
                        w-10 h-10 sm:w-11 sm:h-11
                        rounded-xl
                        bg-white
                        border border-gray-200
                        text-gray-600
                        font-black
                        text-xl
                        hover:bg-red-50 hover:text-red-600 hover:border-red-200
                        transition-all
                      "
                    >
                      -
                    </button>

                    <input
                      type="number"
                      min="1"
                      className="
                        min-w-0
                        flex-1
                        w-full
                        bg-transparent
                        text-center
                        text-xl sm:text-2xl
                        font-black
                        text-gray-800
                        outline-none
                      "
                      value={mealData.mealCount}
                      onChange={e =>
                        setMealData({
                          ...mealData,
                          mealCount: Math.max(1, parseInt(e.target.value) || 1),
                        })
                      }
                    />

                    <button
                      type="button"
                      onClick={() =>
                        setMealData({
                          ...mealData,
                          mealCount: mealData.mealCount + 1,
                        })
                      }
                      className="
                        flex-shrink-0
                        w-10 h-10 sm:w-11 sm:h-11
                        rounded-xl
                        bg-white
                        border border-gray-200
                        text-gray-600
                        font-black
                        text-xl
                        hover:bg-green-50 hover:text-green-600 hover:border-green-200
                        transition-all
                      "
                    >
                      +
                    </button>
                  </div>
                </div>

                {/* Selected Value Preview */}
                <div className="bg-blue-50 border border-blue-100 rounded-2xl p-4">
                  <p className="text-[10px] font-black text-blue-400 uppercase tracking-widest mb-2">
                    Current Selection
                  </p>

                  <div className="flex flex-wrap gap-2">
                    <span className="px-3 py-1 rounded-full bg-white text-blue-700 text-xs font-black border border-blue-100">
                      {members.find((m: any) => String(m.id) === String(mealData.memberId))?.name || 'No member selected'}
                    </span>

                    <span className="px-3 py-1 rounded-full bg-white text-purple-700 text-xs font-black border border-purple-100">
                      {mealData.mealType}
                    </span>

                    <span className="px-3 py-1 rounded-full bg-white text-emerald-700 text-xs font-black border border-emerald-100">
                      {mealData.mealCount} Meal
                    </span>
                  </div>
                </div>

                {/* Save Button */}
                <button
                  disabled={submitting}
                  className="
                    w-full
                    bg-gradient-to-r from-blue-600 to-indigo-600
                    hover:from-blue-700 hover:to-indigo-700
                    disabled:from-gray-400 disabled:to-gray-500
                    text-white
                    py-4
                    rounded-2xl
                    font-black
                    tracking-wide
                    shadow-lg shadow-blue-200
                    hover:shadow-xl hover:shadow-blue-300
                    active:scale-[0.98]
                    transition-all duration-200
                    flex items-center justify-center gap-2
                  "
                >
                  {submitting ? (
                    <>
                      <Loader2 className="animate-spin" size={20} />
                      Saving...
                    </>
                  ) : (
                    <>
                      <Check size={20} />
                      Save Meal Record
                    </>
                  )}
                </button>
              </form>
            </div>
          </div>
        )}

        {/* LEDGER TABLE */}
        <div className={isAdmin ? "lg:col-span-2" : "lg:col-span-3"}>
          <div className="bg-white rounded-2xl shadow-sm border border-gray-100 overflow-hidden">
            <div className="p-6 border-b border-gray-50 flex flex-col md:flex-row justify-between gap-4">
              <div className="flex items-center gap-2">
                <List className="text-blue-600" size={20} />
                <h2 className="text-lg font-bold text-gray-800">Meal Ledger</h2>
              </div>
              <div className="relative">
                <Search className="absolute left-3 top-2.5 text-gray-400" size={16} />
                <input type="text" placeholder="Filter members..." className="pl-10 pr-4 py-2 bg-gray-50 rounded-xl text-sm outline-none focus:ring-2 focus:ring-blue-500 w-full md:w-64" value={searchTerm} onChange={(e) => setSearchTerm(e.target.value)} />
              </div>
            </div>
            
            <div className="overflow-x-auto">
              <table className="w-full text-left">
                <thead className="bg-gray-50 text-gray-400 text-[10px] font-black uppercase tracking-widest">
                  <tr>
                    <th className="p-5">Date</th>
                    <th className="p-5">Member</th>
                    <th className="p-5">Type</th>
                    <th className="p-5 text-center">Qty</th>
                    {isAdmin && <th className="p-5 text-center">Actions</th>}
                  </tr>
                </thead>
                <tbody className="divide-y divide-gray-50">
                  {filteredMeals.map((meal: any) => (
                    <tr key={meal.id} className="hover:bg-blue-50/20 transition-colors">
                      <td className="p-5 text-xs text-gray-500">
                        {editingId === meal.id ? (
                          <input type="date" className="border rounded p-1" value={editFormData.date} onChange={e => setEditFormData({...editFormData, date: e.target.value})} />
                        ) : meal.date}
                      </td>
                      <td className="p-5 font-bold text-gray-800">{meal.memberName}</td>
                      <td className="p-5">
                        {editingId === meal.id ? (
                          <select className="border rounded p-1 text-xs" value={editFormData.mealType} onChange={e => setEditFormData({...editFormData, mealType: e.target.value})}>
                            <option value="BREAKFAST">BREAKFAST</option>
                            <option value="LUNCH">LUNCH</option>
                            <option value="DINNER">DINNER</option>
                          </select>
                        ) : (
                          <span className="text-[10px] font-black text-blue-500">{meal.mealType}</span>
                        )}
                      </td>
                      <td className="p-5 text-center font-black">
                        {editingId === meal.id ? (
                          <input type="number" className="border rounded p-1 w-12 text-center" value={editFormData.mealCount} onChange={e => setEditFormData({...editFormData, mealCount: parseInt(e.target.value)})} />
                        ) : meal.mealCount}
                      </td>
                      {isAdmin && (
                        <td className="p-5">
                          <div className="flex justify-center gap-2">
                            {editingId === meal.id ? (
                              <button onClick={handleUpdate} className="text-green-600 p-1"><Check size={18}/></button>
                            ) : (
                              <button onClick={() => startEditing(meal)} className="text-gray-300 hover:text-blue-600"><Edit3 size={18}/></button>
                            )}
                            <button onClick={() => handleDelete(meal.id)} className="text-gray-300 hover:text-red-600"><Trash2 size={18}/></button>
                          </div>
                        </td>
                      )}
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};