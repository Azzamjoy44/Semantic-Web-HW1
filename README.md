# Recipe Recommender

A Java web application for recipe recommendation, built as part of the **Web and Semantic Web Application Development** course at **UPB-FILS**, Year 4, Semester 2.

---

## Team Members

| Member | Contribution |
|--------|-------------|
| **ZAFAR Azzam** | Project architecture, Maven/Spring Boot setup, Java models, services and controllers, XML/XSD data files, XSL/XSLT transformation, XPath queries, application configuration, README, .gitignore |
| **ALKHALIDY Essam** | All Thymeleaf HTML templates (pages and fragments), CSS styling and UI design |

> 📌 **Public GitHub Repository:** [https://github.com/Azzamjoy44/Semantic-Web-HW1](https://github.com/Azzamjoy44/Semantic-Web-HW1)

---

## Technologies Used

| Technology | Purpose |
|-----------|---------|
| Java 21 | Core language |
| Spring Boot 3.2 | Web framework (MVC) |
| Thymeleaf | HTML templating |
| XML | Data storage (recipes, users) |
| XSD (XML Schema) | Data validation |
| XSL / XSLT | Recipe list transformation with skill-level colour coding |
| XPath | Recipe querying (filter, recommend, detail lookup) |
| Maven | Build tool |
| CSS | UI styling |

---

## How to Run

### Prerequisites
- Java 17+
- Maven 3.8+

### Steps

```bash
# Clone the repository
git clone https://github.com/Azzamjoy44/Semantic-Web-HW1
cd Semantic-Web-HW1

# Build and run
mvn spring-boot:run
```

Then open your browser at: **http://localhost:8090**

### What happens on first run
- The app copies `recipes.xml`, `users.xml`, `recipes.xsd`, and `users.xsd` from the classpath into a `data/` directory in the current working directory.
- All recipe/user reads and writes use this `data/` directory, so the XML files are writable at runtime.

---

## Project Structure

```
Semantic-Web-HW1/
├── src/
│   └── main/
│       ├── java/com/recipeapp/
│       │   ├── RecipeApplication.java          ← Spring Boot entry point
│       │   ├── controller/
│       │   │   ├── HomeController.java          ← Home page
│       │   │   ├── RecipeController.java        ← All recipe pages & features
│       │   │   └── UserController.java          ← User pages
│       │   ├── model/
│       │   │   ├── Recipe.java                  ← Recipe domain class
│       │   │   └── User.java                    ← User domain class
│       │   ├── service/
│       │   │   ├── RecipeService.java           ← Recipe XML CRUD + XPath queries
│       │   │   └── UserService.java             ← User XML CRUD + XPath queries
│       │   └── util/
│       │       ├── XmlUtil.java                 ← DOM parsing, XSD validation, XPath, XSLT
│       │       └── AppConstants.java            ← Cuisine types, difficulty levels
│       └── resources/
│           ├── application.properties
│           ├── data/
│           │   ├── recipes.xml                  ← 22 recipes
│           │   ├── recipes.xsd                  ← XSD schema for recipes
│           │   ├── users.xml                    ← 1 initial user
│           │   └── users.xsd                    ← XSD schema for users
│           ├── xsl/
│           │   └── recipes.xsl                  ← XSLT stylesheet
│           ├── templates/
│           │   ├── index.html                   ← Home / dashboard
│           │   ├── fragments/
│           │   │   └── navbar.html              ← Reusable nav + footer
│           │   ├── recipes/
│           │   │   ├── list.html                ← All recipes (Task 3)
│           │   │   ├── detail.html              ← Single recipe detail (Task 9)
│           │   │   ├── add.html                 ← Add recipe form (Task 4)
│           │   │   ├── xsl-view.html            ← XSL-rendered list (Task 8)
│           │   │   ├── recommend-skill.html     ← Recommend by skill (Task 6)
│           │   │   ├── recommend-skill-cuisine.html ← Skill + cuisine (Task 7)
│           │   │   └── filter-cuisine.html      ← Filter by cuisine (Task 10)
│           │   └── users/
│           │       ├── list.html                ← All users
│           │       └── add.html                 ← Add user form (Task 5)
│           └── static/css/
│               └── style.css                    ← All CSS
├── pom.xml
├── .gitignore
└── README.md
```
