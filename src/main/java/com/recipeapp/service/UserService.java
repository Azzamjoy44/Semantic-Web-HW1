package com.recipeapp.service;

import com.recipeapp.model.User;
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
public class UserService {

    private final XmlUtil xmlUtil;
    private Path usersXmlPath;
    private Path usersXsdPath;

    public UserService(XmlUtil xmlUtil) {
        this.xmlUtil = xmlUtil;
    }

    @PostConstruct
    public void init() throws IOException {
        Path dataDir = Paths.get(System.getProperty("user.dir"), "data");
        if (!Files.exists(dataDir)) {
            Files.createDirectories(dataDir);
        }
        usersXmlPath = dataDir.resolve("users.xml");
        usersXsdPath = dataDir.resolve("users.xsd");

        copyFromClasspathIfMissing("data/users.xml", usersXmlPath);
        copyFromClasspathIfMissing("data/users.xsd", usersXsdPath);
    }

    private void copyFromClasspathIfMissing(String classpathResource, Path target) throws IOException {
        if (!Files.exists(target)) {
            ClassPathResource resource = new ClassPathResource(classpathResource);
            try (InputStream in = resource.getInputStream()) {
                Files.copy(in, target, StandardCopyOption.REPLACE_EXISTING);
            }
        }
    }

    private Document loadDocument() {
        return xmlUtil.parseAndValidate(usersXmlPath, usersXsdPath);
    }

    private User elementToUser(Element el) {
        return new User(
            el.getAttribute("id"),
            xmlUtil.getChildText(el, "name"),
            xmlUtil.getChildText(el, "surname"),
            xmlUtil.getChildText(el, "cookingSkillLevel"),
            xmlUtil.getChildText(el, "preferredCuisine")
        );
    }

    /** Task 5/6/7: Get the first user from XML using XPath. */
    public Optional<User> getFirstUser() {
        Document doc = loadDocument();
        // XPath: select position 1 user
        Node node = xmlUtil.xpathNode(doc, "//user[1]");
        if (node == null) return Optional.empty();
        return Optional.of(elementToUser((Element) node));
    }

    /** Return all users. */
    public List<User> getAllUsers() {
        Document doc = loadDocument();
        NodeList nodes = xmlUtil.xpathNodeList(doc, "//user");
        List<User> list = new ArrayList<>();
        for (int i = 0; i < nodes.getLength(); i++) {
            list.add(elementToUser((Element) nodes.item(i)));
        }
        return list;
    }

    /** Find user by id using XPath. */
    public Optional<User> findById(String id) {
        Document doc = loadDocument();
        Node node = xmlUtil.xpathNode(doc, "//user[@id='" + id + "']");
        if (node == null) return Optional.empty();
        return Optional.of(elementToUser((Element) node));
    }

    /** Task 5: Save a new user to XML. */
    public void addUser(User user) {
        Document doc = loadDocument();
        Element root = doc.getDocumentElement();

        String newId = xmlUtil.generateNextUserId(doc);
        user.setId(newId);

        Element userEl = doc.createElement("user");
        userEl.setAttribute("id", newId);

        appendText(doc, userEl, "name", user.getName());
        appendText(doc, userEl, "surname", user.getSurname());
        appendText(doc, userEl, "cookingSkillLevel", user.getCookingSkillLevel());
        appendText(doc, userEl, "preferredCuisine", user.getPreferredCuisine());

        root.appendChild(userEl);
        xmlUtil.writeDocument(doc, usersXmlPath);
    }

    private void appendText(Document doc, Element parent, String tagName, String value) {
        Element el = doc.createElement(tagName);
        el.setTextContent(value);
        parent.appendChild(el);
    }
}
