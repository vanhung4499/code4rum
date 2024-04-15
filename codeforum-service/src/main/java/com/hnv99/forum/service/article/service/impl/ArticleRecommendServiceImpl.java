package com.hnv99.forum.service.article.service.impl;

import com.hnv99.forum.api.model.vo.PageListVo;
import com.hnv99.forum.api.model.vo.PageParam;
import com.hnv99.forum.api.model.vo.article.dto.ArticleDTO;
import com.hnv99.forum.service.article.repository.dao.ArticleDao;
import com.hnv99.forum.service.article.repository.dao.ArticleTagDao;
import com.hnv99.forum.service.article.repository.entity.ArticleDO;
import com.hnv99.forum.service.article.repository.entity.ArticleTagDO;
import com.hnv99.forum.service.article.service.ArticleReadService;
import com.hnv99.forum.service.article.service.ArticleRecommendService;
import com.hnv99.forum.service.sidebar.service.SidebarService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Article recommendation service implementation
 */
@Service
public class ArticleRecommendServiceImpl implements ArticleRecommendService {

    @Autowired
    private ArticleDao articleDao;

    @Autowired
    private ArticleTagDao articleTagDao;

    @Autowired
    private ArticleReadService articleReadService;

    @Autowired
    private SidebarService sidebarService;

    /**
     * Query the list of related recommended articles
     *
     * @param articleId
     * @param page
     * @return
     */
    @Override
    public PageListVo<ArticleDTO> relatedRecommend(Long articleId, PageParam page) {
        ArticleDO article = articleDao.getById(articleId);
        if (article == null) {
            return PageListVo.emptyVo();
        }
        List<Long> tagIds = articleTagDao.listArticleTags(articleId).stream()
                .map(ArticleTagDO::getTagId).collect(Collectors.toList());
        if (CollectionUtils.isEmpty(tagIds)) {
            return PageListVo.emptyVo();
        }

        List<ArticleDO> recommendArticles = articleDao.listRelatedArticlesOrderByReadCount(article.getCategoryId(), tagIds, page);
        if (recommendArticles.removeIf(s -> s.getId().equals(articleId))) {
            // Remove the current article from the recommendation list
            page.setPageSize(page.getPageSize() - 1);
        }
        return articleReadService.buildArticleListVo(recommendArticles, page.getPageSize());
    }
}

