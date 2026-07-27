package com.example.studymate.security;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/pages")
public class LoginController {

    @GetMapping("/login")
    public String loginPage() {
        return "login"; // templates/login.html 반환
    }

    @GetMapping("/register")
    public String registerPage() {
        return "register"; // templates/register.html 반환
    }
}
