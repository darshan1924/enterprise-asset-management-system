(function () {
    'use strict';

    const SEARCH_FORM_CLASS = 'eams-search';
    const INPUT_SELECTOR = '.eams-input';
    const LABEL_SELECTOR = '.eams-label';

    function isEligibleForm(form) {
        // Skip search bars, filter bars, and single-button action forms (delete/return/approve/etc.)
        if (form.classList.contains(SEARCH_FORM_CLASS)) return false;
        if (form.querySelectorAll(INPUT_SELECTOR).length === 0) return false;
        return true;
    }

    function findLabelFor(field) {
        // Walk up to the nearest wrapper (.mb-3, .mb-4, .col-*) and find its .eams-label
        let wrapper = field.closest('.mb-3, .mb-4, .col-md-4, .col-md-6, .col-md-8, .col-md-12');
        if (!wrapper) return null;
        return wrapper.querySelector(LABEL_SELECTOR);
    }

    function applyRequiredAttributes(form) {
        const fields = form.querySelectorAll(INPUT_SELECTOR);
        fields.forEach(function (field) {
            if (field.hasAttribute('data-no-validate')) return;
            const label = findLabelFor(field);
            const labelText = label ? label.textContent.toLowerCase() : '';
            const isOptional = labelText.includes('(optional)');
            if (!isOptional && field.type !== 'hidden') {
                field.setAttribute('required', 'required');
            }
        });
    }

    function ensureErrorSlot(field) {
        // Reuse an existing server-rendered .eams-error sibling if present, else create one.
        let wrapper = field.closest('.mb-3, .mb-4, .col-md-4, .col-md-6, .col-md-8, .col-md-12') || field.parentElement;
        let errorEl = wrapper.querySelector('.eams-error[data-client-error]');
        if (!errorEl) {
            errorEl = document.createElement('div');
            errorEl.className = 'eams-error';
            errorEl.setAttribute('data-client-error', 'true');
            errorEl.style.display = 'none';
            wrapper.appendChild(errorEl);
        }
        return errorEl;
    }

    function fieldLabelName(field) {
        const label = findLabelFor(field);
        if (label) {
            return label.textContent.replace('(optional)', '').trim();
        }
        return field.name || 'This field';
    }

    function validateField(field) {
        const errorEl = ensureErrorSlot(field);

        if (field.validity.valid) {
            field.classList.remove('is-invalid');
            errorEl.style.display = 'none';
            errorEl.textContent = '';
            return true;
        }

        field.classList.add('is-invalid');
        let message;
        if (field.validity.valueMissing) {
            message = fieldLabelName(field) + ' is required';
        } else if (field.validity.typeMismatch && field.type === 'email') {
            message = 'Enter a valid email address';
        } else if (field.validity.rangeUnderflow) {
            message = fieldLabelName(field) + ' must be at least ' + field.min;
        } else if (field.validity.rangeOverflow) {
            message = fieldLabelName(field) + ' must be at most ' + field.max;
        } else if (field.validity.tooShort) {
            message = fieldLabelName(field) + ' is too short';
        } else if (field.validity.tooLong) {
            message = fieldLabelName(field) + ' is too long';
        } else {
            message = fieldLabelName(field) + ' is invalid';
        }

        errorEl.textContent = message;
        errorEl.style.display = 'block';
        return false;
    }

    function attachLiveValidation(form) {
        const fields = form.querySelectorAll(INPUT_SELECTOR);
        fields.forEach(function (field) {
            field.addEventListener('blur', function () {
                validateField(field);
            });
            field.addEventListener('input', function () {
                if (field.classList.contains('is-invalid')) {
                    validateField(field);
                }
            });
            field.addEventListener('change', function () {
                if (field.tagName === 'SELECT') {
                    validateField(field);
                }
            });
        });
    }

    function attachSubmitGuard(form) {
        form.setAttribute('novalidate', 'novalidate');
        form.addEventListener('submit', function (event) {
            const fields = form.querySelectorAll(INPUT_SELECTOR);
            let formValid = true;

            fields.forEach(function (field) {
                const fieldValid = validateField(field);
                if (!fieldValid) {
                    formValid = false;
                }
            });

            if (!formValid) {
                event.preventDefault();
                event.stopPropagation();

                const firstInvalid = form.querySelector('.is-invalid');
                if (firstInvalid) {
                    firstInvalid.focus();
                    firstInvalid.scrollIntoView({ behavior: 'smooth', block: 'center' });
                }
            }
        });
    }

    function init() {
        const forms = document.querySelectorAll('form');
        forms.forEach(function (form) {
            if (!isEligibleForm(form)) return;
            applyRequiredAttributes(form);
            attachLiveValidation(form);
            attachSubmitGuard(form);
        });
    }

    document.addEventListener('DOMContentLoaded', init);
})();