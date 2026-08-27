package com.yukihira.bookstore;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ActiveProfiles("test")
class SecurityWebTests {

    @Autowired WebApplicationContext context;
    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(context)
                .apply(SecurityMockMvcConfigurers.springSecurity())
                .build();
    }

    @Test
    void homeIsPublic() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(status().isOk());
    }

    @Test
    void anonymousUserIsRedirectedFromAdmin() throws Exception {
        mockMvc.perform(get("/admin"))
                .andExpect(status().is3xxRedirection());
    }

    @Test
    void customerCannotAccessAdmin() throws Exception {
        mockMvc.perform(get("/admin").with(user("customer").roles("CUSTOMER")))
                .andExpect(status().isForbidden());
    }

    @Test
    void adminCanAccessAdmin() throws Exception {
        mockMvc.perform(get("/admin").with(user("admin").roles("ADMIN")))
                .andExpect(status().isOk());
    }

    @Test
    void anonymousUserIsRedirectedFromProfile() throws Exception {
        mockMvc.perform(get("/profile"))
                .andExpect(status().is3xxRedirection());
    }

    @Test
    void customerCannotAccessCustomerAdministration() throws Exception {
        mockMvc.perform(get("/admin/users").with(user("customer").roles("CUSTOMER")))
                .andExpect(status().isForbidden());
    }

    @Test
    void adminCanRenderReports() throws Exception {
        mockMvc.perform(get("/admin/reports").with(user("admin").roles("ADMIN")))
                .andExpect(status().isOk());
    }

    @Test
    void authenticatedRequestToUnknownPageReturnsNotFound() throws Exception {
        mockMvc.perform(get("/khong-ton-tai").with(user("customer").roles("CUSTOMER")))
                .andExpect(status().isNotFound());
    }
}
