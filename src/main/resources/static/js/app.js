// EAMS front-end entry point.
document.addEventListener('DOMContentLoaded', function () {
    const toggleBtn = document.getElementById('eamsSidebarToggle');
    const sidebar = document.getElementById('eamsSidebar');
    if (toggleBtn && sidebar) {
        toggleBtn.addEventListener('click', function () {
            sidebar.classList.toggle('eams-sidebar--open');
        });
    }
});