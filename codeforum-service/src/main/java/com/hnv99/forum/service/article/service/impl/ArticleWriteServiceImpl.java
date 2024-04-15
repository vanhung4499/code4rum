package com.hnv99.forum.service.article.service.impl;

import com.hnv99.forum.api.model.context.ReqInfoContext;
import com.hnv99.forum.api.model.enums.*;
import com.hnv99.forum.api.model.event.ArticleMsgEvent;
import com.hnv99.forum.api.model.exception.ExceptionUtil;
import com.hnv99.forum.api.model.vo.article.ArticlePostReq;
import com.hnv99.forum.api.model.vo.constants.StatusEnum;
import com.hnv99.forum.api.model.vo.user.dto.BaseUserInfoDTO;
import com.hnv99.forum.core.permission.UserRole;
import com.hnv99.forum.core.util.NumUtil;
import com.hnv99.forum.core.util.SpringUtil;
import com.hnv99.forum.core.util.id.IdUtil;
import com.hnv99.forum.service.article.converter.ArticleConverter;
import com.hnv99.forum.service.article.repository.dao.ArticleDao;
import com.hnv99.forum.service.article.repository.dao.ArticleTagDao;
import com.hnv99.forum.service.article.repository.entity.ArticleDO;
import com.hnv99.forum.service.article.service.ArticleWriteService;
import com.hnv99.forum.service.article.service.ColumnSettingService;
import com.hnv99.forum.service.image.service.ImageService;
import com.hnv99.forum.service.user.service.AuthorWhiteListService;
import com.hnv99.forum.service.user.service.UserFootService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

import javax.annotation.Resource;
import java.util.Date;
import java.util.Objects;
import java.util.Set;

/**
 * Service class for article operations.
 */
@Slf4j
@Service
public class ArticleWriteServiceImpl implements ArticleWriteService {

    private final ArticleDao articleDao;
    private final ArticleTagDao articleTagDao;

    @Autowired
    private ColumnSettingService columnSettingService;

    @Autowired
    private UserFootService userFootService;

    @Autowired
    private ImageService imageService;

    @Resource
    private TransactionTemplate transactionTemplate;

    @Autowired
    private AuthorWhiteListService articleWhiteListService;

    public ArticleWriteServiceImpl(ArticleDao articleDao, ArticleTagDao articleTagDao) {
        this.articleDao = articleDao;
        this.articleTagDao = articleTagDao;
    }

    /**
     * Save an article. If articleId exists, it indicates an update; otherwise, it indicates an insert.
     *
     * @param req    The article body.
     * @param author The author of the article.
     * @return The ID of the saved article.
     */
    @Override
    public Long saveArticle(ArticlePostReq req, Long author) {
        ArticleDO article = ArticleConverter.toArticleDo(req, author);
        String content = imageService.mdImgReplace(req.getContent());
        return transactionTemplate.execute(new TransactionCallback<Long>() {
            @Override
            public Long doInTransaction(TransactionStatus status) {
                Long articleId;
                if (NumUtil.nullOrZero(req.getArticleId())) {
                    articleId = insertArticle(article, content, req.getTagIds());
                    log.info("Article published successfully! title={}", req.getTitle());
                } else {
                    articleId = updateArticle(article, content, req.getTagIds());
                    log.info("Article updated successfully! title={}", article.getTitle());
                }
                if (req.getColumnId() != null) {
                    // Update the column information corresponding to the article
                    columnSettingService.saveColumnArticle(articleId, req.getColumnId());
                }
                return articleId;
            }
        });
    }

