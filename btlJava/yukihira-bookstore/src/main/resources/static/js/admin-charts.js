(() => {
    const colors = ['#28634e', '#367ca0', '#ac7330', '#8d569b', '#c45654', '#467f7a', '#788438', '#595ea3'];
    const number = new Intl.NumberFormat('vi-VN', { maximumFractionDigits: 2 });
    const svgNode = (tag, attrs = {}, text) => {
        const node = document.createElementNS('http://www.w3.org/2000/svg', tag);
        Object.entries(attrs).forEach(([key, value]) => node.setAttribute(key, value));
        if (text !== undefined) node.textContent = text;
        return node;
    };
    document.querySelectorAll('[data-analytics-filter]').forEach(form => {
        const period = form.querySelector('[data-period]');
        const ranges = [...form.querySelectorAll('[data-range]')];
        // Editing either endpoint explicitly switches to a custom range; no stale preset wins.
        ranges.forEach(input => input.addEventListener('input', () => { period.value = 'custom'; }));
        form.addEventListener('submit', event => {
            ranges.forEach(input => input.setCustomValidity(''));
            if (period.value === 'custom' && (!ranges[0].value || !ranges[1].value || ranges[0].value > ranges[1].value)) {
                event.preventDefault();
                ranges[1].setCustomValidity('Nhập đủ khoảng ngày, ngày kết thúc không trước ngày bắt đầu.');
                ranges[1].reportValidity();
            }
        });
        ranges.forEach(input => input.addEventListener('input', () => ranges.forEach(field => field.setCustomValidity(''))));
    });

    const drawCharts = root => root.querySelectorAll('[data-chart-type]').forEach(card => {
        const rows = [...card.querySelectorAll('[data-chart-value]')];
        const points = rows.map(row => ({ label: row.dataset.chartLabel, value: Number(row.dataset.chartValue) }));
        const host = card.querySelector('.chart-visual');
        const total = points.reduce((sum, point) => sum + point.value, 0);
        const data = card.querySelector('.chart-data');
        if (!points.length || total === 0) {
            card.querySelector('.chart-empty').hidden = false;
            host.hidden = true;
            data.open = false;
            return;
        }
        data.open = false;
        if (card.dataset.chartType === 'pie') {
            const svg = svgNode('svg', { viewBox: '0 0 240 240', class: 'pie-chart' });
            let offset = 0;
            points.forEach((point, index) => {
                if (point.value <= 0) return;
                const proportion = point.value / total;
                const arc = svgNode('circle', {
                    cx: 120, cy: 120, r: 82, fill: 'none', stroke: colors[index % colors.length],
                    'stroke-width': 40, pathLength: 100, 'stroke-dasharray': `${proportion * 100} ${100 - proportion * 100}`,
                    'stroke-dashoffset': -offset, transform: 'rotate(-90 120 120)'
                });
                arc.append(svgNode('title', {}, `${point.label}: ${number.format(point.value)} ${card.dataset.unit} (${number.format(proportion * 100)}%)`));
                svg.append(arc); offset += proportion * 100;
            });
            svg.append(svgNode('text', { x: 120, y: 118, 'text-anchor': 'middle', class: 'pie-total' }, number.format(total)));
            svg.append(svgNode('text', { x: 120, y: 140, 'text-anchor': 'middle', class: 'pie-unit' }, card.dataset.unit));
            host.append(svg);
            const legend = document.createElement('ul'); legend.className = 'chart-legend';
            points.forEach((point, index) => {
                const li = document.createElement('li');
                const swatch = document.createElement('span'); swatch.className = 'chart-swatch'; swatch.style.background = colors[index % colors.length];
                const label = document.createElement('span'); label.textContent = point.label;
                const value = document.createElement('strong'); value.textContent = `${number.format(point.value)} · ${number.format(point.value / total * 100)}%`;
                li.append(swatch, label, value); legend.append(li);
            });
            host.append(legend);
        } else if (card.dataset.chartType === 'line') {
            // Biểu đồ đường gọn cho trang tổng quan: co giãn theo bề ngang, chỉ ghi nhãn đầu, giữa và cuối.
            const width = 720, height = 180, left = 8, right = 8, top = 14, baseline = 150;
            const svg = svgNode('svg', { viewBox: `0 0 ${width} ${height}`, class: 'line-chart', preserveAspectRatio: 'none' });
            const max = Math.max(...points.map(p => p.value)) || 1;
            const step = (width - left - right) / Math.max(points.length - 1, 1);
            const coords = points.map((point, index) => [left + index * step, baseline - point.value / max * (baseline - top)]);
            svg.append(svgNode('line', { x1: left, y1: baseline, x2: width - right, y2: baseline, stroke: '#d9e3dd' }));
            svg.append(svgNode('polygon', { points: `${left},${baseline} ${coords.map(c => c.join(',')).join(' ')} ${coords[coords.length - 1][0]},${baseline}`, fill: '#28634e1f' }));
            svg.append(svgNode('polyline', { points: coords.map(c => c.join(',')).join(' '), fill: 'none', stroke: colors[0], 'stroke-width': 2.5, 'vector-effect': 'non-scaling-stroke', 'stroke-linejoin': 'round' }));
            coords.forEach(([x, y], index) => {
                const dot = svgNode('circle', { cx: x, cy: y, r: 7, fill: 'transparent' });
                dot.append(svgNode('title', {}, `${points[index].label}: ${number.format(points[index].value)} ${card.dataset.unit}`));
                svg.append(dot);
            });
            host.append(svg);
            const axis = document.createElement('div'); axis.className = 'line-axis';
            [0, Math.floor((points.length - 1) / 2), points.length - 1].forEach(index => {
                const label = document.createElement('span'); label.textContent = points[index].label; axis.append(label);
            });
            host.append(axis);
        } else {
            const width = Math.max(540, points.length * 66 + 80);
            const height = 300;
            const svg = svgNode('svg', { viewBox: `0 0 ${width} ${height}`, width, height, class: 'bar-chart' });
            svg.style.width = `${width}px`;
            const max = Math.max(...points.map(p => p.value));
            const baseline = 230;
            for (let tick = 0; tick <= 4; tick++) {
                const y = baseline - tick * 46;
                svg.append(svgNode('line', { x1: 62, y1: y, x2: width - 12, y2: y, stroke: '#d9e3dd' }));
                const compact = new Intl.NumberFormat('vi-VN', { notation: 'compact', maximumFractionDigits: 1 });
                svg.append(svgNode('text', { x: 55, y: y + 4, 'text-anchor': 'end', class: 'chart-axis' }, compact.format(max * tick / 4)));
            }
            const step = (width - 80) / points.length;
            points.forEach((point, index) => {
                const x = 67 + index * step;
                const barHeight = point.value / max * 184;
                const bar = svgNode('rect', { x: x + step * .15, y: baseline - barHeight, width: step * .65, height: barHeight, rx: 3, fill: colors[index % colors.length] });
                bar.append(svgNode('title', {}, `${point.label}: ${number.format(point.value)} ${card.dataset.unit}`));
                svg.append(bar);
                const label = point.label.length > 13 ? `${point.label.slice(0, 12)}…` : point.label;
                const text = svgNode('text', { x: x + step / 2, y: 251, 'text-anchor': 'middle', class: 'chart-axis' }, label);
                text.append(svgNode('title', {}, point.label)); svg.append(text);
            });
            host.append(svg);
            const hint = document.createElement('p'); hint.className = 'chart-hint';
            hint.textContent = 'Di chuột lên cột để xem giá trị. Bảng số liệu bên dưới có tên và số tiền đầy đủ.';
            host.after(hint);
        }
    });

    // Tab của trang báo cáo: khi không có JS mọi bảng đều hiện, có JS thì chỉ hiện tab đang chọn.
    const initTabs = root => root.querySelectorAll('[data-tabs]').forEach(group => {
        const tabs = [...group.querySelectorAll('[role="tab"]')];
        const select = tab => tabs.forEach(item => {
            const active = item === tab;
            item.setAttribute('aria-selected', String(active));
            item.tabIndex = active ? 0 : -1;
            document.getElementById(item.getAttribute('aria-controls')).hidden = !active;
        });
        tabs.forEach((tab, index) => {
            tab.addEventListener('click', () => select(tab));
            tab.addEventListener('keydown', event => {
                if (event.key !== 'ArrowRight' && event.key !== 'ArrowLeft') return;
                const next = tabs[(index + (event.key === 'ArrowRight' ? 1 : tabs.length - 1)) % tabs.length];
                select(next); next.focus();
            });
        });
        select(tabs[0]);
    });

    // Trang danh mục dựng sẵn biểu đồ trong HTML; các trang còn lại tải khối số liệu sau khi
    // trang đã hiện, nên việc chuyển trang không phải chờ máy chủ tổng hợp.
    drawCharts(document);
    initTabs(document);
    const panel = document.querySelector('[data-analytics-panel]');
    if (panel) {
        // Khối số liệu về sau khi trang đã hiện nên nó đẩy bảng quản lý bên dưới xuống. Nhớ chiều
        // cao đo được lần trước để đặt chỗ sẵn, nhờ vậy các lượt vào sau không còn nhảy bố cục.
        // Chiều cao phụ thuộc bề ngang cửa sổ nên khóa lưu gồm cả đường dẫn lẫn bề ngang.
        // Tiền tố v2: khối biểu đồ nay thu gọn sẵn nên bỏ các chiều cao đã đo khi nó còn mở.
        const heightKey = `analytics-height-v2:${location.pathname}:${window.innerWidth}`;
        let reserved = 0;
        try { reserved = Number(sessionStorage.getItem(heightKey)) || 0; } catch (error) { reserved = 0; }
        if (reserved) panel.style.minHeight = `${reserved}px`;
        fetch(panel.dataset.analyticsUrl, { headers: { Accept: 'text/html' }, credentials: 'same-origin' })
            .then(response => response.ok ? response.text() : Promise.reject(new Error(String(response.status))))
            .then(html => {
                panel.innerHTML = html;
                drawCharts(panel);
                initTabs(panel);
                // Liên kết dạng /admin/reports#bc-san-pham trỏ vào nội dung vừa tải nên phải tự cuộn tới.
                if (location.hash) document.getElementById(decodeURIComponent(location.hash.slice(1)))?.scrollIntoView();
                // Đo chiều cao thật khi chưa đặt chỗ, rồi chỉ giữ lại chỗ trống nếu nội dung
                // thấp hơn dự kiến; bỏ hẳn khi nội dung cao hơn để không thừa khoảng trắng.
                panel.style.minHeight = '';
                const actual = Math.round(panel.getBoundingClientRect().height);
                if (reserved > actual) panel.style.minHeight = `${reserved}px`;
                try { sessionStorage.setItem(heightKey, String(actual)); } catch (error) { /* trình duyệt chặn lưu trữ thì bỏ qua */ }
            })
            .catch(() => {
                panel.style.minHeight = '';
                const alert = document.createElement('p');
                alert.className = 'form-alert error';
                alert.setAttribute('role', 'alert');
                alert.textContent = 'Không tải được số liệu phân tích. Hãy tải lại trang.';
                panel.replaceChildren(alert);
            })
            .finally(() => panel.setAttribute('aria-busy', 'false'));
    }

    // Keep report scope and list filters on pagination without copying a different page number.
    document.querySelectorAll('.pagination a').forEach(link => {
        const target = new URL(link.href, location.href);
        const current = new URLSearchParams(location.search);
        current.forEach((value, name) => {
            if (name !== 'page' && !target.searchParams.has(name)) target.searchParams.set(name, value);
        });
        link.href = target.href;
    });
    let printState = [];
    window.addEventListener('beforeprint', () => {
        printState = [...document.querySelectorAll('.admin-analytics details, .page-charts')].map(node => [node, node.open]);
        printState.forEach(([node]) => { node.open = true; });
    });
    window.addEventListener('afterprint', () => printState.forEach(([node, open]) => { node.open = open; }));
})();
