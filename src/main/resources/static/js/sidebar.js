document.addEventListener('DOMContentLoaded', function () {
    const sidebar = document.getElementById('sidebar');
    const mainWrapper = document.getElementById('mainWrapper');
    const sidebarToggle = document.getElementById('sidebarToggle');
    const sidebarOverlay = document.getElementById('sidebarOverlay');

    if (sidebarToggle && sidebar) {
        sidebarToggle.addEventListener('click', function () {
            if (window.innerWidth < 992) {
                sidebar.classList.toggle('mobile-open');
                if (sidebarOverlay) {
                    sidebarOverlay.classList.toggle('active');
                }
            } else {
                sidebar.classList.toggle('collapsed');
                if (mainWrapper) {
                    mainWrapper.classList.toggle('expanded');
                }
            }
        });
    }

    if (sidebarOverlay) {
        sidebarOverlay.addEventListener('click', function () {
            if (sidebar) {
                sidebar.classList.remove('mobile-open');
            }
            sidebarOverlay.classList.remove('active');
        });
    }
});
