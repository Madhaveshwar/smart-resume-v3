// ── LOAD JOB TITLES (FIXED) ─────────────────────────
try {
  console.log("🌐 Calling API:", window.API_BASE + "/api/job-titles");

  const res = await fetch(window.API_BASE + "/api/job-titles");

  const sel = document.getElementById("jobTitleSelect");

  if (!res.ok) {
    throw new Error("API failed: " + res.status);
  }

  const titles = await res.json();

  console.log("✅ Titles received:", titles);

  // 🔥 IMPORTANT FIX
  if (!titles || titles.length === 0) {
    throw new Error("No data from API");
  }

  sel.innerHTML = `<option value="">-- Select or leave blank --</option>`;

  titles.forEach(t => {
    const opt = document.createElement("option");
    opt.value = t;
    opt.textContent = t;
    sel.appendChild(opt);
  });

} catch (e) {
  console.error("❌ Job titles error:", e);

  // 🔥 FALLBACK (VERY IMPORTANT)
  const sel = document.getElementById("jobTitleSelect");

  sel.innerHTML = `
    <option>Software Engineer</option>
    <option>Data Scientist</option>
    <option>Web Developer</option>
    <option>AI Engineer</option>
    <option>Backend Developer</option>
    <option>Frontend Developer</option>
  `;
}