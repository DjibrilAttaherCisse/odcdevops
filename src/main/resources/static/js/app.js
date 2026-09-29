/**
 * ODC Campus - DevOps Student Management Frontend
 * Single Page Application JavaScript Controller
 */

// API Base URL
const API_URL = '/api/students';

// State Management
let studentsState = [];
let studentToDelete = null;
let currentViewingStudent = null;
let searchDebounceTimeout = null;

// Avatar Gradient Palette
const AVATAR_GRADIENTS = [
    'linear-gradient(135deg, #6366f1 0%, #a855f7 100%)',
    'linear-gradient(135deg, #3b82f6 0%, #2dd4bf 100%)',
    'linear-gradient(135deg, #f43f5e 0%, #fb923c 100%)',
    'linear-gradient(135deg, #10b981 0%, #3b82f6 100%)',
    'linear-gradient(135deg, #8b5cf6 0%, #ec4899 100%)',
    'linear-gradient(135deg, #f59e0b 0%, #ef4444 100%)'
];

// DOM Elements
const studentsTableBody = document.getElementById('studentsTableBody');
const loadingState = document.getElementById('loadingState');
const emptyState = document.getElementById('emptyState');
const searchInput = document.getElementById('searchInput');
const clearSearchBtn = document.getElementById('clearSearchBtn');
const refreshBtn = document.getElementById('refreshBtn');
const openAddModalBtn = document.getElementById('openAddModalBtn');
const metricTotalStudents = document.getElementById('metricTotalStudents');
const tableCountInfo = document.getElementById('tableCountInfo');
const themeToggleBtn = document.getElementById('themeToggleBtn');
const toastContainer = document.getElementById('toastContainer');
const systemStatus = document.getElementById('systemStatus');

// Student Form Modal Elements
const studentModalBackdrop = document.getElementById('studentModalBackdrop');
const studentForm = document.getElementById('studentForm');
const modalTitle = document.getElementById('modalTitle');
const modalSubtitle = document.getElementById('modalSubtitle');
const modalBadgeIcon = document.getElementById('modalBadgeIcon');
const studentMatricule = document.getElementById('studentMatricule');
const studentNom = document.getElementById('studentNom');
const studentPrenom = document.getElementById('studentPrenom');
const studentAddresse = document.getElementById('studentAddresse');
const studentDateNaissance = document.getElementById('studentDateNaissance');
const closeModalBtn = document.getElementById('closeModalBtn');
const cancelModalBtn = document.getElementById('cancelModalBtn');
const saveBtnText = document.getElementById('saveBtnText');

// View Modal Elements
const viewModalBackdrop = document.getElementById('viewModalBackdrop');
const viewModalBody = document.getElementById('viewModalBody');
const closeViewModalBtn = document.getElementById('closeViewModalBtn');
const closeViewModalBtn2 = document.getElementById('closeViewModalBtn2');
const editFromViewBtn = document.getElementById('editFromViewBtn');

// Delete Modal Elements
const deleteModalBackdrop = document.getElementById('deleteModalBackdrop');
const closeDeleteModalBtn = document.getElementById('closeDeleteModalBtn');
const cancelDeleteBtn = document.getElementById('cancelDeleteBtn');
const confirmDeleteBtn = document.getElementById('confirmDeleteBtn');
const deleteModalMessage = document.getElementById('deleteModalMessage');

/* ==========================================================================
   Initialization & Event Listeners
   ========================================================================== */
document.addEventListener('DOMContentLoaded', () => {
    initTheme();
    loadStudents();
    setupEventListeners();
});

