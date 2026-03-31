// ── Shared UI components used across all pages ────────────────────

/**
 * Render a list of resume objects into an HTML string of cards.
 * Mirrors the Thymeleaf #resumeCards fragment exactly.
 */
function renderResumeCards(resumes, mode) {
  if (!resumes || resumes.length === 0) {
    return `<p class="text-center text-gray-500 mt-10">
      No resumes found. ${mode === 'dashboard' ? 'Go to Upload to add some.' : 'Upload some first, or try a different keyword.'}
    </p>`;
  }

  return resumes.map((r, i) => {
    const pct   = r.matchPercentage || 0;
    const glow  = pct >= 75 ? 'card-glow-green' : (pct >= 40 ? 'card-glow-yellow' : 'card-glow-red');
    const badge = pct >= 75
      ? 'bg-green-900 text-green-300 border-green-700'
      : (pct >= 40 ? 'bg-yellow-900 text-yellow-300 border-yellow-700' : 'bg-red-900 text-red-300 border-red-700');
    const avatarBg = pct >= 75 ? 'bg-green-700' : (pct >= 40 ? 'bg-yellow-600' : 'bg-red-700');
    const barColor = pct >= 75 ? 'bg-green-500' : (pct >= 40 ? 'bg-yellow-400' : 'bg-red-500');
    const statusText = pct >= 75
      ? '✅ Strong match — ready to shortlist'
      : (pct >= 40 ? '🟡 Moderate match — worth reviewing' : '🔴 Low match — see suggestions below');
    const statusColor = pct >= 75 ? 'text-green-600' : (pct >= 40 ? 'text-yellow-600' : 'text-red-600');

    const matchedSkillsHtml = (r.matchedSkills || []).length
      ? `<div class="mb-3">
          <p class="section-label text-green-500">✅ Matched Skills (${r.matchedSkills.length})</p>
          <div class="flex flex-wrap">${r.matchedSkills.map(s => `<span class="skill-chip chip-matched">${s}</span>`).join('')}</div>
         </div>` : '';

    const missingSkillsHtml = (r.missingSkills || []).length
      ? `<div class="mb-3">
          <p class="section-label text-red-400">❌ Missing Skills (${r.missingSkills.length})</p>
          <div class="flex flex-wrap">${r.missingSkills.map(s => `<span class="skill-chip chip-missing">${s}</span>`).join('')}</div>
         </div>` : '';

    const detectedSkillsHtml = (!(r.matchedSkills || []).length && (r.detectedSkills || []).length)
      ? `<div class="mb-3">
          <p class="section-label text-blue-400">🔎 Detected Skills</p>
          <div class="flex flex-wrap">${r.detectedSkills.map(s => `<span class="skill-chip chip-detected">${s}</span>`).join('')}</div>
         </div>` : '';

    const suggestionsHtml = (r.suggestions || []).length
      ? `<div class="mt-4 bg-gray-800 border border-yellow-900/50 rounded-xl p-4">
          <p class="section-label text-yellow-400 mb-3">💡 How to Improve Your Resume for This Role</p>
          ${r.suggestions.map(s => `<div class="suggestion-item"><span>${s}</span></div>`).join('')}
         </div>` : '';

    const recommendedHtml = (r.recommendedRoles || []).length
      ? `<div class="mt-4 bg-gray-800 border border-purple-900/50 rounded-xl p-4">
          <p class="section-label text-purple-400 mb-2">🎯 Better Matching Roles for This Candidate</p>
          <div class="flex flex-wrap">${r.recommendedRoles.map(s => `<span class="skill-chip chip-role">${s}</span>`).join('')}</div>
         </div>` : '';

    const suggestedJobsHtml = (!(r.recommendedRoles || []).length && (r.suggestedJobs || []).length)
      ? `<div class="mt-3 bg-gray-800 border border-yellow-900/40 rounded-xl p-3">
          <p class="section-label text-yellow-400 mb-2">💼 Suggested Roles</p>
          <div class="flex flex-wrap">${r.suggestedJobs.map(s => `<span class="skill-chip chip-role">${s}</span>`).join('')}</div>
         </div>` : '';

    const uploadedAtHtml = (mode === 'dashboard' && r.uploadedAt)
      ? `<p class="text-xs text-gray-600 mt-4 border-t border-gray-800 pt-3">Uploaded: ${formatDate(r.uploadedAt)}</p>`
      : '';

    return `
    <div class="bg-gray-900 border border-gray-800 rounded-2xl p-6 shadow-lg hover:border-gray-600 transition-all ${glow}">
      <div class="flex items-start gap-4 mb-4">
        <div class="flex-shrink-0 w-10 h-10 rounded-full flex items-center justify-center font-bold text-sm ${avatarBg}">${i + 1}</div>
        <div class="flex-1 min-w-0">
          <h3 class="text-lg font-bold truncate">${r.name || 'Unknown'}</h3>
          <p class="text-xs text-gray-500">${r.fileName || ''}</p>
        </div>
        <div class="border rounded-xl px-3 py-1 text-sm font-bold whitespace-nowrap flex-shrink-0 ${badge}">${pct}% Match</div>
      </div>
      <div class="progress-bar-wrap mb-1">
        <div class="progress-bar-fill ${barColor}" style="width: ${pct}%"></div>
      </div>
      <p class="text-xs mb-4 ${statusColor}">${statusText}</p>
      <div class="grid grid-cols-2 sm:grid-cols-4 gap-2 text-sm text-gray-300 mb-5">
        <div class="flex items-center gap-1.5 col-span-2 sm:col-span-1">
          <svg class="w-3.5 h-3.5 text-gray-500 flex-shrink-0" fill="none" viewBox="0 0 24 24" stroke="currentColor">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M3 8l7.89 5.26a2 2 0 002.22 0L21 8M5 19h14a2 2 0 002-2V7a2 2 0 00-2-2H5a2 2 0 00-2 2v10a2 2 0 002 2z"/>
          </svg>
          <span class="truncate text-xs">${r.email || 'N/A'}</span>
        </div>
        <div class="flex items-center gap-1.5">
          <svg class="w-3.5 h-3.5 text-gray-500 flex-shrink-0" fill="none" viewBox="0 0 24 24" stroke="currentColor">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M3 5a2 2 0 012-2h3.28a1 1 0 01.948.684l1.498 4.493a1 1 0 01-.502 1.21l-2.257 1.13a11.042 11.042 0 005.516 5.516l1.13-2.257a1 1 0 011.21-.502l4.493 1.498a1 1 0 01.684.949V19a2 2 0 01-2 2h-1C9.716 21 3 14.284 3 6V5z"/>
          </svg>
          <span class="text-xs">${r.phone || 'N/A'}</span>
        </div>
        <div class="flex items-center gap-1.5">
          <svg class="w-3.5 h-3.5 text-gray-500 flex-shrink-0" fill="none" viewBox="0 0 24 24" stroke="currentColor">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M21 13.255A23.931 23.931 0 0112 15c-3.183 0-6.22-.62-9-1.745M16 6V4a2 2 0 00-2-2h-4a2 2 0 00-2 2v2m4 6h.01M5 20h14a2 2 0 002-2V8a2 2 0 00-2-2H5a2 2 0 00-2 2v10a2 2 0 002 2z"/>
          </svg>
          <span class="text-xs">${r.experience || 'N/A'}</span>
        </div>
        <div class="flex items-center gap-1.5">
          <svg class="w-3.5 h-3.5 text-gray-500 flex-shrink-0" fill="none" viewBox="0 0 24 24" stroke="currentColor">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M13 10V3L4 14h7v7l9-11h-7z"/>
          </svg>
          <span class="text-xs text-gray-400">ATS Score: ${r.score || 0}</span>
        </div>
      </div>
      ${matchedSkillsHtml}
      ${missingSkillsHtml}
      ${detectedSkillsHtml}
      ${suggestionsHtml}
      ${recommendedHtml}
      ${suggestedJobsHtml}
      ${uploadedAtHtml}
    </div>`;
  }).join('');
}

function formatDate(isoStr) {
  try {
    const d = new Date(isoStr);
    return d.toLocaleString('en-GB', { day:'2-digit', month:'short', year:'numeric', hour:'2-digit', minute:'2-digit' });
  } catch { return isoStr; }
}

function setActiveNav(page) {
  document.querySelectorAll('nav a').forEach(a => {
    a.classList.remove('bg-gray-800', 'text-white');
    a.classList.add('text-gray-400', 'hover:text-white');
  });
  const active = document.querySelector(`nav a[data-page="${page}"]`);
  if (active) {
    active.classList.add('bg-gray-800', 'text-white');
    active.classList.remove('text-gray-400', 'hover:text-white');
  }
}
