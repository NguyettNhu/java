const fs = require('node:fs');
const path = require('node:path');
const covers = [
    ['van-hoc', 'Văn học', '#3d6559'], ['ky-nang-song', 'Kỹ năng sống', '#96703b'],
    ['thieu-nhi', 'Thiếu nhi', '#bd665b'], ['cong-nghe', 'Công nghệ', '#386780'],
    ['kinh-doanh', 'Kinh doanh', '#51648b'], ['lich-su', 'Lịch sử', '#80614b'],
    ['khoa-hoc', 'Khoa học', '#427c7a'], ['du-lich', 'Du lịch', '#73804e']
];
const output = path.join(__dirname, '../src/main/resources/static/images/demo-covers');
fs.mkdirSync(output, { recursive: true });
for (const [slug, title, color] of covers) {
    const svg = `<svg xmlns="http://www.w3.org/2000/svg" width="360" height="500" viewBox="0 0 360 500" role="img" aria-label="Bìa sách mẫu ${title}">
<rect width="360" height="500" fill="${color}"/><rect x="20" y="20" width="320" height="460" rx="3" fill="none" stroke="#ffffff" stroke-opacity=".4"/>
<path d="M0 350Q120 260 240 350T480 350V500H0Z" fill="#ffffff" fill-opacity=".08"/>
<circle cx="180" cy="198" r="78" fill="#ffffff" fill-opacity=".12"/>
<path d="M124 166Q152 158 180 174Q208 158 236 166V232Q208 224 180 240Q152 224 124 232ZM180 174V240" fill="none" stroke="#ffffff" stroke-width="3" stroke-linejoin="round"/>
<g fill="#ffffff" text-anchor="middle" font-family="Segoe UI, Tahoma, sans-serif"><text x="180" y="70" font-size="19" letter-spacing="2">YUKIHIRA</text>
<text x="180" y="326" font-size="30" font-weight="600">${title}</text><text x="180" y="362" font-size="15">Tủ sách khám phá</text><text x="180" y="450" font-size="12" opacity=".8">Bìa minh họa · Dữ liệu mẫu</text></g></svg>`;
    fs.writeFileSync(path.join(output, `${slug}.svg`), svg, 'utf8');
}
console.log(`Generated ${covers.length} local SVG covers.`);
