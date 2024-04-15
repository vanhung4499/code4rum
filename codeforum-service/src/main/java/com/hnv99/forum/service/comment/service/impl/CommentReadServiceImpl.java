package com.hnv99.forum.service.comment.service.impl;

import com.hnv99.forum.api.model.context.ReqInfoContext;
import com.hnv99.forum.api.model.enums.DocumentTypeEnum;
import com.hnv99.forum.api.model.enums.PraiseStatEnum;
import com.hnv99.forum.api.model.vo.PageParam;
import com.hnv99.forum.api.model.vo.comment.dto.BaseCommentDTO;
import com.hnv99.forum.api.model.vo.comment.dto.SubCommentDTO;
import com.hnv99.forum.api.model.vo.comment.dto.TopCommentDTO;
import com.hnv99.forum.api.model.vo.user.dto.BaseUserInfoDTO;
import com.hnv99.forum.service.comment.converter.CommentConverter;
import com.hnv99.forum.service.comment.repository.dao.CommentDao;
import com.hnv99.forum.service.comment.repository.entity.CommentDO;
import com.hnv99.forum.service.comment.service.CommentReadService;
import com.hnv99.forum.service.statistics.service.CountService;
import com.hnv99.forum.service.user.repository.entity.UserFootDO;
import com.hnv99.forum.service.user.service.UserFootService;
import com.hnv99.forum.service.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Comment Read Service Implementation
 */
@Service
public class CommentReadServiceImpl implements CommentReadService {

    @Autowired
    private CommentDao commentDao;

    @Autowired
    private UserService userService;

    @Autowired
    private CountService countService;

    @Autowired
    private UserFootService userFootService;

    @Override
    public CommentDO queryComment(Long commentId) {
        return commentDao.getById(commentId);
    }

    @Override
    public List<TopCommentDTO> getArticleComments(Long articleId, PageParam page) {
        // 1. Query top-level comments
        List<CommentDO> comments = commentDao.listTopCommentList(articleId, page);
        if (CollectionUtils.isEmpty(comments)) {
            return Collections.emptyList();
        }
        // Store commentId -> comment in a map
        Map<Long, TopCommentDTO> topComments = comments.stream().collect(Collectors.toMap(CommentDO::getId, CommentConverter::toTopDto));

        // 2. Query non-top-level comments
        List<CommentDO> subComments = commentDao.listSubCommentIdMappers(articleId, topComments.keySet());

        // 3. Build parent-child comment relationship
        buildCommentRelation(subComments, topComments);

        // 4. Extract and sort the data to be returned, complete corresponding user information, and finally sort and return
        List<TopCommentDTO> result = new ArrayList<>();
        comments.forEach(comment -> {
            TopCommentDTO dto = topComments.get(comment.getId());
            fillTopCommentInfo(dto);
            result.add(dto);
        });

        // Sort the result based on time
        Collections.sort(result);
        return result;
    }

    /**
     * Build parent-child comment relationship
     */
    private void buildCommentRelation(List<CommentDO> subComments, Map<Long, TopCommentDTO> topComments) {
        Map<Long, SubCommentDTO> subCommentMap = subComments.stream().collect(Collectors.toMap(CommentDO::getId, CommentConverter::toSubDto));
        subComments.forEach(comment -> {
            TopCommentDTO top = topComments.get(comment.getTopCommentId());
            if (top == null) {
                return;
            }
            SubCommentDTO sub = subCommentMap.get(comment.getId());
            top.getChildComments().add(sub);
            if (Objects.equals(comment.getTopCommentId(), comment.getParentCommentId())) {
                return;
            }

            SubCommentDTO parent = subCommentMap.get(comment.getParentCommentId());
            sub.setParentContent(parent == null ? "~~Deleted~~" : parent.getCommentContent());
        });
    }

    /**
     * Fill in the information corresponding to the comment
     *
     * @param comment
     */
    private void fillTopCommentInfo(TopCommentDTO comment) {
        fillCommentInfo(comment);
        comment.getChildComments().forEach(this::fillCommentInfo);
        Collections.sort(comment.getChildComments());
    }

    /**
     * Fill in the information corresponding to the comment, such as user information, number of likes, etc.
     *
     * @param comment
     */
    private void fillCommentInfo(BaseCommentDTO comment) {
        BaseUserInfoDTO userInfoDO = userService.queryBasicUserInfo(comment.getUserId());
        if (userInfoDO == null) {
            // If the user is logged out, provide a default user
            comment.setUserName("Default User");
            comment.setUserPhoto("");
            if (comment instanceof TopCommentDTO) {
                ((TopCommentDTO) comment).setCommentCount(0);
            }
        } else {
            comment.setUserName(userInfoDO.getUserName());
            comment.setUserPhoto(userInfoDO.getPhoto());
            if (comment instanceof TopCommentDTO) {
                ((TopCommentDTO) comment).setCommentCount(((TopCommentDTO) comment).getChildComments().size());
            }
        }

        // Query the number of likes
        Long praiseCount = countService.queryCommentPraiseCount(comment.getCommentId());
        comment.setPraiseCount(praiseCount.intValue());

        // Query whether the current logged-in user has liked it
        Long loginUserId = ReqInfoContext.getReqInfo().getUserId();
        if (loginUserId != null) {
            // Check if the current user has liked it
            UserFootDO foot = userFootService.queryUserFoot(comment.getCommentId(), DocumentTypeEnum.COMMENT.getCode(), loginUserId);
            comment.setPraised(foot != null && Objects.equals(foot.getPraiseStat(), PraiseStatEnum.PRAISE.getCode()));
        } else {
            comment.setPraised(false);
        }
    }

    /**
     * Query the comment with the most replies
     *
     * @param articleId
     * @return
     */
    @Override
    public TopCommentDTO queryHotComment(Long articleId) {
        CommentDO comment = commentDao.getHotComment(articleId);
        if (comment == null) {
            return null;
        }

        TopCommentDTO result = CommentConverter.toTopDto(comment);
        // Query child comments
        List<CommentDO> subComments = commentDao.listSubCommentIdMappers(articleId, Collections.singletonList(comment.getId()));
        List<SubCommentDTO> subs = subComments.stream().map(CommentConverter::toSubDto).collect(Collectors.toList());
        result.setChildComments(subs);

        // Fill in the comment information
        fillTopCommentInfo(result);
        return result;
    }

    @Override
    public int queryCommentCount(Long articleId) {
        return commentDao.commentCount(articleId);
    }
}

