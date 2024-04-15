package com.hnv99.forum.service.sitemap.model;

import lombok.Data;

import java.io.Serializable;

/**
 * Site count information.
 */
@Data
public class SiteCntVo implements Serializable {
    private static final long serialVersionUID = 8747459624770066661L;
    /**
     * Date
     */
    private String day;
    /**
     * Path. When querying the entire site, path is null.
     */
    private String path;
    /**
     * Page views (PV) for the site.
     */
    private Integer pv;
    /**
     * Unique visitors (UV) for the site.
     */
    private Integer uv;
}