    /**
     * Create a new article.
     *
     * @param article The article object.
     * @param content The content of the article.
     * @param tags    The tags associated with the article.
     * @return The ID of the created article.
     */
    private Long insertArticle(ArticleDO article, String content, Set<Long> tags) {
        // Data changes for three tables: article, article_detail, and tag.
        if (needToReview(article)) {
            // Articles published by authors not on the whitelist need to be reviewed.
            article.setStatus(PushStatusEnum.REVIEW.getCode());
        }

        // 1. Save the article.
        // Generate a distributed ID for the article.
        Long articleId = IdUtil.genId();
        article.setId(articleId);
        articleDao.saveOrUpdate(article);

        // 2. Save the article content.
        articleDao.saveArticleContent(articleId, content);

        // 3. Save the article tags.
        articleTagDao.batchSave(articleId, tags);

        // Publish the article, incrementing the read count.
        userFootService.saveOrUpdateUserFoot(DocumentTypeEnum.ARTICLE, articleId, article.getUserId(), article.getUserId(), OperateTypeEnum.READ);

        // Todo: Optimize event publishing here, send multiple events at once? Or use bitwise operations to represent multiple event states.
        // Publish an event for article creation.
        SpringUtil.publishEvent(new ArticleMsgEvent<>(this, ArticleEventEnum.CREATE, article));
        // If the article is published directly online, publish an online event.
        SpringUtil.publishEvent(new ArticleMsgEvent<>(this, ArticleEventEnum.ONLINE, article));
        return articleId;
    }

    /**
     * Update an existing article.
     *
     * @param article The article object.
     * @param content The content of the article.
     * @param tags    The updated tags associated with the article.
     * @return The ID of the updated article.
     */
    private Long updateArticle(ArticleDO article, String content, Set<Long> tags) {
        // FIXME: Support for article version history needs to be supplemented: If the article is under review, update the previous record directly; otherwise, insert a new record.
        boolean review = article.getStatus().equals(PushStatusEnum.REVIEW.getCode());
        if (needToReview(article)) {
            article.setStatus(PushStatusEnum.REVIEW.getCode());
        }
        // Update the article.
        article.setUpdateTime(new Date());
        articleDao.updateById(article);

        // Update the content.
        articleDao.updateArticleContent(article.getId(), content, review);

        // Update tags.
        if (tags != null && tags.size() > 0) {
            articleTagDao.updateTags(article.getId(), tags);
        }

        // Publish events for pending review articles.
        if (article.getStatus() == PushStatusEnum.ONLINE.getCode()) {
            // If the article is still published directly online (for whitelist authors).
            SpringUtil.publishEvent(new ArticleMsgEvent<>(this, ArticleEventEnum.ONLINE, article));
        } else if (review) {
            // For non-whitelist authors, articles under review remain in the review status after modification.
            SpringUtil.publishEvent(new ArticleMsgEvent<>(this, ArticleEventEnum.REVIEW, article));
        }
        return article.getId();
    }

    /**
     * Delete an article
     *
     * @param articleId
     */
    @Override
    public void deleteArticle(Long articleId, Long loginUserId) {
        ArticleDO dto = articleDao.getById(articleId);
        if (dto != null && !Objects.equals(dto.getUserId(), loginUserId)) {
            // No permission
            throw ExceptionUtil.of(StatusEnum.FORBID_ERROR_MIXED, "Please confirm if the article belongs to you!");
        }

        if (dto != null && dto.getDeleted() != YesOrNoEnum.YES.getCode()) {
            dto.setDeleted(YesOrNoEnum.YES.getCode());
            articleDao.updateById(dto);

            // Publish article deletion event
            SpringUtil.publishEvent(new ArticleMsgEvent<>(this, ArticleEventEnum.DELETE, dto));
        }
    }


    /**
     * Articles published by users not on the whitelist need to be reviewed first
     *
     * @param article
     * @return
     */
    private boolean needToReview(ArticleDO article) {
        // Add the admin user to the whitelist
        BaseUserInfoDTO user = ReqInfoContext.getReqInfo().getUser();
        if (user.getRole() != null && user.getRole().equalsIgnoreCase(UserRole.ADMIN.name())) {
            return false;
        }
        return article.getStatus() == PushStatusEnum.ONLINE.getCode() && !articleWhiteListService.authorInArticleWhiteList(article.getUserId());
    }

}

