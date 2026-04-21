package com.recipeapp.service;

import com.recipeapp.model.Recipe;
import com.recipeapp.util.XmlUtil;
import jakarta.annotation.PostConstruct;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.w3c.dom.*;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.*;
import java.util.*;

@Service
public class RecipeService {

    private final XmlUtil xmlUtil;
    private Path recipesXmlPath;
    private Path recipesXsdPath;

    public RecipeService(XmlUtil xmlUtil) {
        this.xmlUtil = xmlUtil;
    }

    @PostConstruct
    public void init() throws IOException {
        // Resolve the writable data directory (inside the running JAR's working dir or classpath)
        Path dataDir = resolveDataDir();
        recipesXmlPath = dataDir.resolve("recipes.xml");
        recipesXsdPath = dataDir.resolve("recipes.xsd");
    }

    /**
     * Resolve (and if needed, copy) the data files to a writable location.
     */
    private Path resolveDataDir() throws IOException {
        // Use a "data" folder next to the application working directory
        Path dataDir = Paths.get(System.getProperty("user.dir"), "data");
        if (!Files.exists(dataDir)) {
            Files.createDirectories(dataDir);
        }
        // Copy from classpath if not already present
        copyFromClasspathIfMissing("data/recipes.xml", dataDir.resolve("recipes.xml"));
        copyFromClasspathIfMissing("data/recipes.xsd", dataDir.resolve("recipes.xsd"));
        return dataDir;
    }

    private void copyFromClasspathIfMissing(String classpathResource, Path target) throws IOException {
        if (!Files.exists(target)) {
            ClassPathResource resource = new ClassPathResource(classpathResource);
            try (InputStream in = resource.getInputStream()) {
                Files.copy(in, target, StandardCopyOption.REPLACE_EXISTING);
            }
        }
    }

    // ─── DOM Loading ────────────────────────────────────────────────────────────

    private Document loadDocument() {
        return xmlUtil.parseAndValidate(recipesXmlPath, recipesXsdPath);
    }

    private Recipe elementToRecipe(Element el) {
        String id = el.getAttribute("id");
        String title = xmlUtil.getChildText(el, "title");
        String primaryDifficulty = xmlUtil.getChildText(el, "primaryDifficulty");
        String description = xmlUtil.getChildText(el, "description");

        int prepTime = 0, cookTime = 0;
        try { prepTime = Integer.parseInt(xmlUtil.getChildText(el, "prepTime")); } catch (Exception ignored) {}
        try { cookTime = Integer.parseInt(xmlUtil.getChildText(el, "cookTime")); } catch (Exception ignored) {}

        // Cuisines
        List<String> cuisines = new ArrayList<>();
        NodeList cuisineNodes = el.getElementsByTagName("cuisine");
        for (int i = 0; i < cuisineNodes.getLength(); i++) {
            cuisines.add(cuisineNodes.item(i).getTextContent().trim());
        }

        // Supported levels
        List<String> levels = new ArrayList<>();
        NodeList levelNodes = el.getElementsByTagName("level");
        for (int i = 0; i < levelNodes.getLength(); i++) {
            levels.add(levelNodes.item(i).getTextContent().trim());
        }

        return new Recipe(id, title, cuisines, primaryDifficulty, levels, description, prepTime, cookTime);
    }

    // ─── Public API ──────────────────────────────────────────────────────────────

    /** Task 3/8: Return all recipes from XML. */
    public List<Recipe> getAllRecipes() {
        Document doc = loadDocument();
        NodeList nodes = xmlUtil.xpathNodeList(doc, "//recipe");
        List<Recipe> list = new ArrayList<>();
        for (int i = 0; i < nodes.getLength(); i++) {
            list.add(elementToRecipe((Element) nodes.item(i)));
        }
        return list;
    }

