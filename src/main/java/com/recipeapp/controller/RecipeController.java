package com.recipeapp.controller;

import com.recipeapp.model.Recipe;
import com.recipeapp.model.User;
import com.recipeapp.service.RecipeService;
import com.recipeapp.service.UserService;
import com.recipeapp.util.AppConstants;
import com.recipeapp.util.XmlUtil;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.w3c.dom.Document;

import java.io.InputStream;
import java.util.*;

@Controller
@RequestMapping("/recipes")
public class RecipeController {

    private final RecipeService recipeService;
    private final UserService userService;
    private final XmlUtil xmlUtil;

    public RecipeController(RecipeService recipeService, UserService userService, XmlUtil xmlUtil) {
        this.recipeService = recipeService;
        this.userService = userService;
        this.xmlUtil = xmlUtil;
    }

    // ─── Task 3: List all recipes (standard Thymeleaf) ──────────────────────────

    @GetMapping
    public String listRecipes(Model model) {
        model.addAttribute("recipes", recipeService.getAllRecipes());
        model.addAttribute("pageTitle", "All Recipes");
        addCommonAttributes(model);
        return "recipes/list";
    }

    // ─── Task 8: List recipes via XSL transformation ─────────────────────────────

    @GetMapping("/xsl-view")
    public String xslView(@RequestParam(required = false) String userId, Model model) {
        List<User> allUsers = userService.getAllUsers();
        model.addAttribute("allUsers", allUsers);

        // Determine active user for skill-level highlighting
        User activeUser = null;
        if (userId != null && !userId.isEmpty()) {
            activeUser = userService.findById(userId).orElse(null);
        }
        if (activeUser == null) {
            activeUser = userService.getFirstUser().orElse(null);
        }

        String skillLevel = (activeUser != null) ? activeUser.getCookingSkillLevel() : "Intermediate";

        try {
            Document doc = recipeService.getRecipesDocument();
            ClassPathResource xslResource = new ClassPathResource("xsl/recipes.xsl");
            try (InputStream xslStream = xslResource.getInputStream()) {
                String htmlContent = xmlUtil.applyXslt(doc, xslStream, skillLevel);
                model.addAttribute("xslContent", htmlContent);
            }
        } catch (Exception e) {
            model.addAttribute("xslError", "XSLT transformation error: " + e.getMessage());
        }

        model.addAttribute("activeUser", activeUser);
        model.addAttribute("skillLevel", skillLevel);
        addCommonAttributes(model);
        return "recipes/xsl-view";
    }

    // ─── Task 9: Recipe detail page ──────────────────────────────────────────────

    @GetMapping("/{id}")
    public String recipeDetail(@PathVariable String id, Model model) {
        Optional<Recipe> recipe = recipeService.findById(id);
        if (recipe.isEmpty()) {
            return "redirect:/recipes";
        }
        model.addAttribute("recipe", recipe.get());
        model.addAttribute("xpathExpression", "//recipe[@id='" + id + "']");
        addCommonAttributes(model);
        return "recipes/detail";
    }

    // ─── Task 4: Add recipe form ──────────────────────────────────────────────────

    @GetMapping("/add")
    public String addRecipeForm(Model model) {
        model.addAttribute("recipe", new Recipe());
        addCommonAttributes(model);
        return "recipes/add";
    }

