package com.hnv99.forum.service.sidebar.service;

import com.hnv99.forum.api.model.enums.ConfigTypeEnum;
import com.hnv99.forum.api.model.enums.SidebarStyleEnum;
import com.hnv99.forum.api.model.enums.rank.ActivityRankTimeEnum;
import com.hnv99.forum.api.model.vo.PageListVo;
import com.hnv99.forum.api.model.vo.PageParam;
import com.hnv99.forum.api.model.vo.article.dto.SimpleArticleDTO;
import com.hnv99.forum.api.model.vo.banner.dto.ConfigDTO;
import com.hnv99.forum.api.model.vo.rank.dto.RankItemDTO;
import com.hnv99.forum.api.model.vo.recommend.RateVisitDTO;
import com.hnv99.forum.api.model.vo.recommend.SideBarDTO;
import com.hnv99.forum.api.model.vo.recommend.SideBarItemDTO;
import com.hnv99.forum.core.util.JsonUtil;
import com.hnv99.forum.core.util.SpringUtil;
import com.hnv99.forum.service.article.repository.dao.ArticleDao;
import com.hnv99.forum.service.article.service.ArticleReadService;
import com.hnv99.forum.service.config.service.ConfigService;
import com.google.common.base.Splitter;
import com.hnv99.forum.service.rank.service.UserActivityRankService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Sidebar Service Implementation
 */
@Service
public class SidebarServiceImpl implements SidebarService {

    @Autowired
    private ArticleReadService articleReadService;

    @Autowired
    private ConfigService configService;

    @Autowired
    private ArticleDao articleDao;

    /**
     * Using Caffeine local cache to handle messages that do not change much in the sidebar.
     *
     * @return
     */
    @Override
    @Cacheable(key = "'homeSidebar'", cacheManager = "caffeineCacheManager", cacheNames = "home")
    public List<SideBarDTO> queryHomeSidebarList() {
        List<SideBarDTO> list = new ArrayList<>();
        list.add(noticeSideBar());
        list.add(columnSideBar());
        list.add(hotArticles());
        SideBarDTO bar = rankList();
        if (bar != null) {
            list.add(bar);
        }
        return list;
    }

    /**
     * Notice information
     *
     * @return
     */
    private SideBarDTO noticeSideBar() {
        List<ConfigDTO> noticeList = configService.getConfigList(ConfigTypeEnum.NOTICE);
        List<SideBarItemDTO> items = new ArrayList<>(noticeList.size());
        noticeList.forEach(configDTO -> {
            List<Integer> configTags;
            if (StringUtils.isBlank(configDTO.getTags())) {
                configTags = Collections.emptyList();
            } else {
                configTags = Splitter.on(",").splitToStream(configDTO.getTags()).map(s -> Integer.parseInt(s.trim())).collect(Collectors.toList());
            }
            items.add(new SideBarItemDTO()
                    .setName(configDTO.getName())
                    .setTitle(configDTO.getContent())
                    .setUrl(configDTO.getJumpUrl())
                    .setTime(configDTO.getCreateTime().getTime())
                    .setTags(configTags)
            );
        });
        return new SideBarDTO()
                .setTitle("About Technical Tribe")
                // TODO Knowledge Planet
                .setImg("https://cdn.tobebetterjavaer.com/paicoding/main/paicoding-zsxq.jpg")
                .setUrl("https://paicoding.com/article/detail/169")
                .setItems(items)
                .setStyle(SidebarStyleEnum.NOTICE.getStyle());
    }


    /**
     * Recommended tutorial sidebar
     *
     * @return
     */
    private SideBarDTO columnSideBar() {
        List<ConfigDTO> columnList = configService.getConfigList(ConfigTypeEnum.COLUMN);
        List<SideBarItemDTO> items = new ArrayList<>(columnList.size());
        columnList.forEach(configDTO -> {
            SideBarItemDTO item = new SideBarItemDTO();
            item.setName(configDTO.getName());
            item.setTitle(configDTO.getContent());
            item.setUrl(configDTO.getJumpUrl());
            item.setImg(configDTO.getBannerUrl());
            items.add(item);
        });
        return new SideBarDTO().setTitle("Featured Tutorials").setItems(items).setStyle(SidebarStyleEnum.COLUMN.getStyle());
    }


    /**
     * Hot Articles
     *
     * @return
     */
    private SideBarDTO hotArticles() {
        PageListVo<SimpleArticleDTO> vo = articleReadService.queryHotArticlesForRecommend(PageParam.newPageInstance(1, 8));
        List<SideBarItemDTO> items = vo.getList().stream().map(s -> new SideBarItemDTO().setTitle(s.getTitle()).setUrl("/article/detail/" + s.getId()).setTime(s.getCreateTime().getTime())).collect(Collectors.toList());
        return new SideBarDTO().setTitle("Hot Articles").setItems(items).setStyle(SidebarStyleEnum.ARTICLES.getStyle());
    }


