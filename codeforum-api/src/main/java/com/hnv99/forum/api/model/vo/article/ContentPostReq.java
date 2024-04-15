package com.hnv99.forum.api.model.vo.article;

import lombok.Data;

import java.io.Serializable;

/**
 * Publish Article Request Parameters
 */
@Data
public class ContentPostReq implements Serializable {
    /**
     * Main content
     */
    private String content;
}
