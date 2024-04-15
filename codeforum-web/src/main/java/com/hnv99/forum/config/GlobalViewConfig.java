package com.hnv99.forum.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * GlobalViewConfig class for global view configuration properties.
 */
@Data
@ConfigurationProperties(prefix = "view.site")
@Component
public class GlobalViewConfig {

    private String cdnImgStyle;

    private String websiteRecord;

    private Integer pageSize;

    private String websiteName;

    private String websiteLogoUrl;

    private String websiteFaviconIconUrl;

    private String contactMeWxQrCode;

    private String contactMeStarQrCode;

    private String zsxqUrl;

    private String zsxqPosterUrl;

    private String contactMeTitle;

    private String wxLoginUrl;

    private String host;

    private String welcomeInfo;

    private String starInfo;

    private String oss;

    private String zsxqArticleReadCount;

    private String needLoginArticleReadCount;

    public String getOss() {
        if (oss == null) {
            this.oss = "";
        }
        return this.oss;
    }
}