    /** Task 9: Find a single recipe by id using XPath. */
    public Optional<Recipe> findById(String id) {
        Document doc = loadDocument();
        // XPath: select recipe element whose @id equals the given id
        Node node = xmlUtil.xpathNode(doc, "//recipe[@id='" + id + "']");
        if (node == null) return Optional.empty();
        return Optional.of(elementToRecipe((Element) node));
    }

    /** Task 6: Recommend recipes whose primaryDifficulty matches the user's skill level (XPath). */
    public List<Recipe> findBySkillLevel(String skillLevel) {
        Document doc = loadDocument();
        // XPath: select recipes where primaryDifficulty equals skillLevel
        NodeList nodes = xmlUtil.xpathNodeList(doc,
            "//recipe[primaryDifficulty='" + skillLevel + "']");
        List<Recipe> list = new ArrayList<>();
        for (int i = 0; i < nodes.getLength(); i++) {
            list.add(elementToRecipe((Element) nodes.item(i)));
        }
        return list;
    }

    /** Task 7: Recommend recipes by skill level AND cuisine (XPath). */
    public List<Recipe> findBySkillLevelAndCuisine(String skillLevel, String cuisine) {
        Document doc = loadDocument();
        // XPath: primaryDifficulty matches AND at least one cuisine child matches
        NodeList nodes = xmlUtil.xpathNodeList(doc,
            "//recipe[primaryDifficulty='" + skillLevel + "' and cuisines/cuisine='" + cuisine + "']");
        List<Recipe> list = new ArrayList<>();
        for (int i = 0; i < nodes.getLength(); i++) {
            list.add(elementToRecipe((Element) nodes.item(i)));
        }
        return list;
    }

    /** Task 10: Filter recipes by a selected cuisine type (XPath). */
    public List<Recipe> findByCuisine(String cuisine) {
        Document doc = loadDocument();
        // XPath: any cuisine child matches
        NodeList nodes = xmlUtil.xpathNodeList(doc,
            "//recipe[cuisines/cuisine='" + cuisine + "']");
        List<Recipe> list = new ArrayList<>();
        for (int i = 0; i < nodes.getLength(); i++) {
            list.add(elementToRecipe((Element) nodes.item(i)));
        }
        return list;
    }

    /** Task 4: Add a new recipe to in-memory DOM and persist to file. */
    public void addRecipe(Recipe recipe) {
        Document doc = loadDocument();
        Element root = doc.getDocumentElement();

        // Generate id
        String newId = xmlUtil.generateNextRecipeId(doc);
        recipe.setId(newId);

        Element recipeEl = doc.createElement("recipe");
        recipeEl.setAttribute("id", newId);

        appendText(doc, recipeEl, "title", recipe.getTitle());

        Element cuisinesEl = doc.createElement("cuisines");
        for (String c : recipe.getCuisines()) {
            appendText(doc, cuisinesEl, "cuisine", c);
        }
        recipeEl.appendChild(cuisinesEl);

        appendText(doc, recipeEl, "primaryDifficulty", recipe.getPrimaryDifficulty());

        Element levelsEl = doc.createElement("supportedLevels");
        for (String lvl : recipe.getSupportedLevels()) {
            appendText(doc, levelsEl, "level", lvl);
        }
        recipeEl.appendChild(levelsEl);

        appendText(doc, recipeEl, "description", recipe.getDescription());
        appendText(doc, recipeEl, "prepTime", String.valueOf(recipe.getPrepTime()));
        appendText(doc, recipeEl, "cookTime", String.valueOf(recipe.getCookTime()));

        root.appendChild(recipeEl);
        xmlUtil.writeDocument(doc, recipesXmlPath);
    }

    /** Task 8: Get the DOM document for XSLT transformation. */
    public Document getRecipesDocument() {
        return loadDocument();
    }

    private void appendText(Document doc, Element parent, String tagName, String value) {
        Element el = doc.createElement(tagName);
        el.setTextContent(value);
        parent.appendChild(el);
    }
}
