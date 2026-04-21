package com.recipeapp.controller;

import com.recipeapp.model.User;
import com.recipeapp.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.Optional;

@Controller
public class HomeController {

    private final UserService userService;

    public HomeController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/")
    public String home(Model model) {
        Optional<User> firstUser = userService.getFirstUser();
        firstUser.ifPresent(u -> model.addAttribute("activeUser", u));
        model.addAttribute("hasUser", firstUser.isPresent());
        return "index";
    }
}
