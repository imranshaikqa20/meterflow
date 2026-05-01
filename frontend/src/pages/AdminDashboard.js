import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import api from "../services/api";

export default function AdminDashboard() {
  const [stats, setStats] = useState({
    users: 0,
    revenue: 0,
    requests: 0
  });

  const [loading, setLoading] = useState(true);
  const navigate = useNavigate();

  useEffect(() => {
    fetchDashboardData();
  }, []);

  const fetchDashboardData = async () => {
    try {
      setLoading(true);

      const [usersRes, revenueRes, trafficRes] = await Promise.all([
        api.get("/admin/users"),
        api.get("/admin/revenue"),
        api.get("/admin/traffic")
      ]);

      setStats({
        users: usersRes.data?.length || 0,
        revenue: revenueRes.data?.totalRevenue || 0,
        requests: trafficRes.data?.totalRequests || 0
      });

    } catch (err) {
      console.error("Dashboard error:", err);
    } finally {
      setLoading(false);
    }
  };

  return (
    <div style={styles.container}>
      <h1 style={styles.title}>📊 Admin Dashboard</h1>

      {loading ? (
        <p style={{ color: "white" }}>Loading...</p>
      ) : (
        <div style={styles.cardContainer}>

          {/* USERS */}
          <div style={styles.card} onClick={() => navigate("/admin/users")}>
            <h3>👥 Users</h3>
            <p style={styles.value}>{stats.users}</p>
          </div>

          {/* REVENUE */}
          <div style={styles.card} onClick={() => navigate("/admin/revenue")}>
            <h3>💰 Revenue</h3>
            <p style={styles.value}>₹ {stats.revenue}</p>
          </div>

          {/* TRAFFIC */}
          <div style={styles.card} onClick={() => navigate("/admin/traffic")}>
            <h3>📊 Requests</h3>
            <p style={styles.value}>{stats.requests}</p>
          </div>

        </div>
      )}
    </div>
  );
}

/*  STYLES */
const styles = {
  container: {
    marginLeft: "240px",
    padding: "30px",
    background: "#0f172a",
    minHeight: "100vh",
    color: "white"
  },

  title: {
    marginBottom: "30px"
  },

  cardContainer: {
    display: "flex",
    gap: "20px",
    flexWrap: "wrap"
  },

  card: {
    background: "#1e293b",
    padding: "20px",
    borderRadius: "12px",
    width: "220px",
    cursor: "pointer",
    transition: "0.3s",
    boxShadow: "0 5px 15px rgba(0,0,0,0.3)"
  },

  value: {
    fontSize: "24px",
    fontWeight: "bold",
    marginTop: "10px"
  }
};