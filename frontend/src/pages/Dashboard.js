import { useEffect, useState } from "react";
import api from "../services/api";
import {
  LineChart, Line,
  BarChart, Bar,
  PieChart, Pie, Cell,
  XAxis, YAxis, Tooltip, CartesianGrid, ResponsiveContainer
} from "recharts";

export default function Dashboard() {
  const [data, setData] = useState([]);
  const [filteredData, setFilteredData] = useState([]);
  const [search, setSearch] = useState("");
  const [loading, setLoading] = useState(true);
  const [activeTab, setActiveTab] = useState("line");
  const [error, setError] = useState("");
  const [remaining, setRemaining] = useState(null);

  useEffect(() => {
    fetchData();
  }, []);

  const fetchData = async () => {
    try {
      setLoading(true);
      setError("");

      const res = await api.get("/usage/summary");

      // ✅ DIRECTLY USE DTO RESPONSE
      const formatted = res.data.map(item => ({
        time: item.label || "Unknown API",   // ✅ FIX
        value: item.value || 0               // ✅ FIX
      }));

      setData(formatted);
      setFilteredData(formatted);

      const remain = res.headers["x-rate-limit-remaining"];
      if (remain !== undefined) setRemaining(remain);

    } catch (err) {
      if (err.response?.status === 429) {
        setError("🚫 Rate limit exceeded. Please wait 1 minute.");
      } else {
        setError("❌ Failed to load dashboard data");
      }
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    setFilteredData(
      data.filter(item =>
        item.time.toLowerCase().includes(search.toLowerCase())
      )
    );
  }, [search, data]);

  const totalRequests = data.reduce((sum, item) => sum + item.value, 0);
  const apiCount = data.length;
  const COLORS = ["#6366f1", "#22c55e", "#f59e0b", "#ef4444"];

  return (
    <div style={styles.container}>
      <div style={styles.wrapper}>
        <h1 style={styles.title}>📊 Dashboard</h1>

        {remaining !== null && (
          <p style={styles.rateLimit}>⚡ Remaining Requests: {remaining}</p>
        )}

        {error && <p style={styles.error}>{error}</p>}

        {loading ? (
          <div style={styles.loader}>Loading...</div>
        ) : (
          <>
            <input
              placeholder="🔍 Search API..."
              value={search}
              onChange={(e) => setSearch(e.target.value)}
              style={styles.search}
            />

            <div style={styles.stats}>
              <div style={styles.statCard}>
                <h4>Total Requests</h4>
                <p>{totalRequests}</p>
              </div>

              <div style={styles.statCard}>
                <h4>Unique APIs</h4>
                <p>{apiCount}</p>
              </div>
            </div>

            <div style={styles.tabs}>
              {["line", "bar", "pie", "table"].map((tab) => (
                <button
                  key={tab}
                  onClick={() => setActiveTab(tab)}
                  style={activeTab === tab ? styles.activeBtn : styles.btn}
                >
                  {tab === "line" && "📈 Usage Trend"}
                  {tab === "bar" && "📊 API Requests"}
                  {tab === "pie" && "🥧 Distribution"}
                  {tab === "table" && "📋 Details"}
                </button>
              ))}
            </div>

            <div style={styles.card}>

              {activeTab === "line" && (
                <ResponsiveContainer width="100%" height={300}>
                  <LineChart data={filteredData}>
                    <CartesianGrid strokeDasharray="3 3" />
                    <XAxis dataKey="time" />
                    <YAxis />
                    <Tooltip />
                    <Line dataKey="value" stroke="#6366f1" />
                  </LineChart>
                </ResponsiveContainer>
              )}

              {activeTab === "bar" && (
                <ResponsiveContainer width="100%" height={300}>
                  <BarChart data={filteredData}>
                    <XAxis dataKey="time" />
                    <YAxis />
                    <Tooltip />
                    <Bar dataKey="value" fill="#22c55e" />
                  </BarChart>
                </ResponsiveContainer>
              )}

              {activeTab === "pie" && (
                <ResponsiveContainer width="100%" height={300}>
                  <PieChart>
                    <Pie data={filteredData} dataKey="value" nameKey="time">
                      {filteredData.map((_, i) => (
                        <Cell key={i} fill={COLORS[i % COLORS.length]} />
                      ))}
                    </Pie>
                    <Tooltip />
                  </PieChart>
                </ResponsiveContainer>
              )}

              {activeTab === "table" && (
                <>
                  <h3 style={{ marginBottom: "15px" }}>API Usage Details</h3>

                  <div style={styles.tableWrapper}>
                    <table style={styles.table}>
                      <thead>
                        <tr>
                          <th style={styles.th}>API</th>
                          <th style={styles.th}>Requests</th>
                          <th style={styles.th}>%</th>
                        </tr>
                      </thead>

                      <tbody>
                        {filteredData.map((item, i) => {
                          const percent = totalRequests
                            ? ((item.value / totalRequests) * 100).toFixed(1)
                            : 0;

                          return (
                            <tr key={i} style={styles.row}>
                              <td style={styles.apiCell}>
                                {item.time}
                              </td>
                              <td style={styles.td}>{item.value}</td>
                              <td style={styles.td}>
                                <span style={styles.badge}>{percent}%</span>
                              </td>
                            </tr>
                          );
                        })}
                      </tbody>
                    </table>
                  </div>
                </>
              )}

            </div>
          </>
        )}
      </div>
    </div>
  );
}

/* styles unchanged */
const styles = {
  container: {
    minHeight: "100vh",
    background: "linear-gradient(135deg, #0f172a, #1e293b)",
    padding: "30px",
    display: "flex",
    justifyContent: "center"
  },
  wrapper: { width: "100%", maxWidth: "1100px" },
  title: { color: "#fff" },
  rateLimit: { color: "#22c55e" },
  error: { color: "#ef4444" },
  search: { width: "300px", padding: "10px", borderRadius: "8px" },
  stats: { display: "flex", gap: "20px" },
  statCard: { flex: 1, background: "#1e293b", padding: "15px", borderRadius: "10px", color: "#fff" },
  tabs: { margin: "15px 0" },
  btn: { marginRight: "10px" },
  activeBtn: { marginRight: "10px", background: "#6366f1", color: "#fff" },
  card: { background: "#1e293b", padding: "20px", borderRadius: "10px", color: "#fff" },
  tableWrapper: { overflowX: "auto", marginTop: "10px" },
  table: { width: "100%", borderCollapse: "separate", borderSpacing: "0 10px" },
  th: { textAlign: "left", padding: "10px", color: "#94a3b8" },
  row: { background: "rgba(255,255,255,0.05)" },
  td: { padding: "12px" },
  apiCell: { color: "#3b82f6", fontWeight: "500" },
  badge: {
    background: "#22c55e",
    padding: "4px 10px",
    borderRadius: "20px",
    fontSize: "12px"
  },
  loader: { color: "white" }
};