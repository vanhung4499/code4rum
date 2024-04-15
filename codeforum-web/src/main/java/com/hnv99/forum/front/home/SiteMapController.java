package com.hnv99.forum.front.home;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.dataformat.xml.ser.ToXmlGenerator;
import com.hnv99.forum.service.sitemap.model.SiteMapVo;
import com.hnv99.forum.service.sitemap.service.SitemapService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.nio.charset.Charset;

/**
 * Controller for generating sitemap.xml.
 * Handles requests to generate, retrieve, and refresh the sitemap.
 */
@RestController
public class SiteMapController {

    private final XmlMapper xmlMapper = new XmlMapper();

    @Autowired
    private SitemapService sitemapService;

    /**
     * Endpoint to retrieve the sitemap data.
     *
     * @return The SiteMapVo object representing the sitemap.
     */
    @RequestMapping(path = "/sitemap", produces = "application/xml;charset=utf-8")
    public SiteMapVo sitemap() {
        return sitemapService.getSiteMap();
    }

    /**
     * Endpoint to retrieve the sitemap XML.
     *
     * @return The sitemap XML as a byte array.
     * @throws JsonProcessingException If an error occurs during XML serialization.
     */
    @RequestMapping(path = "/sitemap.xml", produces = "text/xml")
    public byte[] sitemapXml() throws JsonProcessingException {
        xmlMapper.configure(SerializationFeature.INDENT_OUTPUT, true);
        xmlMapper.configure(ToXmlGenerator.Feature.WRITE_XML_DECLARATION, true);
        SiteMapVo vo = sitemapService.getSiteMap();
        String xmlString = xmlMapper.writeValueAsString(vo);
        xmlString = xmlString.replaceAll(" xmlns=\"\"", "");
        return xmlString.getBytes(Charset.defaultCharset());
    }

    /**
     * Endpoint to refresh the sitemap.
     *
     * @return True if the sitemap refresh was successful.
     */
    @GetMapping(path = "/sitemap/refresh")
    public Boolean refresh() {
        sitemapService.refreshSitemap();
        return true;
    }
}

