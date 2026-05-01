import { BrowserRouter, Routes, Route, Navigate, useLocation } from "react-router-dom";

import Dashboard from "./pages/Dashboard";
import ApiKeys from "./pages/ApiKeys";
import Billing from "./pages/Billing";
import Plans from "./pages/Plans";

import Login from "./pages/Login";
import Register from "./pages/Register";

import Sidebar from "./components/Sidebar";
import AdminSidebar from "./components/AdminSidebar";

//  ADMIN PAGES
import AdminDashboard from "./pages/AdminDashboard";
import AdminUsers from "./pages/AdminUsers";
import AdminRevenue from "./pages/AdminRevenue";
import AdminTraffic from "./pages/AdminTraffic";
import AdminApiKeys from "./pages/AdminApiKeys";

// ===============================
//  Private Route
// ===============================
function PrivateRoute({ children }) {
  const token = localStorage.getItem("token");
  return token ? children : <Navigate to="/login" replace />;
}

// ===============================
//  Admin Route
// ===============================
function AdminRoute({ children }) {
  const token = localStorage.getItem("token");
  const role = localStorage.getItem("role");

  if (!token) return <Navigate to="/login" replace />;
  if (role !== "ADMIN") return <Navigate to="/" replace />;

  return children;
}

// ===============================
//  Layout
// ===============================
function Layout({ children }) {
  const location = useLocation();

  const isAuthPage =
    location.pathname === "/login" || location.pathname === "/register";

  const role = localStorage.getItem("role");

  return (
    <>
      {/*  Sidebar */}
      {!isAuthPage && (
        role === "ADMIN" ? <AdminSidebar /> : <Sidebar />
      )}

      {/*  Main */}
      <div
        style={{
          marginLeft: isAuthPage ? "0" : "240px",
          padding: "20px",
          minHeight: "100vh",
          background: "#0f172a"
        }}
      >
        {children}
      </div>
    </>
  );
}

// ===============================
//  APP ROUTES
// ===============================
export default function App() {
  const role = localStorage.getItem("role");

  return (
    <BrowserRouter>
      <Layout>
        <Routes>

          {/* 🔓 Public */}
          <Route path="/login" element={<Login />} />
          <Route path="/register" element={<Register />} />

          {/* 🔐 ROOT ( CRITICAL FIX) */}
          <Route
            path="/"
            element={
              <PrivateRoute>
                {role === "ADMIN"
                  ? <Navigate to="/admin" replace />
                  : <Dashboard />}
              </PrivateRoute>
            }
          />

          {/*  USER ROUTES */}
          <Route
            path="/apikeys"
            element={
              <PrivateRoute>
                <ApiKeys />
              </PrivateRoute>
            }
          />

          <Route
            path="/billing"
            element={
              <PrivateRoute>
                <Billing />
              </PrivateRoute>
            }
          />

          <Route
            path="/plans"
            element={
              <PrivateRoute>
                <Plans />
              </PrivateRoute>
            }
          />

          {/*  ADMIN ROUTES */}
          <Route
            path="/admin"
            element={
              <AdminRoute>
                <AdminDashboard />
              </AdminRoute>
            }
          />

          <Route
            path="/admin/apikeys"
            element={
              <AdminRoute>
                <AdminApiKeys />
              </AdminRoute>
            }
          />

          <Route
            path="/admin/users"
            element={
              <AdminRoute>
                <AdminUsers />
              </AdminRoute>
            }
          />

          <Route
            path="/admin/revenue"
            element={
              <AdminRoute>
                <AdminRevenue />
              </AdminRoute>
            }
          />

          <Route
            path="/admin/traffic"
            element={
              <AdminRoute>
                <AdminTraffic />
              </AdminRoute>
            }
          />

          {/* Fallback */}
          <Route path="*" element={<Navigate to="/" replace />} />

        </Routes>
      </Layout>
    </BrowserRouter>
  );
}