package com.hnv99.forum.service.article.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.hnv99.forum.api.model.enums.ArticleEventEnum;
import com.hnv99.forum.api.model.enums.OperateArticleEnum;
import com.hnv99.forum.api.model.enums.PushStatusEnum;
import com.hnv99.forum.api.model.enums.YesOrNoEnum;
import com.hnv99.forum.api.model.event.ArticleMsgEvent;
import com.hnv99.forum.api.model.exception.ExceptionUtil;
import com.hnv99.forum.api.model.vo.PageVo;
import com.hnv99.forum.api.model.vo.article.ArticlePostReq;
import com.hnv99.forum.api.model.vo.article.SearchArticleReq;
import com.hnv99.forum.api.model.vo.article.dto.ArticleAdminDTO;
import com.hnv99.forum.api.model.vo.constants.StatusEnum;
import com.hnv99.forum.core.util.SpringUtil;
import com.hnv99.forum.service.article.converter.ArticleStructMapper;
import com.hnv99.forum.service.article.repository.dao.ArticleDao;
import com.hnv99.forum.service.article.repository.dao.ColumnArticleDao;
import com.hnv99.forum.service.article.repository.entity.ArticleDO;
import com.hnv99.forum.service.article.repository.entity.ColumnArticleDO;
import com.hnv99.forum.service.article.repository.params.SearchArticleParams;
import com.hnv99.forum.service.article.service.ArticleSettingService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * Article management backend
 */
@Service
public class ArticleSettingServiceImpl implements ArticleSettingService {

    @Autowired
    private ArticleDao articleDao;

    @Autowired
    private ColumnArticleDao columnArticleDao;

    @Override
    @CacheEvict(key = "'sideBar_' + #req.articleId", cacheManager = "caffeineCacheManager", cacheNames = "article")
    public void updateArticle(ArticlePostReq req) {
        if (req.getStatus() != PushStatusEnum.OFFLINE.getCode()
                && req.getStatus() != PushStatusEnum.ONLINE.getCode()
                && req.getStatus() != PushStatusEnum.REVIEW.getCode()) {
            throw ExceptionUtil.of(StatusEnum.ILLEGAL_ARGUMENTS_MIXED, "Invalid publishing status!");
        }
        ArticleDO article = articleDao.getById(req.getArticleId());
        if (article == null) {
            throw ExceptionUtil.of(StatusEnum.RECORDS_NOT_EXISTS, "Article does not exist!");
        }

        if (StringUtils.isNotBlank(req.getTitle())) {
            article.setTitle(req.getTitle());
        }
        if (StringUtils.isNotBlank(req.getShortTitle())) {
            article.setShortTitle(req.getShortTitle());
        }

        ArticleEventEnum operateEvent = null;
        if (req.getStatus() != null) {
            article.setStatus(req.getStatus());
            if (req.getStatus() == PushStatusEnum.OFFLINE.getCode()) {
                operateEvent = ArticleEventEnum.OFFLINE;
            } else if (req.getStatus() == PushStatusEnum.REVIEW.getCode()) {
                operateEvent = ArticleEventEnum.REVIEW;
            } else if (req.getStatus() == PushStatusEnum.ONLINE.getCode()) {
                operateEvent = ArticleEventEnum.ONLINE;
            }
        }
        articleDao.updateById(article);

        if (operateEvent != null) {
            // Publish events for pending review, online, and offline articles
            SpringUtil.publishEvent(new ArticleMsgEvent<>(this, operateEvent, article));
        }
    }

    @Override
    public PageVo<ArticleAdminDTO> getArticleList(SearchArticleReq req) {
        // Convert parameters from the frontend to database query parameters
        SearchArticleParams searchArticleParams = ArticleStructMapper.INSTANCE.toSearchParams(req);

        // Query the list of articles, paginated
        List<ArticleAdminDTO> articleDTOS = articleDao.listArticlesByParams(searchArticleParams);

        // Query the total number of articles
        Long totalCount = articleDao.countArticleByParams(searchArticleParams);
        return PageVo.build(articleDTOS, req.getPageSize(), req.getPageNumber(), totalCount);
    }

    @Override
    public void deleteArticle(Long articleId) {
        ArticleDO dto = articleDao.getById(articleId);
        if (dto != null && dto.getDeleted() != YesOrNoEnum.YES.getCode()) {
            // Check if the article is associated with tutorials, if it is, it cannot be deleted
            long count = columnArticleDao.count(
                    Wrappers.<ColumnArticleDO>lambdaQuery().eq(ColumnArticleDO::getArticleId, articleId));

            if (count > 0) {
                throw ExceptionUtil.of(StatusEnum.ARTICLE_RELATION_TUTORIAL, articleId, "Please remove the association between the article and tutorials first");
            }

            dto.setDeleted(YesOrNoEnum.YES.getCode());
            articleDao.updateById(dto);

            // Publish article deletion event
            SpringUtil.publishEvent(new ArticleMsgEvent<>(this, ArticleEventEnum.DELETE, dto));
        } else {
            throw ExceptionUtil.of(StatusEnum.ARTICLE_NOT_EXISTS, articleId);
        }
    }

    @Override
    public void operateArticle(Long articleId, OperateArticleEnum operate) {
        ArticleDO articleDO = articleDao.getById(articleId);
        if (articleDO == null) {
            throw ExceptionUtil.of(StatusEnum.ARTICLE_NOT_EXISTS, articleId);
        }
        setArticleStat(articleDO, operate);
        articleDao.updateById(articleDO);
    }

    private void setArticleStat(ArticleDO articleDO, OperateArticleEnum operate) {
        switch (operate) {
            case OFFICIAL:
            case CANCEL_OFFICIAL:
                compareAndUpdate(articleDO::getOfficialStat, articleDO::setOfficialStat, operate.getDbStatCode());
                return;
            case TOPPING:
            case CANCEL_TOPPING:
                compareAndUpdate(articleDO::getToppingStat, articleDO::setToppingStat, operate.getDbStatCode());
                return;
            case CREAM:
            case CANCEL_CREAM:
                compareAndUpdate(articleDO::getCreamStat, articleDO::setCreamStat, operate.getDbStatCode());
                return;
            default:
        }
    }

    /**
     * If they are the same, return false directly without updating; if different, update and return true
     *
     * @param <T>
     * @param supplier
     * @param consumer
     * @param input
     */
    private <T> void compareAndUpdate(Supplier<T> supplier, Consumer<T> consumer, T input) {
        if (Objects.equals(supplier.get(), input)) {
            return;
        }
        consumer.accept(input);
    }
}

