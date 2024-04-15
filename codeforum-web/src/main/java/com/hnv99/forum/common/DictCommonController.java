package com.hnv99.forum.common;

import com.hnv99.forum.api.model.vo.ResVo;
import com.hnv99.forum.core.permission.Permission;
import com.hnv99.forum.core.permission.UserRole;
import com.hnv99.forum.service.config.service.DictCommonService;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * General-purpose controller.
 *
 * @author LouZai
 * @date 2022/9/19
 */
@RestController
@Slf4j
@Permission(role = UserRole.LOGIN)
@Api(value = "Common interface controller", tags = "Global settings")
@RequestMapping(path = {"common/","api/admin/common/", "admin/common/"})
public class DictCommonController {

    @Autowired
    private DictCommonService dictCommonService;

    /**
     * Retrieve a dictionary.
     *
     * @return A response containing the dictionary.
     */
    @ResponseBody
    @GetMapping(path = "/dict")
    public ResVo<Map<String, Object>> list() {
        log.debug("Retrieving dictionary");
        Map<String, Object> bannerDTOPageVo = dictCommonService.getDict();
        return ResVo.ok(bannerDTOPageVo);
    }
}