function setupEventListeners() {
    // Search with debounce
    searchInput.addEventListener('input', (e) => {
        const query = e.target.value.trim();
        clearSearchBtn.classList.toggle('active', query.length > 0);
        clearTimeout(searchDebounceTimeout);
        searchDebounceTimeout = setTimeout(() => {
            loadStudents(query);
        }, 300);
    });

    clearSearchBtn.addEventListener('click', () => {
        searchInput.value = '';
        clearSearchBtn.classList.remove('active');
        loadStudents();
        searchInput.focus();
    });

    refreshBtn.addEventListener('click', () => {
        refreshBtn.querySelector('i').classList.add('fa-spin');
        loadStudents(searchInput.value.trim()).finally(() => {
            setTimeout(() => refreshBtn.querySelector('i').classList.remove('fa-spin'), 400);
        });
    });

    openAddModalBtn.addEventListener('click', () => openAddStudentModal());

    // Student Form Submit
    studentForm.addEventListener('submit', handleFormSubmit);

    // Modal close triggers
    closeModalBtn.addEventListener('click', closeStudentModal);
    cancelModalBtn.addEventListener('click', closeStudentModal);

    closeViewModalBtn.addEventListener('click', closeViewModal);
    closeViewModalBtn2.addEventListener('click', closeViewModal);
    editFromViewBtn.addEventListener('click', () => {
        if (currentViewingStudent) {
            closeViewModal();
            openEditStudentModal(currentViewingStudent);
        }
    });

    closeDeleteModalBtn.addEventListener('click', closeDeleteModal);
    cancelDeleteBtn.addEventListener('click', closeDeleteModal);
    confirmDeleteBtn.addEventListener('click', handleDeleteConfirm);

    // Theme toggle
    themeToggleBtn.addEventListener('click', toggleTheme);

    // Close modals on backdrop click or ESC key
    [studentModalBackdrop, viewModalBackdrop, deleteModalBackdrop].forEach(backdrop => {
        backdrop.addEventListener('click', (e) => {
            if (e.target === backdrop) {
                closeAllModals();
            }
        });
    });

    document.addEventListener('keydown', (e) => {
        if (e.key === 'Escape') {
            closeAllModals();
        }
    });
}

/* ==========================================================================
   Data Fetching & Rendering
   ========================================================================== */
async function loadStudents(searchQuery = '') {
    showLoading(true);
    try {
        let url = API_URL;
        if (searchQuery) {
            url += `?search=${encodeURIComponent(searchQuery)}`;
        }

        const response = await fetch(url);
        if (!response.ok) {
            throw new Error(`Erreur HTTP: ${response.status}`);
        }

        const data = await response.json();
        studentsState = data;
        renderStudentsTable(data);
        updateMetrics();
        setApiStatus(true);
    } catch (error) {
        console.error('Erreur lors du chargement des étudiants:', error);
        showToast('Impossible de contacter le serveur backend.', 'error');
        setApiStatus(false);
        renderStudentsTable([]);
    } finally {
        showLoading(false);
    }
}

function renderStudentsTable(students) {
    studentsTableBody.innerHTML = '';

    if (!students || students.length === 0) {
        emptyState.classList.remove('hidden');
        tableCountInfo.textContent = '0 étudiant affiché';
        return;
    }

    emptyState.classList.add('hidden');
    tableCountInfo.textContent = `Affichage de ${students.length} étudiant(s)`;

    students.forEach((student) => {
        const initials = getInitials(student.nom, student.prenom);
        const avatarBg = getAvatarGradient(student.nom + student.prenom);

        const tr = document.createElement('tr');
        tr.innerHTML = `
            <td>
                <span class="matricule-badge">#${student.matricule}</span>
            </td>
            <td>
                <div class="student-identity">
                    <div class="student-avatar" style="background: ${avatarBg};">
                        ${initials}
                    </div>
                    <div class="student-name-group">
                        <span class="student-fullname">${escapeHtml(student.nom)} ${escapeHtml(student.prenom)}</span>
                        <span class="student-role">Étudiant ODC</span>
                    </div>
                </div>
            </td>
            <td>
                <span class="address-text">
                    <i class="fa-solid fa-location-dot"></i>
                    ${escapeHtml(student.addresse || '-')}
                </span>
            </td>
            <td>${formatDate(student.dateNaissance)}</td>
            <td>${formatDate(student.createdDate || student.updateDate)}</td>
            <td class="text-right">
                <div class="action-buttons">
                    <button class="action-btn view" title="Voir les détails" onclick="viewStudentDetails(${student.matricule})">
                        <i class="fa-solid fa-eye"></i>
                    </button>
                    <button class="action-btn edit" title="Modifier" onclick="prepareEditStudent(${student.matricule})">
                        <i class="fa-solid fa-pen-to-square"></i>
                    </button>
                    <button class="action-btn delete" title="Supprimer" onclick="prepareDeleteStudent(${student.matricule})">
                        <i class="fa-solid fa-trash"></i>
                    </button>
                </div>
            </td>
        `;
        studentsTableBody.appendChild(tr);
    });
}

function updateMetrics() {
    metricTotalStudents.textContent = studentsState.length;
}

function showLoading(isLoading) {
    if (isLoading) {
        loadingState.style.display = 'flex';
        emptyState.classList.add('hidden');
    } else {
        loadingState.style.display = 'none';
    }
}

