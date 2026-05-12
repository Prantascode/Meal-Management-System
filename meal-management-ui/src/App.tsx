import { BrowserRouter as Router, Routes, Route, Navigate } from 'react-router-dom';
import { Login } from './pages/Login';
import { CreateMess } from './pages/CreateMess';
import { Dashboard } from './pages/Dashboard';
import { Members } from './pages/Members';
import { Meals } from './pages/Meals';
import { Deposits } from './pages/Deposits';
import { Expenses } from './pages/Expenses';
import { Reports } from './pages/Reports';
import { Layout } from './components/Layout';
import { UpdateProfile } from './pages/UpdateProfile';
import { UpdatePassword } from './pages/UpdatePassword';

const ProtectedRoute = ({
  children,
  allowedRoles
}: {
  children: React.ReactNode;
  allowedRoles?: string[];
}) => {
  const token = localStorage.getItem('accessToken');
  const rawRole = localStorage.getItem('role') || '';
  const userRole = rawRole.replace('ROLE_', '').toUpperCase();

  if (!token) {
    return <Navigate to="/login" replace />;
  }

  if (allowedRoles && !allowedRoles.includes(userRole)) {
    return <Navigate to="/dashboard" replace />;
  }

  return <>{children}</>;
};

function App() {
  return (
    <Router>
      <Routes>
        {/* Public Routes */}
        <Route path="/login" element={<Login />} />
        <Route path="/create-mess" element={<CreateMess />} />

        {/* Protected Layout Routes */}
        <Route element={<Layout />}>
          <Route
            path="/dashboard"
            element={
              <ProtectedRoute>
                <Dashboard />
              </ProtectedRoute>
            }
          />

          <Route
            path="/profile/update"
            element={
              <ProtectedRoute allowedRoles={['ADMIN', 'MANAGER', 'MEMBER']}>
                <UpdateProfile />
              </ProtectedRoute>
            }
          />

          <Route
            path="/password/update"
            element={
              <ProtectedRoute allowedRoles={['ADMIN', 'MANAGER', 'MEMBER']}>
                <UpdatePassword />
              </ProtectedRoute>
            }
          />

          <Route
            path="/meals"
            element={
              <ProtectedRoute>
                <Meals />
              </ProtectedRoute>
            }
          />

          <Route
            path="/reports"
            element={
              <ProtectedRoute>
                <Reports />
              </ProtectedRoute>
            }
          />

          <Route
            path="/members"
            element={
              <ProtectedRoute allowedRoles={['ADMIN', 'MANAGER', 'MEMBER']}>
                <Members />
              </ProtectedRoute>
            }
          />

          <Route
            path="/deposits"
            element={
              <ProtectedRoute allowedRoles={['ADMIN', 'MANAGER', 'MEMBER']}>
                <Deposits />
              </ProtectedRoute>
            }
          />

          <Route
            path="/expenses"
            element={
              <ProtectedRoute allowedRoles={['ADMIN', 'MANAGER', 'MEMBER']}>
                <Expenses />
              </ProtectedRoute>
            }
          />
        </Route>

        {/* Default Redirects */}
        <Route path="/" element={<Navigate to="/dashboard" replace />} />
        <Route path="*" element={<Navigate to="/dashboard" replace />} />
      </Routes>
    </Router>
  );
}

export default App;