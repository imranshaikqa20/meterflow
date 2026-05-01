import axios from "axios";

const api = axios.create({
  baseURL: "https://meterflow-backend-mmaw.onrender.com",
  headers: {
    "Content-Type": "application/json",
  },
});

// ===============================
//  Helper: Normalize URL
// ===============================
const getPath = (url) => {
  if (!url) return "";
  try {
    return new URL(url, "http://localhost").pathname;
  } catch {
    return url;
  }
};

// ===============================
//  Public endpoints (no API key)
// ===============================
const isPublicEndpoint = (path) => {
  return (
    path.startsWith("/auth") ||
    path.startsWith("/plans") ||
    path.startsWith("/payment") ||
    path.includes("/apikey/generate") ||
    path.includes("/public")
  );
};

// ===============================
//  REQUEST INTERCEPTOR
// ===============================
api.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem("token");
    const apiKey = localStorage.getItem("apiKey");
    const role = localStorage.getItem("role");

    const path = getPath(config.url);

    // 🔍 Debug
    if (process.env.NODE_ENV !== "production") {
      console.log("📡", config.method?.toUpperCase(), path);
    }

    // ===============================
    // ✅ Attach JWT
    // ===============================
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    } else {
      delete config.headers.Authorization;
    }

    // ===============================
    //  Attach API KEY (USER only)
    // ===============================
    const isAdminRoute = path.startsWith("/admin");
    const isBillingRoute = path.startsWith("/billing");

    if (
      apiKey &&
      !isPublicEndpoint(path) &&
      !isAdminRoute &&
      !isBillingRoute // 🔥 FIX: prevent billing errors
    ) {
      config.headers["x-api-key"] = apiKey;
    } else {
      delete config.headers["x-api-key"];
    }

    // ===============================
    //  Admin Tag (optional)
    // ===============================
    if (role === "ADMIN") {
      config.headers["x-user-role"] = "ADMIN";
    }

    return config;
  },
  (error) => Promise.reject(error)
);

// ===============================
//  RESPONSE INTERCEPTOR
// ===============================
api.interceptors.response.use(
  (response) => {
    const remaining = response.headers["x-rate-limit-remaining"];
    if (remaining !== undefined) {
      console.log("⚡ Remaining:", remaining);
    }
    return response;
  },
  (error) => {
    const res = error.response;

    console.log("❌ API Error:", res);

    if (!res) {
      showError("🌐 Network error. Backend may be down.");
      return Promise.reject(error);
    }

    const status = res.status;
    const message =
      res.data?.error || res.data?.message || "Something went wrong";

    switch (status) {
      case 400:
        showError("⚠️ " + message);
        break;

      case 401:
        showError("🔐 Unauthorized");

        // 🔥 Safe logout
        localStorage.clear();
        window.location.href = "/login";
        break;

      case 403:
        showError("🚫 Forbidden");
        break;

      case 404:
        showError("❓ Not Found");
        break;

      case 429:
        showError("🚫 Rate limit exceeded");
        break;

      case 500:
        showError("💥 Server error");
        break;

      default:
        showError(message);
    }

    return Promise.reject(error);
  }
);

// ===============================
//  Error Handler
// ===============================
function showError(msg) {
  // later replace with toast
  alert(msg);
}

export default api;