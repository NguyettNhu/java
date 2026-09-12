(() => {
    document.documentElement.classList.add('js');
    const toggle = document.querySelector('.nav-toggle');
    const navigation = document.querySelector('.sidebar-nav');
    toggle?.addEventListener('click', () => {
        const expanded = toggle.getAttribute('aria-expanded') !== 'true';
        toggle.setAttribute('aria-expanded', String(expanded));
        toggle.textContent = expanded ? 'Đóng menu' : 'Mở menu';
        navigation.classList.toggle('is-open', expanded);
    });

    const dialog = document.getElementById('confirm-dialog');
    let pending = null;
    const confirmed = new WeakSet();
    document.addEventListener('submit', event => {
        const form = event.target;
        if (!form.matches('form[data-confirm]') || confirmed.has(form)) return;
        event.preventDefault();
        if (!dialog?.showModal) {
            if (window.confirm(form.dataset.confirm)) {
                confirmed.add(form);
                if (event.submitter) form.requestSubmit(event.submitter);
                else form.requestSubmit();
                confirmed.delete(form);
            }
            return;
        }
        pending = { form, submitter: event.submitter };
        document.getElementById('confirm-message').textContent = form.dataset.confirm;
        dialog.showModal();
    });
    dialog?.querySelector('[data-confirm-cancel]')?.addEventListener('click', () => dialog.close());
    dialog?.querySelector('[data-confirm-accept]')?.addEventListener('click', () => {
        if (!pending) return;
        const { form, submitter } = pending;
        confirmed.add(form);
        dialog.close();
        if (submitter) form.requestSubmit(submitter);
        else form.requestSubmit();
        confirmed.delete(form);
    });
    dialog?.addEventListener('close', () => { pending = null; });

    const attachFallback = image => {
        const fallback = () => {
            image.hidden = true;
            if (!image.parentElement.querySelector('.image-fallback')) {
                const label = document.createElement('span');
                label.className = 'image-fallback';
                label.textContent = '本';
                label.setAttribute('aria-hidden', 'true');
                image.parentElement.append(label);
            }
        };
        image.addEventListener('error', fallback);
        if (image.complete && image.naturalWidth === 0) fallback();
    };
    document.querySelectorAll('.book-thumbnail img, .book-cover-large img').forEach(attachFallback);

    const imageUrl = document.querySelector('[data-image-url]');
    const preview = document.querySelector('[data-image-preview]');
    const updatePreview = () => {
        if (!preview) return;
        preview.replaceChildren();
        const url = imageUrl.value.trim();
        if (/^(https?:\/\/|\/images\/)/.test(url)) {
            const image = document.createElement('img');
            image.alt = 'Ảnh bìa xem trước';
            image.referrerPolicy = 'no-referrer';
            image.src = url;
            preview.append(image);
            attachFallback(image);
        } else {
            const label = document.createElement('span');
            label.textContent = 'Chưa có ảnh bìa';
            preview.append(label);
        }
    };
    imageUrl?.addEventListener('change', updatePreview);
    if (imageUrl) updatePreview();
    document.querySelector('[data-print]')?.addEventListener('click', () => window.print());
})();