    /**
     * Cache setting based on user + article dimension
     *
     * @param author
     * @param articleId
     * @return
     */
    @Override
    @Cacheable(key = "'sideBar_' + #articleId", cacheManager = "caffeineCacheManager", cacheNames = "article")
    public List<SideBarDTO> queryArticleDetailSidebarList(Long author, Long articleId) {
        List<SideBarDTO> list = new ArrayList<>(2);
        // Cannot directly use pdfSideBar() to avoid ineffective cache
        list.add(SpringUtil.getBean(SidebarServiceImpl.class).pdfSideBar());
        list.add(recommendByAuthor(author, articleId, PageParam.DEFAULT_PAGE_SIZE));
        return list;
    }

    /**
     * PDF Quality Resources
     *
     * @return
     */
    @Cacheable(key = "'sideBar'", cacheManager = "caffeineCacheManager", cacheNames = "article")
    public SideBarDTO pdfSideBar() {
        List<ConfigDTO> pdfList = configService.getConfigList(ConfigTypeEnum.PDF);
        List<SideBarItemDTO> items = new ArrayList<>(pdfList.size());
        pdfList.forEach(configDTO -> {
            SideBarItemDTO dto = new SideBarItemDTO();
            dto.setName(configDTO.getName());
            dto.setUrl(configDTO.getJumpUrl());
            dto.setImg(configDTO.getBannerUrl());
            RateVisitDTO visit;
            if (StringUtils.isNotBlank(configDTO.getExtra())) {
                visit = (JsonUtil.toObj(configDTO.getExtra(), RateVisitDTO.class));
            } else {
                visit = new RateVisitDTO();
            }
            visit.incVisit();
            // Update visit count
            configService.updateVisit(configDTO.getId(), JsonUtil.toStr(visit));
            dto.setVisit(visit);
            items.add(dto);
        });
        return new SideBarDTO().setTitle("Quality PDF").setItems(items).setStyle(SidebarStyleEnum.PDF.getStyle());
    }


    /**
     * Author's recommended article list
     *
     * @param authorId
     * @param size
     * @return
     */
    public SideBarDTO recommendByAuthor(Long authorId, Long articleId, long size) {
        List<SimpleArticleDTO> list = articleDao.listAuthorHotArticles(authorId, PageParam.newPageInstance(PageParam.DEFAULT_PAGE_NUM, size));
        List<SideBarItemDTO> items = list.stream().filter(s -> !s.getId().equals(articleId))
                .map(s -> new SideBarItemDTO()
                        .setTitle(s.getTitle()).setUrl("/article/detail/" + s.getId())
                        .setTime(s.getCreateTime().getTime()))
                .collect(Collectors.toList());
        return new SideBarDTO().setTitle("Related Articles").setItems(items).setStyle(SidebarStyleEnum.ARTICLES.getStyle());
    }


    /**
     * Query sidebar information for tutorials
     *
     * @return
     */
    @Override
    @Cacheable(key = "'columnSidebar'", cacheManager = "caffeineCacheManager", cacheNames = "column")
    public List<SideBarDTO> queryColumnSidebarList() {
        List<SideBarDTO> list = new ArrayList<>();
        list.add(subscribeSideBar());
        return list;
    }


    /**
     * Subscribe to the public account
     *
     * @return
     */
    private SideBarDTO subscribeSideBar() {
        return new SideBarDTO().setTitle("Subscribe").setSubTitle("LouZai")
                .setImg("//cdn.tobebetterjavaer.com/paicoding/a768cfc54f59d4a056f79d1c959dcae9.jpg")
                .setContent("10 must-brush formulae for campus recruitment")
                .setStyle(SidebarStyleEnum.SUBSCRIBE.getStyle());
    }


    @Autowired
    private UserActivityRankService userActivityRankService;

    /**
     * Ranking list
     *
     * @return
     */
    private SideBarDTO rankList() {
        List<RankItemDTO> list = userActivityRankService.queryRankList(ActivityRankTimeEnum.MONTH, 8);
        if (list.isEmpty()) {
            return null;
        }
        SideBarDTO sidebar = new SideBarDTO().setTitle("Monthly Activity Ranking List").setStyle(SidebarStyleEnum.ACTIVITY_RANK.getStyle());
        List<SideBarItemDTO> itemList = new ArrayList<>();
        for (RankItemDTO item : list) {
            SideBarItemDTO sideItem = new SideBarItemDTO().setName(item.getUser().getName())
                    .setUrl(String.valueOf(item.getUser().getUserId()))
                    .setImg(item.getUser().getAvatar())
                    .setTime(item.getScore().longValue());
            itemList.add(sideItem);
        }
        sidebar.setItems(itemList);
        return sidebar;
    }
}