function setApiStatus(isOnline) {
    if (isOnline) {
        systemStatus.innerHTML = `
            <span class="status-indicator"></span>
            <span class="status-text">API Connectée</span>
        `;
        systemStatus.style.borderColor = 'rgba(16, 185, 129, 0.25)';
        systemStatus.style.color = 'var(--accent-emerald)';
    } else {
        systemStatus.innerHTML = `
            <span class="status-indicator" style="background: var(--accent-rose); box-shadow: 0 0 10px var(--accent-rose);"></span>
            <span class="status-text" style="color: var(--accent-rose);">Déconnecté</span>
        `;
        systemStatus.style.borderColor = 'rgba(244, 63, 94, 0.3)';
    }
}

/* ==========================================================================
   CRUD Operations
   ========================================================================= */

// Form Submission (Create or Update)
async function handleFormSubmit(e) {
    e.preventDefault();
    clearFormErrors();

    const matricule = studentMatricule.value;
    const nom = studentNom.value.trim();
    const prenom = studentPrenom.value.trim();
    const addresse = studentAddresse.value.trim();
    const dateNaissance = studentDateNaissance.value;

    let hasError = false;
    if (!nom) {
        document.getElementById('errorNom').textContent = 'Le nom est obligatoire.';
        hasError = true;
    }
    if (!prenom) {
        document.getElementById('errorPrenom').textContent = 'Le prénom est obligatoire.';
        hasError = true;
    }
    if (!addresse) {
        document.getElementById('errorAddresse').textContent = "L'adresse est obligatoire.";
        hasError = true;
    }
    if (!dateNaissance) {
        document.getElementById('errorDateNaissance').textContent = 'La date de naissance est obligatoire.';
        hasError = true;
    }

    if (hasError) return;

    const payload = {
        nom: nom,
        prenom: prenom,
        addresse: addresse,
        dateNaissance: dateNaissance
    };

    saveBtnText.textContent = 'Enregistrement...';

    try {
        let response;
        if (matricule) {
            // Update
            response = await fetch(`${API_URL}/${matricule}`, {
                method: 'PUT',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(payload)
            });
        } else {
            // Create
            response = await fetch(API_URL, {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(payload)
            });
        }

        if (!response.ok) {
            const errData = await response.json().catch(() => ({}));
            throw new Error(errData.message || 'Une erreur est survenue lors de la sauvegarde.');
        }

        showToast(matricule ? 'Étudiant mis à jour avec succès !' : 'Nouvel étudiant créé avec succès !', 'success');
        closeStudentModal();
        loadStudents(searchInput.value.trim());
    } catch (error) {
        console.error('Erreur:', error);
        showToast(error.message, 'error');
    } finally {
        saveBtnText.textContent = 'Enregistrer';
    }
}

// Prepare Edit
window.prepareEditStudent = function(matricule) {
    const student = studentsState.find(s => s.matricule === matricule);
    if (student) {
        openEditStudentModal(student);
    }
};

// Prepare Delete
window.prepareDeleteStudent = function(matricule) {
    const student = studentsState.find(s => s.matricule === matricule);
    if (!student) return;

    studentToDelete = student;
    deleteModalMessage.innerHTML = `Voulez-vous vraiment supprimer l'étudiant <strong>${escapeHtml(student.nom)} ${escapeHtml(student.prenom)}</strong> (Matricule: #${student.matricule}) ?`;
    deleteModalBackdrop.classList.add('active');
};

async function handleDeleteConfirm() {
    if (!studentToDelete) return;

    confirmDeleteBtn.disabled = true;
    confirmDeleteBtn.innerHTML = '<i class="fa-solid fa-spinner fa-spin"></i> Suppression...';

    try {
        const response = await fetch(`${API_URL}/${studentToDelete.matricule}`, {
            method: 'DELETE'
        });

        if (!response.ok) {
            throw new Error('Erreur lors de la suppression de l’étudiant.');
        }

        showToast(`Étudiant #${studentToDelete.matricule} supprimé.`, 'success');
        closeDeleteModal();
        loadStudents(searchInput.value.trim());
    } catch (error) {
        console.error('Erreur:', error);
        showToast(error.message, 'error');
    } finally {
        confirmDeleteBtn.disabled = false;
        confirmDeleteBtn.innerHTML = '<i class="fa-solid fa-trash"></i> Supprimer';
        studentToDelete = null;
    }
}

