package com.hnv99.forum.service.sitemap.service.impl;

import com.hnv99.forum.api.model.enums.ArticleEventEnum;
import com.hnv99.forum.api.model.event.ArticleMsgEvent;
import com.hnv99.forum.api.model.vo.article.dto.SimpleArticleDTO;
import com.hnv99.forum.core.cache.RedisClient;
import com.hnv99.forum.core.util.DateUtil;
import com.hnv99.forum.service.article.repository.dao.ArticleDao;
import com.hnv99.forum.service.article.repository.entity.ArticleDO;
import com.hnv99.forum.service.sitemap.constants.SitemapConstants;
import com.hnv99.forum.service.sitemap.model.SiteCntVo;
import com.hnv99.forum.service.sitemap.model.SiteMapVo;
import com.hnv99.forum.service.sitemap.model.SiteUrlVo;
import com.hnv99.forum.service.sitemap.service.SitemapService;
import com.hnv99.forum.service.statistics.service.CountService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Implementation of SitemapService
 */
@Slf4j
@Service
public class SitemapServiceImpl implements SitemapService {
    @Value("${view.site.host:https://code4rum.com}")
    private String host;
    private static final int SCAN_SIZE = 100;

    private static final String SITE_MAP_CACHE_KEY = "sitemap";

    @Resource
    private ArticleDao articleDao;
    @Resource
    private CountService countService;

    /**
     * Get the sitemap
     *
     * @return The sitemap
     */
    public SiteMapVo getSiteMap() {
        Map<String, Long> siteMap = RedisClient.hGetAll(SITE_MAP_CACHE_KEY, Long.class);
        if (CollectionUtils.isEmpty(siteMap)) {
            initSiteMap();
        }
        siteMap = RedisClient.hGetAll(SITE_MAP_CACHE_KEY, Long.class);
        SiteMapVo vo = initBasicSite();
        if (CollectionUtils.isEmpty(siteMap)) {
            return vo;
        }

        for (Map.Entry<String, Long> entry : siteMap.entrySet()) {
            vo.addUrl(new SiteUrlVo(host + "/article/detail/" + entry.getKey(), DateUtil.time2utc(entry.getValue())));
        }
        return vo;
    }

    /**
     * FIXME: Lock initialization, it is more recommended to use distributed lock
     */
    private synchronized void initSiteMap() {
        long lastId = 0L;
        RedisClient.del(SITE_MAP_CACHE_KEY);
        while (true) {
            List<SimpleArticleDTO> list = articleDao.getBaseMapper().listArticlesOrderById(lastId, SCAN_SIZE);
            list.forEach(s -> countService.refreshArticleStatisticInfo(s.getId()));

            Map<String, Long> map = list.stream().collect(Collectors.toMap(s -> String.valueOf(s.getId()), s -> s.getCreateTime().getTime(), (a, b) -> a));
            RedisClient.hMSet(SITE_MAP_CACHE_KEY, map);
            if (list.size() < SCAN_SIZE) {
                break;
            }
            lastId = list.get(list.size() - 1).getId();
        }
    }

    private SiteMapVo initBasicSite() {
        SiteMapVo vo = new SiteMapVo();
        String time = DateUtil.time2utc(System.currentTimeMillis());
        vo.addUrl(new SiteUrlVo(host + "/", time));
        vo.addUrl(new SiteUrlVo(host + "/column", time));
        vo.addUrl(new SiteUrlVo(host + "/admin-view", time));
        return vo;
    }

    /**
     * Refresh the sitemap
     */
    @Override
    public void refreshSitemap() {
        initSiteMap();
    }

    /**
     * Automatically update the sitemap based on article's online/offline events
     *
     * @param event The article event
     */
    @EventListener(ArticleMsgEvent.class)
    public void autoUpdateSiteMap(ArticleMsgEvent<ArticleDO> event) {
        ArticleEventEnum type = event.getType();
        if (type == ArticleEventEnum.ONLINE) {
            addArticle(event.getContent().getId());
        } else if (type == ArticleEventEnum.OFFLINE || type == ArticleEventEnum.DELETE) {
            rmArticle(event.getContent().getId());
        }
    }

    /**
     * Add a new article and make it online
     *
     * @param articleId The article ID
     */
    private void addArticle(Long articleId) {
        RedisClient.hSet(SITE_MAP_CACHE_KEY, String.valueOf(articleId), System.currentTimeMillis());
    }

