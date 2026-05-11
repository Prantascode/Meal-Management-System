import React, { useEffect, useState } from 'react';
import api from '../api/axiosInstance';
import {
  UserPlus,
  Trash2,
  Loader2,
  X,
  Check,
  ShieldCheck,
  Eye,
  EyeOff,
  Users,
  Edit3,
  Save,
  CheckCircle2,
  AlertCircle,
  Lock
} from 'lucide-react';

export const Members = () => {
  const [members, setMembers] = useState<any[]>([]);
  const [loading, setLoading] = useState(true);
  const [submitLoading, setSubmitLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [showNewPassword, setShowNewPassword] = useState(false);
  const [showEditPassword, setShowEditPassword] = useState(false);

  const [currentUser, setCurrentUser] = useState<any>(null);
  const [profileLoading, setProfileLoading] = useState(true);
  const [isAdding, setIsAdding] = useState(false);
  const [newMember, setNewMember] = useState({
    name: '',
    email: '',
    password: '',
    role: 'MEMBER'
  });

  const [editingId, setEditingId] = useState<number | null>(null);
  const [editFormData, setEditFormData] = useState({
    name: '',
    email: '',
    role: '',
    password: ''
  });

  const [notification, setNotification] = useState<{
    type: 'success' | 'error';
    title: string;
    message: string;
  } | null>(null);

  const [deleteTarget, setDeleteTarget] = useState<any | null>(null);

  const userRole = currentUser?.role?.replace('ROLE_', '').toUpperCase() || '';

  const isAdmin = userRole === 'ADMIN';
  const isManager = userRole === 'MANAGER';
  const isMember = userRole === 'MEMBER';

  const canView = isAdmin || isManager || isMember;
  const canManage = isAdmin;
  useEffect(() => {
    const fetchCurrentUser = async () => {
      try {
        const res = await api.get('/members/me');
        setCurrentUser(res.data);
      } catch (err) {
        setError('Failed to load your profile. Please login again.');
      } finally {
        setProfileLoading(false);
      }
    };

    fetchCurrentUser();
  }, []);
  useEffect(() => {
    if (profileLoading) return;

    if (canView) {
      fetchMembers();
    } else {
      setLoading(false);
      setError('Access Denied');
    }
  }, [profileLoading, canView]);
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

  const fetchMembers = async () => {
    try {
      setLoading(true);
      setError(null);

      const res = await api.get('/members');
      setMembers(Array.isArray(res.data) ? res.data : []);
    } catch (err) {
      setError('Failed to load members from your mess.');
    } finally {
      setLoading(false);
    }
  };

  const startEditing = (member: any) => {
    setEditingId(member.id);
    setEditFormData({
      name: member.name,
      email: member.email,
      role: (member.role || 'MEMBER').replace('ROLE_', ''),
      password: ''
    });
  };

  const cancelEditing = () => {
    setEditingId(null);
    setEditFormData({
      name: '',
      email: '',
      role: '',
      password: ''
    });
  };

  const handleUpdate = async (id: number) => {
    try {
      setSubmitLoading(true);

      const updatePayload: any = {
        name: editFormData.name,
        email: editFormData.email,
        role: editFormData.role
      };

      if (editFormData.password.trim() !== '') {
        updatePayload.password = editFormData.password;
      }

      await api.put(`/members/${id}`, updatePayload);

      setEditingId(null);
      await fetchMembers();

      showNotification(
        'success',
        'Member Updated Successfully',
        editFormData.password.trim() !== ''
          ? 'Member information and password have been updated.'
          : 'The member information has been updated.'
      );
    } catch (err: any) {
      showNotification(
        'error',
        'Update Failed',
        getErrorMessage(
          err,
          'Update failed. Please check the member information.'
        )
      );
    } finally {
      setSubmitLoading(false);
    }
  };

  const handleAddMember = async (e: React.FormEvent) => {
    e.preventDefault();

    if (!canManage) return;

    try {
      setSubmitLoading(true);

      await api.post('/members/register', newMember);

      setIsAdding(false);
      setNewMember({
        name: '',
        email: '',
        password: '',
        role: 'MEMBER'
      });

      await fetchMembers();

      showNotification(
        'success',
        'Member Added Successfully',
        'A new member has been registered in your mess.'
      );
    } catch (err: any) {
      showNotification(
        'error',
        'Registration Failed',
        getErrorMessage(err, 'Member registration failed.')
      );
    } finally {
      setSubmitLoading(false);
    }
  };

  const deactivateMember = async () => {
    if (!canManage || !deleteTarget) return;

    try {
      setSubmitLoading(true);

      await api.delete(`/members/${deleteTarget.id}`);

      setMembers(prev =>
        prev.filter((m: any) => m.id !== deleteTarget.id)
      );

      setDeleteTarget(null);

      showNotification(
        'success',
        'Member Deleted Successfully',
        'The member has been removed from your mess.'
      );
    } catch (err: any) {
      showNotification(
        'error',
        'Delete Failed',
        getErrorMessage(err, 'Could not delete this member.')
      );
    } finally {
      setSubmitLoading(false);
    }
  };

  if (profileLoading || loading)  {
    return (
      <div className="flex h-screen items-center justify-center bg-slate-50">
        <Loader2 className="animate-spin text-indigo-600" size={48} />
      </div>
    );
  }

  if (error) {
    return (
      <div className="p-6 max-w-7xl mx-auto">
        <div className="rounded-2xl border border-red-200 bg-red-50 p-5 text-red-700 shadow-sm">
          <div className="flex items-center gap-3">
            <AlertCircle size={24} />
            <div>
              <h2 className="font-bold">Something went wrong</h2>
              <p className="text-sm">{error}</p>
            </div>
          </div>
        </div>
      </div>
    );
  }

  return (
    <div className="p-6 max-w-7xl mx-auto">

      {/* Popup Notification Message */}
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
      <div className="flex flex-col md:flex-row justify-between items-start md:items-center mb-8 gap-4">
        <div>
          <div className="flex items-center gap-3 mb-1">
            <div className="p-2 bg-purple-100 rounded-lg text-purple-700">
              <Users size={24} />
            </div>

            <h1 className="text-2xl font-bold text-slate-800">
              Mess Members
            </h1>
          </div>

          <p className="text-slate-500 text-sm font-medium flex items-center gap-2">
            {canManage ? (
              <ShieldCheck size={16} className="text-emerald-500" />
            ) : (
              <Eye size={16} className="text-blue-500" />
            )}

            {canManage ? 'Administrative Access' : 'View-Only Access'}
          </p>
        </div>

        {canManage && (
          <button
            onClick={() => setIsAdding(!isAdding)}
            className={`flex items-center text-white px-6 py-2.5 rounded-xl font-bold transition-all shadow-lg active:scale-95 ${
              isAdding
                ? 'bg-slate-500 hover:bg-slate-600'
                : 'bg-indigo-600 hover:bg-indigo-700'
            }`}
          >
            {isAdding ? (
              <>
                <X className="mr-2" size={20} />
                Cancel
              </>
            ) : (
              <>
                <UserPlus className="mr-2" size={20} />
                New Member
              </>
            )}
          </button>
        )}
      </div>

      {/* Add Member Form */}
      {isAdding && (
        <div className="bg-white p-6 rounded-2xl shadow-xl border border-slate-100 mb-8">
          <form
            onSubmit={handleAddMember}
            className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-5 gap-4"
          >
            <input
              placeholder="Full Name"
              required
              className="border border-slate-200 p-3 rounded-xl outline-none focus:ring-2 focus:ring-indigo-500"
              value={newMember.name}
              onChange={e =>
                setNewMember({ ...newMember, name: e.target.value })
              }
            />

            <input
              placeholder="Email"
              type="email"
              required
              className="border border-slate-200 p-3 rounded-xl outline-none focus:ring-2 focus:ring-indigo-500"
              value={newMember.email}
              onChange={e =>
                setNewMember({ ...newMember, email: e.target.value })
              }
            />

            <div className="relative">
            <input
              placeholder="Password"
              type={showNewPassword ? 'text' : 'password'}
              required
              className="w-full border border-slate-200 p-3 pr-10 rounded-xl outline-none focus:ring-2 focus:ring-indigo-500"
              value={newMember.password}
              onChange={e =>
                setNewMember({ ...newMember, password: e.target.value })
              }
            />

              <button
                type="button"
                onClick={() => setShowNewPassword(prev => !prev)}
                className="absolute right-3 top-1/2 -translate-y-1/2 text-slate-400 hover:text-indigo-600"
              >
                {showNewPassword ? <EyeOff size={18} /> : <Eye size={18} />}
              </button>
            </div>

            <select
              className="border border-slate-200 p-3 rounded-xl bg-white outline-none focus:ring-2 focus:ring-indigo-500"
              value={newMember.role}
              onChange={e =>
                setNewMember({ ...newMember, role: e.target.value })
              }
            >
              <option value="MEMBER">Member</option>
              <option value="MANAGER">Manager</option>
            </select>

            <button
              disabled={submitLoading}
              className="bg-indigo-600 hover:bg-indigo-700 disabled:bg-indigo-400 text-white rounded-xl font-bold flex items-center justify-center gap-2 transition"
            >
              {submitLoading ? (
                <Loader2 className="animate-spin" size={20} />
              ) : (
                <>
                  <Check size={20} />
                  Save
                </>
              )}
            </button>
          </form>
        </div>
      )}

      {/* Members Table */}
      <div className="bg-white rounded-2xl shadow-sm overflow-hidden border border-slate-200">
        <div className="overflow-x-auto">
          <table className="w-full text-left">
            <thead className="bg-slate-50 border-b">
              <tr>
                <th className="p-5 font-bold text-slate-500 text-xs uppercase">
                  Member Info
                </th>

                <th className="p-5 font-bold text-slate-500 text-xs uppercase text-center">
                  Status
                </th>

                <th className="p-5 font-bold text-slate-500 text-xs uppercase">
                  Authority
                </th>

                {canManage && (
                  <th className="p-5 font-bold text-slate-500 text-xs uppercase text-center">
                    Actions
                  </th>
                )}
              </tr>
            </thead>

            <tbody className="divide-y divide-slate-100">
              {members.length === 0 ? (
                <tr>
                  <td
                    colSpan={canManage ? 4 : 3}
                    className="p-8 text-center text-slate-500 font-medium"
                  >
                    No members found.
                  </td>
                </tr>
              ) : (
                members.map((m) => (
                  <tr
                    key={m.id}
                    className="hover:bg-slate-50/80 transition-colors group"
                  >
                    <td className="p-5">
                      {editingId === m.id ? (
                        <div className="space-y-2">
                          <input
                            className="border border-slate-200 p-2 rounded-lg text-sm w-full outline-none focus:ring-2 focus:ring-indigo-500"
                            value={editFormData.name}
                            onChange={e =>
                              setEditFormData({
                                ...editFormData,
                                name: e.target.value
                              })
                            }
                          />

                          <input
                            className="border border-slate-200 p-2 rounded-lg text-sm w-full outline-none focus:ring-2 focus:ring-indigo-500"
                            value={editFormData.email}
                            onChange={e =>
                              setEditFormData({
                                ...editFormData,
                                email: e.target.value
                              })
                            }
                          />

                          <div className="relative">
                            <Lock
                              size={14}
                              className="absolute left-2.5 top-2.5 text-slate-400"
                            />
                            <input
                              type={showEditPassword ? 'text' : 'password'}
                              placeholder="New password optional"
                              className="border border-slate-200 pl-8 pr-9 py-2 rounded-lg text-sm w-full outline-none focus:ring-2 focus:ring-indigo-500"
                              value={editFormData.password}
                              onChange={e =>
                                setEditFormData({
                                  ...editFormData,
                                  password: e.target.value
                                })
                              }
                            />

                            <button
                              type="button"
                              onClick={() => setShowEditPassword(prev => !prev)}
                              className="absolute right-2.5 top-2.5 text-slate-400 hover:text-indigo-600"
                            >
                              {showEditPassword ? <EyeOff size={14} /> : <Eye size={14} />}
                            </button>
                          </div>

                          <p className="text-[10px] text-slate-400">
                            Leave password empty if you do not want to change it.
                          </p>
                        </div>
                      ) : (
                        <div className="flex items-center gap-3">
                          <div className="w-10 h-10 bg-slate-100 rounded-full flex items-center justify-center text-slate-500 font-bold">
                            {m.name?.charAt(0)?.toUpperCase() || 'M'}
                          </div>

                          <div>
                            <div className="font-bold text-slate-800">
                              {m.name}
                            </div>
                            <div className="text-xs text-slate-400">
                              {m.email}
                            </div>
                          </div>
                        </div>
                      )}
                    </td>

                    <td className="p-5 text-center">
                      <span className="inline-flex items-center px-3 py-1 rounded-full text-xs font-bold bg-emerald-50 text-emerald-700">
                        Active
                      </span>
                    </td>

                    <td className="p-5">
                      {editingId === m.id ? (
                        <div className="space-y-2">
                        <p className="text-[10px] font-black uppercase tracking-widest text-slate-400">
                          Update Authority
                        </p>

                        <div className="relative">
                          <select
                            className={`
                              w-full appearance-none rounded-xl border px-4 py-2.5 pr-10 text-sm font-bold outline-none transition-all
                              focus:ring-2 focus:ring-indigo-500
                              ${
                                editFormData.role === 'ADMIN'
                                  ? 'border-purple-200 bg-purple-50 text-purple-700'
                                  : editFormData.role === 'MANAGER'
                                  ? 'border-amber-200 bg-amber-50 text-amber-700'
                                  : 'border-slate-200 bg-slate-50 text-slate-700'
                              }
                            `}
                            value={editFormData.role}
                            onChange={e =>
                              setEditFormData({
                                ...editFormData,
                                role: e.target.value
                              })
                            }
                          >
                            <option value="MEMBER">Member</option>
                            <option value="MANAGER">Manager</option>
                            <option value="ADMIN">Admin</option>
                          </select>

                          <div className="pointer-events-none absolute right-3 top-1/2 -translate-y-1/2 text-slate-400">
                            ▼
                          </div>
                        </div>

                        <p className="text-[11px] text-slate-400">
                          Choose the permission level for this account.
                        </p>
                      </div>
                      ) : (
                        <span
                          className={`px-3 py-1 rounded-lg text-[10px] font-black uppercase ${
                            m.role?.includes('ADMIN')
                              ? 'bg-purple-600 text-white'
                              : m.role?.includes('MANAGER')
                              ? 'bg-amber-500 text-white'
                              : 'bg-slate-200 text-slate-700'
                          }`}
                        >
                          {m.role?.replace('ROLE_', '')}
                        </span>
                      )}
                    </td>

                    {canManage && (
                      <td className="p-5 text-center">
                        <div className="flex justify-center gap-2">
                          {editingId === m.id ? (
                            <>
                              <button
                                disabled={submitLoading}
                                onClick={() => handleUpdate(m.id)}
                                className="
                                  group relative inline-flex items-center justify-center gap-2
                                  rounded-xl bg-emerald-600 px-4 py-2
                                  text-sm font-bold text-white
                                  shadow-lg shadow-emerald-500/30
                                  transition-all duration-300
                                  hover:bg-emerald-700 hover:shadow-xl hover:shadow-emerald-500/40
                                  active:scale-95
                                  disabled:cursor-not-allowed disabled:bg-emerald-400 disabled:shadow-none
                                "
                              >
                                {submitLoading ? (
                                  <>
                                    <Loader2 className="animate-spin" size={18} />
                                    Saving...
                                  </>
                                ) : (
                                  <>
                                    <Save size={18} />
                                    Save
                                  </>
                                )}
                              </button>

                              <button
                                disabled={submitLoading}
                                onClick={cancelEditing}
                                className="text-slate-400 p-2 hover:bg-slate-100 rounded-xl disabled:opacity-60 transition"
                              >
                                <X size={20} />
                              </button>
                            </>
                          ) : (
                            <>
                              <button
                                onClick={() => startEditing(m)}
                                className="text-slate-300 hover:text-indigo-600 p-2 hover:bg-indigo-50 rounded-xl transition-all"
                              >
                                <Edit3 size={20} />
                              </button>

                              <button
                                onClick={() => setDeleteTarget(m)}
                                className="text-slate-300 hover:text-red-600 p-2 hover:bg-red-50 rounded-xl transition-all"
                              >
                                <Trash2 size={20} />
                              </button>
                            </>
                          )}
                        </div>
                      </td>
                    )}
                  </tr>
                ))
              )}
            </tbody>
          </table>
        </div>
      </div>

      {/* Custom Delete Confirmation Modal */}
      {deleteTarget && (
        <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/40 px-4">
          <div className="w-full max-w-md rounded-2xl bg-white p-6 shadow-2xl border border-slate-200">

            <div className="flex items-center gap-3 mb-4">
              <div className="rounded-full bg-red-100 p-3 text-red-600">
                <Trash2 size={24} />
              </div>

              <div>
                <h2 className="text-lg font-bold text-slate-800">
                  Delete Member?
                </h2>
                <p className="text-sm text-slate-500">
                  This action cannot be undone.
                </p>
              </div>
            </div>

            <div className="rounded-xl bg-slate-50 border border-slate-100 p-4 mb-6">
              <p className="text-sm text-slate-500">
                You are deleting:
              </p>

              <p className="font-bold text-slate-800">
                {deleteTarget.name}
              </p>

              <p className="text-xs text-slate-400">
                {deleteTarget.email}
              </p>
            </div>

            <div className="flex justify-end gap-3">
              <button
                disabled={submitLoading}
                onClick={() => setDeleteTarget(null)}
                className="px-5 py-2.5 rounded-xl border border-slate-200 text-slate-600 font-bold hover:bg-slate-50 disabled:opacity-60 transition"
              >
                Cancel
              </button>

              <button
                disabled={submitLoading}
                onClick={deactivateMember}
                className="px-5 py-2.5 rounded-xl bg-red-600 text-white font-bold hover:bg-red-700 disabled:bg-red-400 transition flex items-center gap-2"
              >
                {submitLoading ? (
                  <>
                    <Loader2 className="animate-spin" size={18} />
                    Deleting...
                  </>
                ) : (
                  <>
                    <Trash2 size={18} />
                    Delete Member
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