// View Student Details
window.viewStudentDetails = async function(matricule) {
    const student = studentsState.find(s => s.matricule === matricule);
    if (!student) return;

    currentViewingStudent = student;
    const initials = getInitials(student.nom, student.prenom);
    const avatarBg = getAvatarGradient(student.nom + student.prenom);

    viewModalBody.innerHTML = `
        <div style="display:flex; justify-content:center; padding: 2rem;">
            <div class="spinner"></div>
        </div>
    `;
    viewModalBackdrop.classList.add('active');

    try {
        const response = await fetch(`${API_URL.replace('/students', '/grades')}/student/${matricule}`);
        const grades = await response.json();
        
        let gradesHtml = '';
        let totalScore = 0;
        let totalCredits = 0;

        if (grades && grades.length > 0) {
            gradesHtml = `
                <table class="students-table" style="margin-top: 1rem;">
                    <thead>
                        <tr>
                            <th>Matière</th>
                            <th>Note</th>
                            <th>Commentaire</th>
                        </tr>
                    </thead>
                    <tbody>
            `;
            grades.forEach(g => {
                totalScore += g.score * g.course.credits;
                totalCredits += g.course.credits;
                const scoreColor = g.score >= 10 ? 'var(--accent-emerald)' : 'var(--accent-rose)';
                gradesHtml += `
                    <tr>
                        <td><strong>${escapeHtml(g.course.name)}</strong> <br><small>Crédits: ${g.course.credits}</small></td>
                        <td style="color: ${scoreColor}; font-weight: bold;">${g.score.toFixed(2)} / 20</td>
                        <td>${escapeHtml(g.comments || '-')}</td>
                    </tr>
                `;
            });
            gradesHtml += `</tbody></table>`;
            
            if (totalCredits > 0) {
                const average = totalScore / totalCredits;
                const avgColor = average >= 10 ? 'var(--accent-emerald)' : 'var(--accent-rose)';
                gradesHtml += `
                    <div style="margin-top: 1rem; padding: 1rem; background: var(--bg-surface-elevated); border-radius: var(--radius-md); display: flex; justify-content: space-between; align-items: center;">
                        <span style="font-weight: 600;">Moyenne Générale</span>
                        <span style="font-size: 1.25rem; font-weight: 700; color: ${avgColor};">${average.toFixed(2)} / 20</span>
                    </div>
                `;
            }
        } else {
            gradesHtml = `<div style="padding: 1rem; text-align: center; color: var(--text-muted); background: rgba(0,0,0,0.1); border-radius: 8px; margin-top: 1rem;">Aucune note enregistrée pour le moment.</div>`;
        }

        viewModalBody.innerHTML = `
            <div class="detail-profile-card">
                <div class="detail-header">
                    <div class="detail-avatar-large" style="background: ${avatarBg}">
                        ${initials}
                    </div>
                    <div>
                        <h3 style="font-size: 1.35rem; font-weight: 700;">${escapeHtml(student.nom)} ${escapeHtml(student.prenom)}</h3>
                        <p style="color: var(--text-secondary); margin-top: 0.2rem;">
                            <span class="matricule-badge">Matricule #${student.matricule}</span>
                        </p>
                    </div>
                </div>

                <div class="detail-grid">
                    <div class="detail-item">
                        <div class="detail-item-label">Adresse</div>
                        <div class="detail-item-value">${escapeHtml(student.addresse || '-')}</div>
                    </div>
                    <div class="detail-item">
                        <div class="detail-item-label">Date de Naissance</div>
                        <div class="detail-item-value">${formatDate(student.dateNaissance)}</div>
                    </div>
                </div>

                <div style="margin-top: 1rem;">
                    <h4 style="margin-bottom: 0.5rem; font-size: 1rem; font-weight: 600;">Bulletin de Notes</h4>
                    ${gradesHtml}
                </div>
            </div>
        `;
    } catch (e) {
        viewModalBody.innerHTML = `<div style="padding: 2rem; color: var(--accent-rose); text-align: center;">Erreur lors du chargement des notes.</div>`;
    }
};

/* ==========================================================================
   Modal Helpers
   ========================================================================== */
