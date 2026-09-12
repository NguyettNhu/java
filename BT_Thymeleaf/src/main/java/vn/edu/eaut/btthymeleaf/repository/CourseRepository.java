package vn.edu.eaut.btthymeleaf.repository;

import org.springframework.stereotype.Repository;
import vn.edu.eaut.btthymeleaf.model.Course;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public class CourseRepository {

    private final Map<Long, Course> courses = new ConcurrentHashMap<>();
    private final AtomicLong sequence = new AtomicLong();

    public CourseRepository() {
        save(new Course(null, "Spring Boot căn bản", "Nguyễn Minh An", "Cơ bản",
                new BigDecimal("890000"), "Xây dựng ứng dụng web đầu tiên với Spring MVC."));
        save(new Course(null, "Java hướng đối tượng", "Trần Thu Hà", "Cơ bản",
                new BigDecimal("750000"), "Nắm vững class, interface, kế thừa và xử lý ngoại lệ."));
        save(new Course(null, "Thymeleaf thực hành", "Lê Hoàng Nam", "Trung cấp",
                new BigDecimal("990000"), "Thiết kế giao diện động, form binding và validation."));
    }

    public List<Course> findAll(String keyword) {
        String normalizedKeyword = keyword == null ? "" : keyword.trim().toLowerCase(Locale.ROOT);

        return courses.values().stream()
                .filter(course -> normalizedKeyword.isEmpty()
                        || course.getName().toLowerCase(Locale.ROOT).contains(normalizedKeyword)
                        || course.getInstructor().toLowerCase(Locale.ROOT).contains(normalizedKeyword))
                .sorted(Comparator.comparing(Course::getId))
                .map(Course::new)
                .toList();
    }

    public Optional<Course> findById(Long id) {
        return Optional.ofNullable(courses.get(id)).map(Course::new);
    }

    public Course save(Course course) {
        Course saved = new Course(course);
        if (saved.getId() == null) {
            saved.setId(sequence.incrementAndGet());
        }
        courses.put(saved.getId(), saved);
        sequence.accumulateAndGet(saved.getId(), Math::max);
        return new Course(saved);
    }

    public boolean deleteById(Long id) {
        return courses.remove(id) != null;
    }

    public long count() {
        return courses.size();
    }
}
