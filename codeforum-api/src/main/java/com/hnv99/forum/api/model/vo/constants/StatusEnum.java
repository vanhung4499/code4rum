package com.hnv99.forum.api.model.vo.constants;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Exception Code Specification:
 * xxx - xxx - xxx
 * Business - Status - Code
 * <p>
 * Business Values:
 * - 100 Global
 * - 200 Article Related
 * - 300 Comment Related
 * - 400 User Related
 * <p>
 * Status: Based on the meaning of HTTP status
 * - 4xx Issues with caller usage
 * - 5xx Service internal problems
 * <p>
 * Code: Specific business code
 * <p>
 */
@Getter
@AllArgsConstructor
public enum StatusEnum {
    SUCCESS(0, "OK"),

    // -------------------------------- Common

    // Global parameter exception
    ILLEGAL_ARGUMENTS(100_400_001, "Parameter exception"),
    ILLEGAL_ARGUMENTS_MIXED(100_400_002, "Parameter exception: %s"),

    // Global permission related
    FORBID_ERROR(100_403_001, "No permission"),

    FORBID_ERROR_MIXED(100_403_002, "No permission: %s"),
    FORBID_NOTLOGIN(100_403_003, "Not logged in"),

    // Global, data does not exist
    RECORDS_NOT_EXISTS(100_404_001, "Record does not exist: %s"),

    // System exception
    UNEXPECT_ERROR(100_500_001, "Unexpected exception: %s"),

    // Image related exception types
    UPLOAD_PIC_FAILED(100_500_002, "Image upload failed!"),

    // --------------------------------

    // Article related exception types, prefix 200
    ARTICLE_NOT_EXISTS(200_404_001, "Article does not exist: %s"),
    COLUMN_NOT_EXISTS(200_404_002, "Tutorial does not exist: %s"),
    COLUMN_QUERY_ERROR(200_500_003, "Tutorial query exception: %s"),
    // Tutorial article already exists
    COLUMN_ARTICLE_EXISTS(200_500_004, "Tutorial article already exists: %s"),
    ARTICLE_RELATION_TUTORIAL(200_500_006, "Article has been added to tutorial: %s"),

    // --------------------------------

    // Comment related exception types
    COMMENT_NOT_EXISTS(300_404_001, "Comment does not exist: %s"),

    // --------------------------------

    // User related exceptions
    LOGIN_FAILED_MIXED(400_403_001, "Login failed: %s"),
    USER_NOT_EXISTS(400_404_001, "User does not exist: %s"),
    USER_EXISTS(400_404_002, "User already exists: %s"),
    // Duplicate user login name
    USER_LOGIN_NAME_REPEAT(400_404_003, "Duplicate user login name: %s"),
    // Pending review
    USER_NOT_AUDIT(400_500_001, "User not audited: %s"),
    // Star number does not exist
    USER_STAR_NOT_EXISTS(400_404_002, "Star number does not exist: %s"),
    // Duplicate star number
    USER_STAR_REPEAT(400_404_002, "Duplicate star number: %s"),
    USER_PWD_ERROR(400_500_002, "Incorrect username or password");

    private int code;

    private String msg;

    public static boolean is5xx(int code) {
        return code % 1000_000 / 1000 >= 500;
    }

    public static boolean is403(int code) {
        return code % 1000_000 / 1000 == 403;
    }

    public static boolean is4xx(int code) {
        return code % 1000_000 / 1000 < 500;
    }
}