    /**
     * Remove an article or take it offline
     *
     * @param articleId The article ID
     */
    private void rmArticle(Long articleId) {
        RedisClient.hDel(SITE_MAP_CACHE_KEY, String.valueOf(articleId));
    }

    /**
     * Automatically refresh the sitemap at 5:15 AM every day to ensure data consistency
     */
    @Scheduled(cron = "0 15 5 * * ?")
    public void autoRefreshCache() {
        log.info("Start refreshing the URL addresses of sitemap.xml to avoid data inconsistency issues!");
        refreshSitemap();
        log.info("Refresh completed!");
    }

    /**
     * Save site visit information
     *
     * @param visitIp The visitor's IP address
     * @param path    The visited resource path
     */
    @Override
    public void saveVisitInfo(String visitIp, String path) {
        String globalKey = SitemapConstants.SITE_VISIT_KEY;
        String day = SitemapConstants.day(LocalDate.now());

        String todayKey = globalKey + "_" + day;

        Long globalUserVisitCnt = RedisClient.hIncr(globalKey + "_" + visitIp, "pv", 1);
        Long todayUserVisitCnt = RedisClient.hIncr(todayKey, "pv_" + visitIp, 1);

        RedisClient.PipelineAction pipelineAction = RedisClient.pipelineAction();
        if (globalUserVisitCnt == 1) {
            pipelineAction.add(todayKey, "uv", (connection, key, field) -> connection.hIncrBy(key, field, 1));
            pipelineAction.add(todayKey, "uv_" + path, (connection, key, field) -> connection.hIncrBy(key, field, 1));
            pipelineAction.add(globalKey, "uv", (connection, key, field) -> connection.hIncrBy(key, field, 1));
            pipelineAction.add(globalKey, "uv_" + path, (connection, key, field) -> connection.hIncrBy(key, field, 1));
        } else if (todayUserVisitCnt == 1) {
            pipelineAction.add(todayKey, "uv", (connection, key, field) -> connection.hIncrBy(key, field, 1));
            if (RedisClient.hIncr(todayKey, "pv_" + path + "_" + visitIp, 1) == 1) {
                pipelineAction.add(todayKey, "uv_" + path, (connection, key, field) -> connection.hIncrBy(key, field, 1));
            }
            if (RedisClient.hIncr(globalKey + "_" + visitIp, "pv_" + path, 1) == 1) {
                pipelineAction.add(globalKey, "uv_" + path, (connection, key, field) -> connection.hIncrBy(key, field, 1));
            }
        }

        pipelineAction.add(todayKey, "pv", (connection, key, field) -> connection.hIncrBy(key, field, 1));
        pipelineAction.add(todayKey, "pv_" + path, (connection, key, field) -> connection.hIncrBy(key, field, 1));
        if (todayUserVisitCnt > 1) {
            pipelineAction.add(todayKey, "pv_" + path + "_" + visitIp, (connection, key, field) -> connection.hIncrBy(key, field, 1));
        }

        pipelineAction.add(globalKey, "pv", (connection, key, field) -> connection.hIncrBy(key, field, 1));
        pipelineAction.add(globalKey, "pv" + "_" + path, (connection, key, field) -> connection.hIncrBy(key, field, 1));

        pipelineAction.execute();
        if (log.isDebugEnabled()) {
            log.info("User visit information updated! Total user visits: {}, visits today: {}", globalUserVisitCnt, todayUserVisitCnt);
        }
    }

    /**
     * Query site visit information for a specific date or path
     *
     * @param date The date, null for all site information
     * @param path The visited path, null for site information
     * @return Site visit information
     */
    public SiteCntVo querySiteVisitInfo(LocalDate date, String path) {
        String globalKey = SitemapConstants.SITE_VISIT_KEY;
        String day = null, todayKey = globalKey;
        if (date != null) {
            day = SitemapConstants.day(date);
            todayKey = globalKey + "_" + day;
        }

        String pvField = "pv", uvField = "uv";
        if (path != null) {
            pvField += "_" + path;
            uvField += "_" + path;
        }

        Map<String, Integer> map = RedisClient.hMGet(todayKey, Arrays.asList(pvField, uvField), Integer.class);
        SiteCntVo siteInfo = new SiteCntVo();
        siteInfo.setDay(day);
        siteInfo.setPv(map.getOrDefault(pvField, 0));
        siteInfo.setUv(map.getOrDefault(uvField, 0));
        return siteInfo;
    }
}

