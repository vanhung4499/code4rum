package com.hnv99.forum.admin.rest;

import com.hnv99.forum.api.model.enums.PushStatusEnum;
import com.hnv99.forum.api.model.vo.PageVo;
import com.hnv99.forum.api.model.vo.ResVo;
import com.hnv99.forum.api.model.vo.article.*;
import com.hnv99.forum.api.model.vo.article.dto.ColumnArticleDTO;
import com.hnv99.forum.api.model.vo.article.dto.ColumnDTO;
import com.hnv99.forum.api.model.vo.article.dto.SimpleColumnDTO;
import com.hnv99.forum.api.model.vo.constants.StatusEnum;
import com.hnv99.forum.core.permission.Permission;
import com.hnv99.forum.core.permission.UserRole;
import com.hnv99.forum.front.search.vo.SearchColumnVo;
import com.hnv99.forum.service.article.repository.entity.ArticleDO;
import com.hnv99.forum.service.article.service.ArticleReadService;
import com.hnv99.forum.service.article.service.ColumnSettingService;
import com.hnv99.forum.service.image.service.ImageService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Backend for columns.
 * Manages columns and their articles.
 */
@RestController
@Slf4j
@Permission(role = UserRole.LOGIN)
@Api(value = "Column and column article management controller", tags = "Column Management")
@RequestMapping(path = {"api/admin/column/", "admin/column/"})
public class ColumnSettingRestController {

    @Autowired
    private ColumnSettingService columnSettingService;

    @Autowired
    private ArticleReadService articleReadService;

    @Autowired
    private ImageService imageService;

    /**
     * Save a column.
     *
     * @param req The column request.
     * @return A response indicating the success of the operation.
     */
    @Permission(role = UserRole.ADMIN)
    @PostMapping(path = "saveColumn")
    public ResVo<String> saveColumn(@RequestBody ColumnReq req) {
        columnSettingService.saveColumn(req);
        return ResVo.ok("ok");
    }

    /**
     * Save a column article.
     *
     * @param req The column article request.
     * @return A response indicating the success of the operation.
     */
    @Permission(role = UserRole.ADMIN)
    @PostMapping(path = "saveColumnArticle")
    public ResVo<String> saveColumnArticle(@RequestBody ColumnArticleReq req) {

        // Requires the article to exist and be published.
        ArticleDO articleDO = articleReadService.queryBasicArticle(req.getArticleId());
        if (articleDO == null || articleDO.getStatus() == PushStatusEnum.OFFLINE.getCode()) {
            return ResVo.fail(StatusEnum.ILLEGAL_ARGUMENTS_MIXED, "The article associated with the tutorial does not exist or is not published!");
        }

        columnSettingService.saveColumnArticle(req);
        return ResVo.ok("ok");
    }

    /**
     * Delete a column.
     *
     * @param columnId The ID of the column to delete.
     * @return A response indicating the success of the operation.
     */
    @Permission(role = UserRole.ADMIN)
    @GetMapping(path = "deleteColumn")
    public ResVo<String> deleteColumn(@RequestParam(name = "columnId") Long columnId) {
        columnSettingService.deleteColumn(columnId);
        return ResVo.ok("ok");
    }

    /**
     * Delete a column article.
     *
     * @param id The ID of the column article to delete.
     * @return A response indicating the success of the operation.
     */
    @Permission(role = UserRole.ADMIN)
    @GetMapping(path = "deleteColumnArticle")
    public ResVo<String> deleteColumnArticle(@RequestParam(name = "id") Long id) {
        columnSettingService.deleteColumnArticle(id);
        return ResVo.ok("ok");
    }

    /**
     * Sort column articles.
     *
     * @param req The request for sorting column articles.
     * @return A response indicating the success of the operation.
     */
    @Permission(role = UserRole.ADMIN)
    @PostMapping(path = "sortColumnArticleApi")
    public ResVo<String> sortColumnArticleApi(@RequestBody SortColumnArticleReq req) {
        columnSettingService.sortColumnArticleApi(req);
        return ResVo.ok("ok");
    }

    /**
     * Sort column articles by ID.
     *
     * @param req The request for sorting column articles by ID.
     * @return A response indicating the success of the operation.
     */
    @Permission(role = UserRole.ADMIN)
    @PostMapping(path = "sortColumnArticleByIDApi")
    public ResVo<String> sortColumnArticleByIDApi(@RequestBody SortColumnArticleByIDReq req) {
        columnSettingService.sortColumnArticleByIDApi(req);
        return ResVo.ok("ok");
    }

    /**
     * Get a list of tutorials.
     *
     * @param req The search request.
     * @return A response containing the list of tutorials.
     */
    @ApiOperation("Get a list of tutorials")
    @PostMapping(path = "list")
    public ResVo<PageVo<ColumnDTO>> list(@RequestBody SearchColumnReq req) {
        PageVo<ColumnDTO> columnDTOPageVo = columnSettingService.getColumnList(req);
        return ResVo.ok(columnDTOPageVo);
    }

    /**
     * Get a list of articles associated with a tutorial.
     *
     * @param req The request for listing column articles.
     * @return A response containing the list of column articles.
     */
    @PostMapping(path = "listColumnArticle")
    public ResVo<PageVo<ColumnArticleDTO>> listColumnArticle(@RequestBody SearchColumnArticleReq req) {
        PageVo<ColumnArticleDTO> vo = columnSettingService.getColumnArticleList(req);
        return ResVo.ok(vo);
    }

    /**
     * Search for columns.
     *
     * @param key The search key.
     * @return A response containing the search results.
     */
    @ApiOperation("Search for columns")
    @GetMapping(path = "query")
    public ResVo<SearchColumnVo> query(@RequestParam(name = "key", required = false) String key) {
        List<SimpleColumnDTO> list = columnSettingService.listSimpleColumnBySearchKey(key);
        SearchColumnVo vo = new SearchColumnVo();
        vo.setKey(key);
        vo.setItems(list);
        return ResVo.ok(vo);
    }
}

