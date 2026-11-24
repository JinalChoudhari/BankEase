package com.bankease.controller;

import com.bankease.entity.User;
import com.bankease.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.util.regex.Pattern;

@Controller
public class AuthController {
    private final UserRepository users; private final PasswordEncoder encoder;
    public AuthController(UserRepository users, PasswordEncoder encoder){this.users=users;this.encoder=encoder;}
    @GetMapping("/login")
    public String login() {
        return "login";
    }
    @GetMapping("/register") public String registerForm(){return "register";}
    @PostMapping("/register") public String register(@RequestParam String fullName,@RequestParam String email,
            @RequestParam String password, Model model){
        email=email.trim().toLowerCase();
        if(fullName.isBlank() || !Pattern.matches("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$",email) || password.length()<8){
            model.addAttribute("error","Enter a name, valid email and password of at least 8 characters."); return "register";
        }
        if(users.findByEmail(email).isPresent()){model.addAttribute("error","This email is already registered.");return "register";}
        User u=new User();u.setFullName(fullName.trim());u.setEmail(email);u.setPassword(encoder.encode(password));u.setRole("ROLE_CUSTOMER");
        users.save(u);
        return "redirect:/login?registered";
    }
}
