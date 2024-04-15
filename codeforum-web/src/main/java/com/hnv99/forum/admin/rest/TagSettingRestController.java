package com.hnv99.forum.admin.rest;

import com.hnv99.forum.api.model.enums.PushStatusEnum;
import com.hnv99.forum.api.model.vo.PageVo;
import com.hnv99.forum.api.model.vo.ResVo;
import com.hnv99.forum.api.model.vo.article.SearchTagReq;
import com.hnv99.forum.api.model.vo.article.TagReq;
import com.hnv99.forum.api.model.vo.article.dto.TagDTO;
import com.hnv99.forum.api.model.vo.constants.StatusEnum;
import com.hnv99.forum.core.permission.Permission;
import com.hnv99.forum.core.permission.UserRole;
import com.hnv99.forum.service.article.service.TagSettingService;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * Backend for tags.
 * Manages article tag management.
 */
@RestController
@Permission(role = UserRole.LOGIN)
@Api(value = "Article tag management controller", tags = "Tag management")
@RequestMapping(path = {"api/admin/tag/", "admin/tag/"})
public class TagSettingRestController {

    @Autowired
    private TagSettingService tagSettingService;

    /**
     * Save a tag.
     *
     * @param req The request body containing tag information.
     * @return A response indicating the success of the operation.
     */
    @Permission(role = UserRole.ADMIN)
    @PostMapping(path = "save")
    public ResVo<String> save(@RequestBody TagReq req) {
        tagSettingService.saveTag(req);
        return ResVo.ok("ok");
    }

    /**
     * Delete a tag.
     *
     * @param tagId The ID of the tag to delete.
     * @return A response indicating the success of the operation.
     */
    @Permission(role = UserRole.ADMIN)
    @GetMapping(path = "delete")
    public ResVo<String> delete(@RequestParam(name = "tagId") Integer tagId) {
        tagSettingService.deleteTag(tagId);
        return ResVo.ok("ok");
    }

    /**
     * Change the status of a tag.
     *
     * @param tagId      The ID of the tag.
     * @param pushStatus The new status of the tag.
     * @return A response indicating the success of the operation.
     */
    @Permission(role = UserRole.ADMIN)
    @GetMapping(path = "operate")
    public ResVo<String> operate(@RequestParam(name = "tagId") Integer tagId,
                                 @RequestParam(name = "pushStatus") Integer pushStatus) {
        if (pushStatus != PushStatusEnum.OFFLINE.getCode() && pushStatus!= PushStatusEnum.ONLINE.getCode()) {
            return ResVo.fail(StatusEnum.ILLEGAL_ARGUMENTS);
        }
        tagSettingService.operateTag(tagId, pushStatus);
        return ResVo.ok("ok");
    }

    /**
     * Get a list of tags.
     *
     * @param req The request body containing search criteria.
     * @return A response containing a page of tag data.
     */
    @PostMapping(path = "list")
    public ResVo<PageVo<TagDTO>> list(@RequestBody SearchTagReq req) {
        PageVo<TagDTO> tagDTOPageVo = tagSettingService.getTagList(req);
        return ResVo.ok(tagDTOPageVo);
    }
}

