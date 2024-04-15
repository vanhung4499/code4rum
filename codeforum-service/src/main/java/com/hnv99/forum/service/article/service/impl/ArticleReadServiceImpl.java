package com.hnv99.forum.service.article.service.impl;

import com.hnv99.forum.api.model.enums.*;
import com.hnv99.forum.api.model.exception.ExceptionUtil;
import com.hnv99.forum.api.model.vo.PageListVo;
import com.hnv99.forum.api.model.vo.PageParam;
import com.hnv99.forum.api.model.vo.PageVo;
import com.hnv99.forum.api.model.vo.article.dto.ArticleDTO;
import com.hnv99.forum.api.model.vo.article.dto.CategoryDTO;
import com.hnv99.forum.api.model.vo.article.dto.SimpleArticleDTO;
import com.hnv99.forum.api.model.vo.article.dto.TagDTO;
import com.hnv99.forum.api.model.vo.constants.StatusEnum;
import com.hnv99.forum.api.model.vo.user.dto.BaseUserInfoDTO;
import com.hnv99.forum.core.util.ArticleUtil;
import com.hnv99.forum.service.article.converter.ArticleConverter;
import com.hnv99.forum.service.article.repository.dao.ArticleDao;
import com.hnv99.forum.service.article.repository.dao.ArticleTagDao;
import com.hnv99.forum.service.article.repository.entity.ArticleDO;
import com.hnv99.forum.service.article.service.ArticleReadService;
import com.hnv99.forum.service.article.service.CategoryService;
import com.hnv99.forum.service.constant.EsFieldConstant;
import com.hnv99.forum.service.constant.EsIndexConstant;
import com.hnv99.forum.service.statistics.service.CountService;
import com.hnv99.forum.service.user.repository.entity.UserFootDO;
import com.hnv99.forum.service.user.service.UserFootService;
import com.hnv99.forum.service.user.service.UserService;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.MultiMatchQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Article query-related service class
 */
@Service
public class ArticleReadServiceImpl implements ArticleReadService {

    @Autowired
    private ArticleDao articleDao;

    @Autowired
    private ArticleTagDao articleTagDao;

    @Autowired
    private CategoryService categoryService;

    /**
     * In a project, UserFootService is an internal service call
     * When splitting microservices, this will be accessed as a remote service
     */
    @Autowired
    private UserFootService userFootService;

    @Autowired
    private CountService countService;

    @Autowired
    private UserService userService;

    // Whether to enable Elasticsearch
    @Value("${elasticsearch.open}")
    private Boolean openES;

    @Autowired
    private RestHighLevelClient restHighLevelClient;

    @Override
    public ArticleDO queryBasicArticle(Long articleId) {
        return articleDao.getById(articleId);
    }

    @Override
    public String generateSummary(String content) {
        return ArticleUtil.pickSummary(content);
    }

    @Override
    public PageVo<TagDTO> queryTagsByArticleId(Long articleId) {
        List<TagDTO> tagDTOs = articleTagDao.queryArticleTagDetails(articleId);
        return PageVo.build(tagDTOs, 1, 10, tagDTOs.size());
    }

    @Override
    public ArticleDTO queryDetailArticleInfo(Long articleId) {
        ArticleDTO article = articleDao.queryArticleDetail(articleId);
        if (article == null) {
            throw ExceptionUtil.of(StatusEnum.ARTICLE_NOT_EXISTS, articleId);
        }
        // Update category-related information
        CategoryDTO category = article.getCategory();
        category.setCategory(categoryService.queryCategoryName(category.getCategoryId()));

        // Update tag information
        article.setTags(articleTagDao.queryArticleTagDetails(articleId));
        return article;
    }

