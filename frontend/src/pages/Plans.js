import { useEffect, useState } from "react";
import api from "../services/api";

export default function Plans() {
  const [plans, setPlans] = useState([]);
  const [user, setUser] = useState({});
  const [loading, setLoading] = useState(false);

  useEffect(() => {
    fetchPlans();
    const storedUser = JSON.parse(localStorage.getItem("user") || "{}");
    setUser(storedUser);
  }, []);

  //  Fetch plans from backend
  const fetchPlans = async () => {
    try {
      const res = await api.get("/plans");
      setPlans(res.data);
    } catch (err) {
      console.error(err);
      alert("❌ Failed to load plans");
    }
  };

  //  Handle payment
  const handlePayment = async (plan) => {
    try {
      setLoading(true);

      //  1. Create order
      const res = await api.post(`/payment/create-order?amount=${plan.price}`);


      const order =
        typeof res.data === "string" ? JSON.parse(res.data) : res.data;

      //  2. Razorpay options
      const options = {
        key: "rzp_test_SiRngPGWjNndye", // 🔥 replace with your key
        amount: order.amount,
        currency: "INR",
        name: "MeterFlow",
        description: `${plan.name} Plan`,
        order_id: order.id,

        handler: async function (response) {
          try {
            //  Verify payment
            await api.post("/payment/verify", {
              ...response,
              userId: user.id
            });

            alert("✅ Payment Successful!");

            // Update local user
            const updatedUser = {
              ...user,
              plan: plan.name
            };

            localStorage.setItem("user", JSON.stringify(updatedUser));
            setUser(updatedUser);

          } catch (err) {
            console.error(err);
            alert("❌ Payment verification failed");
          }
        }
      };

      const rzp = new window.Razorpay(options);
      rzp.open();

    } catch (err) {
      console.error(err);


      if (err.response?.status === 403) {
        alert("❌ Access denied (check backend security)");
      } else {
        alert("❌ Payment failed");
      }

    } finally {
      setLoading(false);
    }
  };

  return (
    <div style={styles.container}>
      <h2 style={styles.title}>💎 Choose Your Plan</h2>

      <div style={styles.grid}>
        {plans.map((plan) => {
          const isCurrent = user.plan === plan.name;

          return (
            <div key={plan.id} style={styles.card}>
              <h3 style={styles.planName}>{plan.name}</h3>

              <p style={styles.price}>
                ₹{plan.price} <span>/ month</span>
              </p>

              <p style={styles.limit}>
                {plan.requestLimit} requests/min
              </p>

              <p style={styles.desc}>{plan.description}</p>

              <button
                disabled={isCurrent || loading}
                onClick={() => handlePayment(plan)}
                style={{
                  ...styles.button,
                  opacity: isCurrent ? 0.6 : 1
                }}
              >
                {isCurrent
                  ? "Current Plan"
                  : loading
                  ? "Processing..."
                  : "Upgrade"}
              </button>
            </div>
          );
        })}
      </div>
    </div>
  );
}

/*  STYLES */
const styles = {
  container: {
    padding: "40px",
    color: "white"
  },

  title: {
    marginBottom: "25px"
  },

  grid: {
    display: "flex",
    gap: "20px",
    flexWrap: "wrap"
  },

  card: {
    background: "#1e293b",
    padding: "25px",
    borderRadius: "12px",
    width: "250px",
    boxShadow: "0 8px 20px rgba(0,0,0,0.3)",
    textAlign: "center"
  },

  planName: {
    fontSize: "18px",
    marginBottom: "10px"
  },

  price: {
    fontSize: "22px",
    fontWeight: "bold"
  },

  limit: {
    marginTop: "10px",
    fontSize: "14px",
    color: "#cbd5f5"
  },

  desc: {
    fontSize: "12px",
    marginTop: "10px",
    color: "#94a3b8"
  },

  button: {
    marginTop: "15px",
    padding: "10px",
    width: "100%",
    background: "linear-gradient(135deg, #6366f1, #3b82f6)",
    color: "white",
    border: "none",
    borderRadius: "6px",
    cursor: "pointer"
  }
};