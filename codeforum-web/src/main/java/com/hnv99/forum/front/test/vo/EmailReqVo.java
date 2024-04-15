package com.hnv99.forum.front.test.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * Data class representing an email request.
 * Includes fields for the recipient email address, email title, and email content.
 */
@Data
public class EmailReqVo implements Serializable {
    private static final long serialVersionUID = -8560585303684975482L;

    private String to;       // Recipient email address

    private String title;    // Email title

    private String content;  // Email content
}
