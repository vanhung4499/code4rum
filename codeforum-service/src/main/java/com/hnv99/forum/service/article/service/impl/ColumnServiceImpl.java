package com.hnv99.forum.service.article.service.impl;

import com.hnv99.forum.api.model.exception.ExceptionUtil;
import com.hnv99.forum.api.model.vo.PageListVo;
import com.hnv99.forum.api.model.vo.PageParam;
import com.hnv99.forum.api.model.vo.article.dto.ColumnDTO;
import com.hnv99.forum.api.model.vo.article.dto.SimpleArticleDTO;
import com.hnv99.forum.api.model.vo.constants.StatusEnum;
import com.hnv99.forum.api.model.vo.user.dto.BaseUserInfoDTO;
import com.hnv99.forum.api.model.vo.user.dto.ColumnFootCountDTO;
import com.hnv99.forum.service.article.converter.ColumnConvert;
import com.hnv99.forum.service.article.repository.dao.ArticleDao;
import com.hnv99.forum.service.article.repository.dao.ColumnArticleDao;
import com.hnv99.forum.service.article.repository.dao.ColumnDao;
import com.hnv99.forum.service.article.repository.entity.ColumnArticleDO;
import com.hnv99.forum.service.article.repository.entity.ColumnInfoDO;
import com.hnv99.forum.service.article.service.ColumnService;
import com.hnv99.forum.service.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Implementation of the ColumnService interface.
 */
@Service
public class ColumnServiceImpl implements ColumnService {

    @Autowired
    private ColumnDao columnDao;

    @Autowired
    private ArticleDao articleDao;

    @Autowired
    private ColumnArticleDao columnArticleDao;

    @Autowired
    private UserService userService;

    @Override
    public ColumnArticleDO getColumnArticleRelation(Long articleId) {
        return columnArticleDao.selectColumnArticleByArticleId(articleId);
    }

    /**
     * Get the list of columns.
     *
     * @param pageParam Page parameters
     * @return
     */
    @Override
    public PageListVo<ColumnDTO> listColumn(PageParam pageParam) {
        List<ColumnInfoDO> columnList = columnDao.listOnlineColumns(pageParam);
        List<ColumnDTO> result = columnList.stream().map(this::buildColumnInfo).collect(Collectors.toList());
        return PageListVo.newVo(result, pageParam.getPageSize());
    }

    @Override
    public ColumnDTO queryBasicColumnInfo(Long columnId) {
        // Find column information
        ColumnInfoDO column = columnDao.getById(columnId);
        if (column == null) {
            throw ExceptionUtil.of(StatusEnum.COLUMN_NOT_EXISTS, columnId);
        }
        return ColumnConvert.toDto(column);
    }

    @Override
    public ColumnDTO queryColumnInfo(Long columnId) {
        return buildColumnInfo(queryBasicColumnInfo(columnId));
    }

    private ColumnDTO buildColumnInfo(ColumnInfoDO info) {
        return buildColumnInfo(ColumnConvert.toDto(info));
    }

    /**
     * Build column detail information.
     *
     * @param dto
     * @return
     */
    private ColumnDTO buildColumnInfo(ColumnDTO dto) {
        // Complete user information corresponding to the column
        BaseUserInfoDTO user = userService.queryBasicUserInfo(dto.getAuthor());
        dto.setAuthorName(user.getUserName());
        dto.setAuthorAvatar(user.getPhoto());
        dto.setAuthorProfile(user.getProfile());

        // Count statistics
        ColumnFootCountDTO countDTO = new ColumnFootCountDTO();
        // Update the number of articles
        countDTO.setArticleCount(columnDao.countColumnArticles(dto.getColumnId()));
        // Number of column readers
        countDTO.setReadCount(columnDao.countColumnReadPeoples(dto.getColumnId()));
        // Total number of sections
        countDTO.setTotalNums(dto.getNums());
        dto.setCount(countDTO);
        return dto;
    }

    @Override
    public ColumnArticleDO queryColumnArticle(long columnId, Integer section) {
        ColumnArticleDO article = columnDao.getColumnArticleId(columnId, section);
        if (article == null) {
            throw ExceptionUtil.of(StatusEnum.ARTICLE_NOT_EXISTS, section);
        }
        return article;
    }

    @Override
    public List<SimpleArticleDTO> queryColumnArticles(long columnId) {
        return columnDao.listColumnArticles(columnId);
    }

    @Override
    public Long getTutorialCount() {
        return this.columnDao.countColumnArticles();
    }
}

