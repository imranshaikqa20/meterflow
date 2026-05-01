import { useEffect, useState } from "react";
import API from "../services/api";
import AdminSidebar from "../components/AdminSidebar";

export default function AdminTraffic() {
  const [data, setData] = useState({
    totalRequests: 0,
    perEndpoint: [],
    daily: [],
    statusSummary: []
  });

  const [loading, setLoading] = useState(true);

  useEffect(() => {
    fetchTraffic();
  }, []);

  const fetchTraffic = async () => {
    try {
      setLoading(true);

      const res = await API.get("/admin/traffic");

      //
      setData({
        totalRequests: res.data?.totalRequests || 0,
        perEndpoint: res.data?.perEndpoint || [],
        daily: res.data?.daily || [],
        statusSummary: res.data?.statusSummary || []
      });

    } catch (err) {
      console.error("Traffic error:", err);
    } finally {
      setLoading(false);
    }
  };

  return (
    <div style={{ display: "flex" }}>
      <AdminSidebar />

      <div style={styles.container}>
        <h2 style={styles.title}>📊 Traffic Overview</h2>

        {loading ? (
          <p style={styles.loading}>Loading traffic data...</p>
        ) : (
          <>
            {/* TOTAL REQUESTS */}
            <div style={styles.card}>
              <h3>Total Requests</h3>
              <p style={styles.value}>{data.totalRequests}</p>
            </div>

            {/* ENDPOINT DATA */}
            <div style={styles.section}>
              <h3>🔗 Requests per Endpoint</h3>

              {data.perEndpoint.length === 0 ? (
                <p style={styles.empty}>No data available</p>
              ) : (
                <ul style={styles.list}>
                  {data.perEndpoint.map((item, i) => (
                    <li key={i} style={styles.listItem}>
                      <span>{item[0]}</span>
                      <strong>{item[1]}</strong>
                    </li>
                  ))}
                </ul>
              )}
            </div>

            {/* STATUS SUMMARY */}
            <div style={styles.section}>
              <h3>📌 Status Summary</h3>

              {data.statusSummary.length === 0 ? (
                <p style={styles.empty}>No data available</p>
              ) : (
                <ul style={styles.list}>
                  {data.statusSummary.map((item, i) => (
                    <li key={i} style={styles.listItem}>
                      <span>{item[0]}</span>
                      <strong>{item[1]}</strong>
                    </li>
                  ))}
                </ul>
              )}
            </div>
          </>
        )}
      </div>
    </div>
  );
}

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

  card: {
    background: "#1e293b",
    padding: "20px",
    borderRadius: "12px",
    marginBottom: "20px",
    boxShadow: "0 5px 15px rgba(0,0,0,0.3)"
  },

  value: {
    fontSize: "28px",
    fontWeight: "bold",
    marginTop: "10px"
  },

  section: {
    marginTop: "20px",
    background: "#1e293b",
    padding: "20px",
    borderRadius: "12px"
  },

  list: {
    listStyle: "none",
    padding: 0,
    marginTop: "10px"
  },

  listItem: {
    display: "flex",
    justifyContent: "space-between",
    padding: "8px 0",
    borderBottom: "1px solid #334155"
  },

  empty: {
    color: "#94a3b8"
  }
};