package com.hnv99.forum.service.sitemap.service;

import com.hnv99.forum.service.sitemap.model.SiteCntVo;
import com.hnv99.forum.service.sitemap.model.SiteMapVo;

import java.time.LocalDate;

/**
 * Site-related statistics service:
 * - Site map
 * - Page views (PV) / Unique visitors (UV)
 */
public interface SitemapService {

    /**
     * Retrieve the site map.
     *
     * @return The site map.
     */
    SiteMapVo getSiteMap();

    /**
     * Refresh the site map.
     */
    void refreshSitemap();

    /**
     * Save user visit information.
     *
     * @param visitIp The visitor's IP address.
     * @param path    The path of the visited resource.
     */
    void saveVisitInfo(String visitIp, String path);


    /**
     * Query the visit information of the site for a specific day or overall.
     *
     * @param date The date. When null, query all site information.
     * @param path The visit path. When null, query site information.
     * @return The visit information for the site.
     */
    SiteCntVo querySiteVisitInfo(LocalDate date, String path);
}