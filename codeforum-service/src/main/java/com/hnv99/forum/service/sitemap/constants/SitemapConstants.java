package com.hnv99.forum.service.sitemap.constants;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * Site-related constants.
 */
public class SitemapConstants {
    public static final String SITE_VISIT_KEY = "visit_info";

    public static String day(LocalDate day) {
        return DateTimeFormatter.ofPattern("yyyyMMdd").format(day);
    }
}
