-- Ảnh bìa cỡ "L" của Open Library mất khoảng 2,6 giây mỗi tệp vì phải tạo lại ở CDN,
-- trong khi bản "M" chỉ mất khoảng 0,3 giây và vẫn đủ nét cho lưới sách.
UPDATE books
SET image_url = replace(image_url, '-L.jpg', '-M.jpg')
WHERE image_url LIKE 'https://covers.openlibrary.org/%-L.jpg';
