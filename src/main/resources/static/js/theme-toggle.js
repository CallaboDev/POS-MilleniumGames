(function() {
    function applyTheme(theme) {
        const htmlEl = document.documentElement;
        const themeIcon = document.getElementById('themeIcon');
        const themeText = document.getElementById('themeText');

        if (theme === 'dark') {
            htmlEl.setAttribute('data-theme', 'dark');
            htmlEl.setAttribute('data-bs-theme', 'dark');
            if (themeIcon) {
                themeIcon.className = 'bi bi-sun-fill text-warning';
            }
            if (themeText) {
                themeText.textContent = 'Modo claro';
            }
        } else {
            htmlEl.setAttribute('data-theme', 'light');
            htmlEl.setAttribute('data-bs-theme', 'light');
            if (themeIcon) {
                themeIcon.className = 'bi bi-moon-fill';
            }
            if (themeText) {
                themeText.textContent = 'Modo oscuro';
            }
        }
    }

    function initTheme() {
        const savedTheme = localStorage.getItem('theme') || 'light';
        applyTheme(savedTheme);

        const btn = document.getElementById('themeToggleBtn');
        if (btn) {
            btn.onclick = function(e) {
                e.preventDefault();
                const current = document.documentElement.getAttribute('data-theme') || 'light';
                const nextTheme = current === 'dark' ? 'light' : 'dark';
                try {
                    localStorage.setItem('theme', nextTheme);
                } catch (err) {}
                applyTheme(nextTheme);
            };
        }
    }

    if (document.readyState === 'loading') {
        document.addEventListener('DOMContentLoaded', initTheme);
    } else {
        initTheme();
    }
})();
