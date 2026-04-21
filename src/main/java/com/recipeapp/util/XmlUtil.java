package com.recipeapp.util;

import org.springframework.stereotype.Component;
import org.w3c.dom.*;
import org.xml.sax.SAXException;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import javax.xml.xpath.*;
import java.io.*;
import java.nio.file.*;

@Component
public class XmlUtil {

    /**
     * Parse an XML file into a DOM Document, validating against the given XSD path.
     * If validation fails, a RuntimeException is thrown with details.
     */
    public Document parseAndValidate(Path xmlPath, Path xsdPath) {
        try {
            // Validate against XSD
            SchemaFactory sf = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
            Schema schema = sf.newSchema(xsdPath.toFile());
            Validator validator = schema.newValidator();
            validator.validate(new StreamSource(xmlPath.toFile()));

            // Parse to DOM
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            dbf.setNamespaceAware(true);
            DocumentBuilder db = dbf.newDocumentBuilder();
            return db.parse(xmlPath.toFile());
        } catch (SAXException e) {
            throw new RuntimeException("XML validation failed for " + xmlPath.getFileName() + ": " + e.getMessage(), e);
        } catch (Exception e) {
            throw new RuntimeException("Error parsing XML file " + xmlPath.getFileName() + ": " + e.getMessage(), e);
        }
    }

    /**
     * Parse XML without validation (used for writing/reloading after save).
     */
    public Document parse(Path xmlPath) {
        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            dbf.setNamespaceAware(true);
            DocumentBuilder db = dbf.newDocumentBuilder();
            return db.parse(xmlPath.toFile());
        } catch (Exception e) {
            throw new RuntimeException("Error parsing XML file: " + e.getMessage(), e);
        }
    }

    /**
     * Write a DOM Document back to a file.
     */
    public void writeDocument(Document doc, Path outputPath) {
        try {
            TransformerFactory tf = TransformerFactory.newInstance();
            Transformer transformer = tf.newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
            transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");

            DOMSource source = new DOMSource(doc);
            StreamResult result = new StreamResult(outputPath.toFile());
            transformer.transform(source, result);
        } catch (Exception e) {
            throw new RuntimeException("Error writing XML file: " + e.getMessage(), e);
        }
    }

    /**
     * Apply an XSLT stylesheet to an XML document and return the resulting HTML string.
     */
    public String applyXslt(Document xmlDoc, InputStream xslStream, String userSkillLevel) {
        try {
            TransformerFactory tf = TransformerFactory.newInstance();
            StreamSource xslSource = new StreamSource(xslStream);
            Transformer transformer = tf.newTransformer(xslSource);
            transformer.setParameter("userSkillLevel", userSkillLevel);

            DOMSource source = new DOMSource(xmlDoc);
            StringWriter writer = new StringWriter();
            StreamResult result = new StreamResult(writer);
            transformer.transform(source, result);
            return writer.toString();
        } catch (Exception e) {
            throw new RuntimeException("XSLT transformation failed: " + e.getMessage(), e);
        }
    }

    /**
     * Build an XPath instance.
     */
    public XPath newXPath() {
        XPathFactory xpf = XPathFactory.newInstance();
        return xpf.newXPath();
    }

    /**
     * Evaluate an XPath expression returning a NodeList.
     */
    public NodeList xpathNodeList(Document doc, String expression) {
        try {
            XPath xpath = newXPath();
            XPathExpression expr = xpath.compile(expression);
            return (NodeList) expr.evaluate(doc, XPathConstants.NODESET);
        } catch (XPathExpressionException e) {
            throw new RuntimeException("XPath error: " + e.getMessage(), e);
        }
    }

    /**
     * Evaluate an XPath expression returning a single Node.
     */
    public Node xpathNode(Document doc, String expression) {
        try {
            XPath xpath = newXPath();
            XPathExpression expr = xpath.compile(expression);
            return (Node) expr.evaluate(doc, XPathConstants.NODE);
        } catch (XPathExpressionException e) {
            throw new RuntimeException("XPath error: " + e.getMessage(), e);
        }
    }

    /**
     * Evaluate an XPath expression returning a String.
     */
    public String xpathString(Document doc, String expression) {
        try {
            XPath xpath = newXPath();
            return xpath.evaluate(expression, doc);
        } catch (XPathExpressionException e) {
            throw new RuntimeException("XPath error: " + e.getMessage(), e);
        }
    }

    /**
     * Helper: get text content of a child element by tag name.
     */
    public String getChildText(Element parent, String tagName) {
        NodeList nl = parent.getElementsByTagName(tagName);
        if (nl.getLength() > 0) {
            return nl.item(0).getTextContent().trim();
        }
        return "";
    }

    /**
     * Helper: generate next id for recipes (r001 -> r023 etc.)
     */
    public String generateNextRecipeId(Document doc) {
        NodeList nodes = doc.getElementsByTagName("recipe");
        int max = 0;
        for (int i = 0; i < nodes.getLength(); i++) {
            Element el = (Element) nodes.item(i);
            String id = el.getAttribute("id");
            if (id.startsWith("r")) {
                try {
                    int num = Integer.parseInt(id.substring(1));
                    if (num > max) max = num;
                } catch (NumberFormatException ignored) {}
            }
        }
        return String.format("r%03d", max + 1);
    }

    /**
     * Helper: generate next id for users (u001 -> u002 etc.)
     */
    public String generateNextUserId(Document doc) {
        NodeList nodes = doc.getElementsByTagName("user");
        int max = 0;
        for (int i = 0; i < nodes.getLength(); i++) {
            Element el = (Element) nodes.item(i);
            String id = el.getAttribute("id");
            if (id.startsWith("u")) {
                try {
                    int num = Integer.parseInt(id.substring(1));
                    if (num > max) max = num;
                } catch (NumberFormatException ignored) {}
            }
        }
        return String.format("u%03d", max + 1);
    }
}
