import { useEffect, useState } from "react";
import API from "../services/api";
import AdminSidebar from "../components/AdminSidebar";

export default function AdminRevenue() {
  const [revenue, setRevenue] = useState(0);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    fetchRevenue();
  }, []);

  const fetchRevenue = async () => {
    try {
      setLoading(true);

      const res = await API.get("/admin/revenue");

      //
      setRevenue(res.data?.totalRevenue || 0);

    } catch (err) {
      console.error("Revenue error:", err);
    } finally {
      setLoading(false);
    }
  };

  return (
    <div style={{ display: "flex" }}>
      <AdminSidebar />

      <div style={styles.container}>
        <h2 style={styles.title}>💰 Total Revenue</h2>

        {loading ? (
          <p style={styles.loading}>Loading...</p>
        ) : (
          <h1 style={styles.value}>₹ {revenue}</h1>
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

  value: {
    fontSize: "36px",
    fontWeight: "bold",
    color: "#22c55e"
  },

  loading: {
    color: "#cbd5f5"
  }
};