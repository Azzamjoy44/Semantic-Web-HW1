package com.recipeapp.controller;

import com.recipeapp.model.User;
import com.recipeapp.service.UserService;
import com.recipeapp.util.AppConstants;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public String listUsers(Model model) {
        model.addAttribute("users", userService.getAllUsers());
        return "users/list";
    }

    // ─── Task 5: Add user form ───────────────────────────────────────────────────

    @GetMapping("/add")
    public String addUserForm(Model model) {
        model.addAttribute("cuisineTypes", AppConstants.CUISINE_TYPES);
        model.addAttribute("difficultyLevels", AppConstants.DIFFICULTY_LEVELS);
        return "users/add";
    }

    @PostMapping("/add")
    public String addUserSubmit(
            @RequestParam String name,
            @RequestParam String surname,
            @RequestParam String cookingSkillLevel,
            @RequestParam String preferredCuisine,
            RedirectAttributes redirectAttributes,
            Model model) {

        List<String> errors = new ArrayList<>();
        if (name == null || name.isBlank()) errors.add("First name is required.");
        if (surname == null || surname.isBlank()) errors.add("Last name is required.");
        if (cookingSkillLevel == null || cookingSkillLevel.isBlank()) errors.add("Cooking skill level is required.");
        if (preferredCuisine == null || preferredCuisine.isBlank()) errors.add("Preferred cuisine is required.");

        if (!errors.isEmpty()) {
            model.addAttribute("errors", errors);
            model.addAttribute("formName", name);
            model.addAttribute("formSurname", surname);
            model.addAttribute("formSkill", cookingSkillLevel);
            model.addAttribute("formCuisine", preferredCuisine);
            model.addAttribute("cuisineTypes", AppConstants.CUISINE_TYPES);
            model.addAttribute("difficultyLevels", AppConstants.DIFFICULTY_LEVELS);
            return "users/add";
        }

        User user = new User(null, name.trim(), surname.trim(), cookingSkillLevel, preferredCuisine);
        try {
            userService.addUser(user);
            redirectAttributes.addFlashAttribute("successMessage",
                "User " + name + " " + surname + " added successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to add user: " + e.getMessage());
        }
        return "redirect:/users";
    }
}
