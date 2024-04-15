package com.hnv99.forum.service.article.repository.dao;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import com.google.common.collect.Maps;
import com.hnv99.forum.api.model.context.ReqInfoContext;
import com.hnv99.forum.api.model.enums.DocumentTypeEnum;
import com.hnv99.forum.api.model.enums.OfficialStatEnum;
import com.hnv99.forum.api.model.enums.PushStatusEnum;
import com.hnv99.forum.api.model.enums.YesOrNoEnum;
import com.hnv99.forum.api.model.vo.PageParam;
import com.hnv99.forum.api.model.vo.article.dto.ArticleAdminDTO;
import com.hnv99.forum.api.model.vo.article.dto.ArticleDTO;
import com.hnv99.forum.api.model.vo.article.dto.SimpleArticleDTO;
import com.hnv99.forum.api.model.vo.article.dto.YearArticleDTO;
import com.hnv99.forum.api.model.vo.user.dto.BaseUserInfoDTO;
import com.hnv99.forum.core.permission.UserRole;
import com.hnv99.forum.service.article.converter.ArticleConverter;
import com.hnv99.forum.service.article.repository.entity.ArticleDO;
import com.hnv99.forum.service.article.repository.entity.ArticleDetailDO;
import com.hnv99.forum.service.article.repository.entity.ReadCountDO;
import com.hnv99.forum.service.article.repository.mapper.ArticleDetailMapper;
import com.hnv99.forum.service.article.repository.mapper.ArticleMapper;
import com.hnv99.forum.service.article.repository.mapper.ReadCountMapper;
import com.hnv99.forum.service.article.repository.params.SearchArticleParams;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Article-related DB operations
 * <p>
 * Encapsulation of operations involving multiple table structures, only related to DB operations
 */
@Repository
public class ArticleDao extends ServiceImpl<ArticleMapper, ArticleDO> {
    @Resource
    private ArticleDetailMapper articleDetailMapper;
    @Resource
    private ReadCountMapper readCountMapper;
    @Resource
    private ArticleMapper articleMapper;


    /**
     * Query article details
     *
     * @param articleId
     * @return
     */
    public ArticleDTO queryArticleDetail(Long articleId) {
        // Query article record
        ArticleDO article = baseMapper.selectById(articleId);
        if (article == null || Objects.equals(article.getDeleted(), YesOrNoEnum.YES.getCode())) {
            return null;
        }

        // Query article content
        ArticleDTO dto = ArticleConverter.toDto(article);
        if (showReviewContent(article)) {
            ArticleDetailDO detail = findLatestDetail(articleId);
            dto.setContent(detail.getContent());
        } else {
            // For articles under review, only the author can see the original content
            dto.setContent("### Article under review, please check back later");
        }
        return dto;
    }

    private boolean showReviewContent(ArticleDO article) {
        // If the article status is not under review, return true
        if (article.getStatus() != PushStatusEnum.REVIEW.getCode()) {
            return true;
        }

        // Get the user information from the request context
        BaseUserInfoDTO user = ReqInfoContext.getReqInfo().getUser();

        // If the user information is not available, return false
        if (user == null) {
            return false;
        }

        // Authors and admin super admins can see the review content
        return user.getUserId().equals(article.getUserId()) ||
                (user.getRole() != null && user.getRole().equalsIgnoreCase(UserRole.ADMIN.name()));
    }

    // ------------ article content  ----------------

    private ArticleDetailDO findLatestDetail(long articleId) {
        // Query article content
        LambdaQueryWrapper<ArticleDetailDO> contentQuery = Wrappers.lambdaQuery();
        contentQuery.eq(ArticleDetailDO::getDeleted, YesOrNoEnum.NO.getCode())
                .eq(ArticleDetailDO::getArticleId, articleId)
                .orderByDesc(ArticleDetailDO::getVersion);
        return articleDetailMapper.selectList(contentQuery).get(0);
    }

    /**
     * Save article content
     *
     * @param articleId
     * @param content
     * @return
     */
    public Long saveArticleContent(Long articleId, String content) {
        ArticleDetailDO detail = new ArticleDetailDO();
        detail.setArticleId(articleId);
        detail.setContent(content);
        detail.setVersion(1L);
        articleDetailMapper.insert(detail);
        return detail.getId();
    }

    /**
     * Correct article content
     *
     * @param articleId
     * @param content
     * @param update    true indicates updating the last record; false indicates inserting a new record
     */
    public void updateArticleContent(Long articleId, String content, boolean update) {
        if (update) {
            articleDetailMapper.updateContent(articleId, content);
        } else {
            ArticleDetailDO latest = findLatestDetail(articleId);
            latest.setVersion(latest.getVersion() + 1);
            latest.setId(null);
            latest.setContent(content);
            articleDetailMapper.insert(latest);
        }
    }

    // ------------- Article List Query --------------

