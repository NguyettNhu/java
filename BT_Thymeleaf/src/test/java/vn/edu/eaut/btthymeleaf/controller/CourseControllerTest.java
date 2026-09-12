package vn.edu.eaut.btthymeleaf.controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;

import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.flash;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@SpringBootTest
class CourseControllerTest {

    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @BeforeEach
    void setUp() {
        mockMvc = webAppContextSetup(webApplicationContext).build();
    }

    @Test
    void shouldDisplayCourseList() throws Exception {
        mockMvc.perform(get("/courses"))
                .andExpect(status().isOk())
                .andExpect(view().name("courses/list"))
                .andExpect(model().attribute("courses", hasSize(greaterThanOrEqualTo(3))))
                .andExpect(model().attribute("totalCourses", greaterThanOrEqualTo(3L)));
    }

    @Test
    void shouldReturnFormWhenDataIsInvalid() throws Exception {
        mockMvc.perform(post("/courses/save")
                        .param("name", "")
                        .param("instructor", "")
                        .param("level", "")
                        .param("price", "-1"))
                .andExpect(status().isOk())
                .andExpect(view().name("courses/form"))
                .andExpect(model().attributeHasFieldErrors("course", "name", "instructor", "level", "price"));
    }

    @Test
    void shouldCreateCourseAndRedirect() throws Exception {
        mockMvc.perform(post("/courses/save")
                        .param("name", "Kiểm thử Spring MVC")
                        .param("instructor", "Phạm Mai")
                        .param("level", "Trung cấp")
                        .param("price", "650000")
                        .param("description", "Thực hành MockMvc."))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/courses"))
                .andExpect(flash().attribute("successMessage", "Đã thêm khóa học mới."));
    }
}
