import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import api from "../services/api";

export default function ApiKeys() {
  const [keys, setKeys] = useState([]);
  const [usageMap, setUsageMap] = useState({});
  const [loading, setLoading] = useState(true);

  const navigate = useNavigate();

  const getUserId = () => {
    const user = JSON.parse(localStorage.getItem("user"));
    return user?.id;
  };

  useEffect(() => {
    fetchKeys();
  }, []);

  const fetchKeys = async () => {
    try {
      const userId = getUserId();

      if (!userId) {
        alert("User session expired. Please login again.");
        return;
      }

      const res = await api.get(`/apikey/all/${userId}`);
      setKeys(res.data);

      // Fetch usage
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
      console.error(err);
      setKeys([]);
    } finally {
      setLoading(false);
    }
  };

  // ===============================
  //  NEW: REVOKE API KEY
  // ===============================
  const revokeKey = async (id) => {
    const confirmDelete = window.confirm("Are you sure you want to revoke this key?");
    if (!confirmDelete) return;

    try {
      await api.delete(`/apikey/revoke/${id}`);

      alert("API Key revoked successfully");


      fetchKeys();

    } catch (err) {
      console.error(err);
      alert("Failed to revoke key");
    }
  };

  const generateKey = async () => {
    try {
      const userId = getUserId();

      if (!userId) {
        alert("User session expired. Please login again.");
        return;
      }

      const res = await api.post(`/apikey/generate/${userId}`);
      alert("New Key:\n" + res.data.key);

      fetchKeys();

    } catch (err) {
      console.error(err);
    }
  };

  const copyKey = (key) => {
    navigator.clipboard.writeText(key);
    alert("Copied!");
  };

  const selectKey = (key) => {
    localStorage.setItem("apiKey", key);
    navigate("/billing");
  };

  return (
    <div style={styles.container}>
      <div style={styles.card}>

        {/* HEADER */}
        <div style={styles.header}>
          <div>
            <h2 style={styles.title}>🔑 API Keys</h2>
            <p style={styles.subtitle}>Manage your API access</p>
          </div>

          <button style={styles.btn} onClick={generateKey}>
            + Generate Key
          </button>
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
                  <th style={{ textAlign: "left" }}>Key</th>
                  <th>Status</th>
                  <th>Usage</th>
                  <th>Actions</th> {}
                </tr>
              </thead>

              <tbody>
                {keys.map((k) => (
                  <tr key={k.id} style={styles.row}>
                    <td>{k.id}</td>

                    {/* KEY */}
                    <td style={styles.left}>
                      <div style={styles.keyRow}>
                        <div style={styles.keyChip}>
                          {k.key.substring(0, 14)}••••••
                        </div>

                        <div style={styles.actionBtns}>
                          <button
                            onClick={() => copyKey(k.key)}
                            style={styles.iconBtn}
                          >
                            📋
                          </button>

                          <button
                            onClick={() => selectKey(k.key)}
                            style={styles.useBtn}
                            disabled={!k.active}
                          >
                            Use
                          </button>
                        </div>
                      </div>
                    </td>

                    {/* STATUS */}
                    <td>
                      <span
                        style={{
                          ...styles.badge,
                          background: k.active ? "#22c55e" : "#ef4444"
                        }}
                      >
                        {k.active ? "ACTIVE" : "REVOKED"}
                      </span>
                    </td>

                    {/* USAGE */}
                    <td style={styles.usage}>
                      {usageMap[k.key] ?? 0}
                    </td>

                    {/*  ACTIONS */}
                    <td>
                      {k.active ? (
                        <button
                          onClick={() => revokeKey(k.id)}
                          style={styles.revokeBtn}
                        >
                          Revoke
                        </button>
                      ) : (
                        <span style={styles.disabledText}>Disabled</span>
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

/* STYLES */
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
    display: "flex",
    justifyContent: "space-between",
    alignItems: "center",
    marginBottom: "15px"
  },

  title: { margin: 0 },

  subtitle: {
    fontSize: "13px",
    color: "#94a3b8"
  },

  btn: {
    padding: "8px 14px",
    borderRadius: "8px",
    border: "none",
    background: "#4f7cff",
    color: "white",
    cursor: "pointer"
  },

  tableWrapper: {
    maxHeight: "260px",
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
    justifyContent: "space-between",
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

  actionBtns: {
    display: "flex",
    gap: "6px"
  },

  iconBtn: {
    padding: "5px 8px",
    background: "#334155",
    border: "none",
    borderRadius: "6px",
    color: "white",
    cursor: "pointer"
  },

  useBtn: {
    padding: "5px 10px",
    background: "linear-gradient(135deg,#6366f1,#3b82f6)",
    border: "none",
    borderRadius: "6px",
    color: "white",
    cursor: "pointer",
    fontSize: "12px"
  },

  revokeBtn: {
    padding: "6px 12px",
    background: "#ef4444",
    border: "none",
    borderRadius: "6px",
    color: "white",
    cursor: "pointer",
    fontSize: "12px"
  },

  disabledText: {
    color: "#94a3b8",
    fontSize: "12px"
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