package com.hnv99.forum.core.util;

import com.baomidou.mybatisplus.core.toolkit.StringUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Utility class for articles
 */
public class ArticleUtil {
    private static final Integer MAX_SUMMARY_CHECK_TXT_LEN = 2000;
    private static final Integer SUMMARY_LEN = 256;
    private static Pattern LINK_IMG_PATTERN = Pattern.compile("!?\\[(.*?)\\]\\((.*?)\\)");
    private static Pattern CONTENT_PATTERN = Pattern.compile("[0-9a-zA-Z\u4e00-\u9fa5:;\"'<>,.?/·~！：；“”‘’《》，。？、（）]");

    private static Pattern HTML_TAG_PATTERN = Pattern.compile("<[^>]+>");

    public static String pickSummary(String summary) {
        if (StringUtils.isBlank(summary)) {
            return StringUtils.EMPTY;
        }

        // First, remove all images and links
        summary = summary.substring(0, Math.min(summary.length(), MAX_SUMMARY_CHECK_TXT_LEN)).trim();
        // Remove markdown images and hyperlinks
        summary = summary.replaceAll(LINK_IMG_PATTERN.pattern(), "");
        // Remove HTML tags
        summary = HTML_TAG_PATTERN.matcher(summary).replaceAll("");

        // Match corresponding characters
        StringBuilder result = new StringBuilder();
        Matcher matcher = CONTENT_PATTERN.matcher(summary);
        while (matcher.find()) {
            result.append(summary, matcher.start(), matcher.end());
            if (result.length() >= SUMMARY_LEN) {
                return result.substring(0, SUMMARY_LEN).trim();
            }
        }
        return result.toString().trim();
    }
}
