import { useState } from "react";
import { useNavigate } from "react-router-dom";
import api from "../services/api";

export default function Login() {
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState("");

  const navigate = useNavigate();

  const handleLogin = async () => {
    setError("");

    const trimmedEmail = email.trim();
    const trimmedPassword = password.trim();

    // 🔴 Validation
    if (!trimmedEmail || !trimmedPassword) {
      setError("Email and Password are required");
      return;
    }

    setLoading(true);

    try {
      const res = await api.post("/auth/login", {
        email: trimmedEmail,
        password: trimmedPassword
      });

      console.log("✅ Login Response:", res.data);

      // ===============================
      //  SAFE EXTRACTION
      // ===============================
      const token = res.data?.token;
      let role = res.data?.role || "USER";

      role = role.toUpperCase(); // normalize

      if (!token) {
        throw new Error("Token missing in response");
      }

      // ===============================
      // 💾 STORE DATA
      // ===============================
      localStorage.setItem("token", token);
      localStorage.setItem("role", role);

      const userData = {
        id: res.data?.userId,
        email: res.data?.email || trimmedEmail,
        plan: res.data?.plan || "FREE",
        role: role
      };

      localStorage.setItem("user", JSON.stringify(userData));

      // cleanup old api key
      localStorage.removeItem("apiKey");

      console.log("🧠 Stored role:", role);

      // ===============================
      //  REDIRECT (CORRECT WAY)
      // ===============================
      if (role === "ADMIN") {
        navigate("/admin");
      } else {
        navigate("/");
      }

    } catch (err) {
      console.log("❌ Login error:", err);

      setError(
        err.response?.data?.error ||
        err.response?.data?.message ||
        err.message ||
        "Login failed. Please check your credentials."
      );
    } finally {
      setLoading(false);
    }
  };

  //  Enter key support
  const handleKeyPress = (e) => {
    if (e.key === "Enter") {
      handleLogin();
    }
  };

  return (
    <div style={styles.container}>
      <div style={styles.card}>
        <h2 style={styles.title}>🔐 Login</h2>

        {error && <p style={styles.error}>{error}</p>}

        <input
          style={styles.input}
          placeholder="Email"
          value={email}
          onChange={(e) => setEmail(e.target.value)}
          onKeyDown={handleKeyPress}
        />

        <input
          style={styles.input}
          placeholder="Password"
          type="password"
          value={password}
          onChange={(e) => setPassword(e.target.value)}
          onKeyDown={handleKeyPress}
        />

        <button
          onClick={handleLogin}
          disabled={loading}
          style={{
            ...styles.button,
            opacity: loading ? 0.6 : 1,
            cursor: loading ? "not-allowed" : "pointer"
          }}
        >
          {loading ? "Logging in..." : "Login"}
        </button>

        <p style={styles.text}>
          Don’t have an account?{" "}
          <span style={styles.link} onClick={() => navigate("/register")}>
            Register
          </span>
        </p>
      </div>
    </div>
  );
}

// ===============================
//  STYLES
// ===============================
const styles = {
  container: {
    height: "100vh",
    display: "flex",
    justifyContent: "center",
    alignItems: "center",
    background: "#0f172a"
  },

  card: {
    width: "350px",
    padding: "30px",
    background: "#1e293b",
    borderRadius: "10px",
    textAlign: "center",
    boxShadow: "0 10px 25px rgba(0,0,0,0.3)"
  },

  title: {
    color: "white",
    marginBottom: "20px"
  },

  input: {
    width: "100%",
    padding: "10px",
    marginBottom: "15px",
    borderRadius: "5px",
    border: "none",
    outline: "none"
  },

  button: {
    width: "100%",
    padding: "10px",
    background: "#3b82f6",
    color: "white",
    border: "none",
    borderRadius: "5px"
  },

  text: {
    marginTop: "15px",
    color: "#cbd5f5"
  },

  link: {
    color: "#3b82f6",
    cursor: "pointer"
  },

  error: {
    color: "#f87171",
    marginBottom: "10px"
  }
};