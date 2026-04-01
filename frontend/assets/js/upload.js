document.addEventListener("DOMContentLoaded", async () => {
  console.log("✅ Upload JS Loaded");

  // Check API
  if (!window.API_BASE) {
    console.error("❌ API_BASE not defined");
    return;
  }

  // ── Load Job Titles ─────────────────────────
  try {
    const res = await fetch(`${window.API_BASE}/api/job-titles`);

    if (!res.ok) throw new Error("API error");

    const titles = await res.json();
    console.log("✅ Job Titles:", titles);

    const sel = document.getElementById("jobTitleSelect");

    if (!sel) {
      console.error("❌ Dropdown not found");
      return;
    }

    sel.innerHTML = `<option value="">-- Select or leave blank --</option>`;

    titles.forEach(t => {
      const opt = document.createElement("option");
      opt.value = t;
      opt.textContent = t;
      sel.appendChild(opt);
    });

  } catch (e) {
    console.error("❌ Failed to load job titles:", e);
  }

  // ── File Input Handling ─────────────────────
  const fileInput = document.getElementById("fileInput");
  const fileCount = document.getElementById("fileCount");

  if (fileInput && fileCount) {
    fileInput.addEventListener("change", () => {
      const n = fileInput.files.length;
      fileCount.textContent =
        n === 0 ? "No files selected" : `${n} file${n > 1 ? "s" : ""} selected`;
    });
  }

  // ── Drag & Drop ─────────────────────────────
  const dropzone = document.getElementById("dropzone");

  if (dropzone && fileInput && fileCount) {
    ["dragenter", "dragover"].forEach(ev =>
      dropzone.addEventListener(ev, e => {
        e.preventDefault();
        dropzone.classList.add("drag-over");
      })
    );

    ["dragleave", "drop"].forEach(ev =>
      dropzone.addEventListener(ev, e => {
        e.preventDefault();
        dropzone.classList.remove("drag-over");
      })
    );

    dropzone.addEventListener("drop", e => {
      const dt = new DataTransfer();
      Array.from(e.dataTransfer.files).forEach(f => dt.items.add(f));
      fileInput.files = dt.files;

      const n = dt.files.length;
      fileCount.textContent = `${n} file${n > 1 ? "s" : ""} selected`;
    });
  }

  // ── Submit Upload ───────────────────────────
  const submitBtn = document.getElementById("submitBtn");

  if (submitBtn && fileInput) {
    submitBtn.addEventListener("click", async () => {

      if (fileInput.files.length === 0) {
        alert("Please select at least one resume file.");
        return;
      }

      submitBtn.disabled = true;
      submitBtn.innerHTML = "Analysing...";

      const formData = new FormData();

      // ✅ FIX: only send ONE file (backend expects single file)
      const file = fileInput.files[0];
      formData.append("file", file);

      // ✅ FIX: correct names
      formData.append(
        "jobTitle",
        document.getElementById("jobTitleSelect")?.value || ""
      );

      formData.append(
        "jobDescription",
        document.getElementById("jobDescInput")?.value || ""
      );

      try {
        // ✅ FIX: correct endpoint
        const res = await fetch(`${window.API_BASE}/api/upload`, {
          method: "POST",
          body: formData
        });

        if (!res.ok) throw new Error("Upload failed");

        const data = await res.json();
        console.log("✅ Response:", data);

        alert("✅ Upload successful!");

      } catch (e) {
        console.error("❌ Upload error:", e);
        alert("Error uploading file. Check backend.");
      } finally {
        submitBtn.disabled = false;
        submitBtn.innerHTML = "🔍 Analyse Resumes";
      }
    });
  }
});

// ── Results UI ─────────────────────────────────────────

function showResults(data) {
  document.getElementById("heroSection")?.classList.add("hidden");
  document.getElementById("uploadSection")?.classList.add("hidden");
  document.getElementById("resultsHeader")?.classList.remove("hidden");

  const jobTitle = data.jobTitle || "";

  document.getElementById("resultJobTitle").textContent =
    jobTitle ? `— ${jobTitle}` : "";

  document.getElementById("resultSubtitle").textContent =
    `Upload successful`;

  document.getElementById("resumeCards").innerHTML = "";
}

// ── Reset UI ─────────────────────────────────────────

function showUploadForm() {
  document.getElementById("heroSection")?.classList.remove("hidden");
  document.getElementById("uploadSection")?.classList.remove("hidden");
  document.getElementById("resultsHeader")?.classList.add("hidden");
  document.getElementById("resumeCards").innerHTML = "";
}