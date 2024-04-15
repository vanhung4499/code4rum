package com.hnv99.forum.service.user.service.userfoot;

import com.hnv99.forum.api.model.enums.DocumentTypeEnum;
import com.hnv99.forum.api.model.enums.OperateTypeEnum;
import com.hnv99.forum.api.model.vo.PageParam;
import com.hnv99.forum.api.model.vo.user.dto.SimpleUserInfoDTO;
import com.hnv99.forum.api.model.vo.user.dto.UserFootStatisticDTO;
import com.hnv99.forum.service.article.service.ArticleReadService;
import com.hnv99.forum.service.comment.repository.entity.CommentDO;
import com.hnv99.forum.service.comment.service.CommentReadService;
import com.hnv99.forum.service.user.repository.dao.UserFootDao;
import com.hnv99.forum.service.user.repository.entity.UserFootDO;
import com.hnv99.forum.service.user.service.UserFootService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * User footprint service implementation
 */
@Slf4j
@Service
public class UserFootServiceImpl implements UserFootService {
    private final UserFootDao userFootDao;

    @Autowired
    private ArticleReadService articleReadService;

    @Autowired
    private CommentReadService commentReadService;

    public UserFootServiceImpl(UserFootDao userFootDao) {
        this.userFootDao = userFootDao;
    }

    @Override
    public UserFootDO saveOrUpdateUserFoot(DocumentTypeEnum documentType, Long documentId, Long authorId, Long userId, OperateTypeEnum operateTypeEnum) {
        UserFootDO readUserFootDO = userFootDao.getByDocumentAndUserId(documentId, documentType.getCode(), userId);
        if (readUserFootDO == null) {
            readUserFootDO = new UserFootDO();
            readUserFootDO.setUserId(userId);
            readUserFootDO.setDocumentId(documentId);
            readUserFootDO.setDocumentType(documentType.getCode());
            readUserFootDO.setDocumentUserId(authorId);
            setUserFootStat(readUserFootDO, operateTypeEnum);
            userFootDao.save(readUserFootDO);
        } else if (setUserFootStat(readUserFootDO, operateTypeEnum)) {
            readUserFootDO.setUpdateTime(new Date());
            userFootDao.updateById(readUserFootDO);
        }
        return readUserFootDO;
    }

    @Override
    public void saveCommentFoot(CommentDO comment, Long articleAuthor, Long parentCommentAuthor) {
        saveOrUpdateUserFoot(DocumentTypeEnum.ARTICLE, comment.getArticleId(), articleAuthor, comment.getUserId(), OperateTypeEnum.COMMENT);
        if (comment.getParentCommentId() != null && comment.getParentCommentId() != 0) {
            saveOrUpdateUserFoot(DocumentTypeEnum.COMMENT, comment.getParentCommentId(), parentCommentAuthor, comment.getUserId(), OperateTypeEnum.COMMENT);
        }
    }

    @Override
    public void removeCommentFoot(CommentDO comment, Long articleAuthor, Long parentCommentAuthor) {
        saveOrUpdateUserFoot(DocumentTypeEnum.ARTICLE, comment.getArticleId(), articleAuthor, comment.getUserId(), OperateTypeEnum.DELETE_COMMENT);
        if (comment.getParentCommentId() != null) {
            saveOrUpdateUserFoot(DocumentTypeEnum.COMMENT, comment.getParentCommentId(), parentCommentAuthor, comment.getUserId(), OperateTypeEnum.DELETE_COMMENT);
        }
    }

    private boolean setUserFootStat(UserFootDO userFootDO, OperateTypeEnum operate) {
        switch (operate) {
            case READ:
                userFootDO.setReadStat(1);
                return true;
            case PRAISE:
            case CANCEL_PRAISE:
                return compareAndUpdate(userFootDO::getPraiseStat, userFootDO::setPraiseStat, operate.getDbStatCode());
            case COLLECTION:
            case CANCEL_COLLECTION:
                return compareAndUpdate(userFootDO::getCollectionStat, userFootDO::setCollectionStat, operate.getDbStatCode());
            case COMMENT:
            case DELETE_COMMENT:
                return compareAndUpdate(userFootDO::getCommentStat, userFootDO::setCommentStat, operate.getDbStatCode());
            default:
                return false;
        }
    }

    private <T> boolean compareAndUpdate(Supplier<T> supplier, Consumer<T> consumer, T input) {
        if (Objects.equals(supplier.get(), input)) {
            return false;
        }
        consumer.accept(input);
        return true;
    }

    @Override
    public List<Long> queryUserReadArticleList(Long userId, PageParam pageParam) {
        return userFootDao.listReadArticleByUserId(userId, pageParam);
    }

    @Override
    public List<Long> queryUserCollectionArticleList(Long userId, PageParam pageParam) {
        return userFootDao.listCollectedArticlesByUserId(userId, pageParam);
    }

    @Override
    public List<SimpleUserInfoDTO> queryArticlePraisedUsers(Long articleId) {
        return userFootDao.listDocumentPraisedUsers(articleId, DocumentTypeEnum.ARTICLE.getCode(), 10);
    }

    @Override
    public UserFootDO queryUserFoot(Long documentId, Integer type, Long userId) {
        return userFootDao.getByDocumentAndUserId(documentId, type, userId);
    }

    @Override
    public UserFootStatisticDTO getFootCount() {
        return userFootDao.getFootCount();
    }
}

