package com.yukihira.bookstore.admin;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice(basePackages = "com.yukihira.bookstore.admin")
public class AdminViewAdvice {
    @ModelAttribute("adminSection")
    public String section(HttpServletRequest request) {
        String path = request.getRequestURI().substring(request.getContextPath().length());
        String[] parts = path.split("/");
        return parts.length > 2 ? parts[2] : "dashboard";
    }

    @InitBinder
    public void trimFields(WebDataBinder binder) {
        binder.registerCustomEditor(String.class, new StringTrimmerEditor(true));
    }
}
