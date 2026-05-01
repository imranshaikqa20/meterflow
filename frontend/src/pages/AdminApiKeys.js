import { useEffect, useState } from "react";
import api from "../services/api";

export default function AdminApiKeys() {
  const [keys, setKeys] = useState([]);
  const [usageMap, setUsageMap] = useState({});
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    fetchAllKeys();
  }, []);

  // ===============================
  //  FETCH ALL KEYS (ADMIN)
  // ===============================
  const fetchAllKeys = async () => {
    try {
      setLoading(true);

      const res = await api.get("/apikey/all"); // 🔥 ADMIN ENDPOINT
      setKeys(res.data);

      //  Fetch usage per key
      const usageData = {};

      await Promise.all(
        res.data.map(async (k) => {
          try {
            const usageRes = await api.get(`/usage/count/${k.key}`);
            usageData[k.key] = usageRes.data.count;
          } catch {
            usageData[k.key] = 0;
          }
        })
      );

      setUsageMap(usageData);

    } catch (err) {
      console.error("Admin keys error:", err);
    } finally {
      setLoading(false);
    }
  };

  // ===============================
  //  REVOKE KEY
  // ===============================
  const revokeKey = async (id) => {
    const confirm = window.confirm("Are you sure you want to revoke this key?");
    if (!confirm) return;

    try {
      await api.put(`/admin/apikey/${id}/disable`);

      alert("API Key revoked successfully");

      fetchAllKeys(); // refresh

    } catch (err) {
      console.error(err);
      alert("Failed to revoke key");
    }
  };

  // ===============================
  // COPY KEY
  // ===============================
  const copyKey = (key) => {
    navigator.clipboard.writeText(key);
    alert("Copied!");
  };

  return (
    <div style={styles.container}>
      <div style={styles.card}>

        {/* HEADER */}
        <div style={styles.header}>
          <h2 style={styles.title}>🛠 Admin - API Keys</h2>
          <p style={styles.subtitle}>Manage all user API keys</p>
        </div>

        {/* TABLE */}
        {loading ? (
          <p style={styles.text}>Loading...</p>
        ) : (
          <div style={styles.tableWrapper}>
            <table style={styles.table}>
              <thead>
                <tr>
                  <th>ID</th>
                  <th>User</th>
                  <th style={{ textAlign: "left" }}>Key</th>
                  <th>Status</th>
                  <th>Usage</th>
                  <th>Action</th>
                </tr>
              </thead>

              <tbody>
                {keys.map((k) => (
                  <tr key={k.id} style={styles.row}>
                    <td>{k.id}</td>

                    {/* USER */}
                    <td>{k.userId}</td>

                    {/* KEY */}
                    <td style={styles.left}>
                      <div style={styles.keyRow}>
                        <div style={styles.keyChip}>
                          {k.key.substring(0, 14)}••••••
                        </div>

                        <button
                          onClick={() => copyKey(k.key)}
                          style={styles.iconBtn}
                        >
                          📋
                        </button>
                      </div>
                    </td>

                    {/* STATUS */}
                    <td>
                      <span
                        style={{
                          ...styles.badge,
                          background:
                            k.status === "ACTIVE" ? "#22c55e" : "#ef4444"
                        }}
                      >
                        {k.status}
                      </span>
                    </td>

                    {/* USAGE */}
                    <td style={styles.usage}>
                      {usageMap[k.key] ?? 0}
                    </td>

                    {/* REVOKE BUTTON */}
                    <td>
                      {k.status === "ACTIVE" ? (
                        <button
                          style={styles.revokeBtn}
                          onClick={() => revokeKey(k.id)}
                        >
                          Revoke
                        </button>
                      ) : (
                        <span style={{ color: "#64748b" }}>Disabled</span>
                      )}
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

// ===============================
//  STYLES
// ===============================
const styles = {
  container: {
    padding: "30px",
    background: "#0f172a",
    minHeight: "100vh",
    color: "white"
  },

  card: {
    background: "#1e293b",
    borderRadius: "14px",
    padding: "20px"
  },

  header: {
    marginBottom: "15px"
  },

  title: {
    margin: 0
  },

  subtitle: {
    fontSize: "13px",
    color: "#94a3b8"
  },

  tableWrapper: {
    maxHeight: "400px",
    overflowY: "auto"
  },

  table: {
    width: "100%",
    borderCollapse: "collapse",
    textAlign: "center"
  },

  row: {
    borderTop: "1px solid #334155"
  },

  left: {
    textAlign: "left"
  },

  keyRow: {
    display: "flex",
    alignItems: "center",
    gap: "10px"
  },

  keyChip: {
    background: "#0f172a",
    padding: "6px 10px",
    borderRadius: "8px",
    fontFamily: "monospace",
    fontSize: "13px",
    color: "#cbd5f5",
    border: "1px solid #334155"
  },

  iconBtn: {
    padding: "5px 8px",
    background: "#334155",
    border: "none",
    borderRadius: "6px",
    color: "white",
    cursor: "pointer"
  },

  revokeBtn: {
    padding: "6px 12px",
    background: "#ef4444",
    border: "none",
    borderRadius: "6px",
    color: "white",
    cursor: "pointer"
  },

  badge: {
    padding: "4px 10px",
    borderRadius: "20px",
    fontSize: "12px",
    color: "white"
  },

  usage: {
    color: "#60a5fa",
    fontWeight: "600"
  },

  text: {
    color: "#cbd5f5"
  }
};