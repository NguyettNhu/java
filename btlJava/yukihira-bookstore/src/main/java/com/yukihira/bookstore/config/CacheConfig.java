package com.yukihira.bookstore.config;

import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Configuration;

/**
 * Dữ liệu tham chiếu (thể loại, tác giả, nhà xuất bản) gần như tĩnh nhưng trước đây
 * bị truy vấn 6–9 lần cho mỗi trang admin, mỗi lần là một transaction riêng. Với cơ sở
 * dữ liệu đặt ở xa, mỗi transaction tốn 3 lượt đi–về mạng nên chi phí này chiếm phần lớn
 * thời gian tải trang. Cache trong bộ nhớ và xóa khi có thao tác ghi.
 *
 * <p>Kho chứa cache do spring.cache trong application.yml quyết định, không khai báo
 * CacheManager ở đây, để hồ sơ test có thể tắt cache bằng spring.cache.type=none.
 */
@Configuration
@EnableCaching
public class CacheConfig {

    public static final String REFERENCES = "references";
}
