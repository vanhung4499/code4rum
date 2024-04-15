package com.hnv99.forum.service.image.service;

import com.github.hui.quick.plugin.base.constants.MediaType;
import com.github.hui.quick.plugin.base.file.FileReadUtil;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.Maps;
import com.hnv99.forum.api.model.exception.ExceptionUtil;
import com.hnv99.forum.api.model.vo.constants.StatusEnum;
import com.hnv99.forum.core.async.AsyncExecute;
import com.hnv99.forum.core.async.AsyncUtil;
import com.hnv99.forum.core.mdc.MdcDot;
import com.hnv99.forum.core.util.MdImgLoader;
import com.hnv99.forum.service.image.oss.ImageUploader;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Implementation of Image Service
 */
@Slf4j
@Service
public class ImageServiceImpl implements ImageService {

    @Autowired
    private ImageUploader imageUploader;

    /**
     * Cache for storing mappings between external image URLs and stored image URLs
     */
    private LoadingCache<String, String> imgReplaceCache = CacheBuilder.newBuilder()
            .maximumSize(300)
            .expireAfterWrite(5, TimeUnit.MINUTES)
            .build(new CacheLoader<String, String>() {
                @Override
                public String load(String img) {
                    try {
                        InputStream stream = FileReadUtil.getStreamByFileName(img);
                        URI uri = URI.create(img);
                        String path = uri.getPath();
                        int index = path.lastIndexOf(".");
                        String fileType = null;
                        if (index > 0) {
                            // Get file type from URL
                            fileType = path.substring(index + 1);
                        }
                        return imageUploader.upload(stream, fileType);
                    } catch (Exception e) {
                        log.error("Exception occurred while saving external image! img:{}", img, e);
                        return "";
                    }
                }
            });

    /**
     * Save an image from a HttpServletRequest
     *
     * @param request HttpServletRequest containing the image
     * @return URL of the stored image
     */
    @Override
    public String saveImg(HttpServletRequest request) {
        MultipartFile file = null;
        if (request instanceof MultipartHttpServletRequest) {
            file = ((MultipartHttpServletRequest) request).getFile("image");
        }
        if (file == null) {
            throw ExceptionUtil.of(StatusEnum.ILLEGAL_ARGUMENTS_MIXED, "Missing image to upload");
        }

        // Validate supported image formats (png, jpg, gif)
        String fileType = validateStaticImg(file.getContentType());
        if (fileType == null) {
            throw ExceptionUtil.of(StatusEnum.ILLEGAL_ARGUMENTS_MIXED, "Image format must be png, jpg, or gif");
        }

        try {
            return imageUploader.upload(file.getInputStream(), fileType);
        } catch (IOException e) {
            log.error("Error parsing image from httpRequest to BufferedImage! e:", e);
            throw ExceptionUtil.of(StatusEnum.UPLOAD_PIC_FAILED);
        }
    }

    /**
     * Save an external image
     *
     * @param img URL of the external image
     * @return URL of the stored image
     */
    @Override
    public String saveImg(String img) {
        if (imageUploader.uploadIgnore(img)) {
            // Image has already been saved, no need to save again; ignore non-http images
            return img;
        }

        try {
            String ans = imgReplaceCache.get(img);
            if (StringUtils.isBlank(ans)) {
                return buildUploadFailImgUrl(img);
            }
            return ans;
        } catch (Exception e) {
            log.error("Exception occurred while saving external image! img:{}", img, e);
            return buildUploadFailImgUrl(img);
        }
    }

    /**
     * Replace Markdown image links with stored images
     *
     * @param content Markdown content
     * @return Content with replaced image links
     */
    @Override
    @MdcDot
    @AsyncExecute(timeOutRsp = "#content")
    public String mdImgReplace(String content) {
        List<MdImgLoader.MdImg> imgList = MdImgLoader.loadImgs(content);
        if (CollectionUtils.isEmpty(imgList)) {
            return content;
        }

        if (imgList.size() == 1) {
            // When there is only one image, no need for asynchronous processing, save and return directly
            MdImgLoader.MdImg img = imgList.get(0);
            String newImg = saveImg(img.getUrl());
            return StringUtils.replace(content, img.getOrigin(), "![" + img.getDesc() + "](" + newImg + ")");
        }

        // For more than one image, perform concurrent image saving to improve performance
        AsyncUtil.CompletableFutureBridge bridge = AsyncUtil.concurrentExecutor("MdImgReplace");
        Map<MdImgLoader.MdImg, String> imgReplaceMap = Maps.newHashMapWithExpectedSize(imgList.size());
        for (MdImgLoader.MdImg img : imgList) {
            bridge.runAsyncWithTimeRecord(() -> {
                imgReplaceMap.put(img, saveImg(img.getUrl()));
            }, img.getUrl());
        }
        bridge.allExecuted().prettyPrint();

        // Replace images in content
        for (Map.Entry<MdImgLoader.MdImg, String> entry : imgReplaceMap.entrySet()) {
            MdImgLoader.MdImg img = entry.getKey();
            String newImg = entry.getValue();
            content = StringUtils.replace(content, img.getOrigin(), "![" + img.getDesc() + "](" + newImg + ")");
        }
        return content;
    }

    /**
     * Build URL for an upload failed image
     *
     * @param img URL of the failed image upload
     * @return URL of the image with a failure indicator
     */
    private String buildUploadFailImgUrl(String img) {
        return img.contains("saveError") ? img : img + "?&cause=saveError!";
    }

    /**
     * Validate image format
     *
     * @param mime Image MIME type
     * @return File extension of the image format, or null if invalid
     */
    private String validateStaticImg(String mime) {
        if ("svg".equalsIgnoreCase(mime)) {
            // TODO: Implement security protection when saving uploaded files to the server to prevent uploading potentially harmful scripts
            return "svg";
        }

        if (mime.contains(MediaType.ImageJpg.getExt())) {
            mime = mime.replace("jpg", "jpeg");
        }
        for (MediaType type : ImageUploader.STATIC_IMG_TYPE) {
            if (type.getMime().equals(mime)) {
                return type.getExt();
            }
        }
        return null;
    }
}