    public List<ArticleDO> listArticlesByUserId(Long userId, PageParam pageParam) {
        LambdaQueryWrapper<ArticleDO> query = Wrappers.lambdaQuery();
        query.eq(ArticleDO::getDeleted, YesOrNoEnum.NO.getCode())
                .eq(ArticleDO::getUserId, userId)
                .last(PageParam.getLimitSql(pageParam))
                .orderByDesc(ArticleDO::getId);
        if (!Objects.equals(ReqInfoContext.getReqInfo().getUserId(), userId)) {
            // Authors can view drafts, under review, and online articles; other users can only view online articles
            query.eq(ArticleDO::getStatus, PushStatusEnum.ONLINE.getCode());
        }
        return baseMapper.selectList(query);
    }


    public List<ArticleDO> listArticlesByCategoryId(Long categoryId, PageParam pageParam) {
        if (categoryId != null && categoryId <= 0) {
            // When the category does not exist, it means querying all
            categoryId = null;
        }
        LambdaQueryWrapper<ArticleDO> query = Wrappers.lambdaQuery();
        query.eq(ArticleDO::getDeleted, YesOrNoEnum.NO.getCode())
                .eq(ArticleDO::getStatus, PushStatusEnum.ONLINE.getCode());

        // If there are four top records in the pagination, add the official query condition
        // If querying official articles, non-top articles only limit all categories
        if (categoryId == null && pageParam.getPageSize() == PageParam.TOP_PAGE_SIZE) {
            query.eq(ArticleDO::getOfficialStat, OfficialStatEnum.OFFICIAL.getCode());
        }

        Optional.ofNullable(categoryId).ifPresent(cid -> query.eq(ArticleDO::getCategoryId, cid));
        query.last(PageParam.getLimitSql(pageParam))
                .orderByDesc(ArticleDO::getToppingStat,  ArticleDO::getCreateTime);
        return baseMapper.selectList(query);
    }

    public Long countArticleByCategoryId(Long categoryId) {
        LambdaQueryWrapper<ArticleDO> query = Wrappers.lambdaQuery();
        query.eq(ArticleDO::getDeleted, YesOrNoEnum.NO.getCode())
                .eq(ArticleDO::getStatus, PushStatusEnum.ONLINE.getCode())
                .eq(ArticleDO::getCategoryId, categoryId);
        return baseMapper.selectCount(query);
    }

    /**
     * Count the number of articles by category
     *
     * @return key: categoryId, value: count
     */
    public Map<Long, Long> countArticleByCategoryId() {
        QueryWrapper<ArticleDO> query = Wrappers.query();
        query.select("category_id, count(*) as cnt")
                .eq("deleted", YesOrNoEnum.NO.getCode())
                .eq("status", PushStatusEnum.ONLINE.getCode()).groupBy("category_id");
        List<Map<String, Object>> mapList = baseMapper.selectMaps(query);
        Map<Long, Long> result = Maps.newHashMapWithExpectedSize(mapList.size());
        for (Map<String, Object> mp : mapList) {
            Long cnt = (Long) mp.get("cnt");
            if (cnt != null && cnt > 0) {
                result.put((Long) mp.get("category_id"), cnt);
            }
        }
        return result;
    }

    public List<ArticleDO> listArticlesByBySearchKey(String key, PageParam pageParam) {
        LambdaQueryWrapper<ArticleDO> query = Wrappers.lambdaQuery();
        query.eq(ArticleDO::getDeleted, YesOrNoEnum.NO.getCode())
                .eq(ArticleDO::getStatus, PushStatusEnum.ONLINE.getCode())
                .and(!StringUtils.isEmpty(key),
                        v -> v.like(ArticleDO::getTitle, key)
                                .or()
                                .like(ArticleDO::getShortTitle, key)
                                .or()
                                .like(ArticleDO::getSummary, key));
        query.last(PageParam.getLimitSql(pageParam))
                .orderByDesc(ArticleDO::getId);
        return baseMapper.selectList(query);
    }

    /**
     * Recommend similar articles by keyword from the title, returning only primary key + title
     *
     * @param key
     * @return
     */
    public List<ArticleDO> listSimpleArticlesByBySearchKey(String key) {
        LambdaQueryWrapper<ArticleDO> query = Wrappers.lambdaQuery();
        query.eq(ArticleDO::getDeleted, YesOrNoEnum.NO.getCode())
                .eq(ArticleDO::getStatus, PushStatusEnum.ONLINE.getCode())
                .and(!StringUtils.isEmpty(key),
                        v -> v.like(ArticleDO::getTitle, key)
                                .or()
                                .like(ArticleDO::getShortTitle, key)
                );
        query.select(ArticleDO::getId, ArticleDO::getTitle, ArticleDO::getShortTitle)
                .last("limit 10")
                .orderByDesc(ArticleDO::getId);
        return baseMapper.selectList(query);
    }

