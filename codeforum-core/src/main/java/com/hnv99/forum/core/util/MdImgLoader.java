package com.hnv99.forum.core.util;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Markdown image loader for extracting images from markdown text
 */
public class MdImgLoader {
    private static final Pattern IMG_PATTERN = Pattern.compile("!\\[(.*?)\\]\\((.*?)\\)");

    /**
     * Represents a Markdown image
     */
    public static class MdImg {
        /**
         * Original text
         */
        private String origin;
        /**
         * Image description
         */
        private String desc;
        /**
         * Image URL
         */
        private String url;

        public MdImg(String origin, String desc, String url) {
            this.origin = origin;
            this.desc = desc;
            this.url = url;
        }

        public String getOrigin() {
            return origin;
        }

        public String getDesc() {
            return desc;
        }

        public String getUrl() {
            return url;
        }
    }

    /**
     * Extracts images from the provided markdown content
     *
     * @param content Markdown content
     * @return List of MdImg objects representing the images
     */
    public static List<MdImg> loadImgs(String content) {
        Matcher matcher = IMG_PATTERN.matcher(content);
        List<MdImg> list = new ArrayList<>();
        while (matcher.find()) {
            list.add(new MdImg(matcher.group(0), matcher.group(1), matcher.group(2)));
        }
        return list;
    }
}
