import { useEffect, useState } from "react";
import API from "../services/api";
import AdminSidebar from "../components/AdminSidebar";

export default function AdminUsers() {
  const [users, setUsers] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    fetchUsers();
  }, []);

  const fetchUsers = async () => {
    try {
      setLoading(true);
      const res = await API.get("/admin/users");
      setUsers(res.data || []);
    } catch (err) {
      console.error("Users error:", err);
    } finally {
      setLoading(false);
    }
  };

  return (
    <div style={{ display: "flex" }}>
      <AdminSidebar />

      <div style={styles.container}>
        <h2 style={styles.title}>👥 All Users</h2>

        {loading ? (
          <p style={styles.loading}>Loading users...</p>
        ) : users.length === 0 ? (
          <p style={styles.empty}>No users found</p>
        ) : (
          <div style={styles.tableWrapper}>
            <table style={styles.table}>
              <thead>
                <tr>
                  <th style={styles.th}>#</th>
                  <th style={styles.th}>Email</th>
                  <th style={styles.th}>Role</th>
                  <th style={styles.th}>Plan</th>
                </tr>
              </thead>

              <tbody>
                {users.map((u, i) => (
                  <tr key={i} style={styles.tr}>
                    <td style={styles.td}>{i + 1}</td>
                    <td style={styles.td}>{u.email}</td>

                    <td style={styles.td}>
                      <span
                        style={
                          u.role === "ADMIN"
                            ? styles.adminBadge
                            : styles.userBadge
                        }
                      >
                        {u.role}
                      </span>
                    </td>

                    <td style={styles.td}>
                      <span
                        style={
                          u.plan === "PRO"
                            ? styles.proBadge
                            : styles.freeBadge
                        }
                      >
                        {u.plan || "FREE"}
                      </span>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        )}
      </div>
    </div>
  );
}

/* 🎨 STYLES */
const styles = {
  container: {
    flex: 1,
    padding: "30px",
    background: "#0f172a",
    minHeight: "100vh",
    color: "white"
  },

  title: {
    marginBottom: "20px"
  },

  loading: {
    color: "#cbd5f5"
  },

  empty: {
    color: "#94a3b8"
  },

  tableWrapper: {
    background: "#1e293b",
    borderRadius: "12px",
    padding: "20px",
    boxShadow: "0 5px 15px rgba(0,0,0,0.3)",
    overflowX: "auto"
  },

  table: {
    width: "100%",
    borderCollapse: "collapse"
  },

  th: {
    textAlign: "left",
    padding: "12px",
    borderBottom: "1px solid #334155",
    color: "#94a3b8"
  },

  td: {
    padding: "12px",
    borderBottom: "1px solid #334155"
  },

  tr: {
    transition: "0.2s"
  },

  adminBadge: {
    background: "#ef4444",
    padding: "4px 10px",
    borderRadius: "6px",
    fontSize: "12px"
  },

  userBadge: {
    background: "#3b82f6",
    padding: "4px 10px",
    borderRadius: "6px",
    fontSize: "12px"
  },

  proBadge: {
    background: "#22c55e",
    padding: "4px 10px",
    borderRadius: "6px",
    fontSize: "12px",
    color: "#000"
  },

  freeBadge: {
    background: "#334155",
    padding: "4px 10px",
    borderRadius: "6px",
    fontSize: "12px"
  }
};