    /**
     * Increment read count
     *
     * @param articleId
     * @return
     */
    public int incReadCount(Long articleId) {
        LambdaQueryWrapper<ReadCountDO> query = Wrappers.lambdaQuery();
        query.eq(ReadCountDO::getDocumentId, articleId).eq(ReadCountDO::getDocumentType, DocumentTypeEnum.ARTICLE.getCode());
        ReadCountDO record = readCountMapper.selectOne(query);
        if (record == null) {
            record = new ReadCountDO().setDocumentId(articleId).setDocumentType(DocumentTypeEnum.ARTICLE.getCode()).setCnt(1);
            readCountMapper.insert(record);
        } else {
            // fixme: There is a concurrency coverage issue here, it is recommended to use "update read_count set cnt = cnt + 1 where id = xxx"
            record.setCnt(record.getCnt() + 1);
            readCountMapper.updateById(record);
        }
        return record.getCnt();
    }

    /**
     * Count the number of articles by user
     *
     * @param userId
     * @return
     */
    public int countArticleByUser(Long userId) {
        return lambdaQuery().eq(ArticleDO::getUserId, userId)
                .eq(ArticleDO::getStatus, PushStatusEnum.ONLINE.getCode())
                .eq(ArticleDO::getDeleted, YesOrNoEnum.NO.getCode())
                .count().intValue();
    }

    /**
     * Hot article recommendation, suitable for the sidebar on the homepage
     *
     * @param pageParam
     * @return
     */
    public List<SimpleArticleDTO> listHotArticles(PageParam pageParam) {
        return baseMapper.listArticlesByReadCounts(pageParam);
    }

    /**
     * Author's hot article recommendation, suitable for the sidebar on the author's details page
     *
     * @param userId
     * @param pageParam
     * @return
     */
    public List<SimpleArticleDTO> listAuthorHotArticles(long userId, PageParam pageParam) {
        return baseMapper.listArticlesByUserIdOrderByReadCounts(userId, pageParam);
    }

    /**
     * Recommend articles based on the same category + tags
     *
     * @param categoryId
     * @param tagIds
     * @return
     */
    public List<ArticleDO> listRelatedArticlesOrderByReadCount(Long categoryId, List<Long> tagIds, PageParam pageParam) {
        List<ReadCountDO> list = baseMapper.listArticleByCategoryAndTags(categoryId, tagIds, pageParam);
        if (CollectionUtils.isEmpty(list)) {
            return new ArrayList<>();
        }

        List<Long> ids = list.stream().map(ReadCountDO::getDocumentId).collect(Collectors.toList());
        List<ArticleDO> result = baseMapper.selectBatchIds(ids);
        result.sort((o1, o2) -> {
            int i1 = ids.indexOf(o1.getId());
            int i2 = ids.indexOf(o2.getId());
            return Integer.compare(i1, i2);
        });
        return result;
    }


    /**
     * Get the writing history by user ID
     *
     * @param userId
     * @return
     */
    public List<YearArticleDTO> listYearArticleByUserId(Long userId) {
        return baseMapper.listYearArticleByUserId(userId);
    }

    /**
     * Extract template code
     */
    private LambdaQueryChainWrapper<ArticleDO> buildQuery(SearchArticleParams searchArticleParams) {
        return lambdaQuery()
                .like(StringUtils.isNotBlank(searchArticleParams.getTitle()), ArticleDO::getTitle, searchArticleParams.getTitle())
                // ID not empty
                .eq(Objects.nonNull(searchArticleParams.getArticleId()), ArticleDO::getId, searchArticleParams.getArticleId())
                .eq(Objects.nonNull(searchArticleParams.getUserId()), ArticleDO::getUserId, searchArticleParams.getUserId())
                .eq(Objects.nonNull(searchArticleParams.getStatus()) && searchArticleParams.getStatus() != -1, ArticleDO::getStatus, searchArticleParams.getStatus())
                .eq(Objects.nonNull(searchArticleParams.getOfficialStat())&& searchArticleParams.getOfficialStat() != -1, ArticleDO::getOfficialStat, searchArticleParams.getOfficialStat())
                .eq(Objects.nonNull(searchArticleParams.getToppingStat())&& searchArticleParams.getToppingStat() != -1, ArticleDO::getToppingStat, searchArticleParams.getToppingStat())
                .eq(ArticleDO::getDeleted, YesOrNoEnum.NO.getCode());
    }


    /**
     * Article list (for backend)
     *
     */
    public List<ArticleAdminDTO> listArticlesByParams(SearchArticleParams params) {
        return articleMapper.listArticlesByParams(params,
                PageParam.newPageInstance(params.getPageNum(), params.getPageSize()));
    }

    /**
     * Total number of articles (for backend)
     */
    public Long countArticleByParams(SearchArticleParams searchArticleParams) {
        return articleMapper.countArticlesByParams(searchArticleParams);
    }

    /**
     * Total number of articles (for backend)
     *
     * @return
     */
    public Long countArticle() {
        return lambdaQuery()
                .eq(ArticleDO::getDeleted, YesOrNoEnum.NO.getCode())
                .count();
    }

    public List<ArticleDO> selectByIds(List<Integer> ids) {

        List<ArticleDO> articleDOS = baseMapper.selectBatchIds(ids);
        return articleDOS;

    }

}

