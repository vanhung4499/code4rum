package com.hnv99.forum.common.image.rest;

import com.hnv99.forum.api.model.vo.ResVo;
import com.hnv99.forum.api.model.vo.constants.StatusEnum;
import com.hnv99.forum.common.image.vo.ImageVo;
import com.hnv99.forum.core.permission.Permission;
import com.hnv99.forum.core.permission.UserRole;
import com.hnv99.forum.service.image.service.ImageService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

/**
 * Image service, requires authentication before allowing operations.
 * Manages image-related operations such as uploading and saving.
 */
@Permission(role = UserRole.LOGIN)
@RequestMapping(path = {"image/", "admin/image/", "api/admin/image/",})
@RestController
@Slf4j
public class ImageRestController {

    @Autowired
    private ImageService imageService;

    /**
     * Uploads an image.
     *
     * @param request The HTTP servlet request containing the image.
     * @return A response containing the uploaded image's information.
     */
    @RequestMapping(path = "upload")
    public ResVo<ImageVo> upload(HttpServletRequest request) {
        ImageVo imageVo = new ImageVo();
        try {
            String imagePath = imageService.saveImg(request);
            imageVo.setImagePath(imagePath);
        } catch (Exception e) {
            log.error("Error while saving the uploaded file!", e);
            return ResVo.fail(StatusEnum.UPLOAD_PIC_FAILED);
        }
        return ResVo.ok(imageVo);
    }

    /**
     * Saves an image.
     *
     * @param imgUrl The URL of the image to save.
     * @return A response containing the saved image's information.
     */
    @RequestMapping(path = "save")
    public ResVo<ImageVo> save(@RequestParam(name = "img", defaultValue = "") String imgUrl) {
        ImageVo imageVo = new ImageVo();
        if (StringUtils.isBlank(imgUrl)) {
            return ResVo.ok(imageVo);
        }

        String url = imageService.saveImg(imgUrl);
        imageVo.setImagePath(url);
        return ResVo.ok(imageVo);
    }
}

