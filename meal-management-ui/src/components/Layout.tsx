import { useEffect, useState } from "react";
import { Outlet, useLocation } from "react-router-dom";
import { Menu, Loader2 } from "lucide-react";
import { Sidebar } from "./Sidebar";

export const Layout = () => {
  const [sidebarOpen, setSidebarOpen] = useState(false);
  const [pageLoading, setPageLoading] = useState(false);

  const location = useLocation();

  const toggleSidebar = () => {
    setSidebarOpen((prev) => !prev);
  };

  const closeSidebar = () => {
    if (sidebarOpen) {
      setSidebarOpen(false);
    }
  };

  // Page changing loader
  useEffect(() => {
    setPageLoading(true);

    const timer = setTimeout(() => {
      setPageLoading(false);
    }, 500);

    return () => clearTimeout(timer);
  }, [location.pathname]);

  return (
    <div className="min-h-screen bg-slate-50 overflow-x-hidden">

      {/* Page Loader */}
      {pageLoading && (
        <div className="fixed inset-0 z-[9999] flex items-center justify-center bg-white/70 backdrop-blur-sm">
          <div className="flex flex-col items-center gap-3 rounded-2xl bg-white px-8 py-6 shadow-2xl border border-slate-100">
            <Loader2 className="animate-spin text-indigo-600" size={42} />
            <p className="text-sm font-bold text-slate-700">
              Loading page...
            </p>
          </div>
        </div>
      )}

      {/* Sidebar */}
      <aside
        className={`
          fixed top-0 left-0 z-50 h-full w-64
          transform transition-transform duration-300
          ${sidebarOpen ? "translate-x-0" : "-translate-x-full"}
        `}
      >
        <Sidebar onClose={() => setSidebarOpen(false)} />
      </aside>

      {/* Main content */}
      <main
        onClick={closeSidebar}
        className={`
          min-h-screen bg-gray-50 transition-all duration-300
          ${sidebarOpen ? "ml-64" : "ml-0"}
        `}
      >
        {/* Three-line button */}
        <div className="p-4">
          <button
            onClick={(e) => {
              e.stopPropagation();
              toggleSidebar();
            }}
            className="p-3 rounded-xl bg-slate-900 text-white shadow-lg hover:bg-slate-800 transition"
          >
            <Menu size={24} />
          </button>
        </div>

        <Outlet />
      </main>
    </div>
  );
};