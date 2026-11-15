(function () {
    const modalElement = document.getElementById('globalConfirmModal');
    if (!modalElement || !window.bootstrap) return;

    const modal = bootstrap.Modal.getOrCreateInstance(modalElement);
    const title = document.getElementById('globalConfirmTitle');
    const message = document.getElementById('globalConfirmMessage');
    const accept = document.getElementById('globalConfirmAccept');
    const cancel = document.getElementById('globalConfirmCancel');
    const label = accept.querySelector('.confirm-button-label');
    const loading = accept.querySelector('.confirm-button-loading');
    let action = null;
    let busy = false;
    let settle = null;

    function reset() {
        busy = false;
        accept.disabled = false;
        cancel.disabled = false;
        label.classList.remove('d-none');
        loading.classList.add('d-none');
        action = null;
    }

    modalElement.addEventListener('hidden.bs.modal', () => {
        reset();
        if (settle) {
            settle(false);
            settle = null;
        }
    });
    modalElement.addEventListener('show.bs.modal', () => {
        busy = false;
        accept.disabled = false;
        cancel.disabled = false;
        label.classList.remove('d-none');
        loading.classList.add('d-none');
    });

    window.confirmDeletion = function (options) {
        const settings = typeof options === 'string' ? { message: options } : (options || {});
        title.textContent = settings.title || '¿Confirmar eliminación?';
        message.textContent = settings.message || '¿Estás seguro de que deseas eliminar este elemento? Esta acción no se puede deshacer.';
        label.textContent = settings.confirmLabel || 'Eliminar';
        action = typeof settings.onConfirm === 'function' ? settings.onConfirm : null;
        modal.show();
        return new Promise((resolve) => {
            settle = resolve;
        });
    };

    accept.addEventListener('click', async function () {
        if (busy) return;
        busy = true;
        accept.disabled = true;
        cancel.disabled = true;
        label.classList.add('d-none');
        loading.classList.remove('d-none');
        try {
            if (action) await action();
            const resolve = settle;
            settle = null;
            modal.hide();
            if (resolve) resolve(true);
        } catch (error) {
            reset();
            const resolve = settle;
            settle = null;
            modal.hide();
            if (resolve) resolve(false);
            window.dispatchEvent(new CustomEvent('confirmation:error', { detail: error }));
        }
    });

    document.addEventListener('submit', function (event) {
        const form = event.target.closest('form[data-confirm-delete]');
        if (!form || form.dataset.confirmed === 'true') return;
        event.preventDefault();
        window.confirmDeletion({
            message: form.dataset.confirmMessage,
            onConfirm: () => {
                form.dataset.confirmed = 'true';
                HTMLFormElement.prototype.submit.call(form);
            }
        });
    });

    document.addEventListener('click', function (event) {
        const link = event.target.closest('a[data-confirm-delete]');
        if (!link || link.dataset.confirmed === 'true') return;
        event.preventDefault();
        window.confirmDeletion({
            message: link.dataset.confirmMessage,
            onConfirm: () => {
                link.dataset.confirmed = 'true';
                window.location.assign(link.href);
            }
        });
    });
}());
