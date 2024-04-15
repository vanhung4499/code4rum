package com.hnv99.forum.config.init;

import lombok.Getter;
import org.springframework.core.io.ClassPathResource;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Database Change Set Loader
 */
public class DbChangeSetLoader {
    public static XMLReader getInstance() throws Exception {
        // Obtain factory using javax.xml.parsers.SAXParserFactory native API
        SAXParserFactory factory = SAXParserFactory.newInstance();
        // Obtain parse using javax.xml.parsers.SAXParser native API
        SAXParser saxParser = factory.newSAXParser();
        // Get XML reader
        return saxParser.getXMLReader();
    }

    public static List<ClassPathResource> loadDbChangeSetResources(String source) {
        try {
            XMLReader xmlReader = getInstance();
            ChangeHandler logHandler = new ChangeHandler("include", "file");
            xmlReader.setContentHandler(logHandler);
            // Parse XML
            xmlReader.parse(new ClassPathResource(source.replace("classpath:", "").trim()).getFile().getPath());
            List<String> changeSetFiles = logHandler.getSets();

            List<ClassPathResource> result = new ArrayList<>();
            ChangeHandler setHandler = new ChangeHandler("sqlFile", "path");
            for (String set : changeSetFiles) {
                xmlReader.setContentHandler(setHandler);
                // Parse XML
                xmlReader.parse(new ClassPathResource(set).getFile().getPath());
                result.addAll(setHandler.getSets().stream().map(ClassPathResource::new).collect(Collectors.toList()));
                setHandler.reset();
            }
            return result;
        } catch (Exception e) {
            throw new IllegalStateException("Exception occurred while loading initialization scripts!");
        }
    }

    public static class ChangeHandler extends DefaultHandler {
        @Getter
        private List<String> sets = new ArrayList<>();

        private final String tag;
        private final String attr;

        public ChangeHandler(String tag, String attr) {
            this.tag = tag;
            this.attr = attr;
        }

        @Override
        public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
            if (tag.equals(qName)) {
                sets.add(attributes.getValue(attr));
            }
        }

        public void reset() {
            sets.clear();
        }
    }
}

