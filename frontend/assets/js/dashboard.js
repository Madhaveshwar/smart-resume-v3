// ── Dashboard Page Logic ──────────────────────────────────────────

document.addEventListener("DOMContentLoaded", () => {
  setActiveNav('dashboard');
  loadDashboard();

  document.getElementById("clearBtn").addEventListener("click", async () => {
    if (!confirm("Delete ALL resumes from the database?")) return;
    try {
      await fetch(`${API_BASE}/api/clear`, { method: "POST" });
      loadDashboard();
    } catch (e) {
      alert("Error connecting to backend.");
      console.error(e);
    }
  });
});

async function loadDashboard() {
  const cards    = document.getElementById("resumeCards");
  const subtitle = document.getElementById("dashSubtitle");

  cards.innerHTML = `<p class="text-gray-500 text-center py-10">Loading…</p>`;

  try {
    const res  = await fetch(`${API_BASE}/api/dashboard`);
    const data = await res.json();

    subtitle.textContent = `${data.totalStored} resume(s) stored in database`;
    cards.innerHTML = renderResumeCards(data.resumes, "dashboard");
  } catch (e) {
    cards.innerHTML = `<p class="text-red-400 text-center py-10">Error connecting to backend. Make sure the Spring Boot server is running.</p>`;
    subtitle.textContent = "";
    console.error(e);
  }
}
