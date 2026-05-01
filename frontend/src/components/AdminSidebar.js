import { Link, useLocation, useNavigate } from "react-router-dom";

export default function AdminSidebar() {
  const location = useLocation();
  const navigate = useNavigate();

  const isActive = (path) => location.pathname === path;

  const handleLogout = () => {
    localStorage.clear();
    navigate("/login");
  };

  const user = JSON.parse(localStorage.getItem("user") || "{}");

  return (
    <div style={styles.sidebar}>

      {/* TOP */}
      <div>
        <h2 style={styles.logo}>🚀 Admin Panel</h2>

        <nav style={styles.nav}>
          <Link
            to="/admin"
            style={isActive("/admin") ? styles.activeLink : styles.link}
          >
            📊 Dashboard
          </Link>

          <Link
            to="/admin/users"
            style={isActive("/admin/users") ? styles.activeLink : styles.link}
          >
            👥 Users
          </Link>

          <Link
            to="/admin/revenue"
            style={isActive("/admin/revenue") ? styles.activeLink : styles.link}
          >
            💰 Revenue
          </Link>

          <Link
            to="/admin/traffic"
            style={isActive("/admin/traffic") ? styles.activeLink : styles.link}
          >
            📈 Traffic
          </Link>
        </nav>
      </div>

      {/* BOTTOM USER INFO */}
      <div>
        <div style={styles.userBox}>
          <div style={styles.avatar}>👤</div>

          <div>
            <p style={styles.name}>{user.email || "admin@gmail.com"}</p>
            <span style={styles.badge}>ADMIN</span>
          </div>
        </div>

        <button style={styles.logout} onClick={handleLogout}>
          🚪 Logout
        </button>
      </div>
    </div>
  );
}

/*  STYLES */
const styles = {
  sidebar: {
    width: "240px",
    height: "100vh",
    background: "linear-gradient(180deg, #0f172a, #1e293b)",
    color: "white",
    padding: "25px 20px",
    position: "fixed",
    left: 0,
    top: 0,
    display: "flex",
    flexDirection: "column",
    justifyContent: "space-between",
    borderRight: "1px solid #334155",
    boxSizing: "border-box"
  },

  logo: {
    fontSize: "22px",
    fontWeight: "bold",
    marginBottom: "25px"
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

  userBox: {
    display: "flex",
    gap: "10px",
    alignItems: "center",
    background: "#1e293b",
    padding: "10px",
    borderRadius: "8px",
    marginBottom: "10px"
  },

  avatar: {
    background: "#334155",
    padding: "10px",
    borderRadius: "50%"
  },

  name: {
    margin: 0,
    fontSize: "13px"
  },

  badge: {
    fontSize: "11px",
    color: "#3b82f6"
  },

  logout: {
    width: "100%",
    padding: "10px",
    background: "linear-gradient(135deg, #ef4444, #dc2626)",
    border: "none",
    color: "white",
    borderRadius: "8px",
    cursor: "pointer"
  }
};