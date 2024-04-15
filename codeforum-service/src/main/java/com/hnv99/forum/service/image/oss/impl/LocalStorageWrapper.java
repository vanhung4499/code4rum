package com.hnv99.forum.service.image.oss.impl;

import com.github.hui.quick.plugin.base.file.FileWriteUtil;
import com.hnv99.forum.api.model.exception.ExceptionUtil;
import com.hnv99.forum.api.model.vo.constants.StatusEnum;
import com.hnv99.forum.core.config.ImageProperties;
import com.hnv99.forum.core.util.StopWatchUtil;
import com.hnv99.forum.service.image.oss.ImageUploader;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.stereotype.Component;
import org.springframework.util.StreamUtils;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Random;

/**
 * Local storage for uploaded files
 */
@Slf4j
@ConditionalOnExpression(value = "#{'local'.equals(environment.getProperty('image.oss.type'))}")
@Component
public class LocalStorageWrapper implements ImageUploader {
    @Autowired
    private ImageProperties imageProperties;
    private Random random;

    public LocalStorageWrapper() {
        random = new Random();
    }

    @Override
    public String upload(InputStream input, String fileType) {
        // Record time consumption distribution
        StopWatchUtil stopWatchUtil = StopWatchUtil.init("Image upload");
        try {
            if (fileType == null) {
                // Determine file type based on magic number
                InputStream finalInput = input;
                byte[] bytes = stopWatchUtil.record("Stream to bytes", () -> StreamUtils.copyToByteArray(finalInput));
                input = new ByteArrayInputStream(bytes);
                fileType = getFileType((ByteArrayInputStream) input, fileType);
            }

            String path = imageProperties.getAbsTmpPath() + imageProperties.getWebImgPath();
            String fileName = genTmpFileName();

            InputStream finalInput = input;
            String finalFileType = fileType;
            FileWriteUtil.FileInfo file = stopWatchUtil.record("Storage", () -> FileWriteUtil.saveFileByStream(finalInput, path, fileName, finalFileType));
            return imageProperties.buildImgUrl(imageProperties.getWebImgPath() + file.getFilename() + "." + file.getFileType());
        } catch (Exception e) {
            log.error("Parse img from httpRequest to BufferedImage error! e:", e);
            throw ExceptionUtil.of(StatusEnum.UPLOAD_PIC_FAILED);
        } finally {
            log.info("Image upload time: {}", stopWatchUtil.prettyPrint());
        }
    }

    /**
     * Generate temporary file name
     *
     * @return
     */
    private String genTmpFileName() {
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddhhmmssSSS")) + "_" + random.nextInt(100);
    }

    /**
     * External network image transfer determination, only need to transfer for images that have not been transferred and start with http
     *
     * @param img
     * @return true means no need to transfer
     */
    @Override
    public boolean uploadIgnore(String img) {
        if (StringUtils.isNotBlank(imageProperties.getCdnHost()) && img.startsWith(imageProperties.getCdnHost())) {
            return true;
        }

        // If it is an OSS image, no need to transfer
        if (StringUtils.isNotBlank(imageProperties.getOss().getHost()) && img.startsWith(imageProperties.getOss().getHost())) {
            return true;
        }

        return !img.startsWith("http");
    }
}

