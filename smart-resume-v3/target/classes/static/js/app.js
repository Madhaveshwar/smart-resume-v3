document.addEventListener("DOMContentLoaded", () => {
    const fileInput  = document.getElementById("fileInput");
    const fileCount  = document.getElementById("fileCount");
    const dropzone   = document.getElementById("dropzone");
    const uploadForm = document.getElementById("uploadForm");
    const submitBtn  = uploadForm ? uploadForm.querySelector("button[type='submit']") : null;

    if (fileInput && fileCount) {
        fileInput.addEventListener("change", () => {
            const n = fileInput.files.length;
            fileCount.textContent = n === 0
                ? "No files selected"
                : `${n} file${n > 1 ? "s" : ""} selected`;
        });
    }

    if (dropzone && fileInput) {
        ["dragenter","dragover"].forEach(e =>
            dropzone.addEventListener(e, ev => { ev.preventDefault(); dropzone.classList.add("drag-over"); }));
        ["dragleave","drop"].forEach(e =>
            dropzone.addEventListener(e, ev => { ev.preventDefault(); dropzone.classList.remove("drag-over"); }));
        dropzone.addEventListener("drop", e => {
            const files = Array.from(e.dataTransfer.files);
            const dt = new DataTransfer();
            files.forEach(f => dt.items.add(f));
            fileInput.files = dt.files;
            if (fileCount) fileCount.textContent = `${files.length} file${files.length > 1 ? "s" : ""} selected`;
        });
    }

    if (uploadForm && submitBtn) {
        uploadForm.addEventListener("submit", () => {
            submitBtn.disabled = true;
            submitBtn.innerHTML = `<span class="spinner"></span>Analysing…`;
        });
    }
});
