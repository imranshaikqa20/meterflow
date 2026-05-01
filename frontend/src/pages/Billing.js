import { useEffect, useState } from "react";
import api from "../services/api";

export default function Billing() {
  const [bills, setBills] = useState([]);
  const [loading, setLoading] = useState(true);
  const [generating, setGenerating] = useState(false);
  const [error, setError] = useState("");
  const [apiKey, setApiKey] = useState("");

  useEffect(() => {
    const storedKey = localStorage.getItem("apiKey");

    if (!storedKey || storedKey === "undefined") {
      setError("❌ No API key selected. Go to API Keys and click USE.");
      setLoading(false);
      return;
    }

    setApiKey(storedKey);
  }, []);

  useEffect(() => {
    if (apiKey) fetchBilling(apiKey);
  }, [apiKey]);

  const fetchBilling = async (key) => {
    try {
      setLoading(true);
      setError("");

      const res = await api.get(`/billing/${key}`);

      if (Array.isArray(res.data)) {
        setBills(res.data);
        if (res.data.length === 0) {
          setError("No billing data. Click Generate Bill.");
        }
      } else {
        setBills([res.data]);
      }

    } catch (err) {
      setError("Failed to load billing data");
      setBills([]);
    } finally {
      setLoading(false);
    }
  };

  const generateBill = async () => {
    setGenerating(true);
    try {
      await api.post(`/billing/generate/${apiKey}`);
      fetchBilling(apiKey);
    } catch {
      alert("Failed to generate bill");
    } finally {
      setGenerating(false);
    }
  };

  return (
    <div style={styles.container}>
      <div style={styles.card}>

        {/* HEADER */}
        <div style={styles.header}>
          <div>
            <h2 style={styles.title}>💰 Billing </h2>
            <p style={styles.subtitle}>Track usage and payments</p>
          </div>

          <button
            style={styles.button}
            onClick={generateBill}
            disabled={generating}
          >
            {generating ? "Generating..." : "+ Generate Bill"}
          </button>
        </div>

        {/* API KEY */}
        <div style={styles.apiBox}>
          🔑 {apiKey || "No API Key Selected"}
        </div>

        {/* ERROR */}
        {error && <p style={styles.error}>{error}</p>}

        {/* CONTENT */}
        {loading ? (
          <p style={styles.text}>Loading billing data...</p>
        ) : bills.length === 0 ? (
          <p style={styles.text}>No billing data available</p>
        ) : (
          <div style={styles.tableWrapper}>
            <table style={styles.table}>
              <thead>
                <tr>
                  <th>ID</th>
                  <th>Requests</th>
                  <th>Amount</th>
                  <th>Status</th>
                  <th>Date</th>
                </tr>
              </thead>

              <tbody>
                {bills.map((b) => (
                  <tr key={b.id} style={styles.row}>
                    <td>{b.id}</td>

                    <td>
                      <span style={styles.requests}>
                        {b.totalRequests}
                      </span>
                    </td>

                    <td style={styles.amount}>
                      ₹{b.amount}
                    </td>

                    <td>
                      <span
                        style={{
                          ...styles.badge,
                          background:
                            b.status === "PAID"
                              ? "linear-gradient(135deg,#22c55e,#16a34a)"
                              : "linear-gradient(135deg,#f59e0b,#d97706)"
                        }}
                      >
                        {b.status}
                      </span>
                    </td>

                    <td style={styles.date}>
                      {b.createdAt
                        ? new Date(b.createdAt).toLocaleString()
                        : "-"}
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

/* 🎨 MODERN STYLES */
const styles = {
  container: {
    padding: "30px",
    background: "#0f172a",
    minHeight: "100vh",
    color: "white"
  },

  card: {
    background: "#1e293b",
    borderRadius: "16px",
    padding: "25px",
    boxShadow: "0 10px 30px rgba(0,0,0,0.4)"
  },

  header: {
    display: "flex",
    justifyContent: "space-between",
    alignItems: "center",
    marginBottom: "20px"
  },

  title: {
    margin: 0
  },

  subtitle: {
    fontSize: "13px",
    color: "#94a3b8"
  },

  button: {
    padding: "10px 18px",
    borderRadius: "8px",
    border: "none",
    background: "linear-gradient(135deg,#6366f1,#3b82f6)",
    color: "white",
    cursor: "pointer",
    fontWeight: "500"
  },

  apiBox: {
    background: "#0f172a",
    padding: "10px",
    borderRadius: "8px",
    fontSize: "12px",
    marginBottom: "15px",
    color: "#94a3b8"
  },

  tableWrapper: {
    overflowX: "auto"
  },

  table: {
    width: "100%",
    borderCollapse: "collapse",
    textAlign: "center"
  },

  row: {
    borderTop: "1px solid #334155"
  },

  requests: {
    fontWeight: "bold",
    color: "#60a5fa"
  },

  amount: {
    color: "#22c55e",
    fontWeight: "bold"
  },

  badge: {
    padding: "5px 12px",
    borderRadius: "20px",
    fontSize: "12px",
    color: "white"
  },

  date: {
    fontSize: "12px",
    color: "#cbd5f5"
  },

  text: {
    color: "#cbd5f5"
  },

  error: {
    color: "#ef4444",
    marginBottom: "10px"
  }
};