window.openAddStudentModal = function() {
    studentForm.reset();
    clearFormErrors();
    studentMatricule.value = '';
    modalTitle.textContent = 'Ajouter un Étudiant';
    modalSubtitle.textContent = 'Remplissez les informations ci-dessous';
    modalBadgeIcon.innerHTML = '<i class="fa-solid fa-user-plus"></i>';
    modalBadgeIcon.classList.remove('danger');
    saveBtnText.textContent = 'Créer l’étudiant';
    studentModalBackdrop.classList.add('active');
    setTimeout(() => studentNom.focus(), 100);
};

function openEditStudentModal(student) {
    clearFormErrors();
    studentMatricule.value = student.matricule;
    studentNom.value = student.nom || '';
    studentPrenom.value = student.prenom || '';
    studentAddresse.value = student.addresse || '';
    studentDateNaissance.value = student.dateNaissance || '';

    modalTitle.textContent = 'Modifier l’Étudiant';
    modalSubtitle.textContent = `Mise à jour du profil #${student.matricule}`;
    modalBadgeIcon.innerHTML = '<i class="fa-solid fa-user-pen"></i>';
    saveBtnText.textContent = 'Mettre à jour';
    studentModalBackdrop.classList.add('active');
    setTimeout(() => studentNom.focus(), 100);
}

function closeStudentModal() {
    studentModalBackdrop.classList.remove('active');
    studentForm.reset();
    clearFormErrors();
}

function closeViewModal() {
    viewModalBackdrop.classList.remove('active');
    currentViewingStudent = null;
}

function closeDeleteModal() {
    deleteModalBackdrop.classList.remove('active');
    studentToDelete = null;
}

function closeAllModals() {
    closeStudentModal();
    closeViewModal();
    closeDeleteModal();
}

function clearFormErrors() {
    document.querySelectorAll('.field-error').forEach(el => el.textContent = '');
}

/* ==========================================================================
   Utilities
   ========================================================================== */
function getInitials(nom, prenom) {
    const first = (nom || '').trim().charAt(0);
    const second = (prenom || '').trim().charAt(0);
    return `${first}${second}`.toUpperCase() || 'ST';
}

function getAvatarGradient(seedStr) {
    let hash = 0;
    for (let i = 0; i < seedStr.length; i++) {
        hash = seedStr.charCodeAt(i) + ((hash << 5) - hash);
    }
    const index = Math.abs(hash) % AVATAR_GRADIENTS.length;
    return AVATAR_GRADIENTS[index];
}

function formatDate(dateStr) {
    if (!dateStr) return '-';
    try {
        const date = new Date(dateStr);
        if (isNaN(date.getTime())) return dateStr;
        return date.toLocaleDateString('fr-FR', {
            year: 'numeric',
            month: 'short',
            day: 'numeric'
        });
    } catch {
        return dateStr;
    }
}

function escapeHtml(text) {
    if (!text) return '';
    const map = {
        '&': '&amp;',
        '<': '&lt;',
        '>': '&gt;',
        '"': '&quot;',
        "'": '&#039;'
    };
    return text.toString().replace(/[&<>"']/g, m => map[m]);
}

function showToast(message, type = 'info') {
    const toast = document.createElement('div');
    toast.className = `toast toast-${type}`;

    let iconClass = 'fa-circle-info';
    if (type === 'success') iconClass = 'fa-circle-check';
    if (type === 'error') iconClass = 'fa-triangle-exclamation';

    toast.innerHTML = `
        <i class="fa-solid ${iconClass} toast-icon"></i>
        <div class="toast-message">${escapeHtml(message)}</div>
    `;

    toastContainer.appendChild(toast);

    setTimeout(() => {
        toast.style.opacity = '0';
        toast.style.transform = 'translateY(15px) scale(0.95)';
        setTimeout(() => toast.remove(), 250);
    }, 3500);
}

// Theme handling
function initTheme() {
    const savedTheme = localStorage.getItem('odc_theme') || 'dark';
    document.documentElement.setAttribute('data-theme', savedTheme);
    updateThemeIcon(savedTheme);
}

function toggleTheme() {
    const currentTheme = document.documentElement.getAttribute('data-theme') || 'dark';
    const newTheme = currentTheme === 'dark' ? 'light' : 'dark';
    document.documentElement.setAttribute('data-theme', newTheme);
    localStorage.setItem('odc_theme', newTheme);
    updateThemeIcon(newTheme);
}

function updateThemeIcon(theme) {
    themeToggleBtn.innerHTML = theme === 'dark'
        ? '<i class="fa-solid fa-sun"></i>'
        : '<i class="fa-solid fa-moon"></i>';
}
