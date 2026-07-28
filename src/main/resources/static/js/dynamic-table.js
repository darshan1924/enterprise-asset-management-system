/**
 * EAMS — Client-side dynamic table sorting + server-side sort opt-in.
 *
 * Any <table class="eams-table"> gets sortable columns automatically.
 * - Headers WITHOUT data-sort-field: sort only the current page's rows (client-side).
 * - Headers WITH data-sort-field="fieldName": navigate with ?sort=fieldName,dir
 *   to trigger a true full-dataset sort on the server.
 *
 * Also wires the page-size dropdown from fragments/pagination.html.
 */
(function () {
    'use strict';

    function cellSortValue(cell) {
        const raw = cell.textContent.trim();

        if (/^\d{4}-\d{2}-\d{2}$/.test(raw)) {
            return { type: 'date', value: new Date(raw).getTime() };
        }

        const numericCandidate = raw.replace(/[₹,]/g, '');
        if (numericCandidate !== '' && !isNaN(numericCandidate)) {
            return { type: 'number', value: parseFloat(numericCandidate) };
        }

        return { type: 'text', value: raw.toLowerCase() };
    }

    function isActionColumn(table, columnIndex) {
        const sampleRows = table.querySelectorAll('tbody tr');
        for (let i = 0; i < Math.min(sampleRows.length, 3); i++) {
            const cell = sampleRows[i].children[columnIndex];
            if (!cell) continue;
            if (cell.querySelector('form, .btn, a.eams-icon-btn')) {
                return true;
            }
        }
        return false;
    }

    function clearSortIndicators(headerRow) {
        headerRow.querySelectorAll('th').forEach(function (th) {
            th.classList.remove('eams-sorted-asc', 'eams-sorted-desc');
            const existingIcon = th.querySelector('.eams-sort-icon');
            if (existingIcon) existingIcon.remove();
        });
    }

    function sortTableByColumn(table, columnIndex, direction) {
        const tbody = table.querySelector('tbody');
        if (!tbody) return;

        const emptyStateRow = tbody.querySelector('tr td[colspan]');
        if (emptyStateRow) return;

        const rows = Array.from(tbody.querySelectorAll('tr'));

        rows.sort(function (rowA, rowB) {
            const cellA = rowA.children[columnIndex];
            const cellB = rowB.children[columnIndex];
            if (!cellA || !cellB) return 0;

            const a = cellSortValue(cellA);
            const b = cellSortValue(cellB);

            let comparison;
            if (a.type === 'number' || a.type === 'date') {
                comparison = a.value - b.value;
            } else {
                comparison = a.value.localeCompare(b.value);
            }

            return direction === 'asc' ? comparison : -comparison;
        });

        rows.forEach(function (row) {
            tbody.appendChild(row);
        });
    }

    function currentSortState() {
        const params = new URLSearchParams(window.location.search);
        const sortParam = params.get('sort');
        if (!sortParam) return { field: null, direction: null };
        const [field, direction] = sortParam.split(',');
        return { field: field, direction: direction || 'asc' };
    }

    function navigateWithServerSort(field, direction) {
        const url = new URL(window.location.href);
        url.searchParams.set('sort', field + ',' + direction);
        url.searchParams.delete('page');
        window.location.href = url.toString();
    }

    function initSortableTable(table) {
        const headerRow = table.querySelector('thead tr');
        if (!headerRow) return;

        const headers = Array.from(headerRow.querySelectorAll('th'));
        const activeSort = currentSortState();

        headers.forEach(function (th, columnIndex) {
            if (isActionColumn(table, columnIndex)) return;
            if (th.textContent.trim() === '') return;

            const serverSortField = th.getAttribute('data-sort-field');

            th.classList.add('eams-sortable-th');
            th.setAttribute('role', 'button');
            th.setAttribute('tabindex', '0');

            if (serverSortField && serverSortField === activeSort.field) {
                th.classList.add(activeSort.direction === 'asc' ? 'eams-sorted-asc' : 'eams-sorted-desc');
                const icon = document.createElement('i');
                icon.className = 'bi eams-sort-icon ' + (activeSort.direction === 'asc' ? 'bi-caret-up-fill' : 'bi-caret-down-fill');
                th.appendChild(icon);
            }

            function triggerSort() {
                if (serverSortField) {
                    const nextDirection = (serverSortField === activeSort.field && activeSort.direction === 'asc')
                        ? 'desc' : 'asc';
                    navigateWithServerSort(serverSortField, nextDirection);
                    return;
                }

                const currentDirection = th.classList.contains('eams-sorted-asc') ? 'asc' : 'desc';
                const nextDirection = currentDirection === 'asc' ? 'desc' : 'asc';

                clearSortIndicators(headerRow);
                th.classList.add(nextDirection === 'asc' ? 'eams-sorted-asc' : 'eams-sorted-desc');

                const icon = document.createElement('i');
                icon.className = 'bi eams-sort-icon ' + (nextDirection === 'asc' ? 'bi-caret-up-fill' : 'bi-caret-down-fill');
                th.appendChild(icon);

                sortTableByColumn(table, columnIndex, nextDirection);
            }

            th.addEventListener('click', triggerSort);
            th.addEventListener('keydown', function (event) {
                if (event.key === 'Enter' || event.key === ' ') {
                    event.preventDefault();
                    triggerSort();
                }
            });
        });
    }

    function initPageSizeSelector() {
        document.querySelectorAll('.eams-page-size-dropdown').forEach(function (select) {
            select.addEventListener('change', function () {
                const url = new URL(window.location.href);
                url.searchParams.set('size', select.value);
                url.searchParams.delete('page');
                window.location.href = url.toString();
            });
        });
    }

    function init() {
        document.querySelectorAll('table.eams-table').forEach(initSortableTable);
        initPageSizeSelector();
    }

    document.addEventListener('DOMContentLoaded', init);
})();