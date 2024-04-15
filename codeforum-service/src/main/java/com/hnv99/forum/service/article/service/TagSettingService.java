package com.hnv99.forum.service.article.service;

import com.hnv99.forum.api.model.vo.PageVo;
import com.hnv99.forum.api.model.vo.article.SearchTagReq;
import com.hnv99.forum.api.model.vo.article.TagReq;
import com.hnv99.forum.api.model.vo.article.dto.TagDTO;

/**
 * Tag Backend Service Interface
 */
public interface TagSettingService {

    /**
     * Save tag
     *
     * @param tagReq Request object containing tag information
     */
    void saveTag(TagReq tagReq);

    /**
     * Delete tag by ID
     *
     * @param tagId ID of the tag to delete
     */
    void deleteTag(Integer tagId);

    /**
     * Operate on tag (e.g., change push status)
     *
     * @param tagId      ID of the tag to operate on
     * @param pushStatus New push status for the tag
     */
    void operateTag(Integer tagId, Integer pushStatus);

    /**
     * Retrieve a list of tags
     *
     * @param req Request object containing search parameters
     * @return PageVo containing the list of tags
     */
    PageVo<TagDTO> getTagList(SearchTagReq req);

    /**
     * Retrieve tag information by ID
     *
     * @param tagId ID of the tag
     * @return TagDTO containing the tag information
     */
    TagDTO getTagById(Long tagId);
}

