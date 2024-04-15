package com.hnv99.forum.service.image.service;

import javax.servlet.http.HttpServletRequest;

/**
 * Interface for Image Service
 */
public interface ImageService {

    /**
     * Replace Markdown image links with stored images
     *
     * @param content Markdown content
     * @return Content with replaced image links
     */
    String mdImgReplace(String content);

    /**
     * Store an external image
     *
     * @param img URL of the external image
     * @return URL of the stored image
     */
    String saveImg(String img);

    /**
     * Save an image from a HttpServletRequest
     *
     * @param request HttpServletRequest containing the image
     * @return URL of the stored image
     */
    String saveImg(HttpServletRequest request);
}
