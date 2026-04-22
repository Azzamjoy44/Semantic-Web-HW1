# Recipe Recommender

A Java web application for recipe recommendation, built for the First Homework for The **Web and Semantic Web Application Development** course at **UPB-FILS**, Year 4, Semester 2.

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
