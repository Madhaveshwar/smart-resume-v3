// 🔥 DEBUG
console.log("🔥 upload.js loaded");

// ── LOAD JOB TITLES ─────────────────────────
async function loadJobTitles() {
  console.log("🔥 Loading job titles...");

  const sel = document.getElementById("jobTitleSelect");

  if (!sel) {
    console.error("❌ Dropdown not found");
    return;
  }

  try {
    console.log("🌐 API:", window.API_BASE);

    const res = await fetch(window.API_BASE + "/api/job-titles");

    if (!res.ok) throw new Error("API failed");

    const titles = await res.json();

    console.log("✅ Titles:", titles);

    sel.innerHTML = `<option value="">-- Select or leave blank --</option>`;

    titles.forEach(t => {
      const opt = document.createElement("option");
      opt.value = t;
      opt.textContent = t;
      sel.appendChild(opt);
    });

  } catch (e) {
    console.error("❌ API FAILED → using fallback", e);

    sel.innerHTML = `
      <option value="">-- Select or leave blank --</option>
      <option>Software Engineer</option>
      <option>Data Scientist</option>
      <option>Web Developer</option>
      <option>AI Engineer</option>
      <option>Backend Developer</option>
      <option>Frontend Developer</option>
    `;
  }
}

// ── MAIN LOAD ─────────────────────────
async function initUpload() {
  console.log("✅ Initialization started");

  await loadJobTitles();

  const fileInput = document.getElementById("fileInput");
  const analyzeBtn = document.getElementById("analyzeBtn");
  const dropzone = document.getElementById("dropzone");
  const fileCount = document.getElementById("fileCount");

  if (fileInput && dropzone && fileCount) {
    fileInput.addEventListener("change", () => {
      if (fileInput.files.length > 0) {
        fileCount.textContent = fileInput.files.length === 1 
          ? fileInput.files[0].name 
          : `${fileInput.files.length} files selected`;
      } else {
        fileCount.textContent = "No files selected";
      }
    });

    dropzone.addEventListener("dragover", (e) => {
      e.preventDefault();
      dropzone.classList.add("border-blue-500", "bg-gray-800");
    });

    dropzone.addEventListener("dragleave", (e) => {
      e.preventDefault();
      dropzone.classList.remove("border-blue-500", "bg-gray-800");
    });

    dropzone.addEventListener("drop", (e) => {
      e.preventDefault();
      dropzone.classList.remove("border-blue-500", "bg-gray-800");
      if (e.dataTransfer && e.dataTransfer.files && e.dataTransfer.files.length > 0) {
        fileInput.files = e.dataTransfer.files;
        fileInput.dispatchEvent(new Event("change"));
      }
    });
  }

  if (analyzeBtn && fileInput) {
    console.log("✅ Analysis button safely bound!");
    analyzeBtn.addEventListener("click", async () => {

      if (!fileInput.files || fileInput.files.length === 0) {
        alert("Please select at least one file.");
        return;
      }

      console.log("📂 Selected files:", fileInput.files);

      analyzeBtn.disabled = true;
      analyzeBtn.innerHTML = "Analysing...";

      const formData = new FormData();

      // 🔥 IMPORTANT FIX: MULTIPLE FILES
      Array.from(fileInput.files).forEach(file => {
        formData.append("files", file);
      });

      formData.append(
        "jobTitle",
        document.getElementById("jobTitleSelect")?.value || ""
      );

      formData.append(
        "jobDescription",
        document.getElementById("jobDescInput")?.value || ""
      );

      try {
        console.log("🚀 Sending request...");

        const res = await fetch(window.API_BASE + "/api/upload", {
          method: "POST",
          body: formData
        });

        console.log("📡 Response status:", res.status);

        if (!res.ok) {
          throw new Error("Upload failed: " + res.status);
        }

        const data = await res.json();
        console.log("✅ Response data:", data);

        showResults(data);

      } catch (e) {
        console.error("❌ Upload error:", e);
        alert("Upload failed. Check console.");
      } finally {
        analyzeBtn.disabled = false;
        analyzeBtn.innerHTML = "🔍 Analyse Resumes";
      }
    });
  } else {
    console.error("❌ Missing attach items. analyzeBtn:", !!analyzeBtn, "fileInput:", !!fileInput);
  }
}

// Safely execute when DOM is actually ready, supporting deferred loading naturally
if (document.readyState === 'loading') {
  document.addEventListener('DOMContentLoaded', initUpload);
} else {
  initUpload();
}


// ── RESULTS UI ─────────────────────────
function showResults(data) {
  document.getElementById("heroSection")?.classList.add("hidden");
  document.getElementById("uploadSection")?.classList.add("hidden");
  document.getElementById("resultsHeader")?.classList.remove("hidden");

  document.getElementById("resultJobTitle").textContent =
    data.jobTitle ? `— ${data.jobTitle}` : "";

  document.getElementById("resultSubtitle").textContent =
    `${data.totalResumes} resume(s) processed`;

  const container = document.getElementById("resumeCards");

  container.innerHTML = data.resumes.map((r, i) => `
    <div style="background:#111827; padding:20px; margin:15px 0; border-radius:10px;">
      
      <h3 style="color:white;">${i + 1}. ${r.name}</h3>

      <p style="color:#22c55e;">Match Score: ${r.score}%</p>

      <p style="color:#4ade80;">✔ ${r.matchedSkills.join(", ")}</p>

      <p style="color:#f87171;">❌ ${r.missingSkills.join(", ")}</p>

      <ul style="color:white;">
        ${r.suggestions.map(s => `<li>${s}</li>`).join("")}
      </ul>

    </div>
  `).join("");
}