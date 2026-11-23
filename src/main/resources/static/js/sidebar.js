document.addEventListener('DOMContentLoaded', function () {
    const sidebar = document.getElementById('sidebar');
    const mainWrapper = document.getElementById('mainWrapper');
    const sidebarToggle = document.getElementById('sidebarToggle');
    const sidebarOverlay = document.getElementById('sidebarOverlay');

    function closeMobileSidebar() {
        if (sidebar) {
            sidebar.classList.remove('mobile-open');
        }
        if (sidebarOverlay) {
            sidebarOverlay.classList.remove('active');
        }
        document.body.classList.remove('mobile-sidebar-open');
    }

    if (sidebarToggle && sidebar) {
        sidebarToggle.addEventListener('click', function () {
            if (window.innerWidth < 992) {
                const isOpening = !sidebar.classList.contains('mobile-open');
                sidebar.classList.toggle('mobile-open', isOpening);
                if (sidebarOverlay) {
                    sidebarOverlay.classList.toggle('active', isOpening);
                }
                document.body.classList.toggle('mobile-sidebar-open', isOpening);
            } else {
                sidebar.classList.toggle('collapsed');
                if (mainWrapper) {
                    mainWrapper.classList.toggle('expanded');
                }
            }
        });
    }

    if (sidebarOverlay) {
        sidebarOverlay.addEventListener('click', closeMobileSidebar);
    }

    if (sidebar) {
        sidebar.querySelectorAll('.nav-link-item').forEach(function (link) {
            link.addEventListener('click', function () {
                if (window.innerWidth < 992) {
                    closeMobileSidebar();
                }
            });
        });
    }

    document.addEventListener('keydown', function (event) {
        if (event.key === 'Escape' && window.innerWidth < 992) {
            closeMobileSidebar();
        }
    });

    window.addEventListener('resize', function () {
        if (window.innerWidth >= 992) {
            closeMobileSidebar();
        }
    });
});
