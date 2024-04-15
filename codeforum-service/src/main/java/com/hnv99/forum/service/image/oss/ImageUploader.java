package com.hnv99.forum.service.image.oss;

import com.github.hui.quick.plugin.base.constants.MediaType;
import com.github.hui.quick.plugin.base.file.FileReadUtil;
import org.apache.commons.lang3.StringUtils;

import java.awt.*;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Interface for Image Uploader
 */
public interface ImageUploader {

    /**
     * Default file type
     */
    String DEFAULT_FILE_TYPE = "txt";

    /**
     * Set of supported static image types
     */
    Set<MediaType> STATIC_IMG_TYPE = new HashSet<>(Arrays.asList(MediaType.ImagePng, MediaType.ImageJpg, MediaType.ImageWebp, MediaType.ImageGif));

    /**
     * Upload a file
     *
     * @param input    InputStream of the file
     * @param fileType File type
     * @return URL of the uploaded file
     */
    String upload(InputStream input, String fileType);

    /**
     * Determine if an external image URL needs processing
     *
     * @param fileUrl External image URL
     * @return true if the URL should be ignored and not processed
     */
    boolean uploadIgnore(String fileUrl);

    /**
     * Get the file type based on input stream and file type
     *
     * @param input    InputStream of the file
     * @param fileType File type
     * @return File type
     */
    default String getFileType(ByteArrayInputStream input, String fileType) {
        if (StringUtils.isNotBlank(fileType)) {
            return fileType;
        }

        MediaType type = MediaType.typeOfMagicNum(FileReadUtil.getMagicNum(input));
        if (STATIC_IMG_TYPE.contains(type)) {
            return type.getExt();
        }
        return DEFAULT_FILE_TYPE;
    }
}
