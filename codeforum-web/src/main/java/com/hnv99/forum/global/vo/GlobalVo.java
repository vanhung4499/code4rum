package com.hnv99.forum.global.vo;

import com.hnv99.forum.api.model.vo.seo.SeoTagVo;
import com.hnv99.forum.api.model.vo.user.dto.BaseUserInfoDTO;
import com.hnv99.forum.config.GlobalViewConfig;
import com.hnv99.forum.service.sitemap.model.SiteCntVo;
import lombok.Data;

import java.util.List;

/**
 * GlobalVo class for storing global information about the website.
 */
@Data
public class GlobalVo {
    /**
     * Website related configuration
     */
    private GlobalViewConfig siteInfo;

    /**
     * Site statistics information
     */
    private SiteCntVo siteStatisticInfo;

    /**
     * Today's site statistics information
     */
    private SiteCntVo todaySiteStatisticInfo;

    /**
     * Environment
     */
    private String env;

    /**
     * Indicates whether the user is logged in
     */
    private Boolean isLogin;

    /**
     * Logged-in user information
     */
    private BaseUserInfoDTO user;

    /**
     * Number of notification messages
     */
    private Integer msgNum;

    /**
     * Number of online users
     */
    private Integer onlineCnt;

    private String currentDomain;

    private List<SeoTagVo> ogp;
    private String jsonLd;
}

