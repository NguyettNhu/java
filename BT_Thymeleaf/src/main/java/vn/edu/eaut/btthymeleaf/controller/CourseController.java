package vn.edu.eaut.btthymeleaf.controller;

import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import vn.edu.eaut.btthymeleaf.model.Course;
import vn.edu.eaut.btthymeleaf.repository.CourseRepository;

@Controller
@RequestMapping("/courses")
public class CourseController {

    private final CourseRepository courseRepository;

    public CourseController(CourseRepository courseRepository) {
        this.courseRepository = courseRepository;
    }

    @GetMapping
    public String list(@RequestParam(defaultValue = "") String keyword, Model model) {
        model.addAttribute("courses", courseRepository.findAll(keyword));
        model.addAttribute("keyword", keyword);
        model.addAttribute("totalCourses", courseRepository.count());
        return "courses/list";
    }

    @GetMapping("/new")
    public String createForm(Model model) {
        model.addAttribute("course", new Course());
        model.addAttribute("pageTitle", "Thêm khóa học");
        return "courses/form";
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        return courseRepository.findById(id)
                .map(course -> {
                    model.addAttribute("course", course);
                    model.addAttribute("pageTitle", "Cập nhật khóa học");
                    return "courses/form";
                })
                .orElseGet(() -> {
                    redirectAttributes.addFlashAttribute("errorMessage", "Không tìm thấy khóa học cần sửa.");
                    return "redirect:/courses";
                });
    }

    @PostMapping("/save")
    public String save(@Valid @ModelAttribute("course") Course course,
                       BindingResult bindingResult,
                       Model model,
                       RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("pageTitle", course.getId() == null ? "Thêm khóa học" : "Cập nhật khóa học");
            return "courses/form";
        }

        boolean isNew = course.getId() == null;
        courseRepository.save(course);
        redirectAttributes.addFlashAttribute("successMessage",
                isNew ? "Đã thêm khóa học mới." : "Đã cập nhật khóa học.");
        return "redirect:/courses";
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        if (courseRepository.deleteById(id)) {
            redirectAttributes.addFlashAttribute("successMessage", "Đã xóa khóa học.");
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "Không tìm thấy khóa học cần xóa.");
        }
        return "redirect:/courses";
    }
}