    /**
     * Query all related information of an article, including main content, category, tags, reading count, whether the current logged-in user has liked, commented, and more.
     *
     * @param articleId
     * @param readUser
     * @return
     */
    @Override
    public ArticleDTO queryFullArticleInfo(Long articleId, Long readUser) {
        ArticleDTO article = queryDetailArticleInfo(articleId);

        // Increment article reading count
        countService.incrArticleReadCount(article.getAuthor(), articleId);

        // Article operation flags
        if (readUser != null) {
            // Update for user footprint and check if liked, commented, or collected
            UserFootDO foot = userFootService.saveOrUpdateUserFoot(DocumentTypeEnum.ARTICLE, articleId,
                    article.getAuthor(), readUser, OperateTypeEnum.READ);
            article.setPraised(Objects.equals(foot.getPraiseStat(), PraiseStatEnum.PRAISE.getCode()));
            article.setCommented(Objects.equals(foot.getCommentStat(), CommentStatEnum.COMMENT.getCode()));
            article.setCollected(Objects.equals(foot.getCollectionStat(), CollectionStatEnum.COLLECTION.getCode()));
        } else {
            // Not logged in, all set to false
            article.setPraised(false);
            article.setCommented(false);
            article.setCollected(false);
        }

        // Update article statistical counts
        article.setCount(countService.queryArticleStatisticInfo(articleId));

        // Set liked users for the article
        article.setPraisedUsers(userFootService.queryArticlePraisedUsers(articleId));
        return article;
    }

    /**
     * Query articles by category
     *
     * @param categoryId
     * @param page
     * @return
     */
    @Override
    public PageListVo<ArticleDTO> queryArticlesByCategory(Long categoryId, PageParam page) {
        List<ArticleDO> records = articleDao.listArticlesByCategoryId(categoryId, page);
        return buildArticleListVo(records, page.getPageSize());
    }

    /**
     * Query top articles by category
     *
     * @param categoryId
     * @return
     */
    @Override
    public List<ArticleDTO> queryTopArticlesByCategory(Long categoryId) {
        PageParam page = PageParam.newPageInstance(PageParam.DEFAULT_PAGE_NUM, PageParam.TOP_PAGE_SIZE);
        List<ArticleDO> articleDTOS = articleDao.listArticlesByCategoryId(categoryId, page);
        return articleDTOS.stream().map(this::fillArticleRelatedInfo).collect(Collectors.toList());
    }

    @Override
    public Long queryArticleCountByCategory(Long categoryId) {
        return articleDao.countArticleByCategoryId(categoryId);
    }

    @Override
    public Map<Long, Long> queryArticleCountsByCategory() {
        return articleDao.countArticleByCategoryId();
    }

    @Override
    public PageListVo<ArticleDTO> queryArticlesByTag(Long tagId, PageParam page) {
        List<ArticleDO> records = articleDao.listRelatedArticlesOrderByReadCount(null, Arrays.asList(tagId), page);
        return buildArticleListVo(records, page.getPageSize());
    }

    @Override
    public List<SimpleArticleDTO> querySimpleArticleBySearchKey(String key) {
        // todo When the key is empty, return popular recommendations
        if (StringUtils.isBlank(key)) {
            return Collections.emptyList();
        }
        key = key.trim();
        if (!openES) {
            List<ArticleDO> records = articleDao.listSimpleArticlesByBySearchKey(key);
            return records.stream().map(s -> new SimpleArticleDTO().setId(s.getId()).setTitle(s.getTitle()))
                    .collect(Collectors.toList());
        }
        // TODO Integration with ES
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        MultiMatchQueryBuilder multiMatchQueryBuilder = QueryBuilders.multiMatchQuery(key,
                EsFieldConstant.ES_FIELD_TITLE,
                EsFieldConstant.ES_FIELD_SHORT_TITLE);
        searchSourceBuilder.query(multiMatchQueryBuilder);

        SearchRequest searchRequest = new SearchRequest(new String[]{EsIndexConstant.ES_INDEX_ARTICLE},
                searchSourceBuilder);
        SearchResponse searchResponse = null;
        try {
            searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
        } catch (IOException e) {
            e.printStackTrace();
        }
        SearchHits hits = searchResponse.getHits();
        SearchHit[] hitsList = hits.getHits();
        List<Integer> ids = new ArrayList<>();
        for (SearchHit documentFields : hitsList) {
            ids.add(Integer.parseInt(documentFields.getId()));
        }
        if (ObjectUtils.isEmpty(ids)) {
            return null;
        }
        List<ArticleDO> records = articleDao.selectByIds(ids);
        return records.stream().map(s -> new SimpleArticleDTO().setId(s.getId()).setTitle(s.getTitle()))
                .collect(Collectors.toList());
    }

