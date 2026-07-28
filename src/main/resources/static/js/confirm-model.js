(function () {
    'use strict';

    let modalInstance = null;
    let pendingForm = null;

    function getModalElements() {
        return {
            modalEl: document.getElementById('eamsConfirmModal'),
            messageEl: document.getElementById('eamsConfirmMessage'),
            actionBtn: document.getElementById('eamsConfirmActionBtn'),
        };
    }

    function initModal() {
        const { modalEl, actionBtn } = getModalElements();
        if (!modalEl || typeof bootstrap === 'undefined') return;

        modalInstance = new bootstrap.Modal(modalEl);

        actionBtn.addEventListener('click', function () {
            if (!pendingForm) return;
            actionBtn.disabled = true;
            actionBtn.innerHTML = '<span class="spinner-border spinner-border-sm"></span> Please wait…';
            const formToSubmit = pendingForm;
            pendingForm = null;
            modalInstance.hide();
            formToSubmit.submit();
        });

        modalEl.addEventListener('hidden.bs.modal', function () {
            actionBtn.disabled = false;
            actionBtn.innerHTML = 'Confirm';
            pendingForm = null;
        });
    }

    function interceptConfirmForms() {
        document.addEventListener('submit', function (event) {
            const form = event.target;
            if (!(form instanceof HTMLFormElement)) return;

            const confirmMessage = form.getAttribute('data-confirm');
            if (!confirmMessage) return;

            // Already confirmed and being submitted programmatically — let it through.
            if (form.dataset.confirmed === 'true') {
                delete form.dataset.confirmed;
                return;
            }

            event.preventDefault();

            const { messageEl } = getModalElements();
            if (!modalInstance) {
                if (window.confirm(confirmMessage)) {
                    form.submit();
                }
                return;
            }

            messageEl.textContent = confirmMessage;
            pendingForm = form;
            modalInstance.show();
        });
    }

    document.addEventListener('DOMContentLoaded', function () {
        initModal();
        interceptConfirmForms();
    });
})();