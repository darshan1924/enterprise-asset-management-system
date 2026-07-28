(function () {
    'use strict';

    const DEBOUNCE_MS = 500;
    const MIN_CHARS = 2;

    function debounce(fn, delay) {
        let timer = null;
        return function (...args) {
            clearTimeout(timer);
            timer = setTimeout(function () {
                fn.apply(null, args);
            }, delay);
        };
    }

    function initKeywordSearch() {
        const searchForms = document.querySelectorAll('form.eams-search');

        searchForms.forEach(function (form) {
            const input = form.querySelector('input[name="keyword"]');
            if (!input) return;

            const submitDebounced = debounce(function () {
                if (input.value.length === 0 || input.value.length >= MIN_CHARS) {
                    form.submit();
                }
            }, DEBOUNCE_MS);

            input.addEventListener('input', submitDebounced);

            input.addEventListener('keydown', function (event) {
                if (event.key === 'Enter') {
                    event.preventDefault();
                    form.submit();
                }
            });
        });
    }

    function initFilterAutoSubmit() {
        const filterForms = document.querySelectorAll('form.eams-search, form[action$="/assets"]');

        filterForms.forEach(function (form) {
            const selects = form.querySelectorAll('select[name="categoryId"], select[name="status"], select[name="vendorId"]');
            selects.forEach(function (select) {
                select.addEventListener('change', function () {
                    form.submit();
                });
            });
        });
    }

    function init() {
        initKeywordSearch();
        initFilterAutoSubmit();
    }

    document.addEventListener('DOMContentLoaded', init);
})();