    @Override
    public PageListVo<ArticleDTO> queryArticlesBySearchKey(String key, PageParam page) {
        List<ArticleDO> records = articleDao.listArticlesByBySearchKey(key, page);
        return buildArticleListVo(records, page.getPageSize());
    }


    @Override
    public PageListVo<ArticleDTO> queryArticlesByUserAndType(Long userId, PageParam pageParam, HomeSelectEnum select) {
        List<ArticleDO> records = null;
        if (select == HomeSelectEnum.ARTICLE) {
            // User's article list
            records = articleDao.listArticlesByUserId(userId, pageParam);
        } else if (select == HomeSelectEnum.READ) {
            // User's reading records
            List<Long> articleIds = userFootService.queryUserReadArticleList(userId, pageParam);
            records = CollectionUtils.isEmpty(articleIds) ? Collections.emptyList() : articleDao.listByIds(articleIds);
            records = sortByIds(articleIds, records);
        } else if (select == HomeSelectEnum.COLLECTION) {
            // User's collection list
            List<Long> articleIds = userFootService.queryUserCollectionArticleList(userId, pageParam);
            records = CollectionUtils.isEmpty(articleIds) ? Collections.emptyList() : articleDao.listByIds(articleIds);
            records = sortByIds(articleIds, records);
        }

        if (CollectionUtils.isEmpty(records)) {
            return PageListVo.emptyVo();
        }
        return buildArticleListVo(records, pageParam.getPageSize());
    }

    /**
     * fixme: This sorting logic seems to have a problem
     *
     * @param articleIds
     * @param records
     * @return
     */
    private List<ArticleDO> sortByIds(List<Long> articleIds, List<ArticleDO> records) {
        List<ArticleDO> articleDOS = new ArrayList<>();
        Map<Long, ArticleDO> articleDOMap = records.stream().collect(Collectors.toMap(ArticleDO::getId, t -> t));
        articleIds.forEach(articleId -> {
            if (articleDOMap.containsKey(articleId)) {
                articleDOS.add(articleDOMap.get(articleId));
            }
        });
        return articleDOS;
    }

    @Override
    public PageListVo<ArticleDTO> buildArticleListVo(List<ArticleDO> records, long pageSize) {
        List<ArticleDTO> result = records.stream().map(this::fillArticleRelatedInfo).collect(Collectors.toList());
        return PageListVo.newVo(result, pageSize);
    }

    /**
     * Fill in the reading count, author, category, tags, and other information for the article
     *
     * @param record
     * @return
     */
    private ArticleDTO fillArticleRelatedInfo(ArticleDO record) {
        ArticleDTO dto = ArticleConverter.toDto(record);
        // Category information
        dto.getCategory().setCategory(categoryService.queryCategoryName(record.getCategoryId()));
        // Tag list
        dto.setTags(articleTagDao.queryArticleTagDetails(record.getId()));
        // Reading count statistics
        dto.setCount(countService.queryArticleStatisticInfo(record.getId()));
        // Author information
        BaseUserInfoDTO author = userService.queryBasicUserInfo(dto.getAuthor());
        dto.setAuthorName(author.getUserName());
        dto.setAuthorAvatar(author.getPhoto());
        return dto;
    }

    @Override
    public PageListVo<SimpleArticleDTO> queryHotArticlesForRecommend(PageParam pageParam) {
        List<SimpleArticleDTO> list = articleDao.listHotArticles(pageParam);
        return PageListVo.newVo(list, pageParam.getPageSize());
    }

    @Override
    public int queryArticleCount(long authorId) {
        return articleDao.countArticleByUser(authorId);
    }

    @Override
    public Long getArticleCount() {
        return articleDao.countArticle();
    }

}