    @PostMapping("/add")
    public String addRecipeSubmit(
            @RequestParam String title,
            @RequestParam String cuisine1,
            @RequestParam String cuisine2,
            @RequestParam String primaryDifficulty,
            @RequestParam(defaultValue = "") String description,
            @RequestParam(defaultValue = "0") int prepTime,
            @RequestParam(defaultValue = "0") int cookTime,
            RedirectAttributes redirectAttributes,
            Model model) {

        // Validation
        List<String> errors = new ArrayList<>();
        if (title == null || title.isBlank()) errors.add("Title is required.");
        if (cuisine1 == null || cuisine1.isBlank()) errors.add("First cuisine is required.");
        if (cuisine2 == null || cuisine2.isBlank()) errors.add("Second cuisine is required.");
        if (cuisine1 != null && cuisine1.equals(cuisine2)) errors.add("The two cuisine types must be different.");
        if (primaryDifficulty == null || primaryDifficulty.isBlank()) errors.add("Primary difficulty is required.");
        if (prepTime < 0) errors.add("Prep time cannot be negative.");
        if (cookTime < 0) errors.add("Cook time cannot be negative.");

        if (!errors.isEmpty()) {
            model.addAttribute("errors", errors);
            model.addAttribute("formTitle", title);
            model.addAttribute("formCuisine1", cuisine1);
            model.addAttribute("formCuisine2", cuisine2);
            model.addAttribute("formDifficulty", primaryDifficulty);
            model.addAttribute("formDescription", description);
            model.addAttribute("formPrepTime", prepTime);
            model.addAttribute("formCookTime", cookTime);
            addCommonAttributes(model);
            return "recipes/add";
        }

        Recipe recipe = new Recipe(
            null,
            title.trim(),
            List.of(cuisine1, cuisine2),
            primaryDifficulty,
            description.trim(),
            prepTime,
            cookTime
        );

        try {
            recipeService.addRecipe(recipe);
            redirectAttributes.addFlashAttribute("successMessage", "Recipe \"" + title + "\" added successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to add recipe: " + e.getMessage());
        }
        return "redirect:/recipes";
    }

    // ─── Task 6: Recommendations by skill level ────────────────────────────────

    @GetMapping("/recommend/by-skill")
    public String recommendBySkill(@RequestParam(required = false) String userId, Model model) {
        List<User> allUsers = userService.getAllUsers();
        model.addAttribute("allUsers", allUsers);

        User user = resolveUser(userId);
        if (user == null) {
            model.addAttribute("errorMessage", "No user found. Please add a user first.");
            addCommonAttributes(model);
            return "recipes/recommend-skill";
        }
        List<Recipe> recommendations = recipeService.findBySkillLevel(user.getCookingSkillLevel());
        model.addAttribute("user", user);
        model.addAttribute("selectedUserId", user.getId());
        model.addAttribute("recipes", recommendations);
        model.addAttribute("xpathExpression",
            "//recipe[primaryDifficulty='" + user.getCookingSkillLevel() + "']");
        addCommonAttributes(model);
        return "recipes/recommend-skill";
    }

    // ─── Task 7: Recommendations by skill level + cuisine ──────────────────────

    @GetMapping("/recommend/by-skill-cuisine")
    public String recommendBySkillAndCuisine(@RequestParam(required = false) String userId, Model model) {
        List<User> allUsers = userService.getAllUsers();
        model.addAttribute("allUsers", allUsers);

        User user = resolveUser(userId);
        if (user == null) {
            model.addAttribute("errorMessage", "No user found. Please add a user first.");
            addCommonAttributes(model);
            return "recipes/recommend-skill-cuisine";
        }
        List<Recipe> recommendations = recipeService.findBySkillLevelAndCuisine(
            user.getCookingSkillLevel(), user.getPreferredCuisine());
        model.addAttribute("user", user);
        model.addAttribute("selectedUserId", user.getId());
        model.addAttribute("recipes", recommendations);
        model.addAttribute("xpathExpression",
            "//recipe[primaryDifficulty='" + user.getCookingSkillLevel() +
            "' and cuisines/cuisine='" + user.getPreferredCuisine() + "']");
        addCommonAttributes(model);
        return "recipes/recommend-skill-cuisine";
    }

    private User resolveUser(String userId) {
        if (userId != null && !userId.isEmpty()) {
            return userService.findById(userId).orElse(null);
        }
        return userService.getFirstUser().orElse(null);
    }

    // ─── Task 10: Filter by cuisine ──────────────────────────────────────────────

    @GetMapping("/filter/by-cuisine")
    public String filterByCuisine(
            @RequestParam(required = false) String cuisine, Model model) {
        if (cuisine != null && !cuisine.isBlank()) {
            List<Recipe> results = recipeService.findByCuisine(cuisine);
            model.addAttribute("recipes", results);
            model.addAttribute("selectedCuisine", cuisine);
            model.addAttribute("xpathExpression",
                "//recipe[cuisines/cuisine='" + cuisine + "']");
        }
        addCommonAttributes(model);
        return "recipes/filter-cuisine";
    }

    // ─── Helper ────────────────────────────────────────────────────────────────

    private void addCommonAttributes(Model model) {
        model.addAttribute("cuisineTypes", AppConstants.CUISINE_TYPES);
        model.addAttribute("difficultyLevels", AppConstants.DIFFICULTY_LEVELS);
    }
}
