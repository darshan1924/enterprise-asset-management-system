(function () {
    'use strict';

    function buildUrlWithPage(pageNumberZeroBased) {
        const url = new URL(window.location.href);
        url.searchParams.set('page', pageNumberZeroBased);
        return url.toString();
    }

    function initJumpToPage() {
        const bars = document.querySelectorAll('.eams-pagination-bar[data-total-pages]');

        bars.forEach(function (bar) {
            const totalPages = parseInt(bar.getAttribute('data-total-pages'), 10);
            if (!totalPages || totalPages <= 7) return; // numbered links already cover small sets

            const currentPage = parseInt(bar.getAttribute('data-current-page'), 10) || 0;

            const wrapper = document.createElement('div');
            wrapper.className = 'eams-jump-page';

            const label = document.createElement('span');
            label.textContent = 'Go to';
            label.style.fontSize = '12px';
            label.style.color = 'var(--text-muted)';

            const input = document.createElement('input');
            input.type = 'number';
            input.min = '1';
            input.max = String(totalPages);
            input.value = String(currentPage + 1);
            input.className = 'eams-jump-input';
            input.setAttribute('aria-label', 'Jump to page number');

            input.addEventListener('keydown', function (event) {
                if (event.key === 'Enter') {
                    event.preventDefault();
                    navigateToInputValue();
                }
            });

            input.addEventListener('blur', navigateToInputValue);

            function navigateToInputValue() {
                let target = parseInt(input.value, 10);
                if (isNaN(target) || target < 1) target = 1;
                if (target > totalPages) target = totalPages;
                const zeroBased = target - 1;
                if (zeroBased !== currentPage) {
                    window.location.href = buildUrlWithPage(zeroBased);
                }
            }

            wrapper.appendChild(label);
            wrapper.appendChild(input);
            bar.appendChild(wrapper);
        });
    }

    document.addEventListener('DOMContentLoaded', initJumpToPage);
})();