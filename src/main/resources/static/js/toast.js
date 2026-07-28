(function () {
    'use strict';

    const AUTO_DISMISS_MS = 4000;

    function getToastContainer() {
        return document.querySelector('.eams-toast-container .toast-container');
    }

    function iconFor(type) {
        switch (type) {
            case 'success': return 'bi-check-circle-fill';
            case 'error':   return 'bi-x-circle-fill';
            case 'warning': return 'bi-exclamation-triangle-fill';
            default:        return 'bi-info-circle-fill';
        }
    }

    function colorVarFor(type) {
        switch (type) {
            case 'success': return 'var(--accent-primary)';
            case 'error':   return 'var(--accent-danger)';
            case 'warning': return 'var(--accent-warning)';
            default:        return 'var(--accent-info)';
        }
    }

    function showToast(type, message) {
        const container = getToastContainer();
        if (!container || !message) return;

        const toastEl = document.createElement('div');
        toastEl.className = 'toast eams-toast';
        toastEl.setAttribute('role', 'alert');
        toastEl.setAttribute('aria-live', 'assertive');
        toastEl.setAttribute('aria-atomic', 'true');

        toastEl.innerHTML =
            '<div class="d-flex align-items-center">' +
                '<div class="toast-body d-flex align-items-center gap-2">' +
                    '<i class="bi ' + iconFor(type) + '" style="color:' + colorVarFor(type) + '; font-size: 16px;"></i>' +
                    '<span>' + message + '</span>' +
                '</div>' +
                '<button type="button" class="btn-close btn-close-white me-2 m-auto" data-bs-dismiss="toast" aria-label="Close"></button>' +
            '</div>';

        container.appendChild(toastEl);

        const bsToast = new bootstrap.Toast(toastEl, { delay: AUTO_DISMISS_MS });
        bsToast.show();

        toastEl.addEventListener('hidden.bs.toast', function () {
            toastEl.remove();
        });
    }

    function fireFlashToastOnLoad() {
        const flashData = document.getElementById('eamsFlashData');
        if (!flashData) return;

        const success = flashData.getAttribute('data-success');
        const error = flashData.getAttribute('data-error');

        if (success && success !== 'null') showToast('success', success);
        if (error && error !== 'null') showToast('error', error);
    }

    window.eamsToast = showToast;

    document.addEventListener('DOMContentLoaded', fireFlashToastOnLoad);
})();