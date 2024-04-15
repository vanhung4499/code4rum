package com.hnv99.forum.admin.rest;

import com.hnv99.forum.api.model.vo.ResVo;
import com.hnv99.forum.api.model.vo.user.dto.BaseUserInfoDTO;
import com.hnv99.forum.core.permission.Permission;
import com.hnv99.forum.core.permission.UserRole;
import com.hnv99.forum.service.user.service.AuthorWhiteListService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Author whitelist service controller.
 * Manages whitelisted authors for publishing articles.
 */
@RestController
@Api(value = "Author whitelist management controller for publishing articles", tags = "Author Whitelist")
@Permission(role = UserRole.ADMIN)
@RequestMapping(path = {"api/admin/author/whitelist"})
public class AuthorWhiteListController {
    @Autowired
    private AuthorWhiteListService articleWhiteListService;

    /**
     * Get the whitelist of authors.
     *
     * @return A response containing the list of whitelisted authors.
     */
    @GetMapping(path = "get")
    @ApiOperation(value = "Whitelist", notes = "Returns the list of whitelisted authors")
    public ResVo<List<BaseUserInfoDTO>> whiteList() {
        return ResVo.ok(articleWhiteListService.queryAllArticleWhiteListAuthors());
    }

    /**
     * Add an author to the whitelist.
     *
     * @param authorId The user ID of the author to add to the whitelist.
     * @return A response indicating whether the operation was successful.
     */
    @GetMapping(path = "add")
    @ApiOperation(value = "Add to Whitelist", notes = "Adds the specified author to the whitelist")
    @ApiImplicitParam(name = "authorId", value = "The user ID of the author to add to the whitelist", required = true, allowEmptyValue = false, example = "1")
    public ResVo<Boolean> addAuthor(@RequestParam("authorId") Long authorId) {
        articleWhiteListService.addAuthorToArticleWhiteList(authorId);
        return ResVo.ok(true);
    }

    /**
     * Remove an author from the whitelist.
     *
     * @param authorId The user ID of the author to remove from the whitelist.
     * @return A response indicating whether the operation was successful.
     */
    @GetMapping(path = "remove")
    @ApiOperation(value = "Remove from Whitelist", notes = "Removes the specified author from the whitelist")
    @ApiImplicitParam(name = "authorId", value = "The user ID of the author to remove from the whitelist", required = true, allowEmptyValue = false, example = "1")
    public ResVo<Boolean> rmAuthor(@RequestParam("authorId") Long authorId) {
        articleWhiteListService.removeAuthorFromArticleWhiteList(authorId);
        return ResVo.ok(true);
    }
}

