import { Link, useLocation, useNavigate } from "react-router-dom";

export default function Sidebar() {
  const location = useLocation();
  const navigate = useNavigate();

  // 🔒 Hide sidebar on auth pages
  if (location.pathname === "/login" || location.pathname === "/register") {
    return null;
  }

  const user = JSON.parse(localStorage.getItem("user") || "{}");

  // 🔥 Smart display name
  const displayName =
    user.name ||
    (user.email ? user.email.split("@")[0] : "Guest");

  const currentPlan = user.plan || "FREE";

  const handleLogout = () => {
    localStorage.clear();
    navigate("/login");
  };

  const isActive = (path) => location.pathname === path;

  return (
    <div style={styles.sidebar}>
      {/* TOP SECTION */}
      <div>
        <h2 style={styles.logo}>🚀 MeterFlow</h2>

        {/* NAVIGATION */}
        <nav style={styles.nav}>
          <Link
            to="/"
            style={isActive("/") ? styles.activeLink : styles.link}
          >
            📊 Dashboard
          </Link>

          <Link
            to="/apikeys"
            style={isActive("/apikeys") ? styles.activeLink : styles.link}
          >
            🔑 API Keys
          </Link>

          <Link
            to="/billing"
            style={isActive("/billing") ? styles.activeLink : styles.link}
          >
            💰 Billing
          </Link>

          {/* 🔥 NEW PLANS PAGE */}
          <Link
            to="/plans"
            style={isActive("/plans") ? styles.activeLink : styles.link}
          >
            💎 Plans
          </Link>
        </nav>
      </div>

      {/* USER SECTION */}
      <div style={styles.userSection}>
        <div style={styles.userBox}>
          <div style={styles.avatar}>👤</div>

          <div>
            <p style={styles.userName}>{displayName}</p>

            {user.email && (
              <p style={styles.userEmail}>{user.email}</p>
            )}

            {/* 🔥 PLAN BADGE */}
            <span
              style={
                currentPlan === "PRO"
                  ? styles.proBadge
                  : styles.freeBadge
              }
            >
              {currentPlan}
            </span>
          </div>
        </div>

        <button onClick={handleLogout} style={styles.logoutBtn}>
          🚪 Logout
        </button>
      </div>
    </div>
  );
}

/* 🎨 STYLES */
const styles = {
  sidebar: {
    width: "240px",
    height: "100vh",
    background: "linear-gradient(180deg, #0f172a, #1e293b)",
    color: "white",
    position: "fixed",
    left: 0,
    top: 0,
    display: "flex",
    flexDirection: "column",
    justifyContent: "space-between",
    padding: "25px 20px",
    boxSizing: "border-box",
    borderRight: "1px solid #334155"
  },

  logo: {
    fontSize: "22px",
    fontWeight: "bold",
    marginBottom: "30px"
  },

  nav: {
    display: "flex",
    flexDirection: "column",
    gap: "10px"
  },

  link: {
    textDecoration: "none",
    color: "#cbd5f5",
    padding: "12px 15px",
    borderRadius: "8px",
    transition: "0.2s"
  },

  activeLink: {
    textDecoration: "none",
    color: "white",
    background: "linear-gradient(135deg, #6366f1, #3b82f6)",
    padding: "12px 15px",
    borderRadius: "8px",
    fontWeight: "600"
  },

  userSection: {
    borderTop: "1px solid #334155",
    paddingTop: "20px"
  },

  userBox: {
    display: "flex",
    gap: "10px",
    alignItems: "center",
    marginBottom: "15px"
  },

  avatar: {
    fontSize: "20px",
    background: "#334155",
    padding: "10px",
    borderRadius: "50%"
  },

  userName: {
    fontSize: "14px",
    fontWeight: "600"
  },

  userEmail: {
    fontSize: "12px",
    color: "#94a3b8"
  },

  freeBadge: {
    background: "#334155",
    padding: "3px 8px",
    borderRadius: "6px",
    fontSize: "10px",
    marginTop: "5px",
    display: "inline-block"
  },

  proBadge: {
    background: "#22c55e",
    padding: "3px 8px",
    borderRadius: "6px",
    fontSize: "10px",
    marginTop: "5px",
    display: "inline-block",
    color: "#000"
  },

  logoutBtn: {
    width: "100%",
    padding: "10px",
    background: "linear-gradient(135deg, #ef4444, #dc2626)",
    border: "none",
    color: "white",
    borderRadius: "8px",
    cursor: "pointer"
  }
};