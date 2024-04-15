package com.hnv99.forum.admin.rest;

import com.hnv99.forum.api.model.enums.PushStatusEnum;
import com.hnv99.forum.api.model.vo.PageVo;
import com.hnv99.forum.api.model.vo.ResVo;
import com.hnv99.forum.api.model.vo.banner.ConfigReq;
import com.hnv99.forum.api.model.vo.banner.SearchConfigReq;
import com.hnv99.forum.api.model.vo.banner.dto.ConfigDTO;
import com.hnv99.forum.api.model.vo.constants.StatusEnum;
import com.hnv99.forum.core.permission.Permission;
import com.hnv99.forum.core.permission.UserRole;
import com.hnv99.forum.service.config.service.impl.ConfigSettingServiceImpl;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * Backend for banners.
 * Manages backend operational configurations.
 */
@RestController
@Permission(role = UserRole.LOGIN)
@Api(value = "Backend operational configuration management controller", tags = "Configuration Management")
@RequestMapping(path = {"api/admin/config/", "admin/config/"})
public class ConfigSettingrRestController {

    @Autowired
    private ConfigSettingServiceImpl configSettingService;

    /**
     * Save a configuration.
     *
     * @param configReq The configuration request.
     * @return A response indicating the success of the operation.
     */
    @Permission(role = UserRole.ADMIN)
    @PostMapping(path = "save")
    public ResVo<String> save(@RequestBody ConfigReq configReq) {
        configSettingService.saveConfig(configReq);
        return ResVo.ok("ok");
    }

    /**
     * Delete a configuration.
     *
     * @param configId The ID of the configuration to delete.
     * @return A response indicating the success of the operation.
     */
    @Permission(role = UserRole.ADMIN)
    @GetMapping(path = "delete")
    public ResVo<String> delete(@RequestParam(name = "configId") Integer configId) {
        configSettingService.deleteConfig(configId);
        return ResVo.ok("ok");
    }

    /**
     * Operate on a configuration.
     *
     * @param configId The ID of the configuration to operate on.
     * @param pushStatus The push status.
     * @return A response indicating the success of the operation.
     */
    @Permission(role = UserRole.ADMIN)
    @GetMapping(path = "operate")
    public ResVo<String> operate(@RequestParam(name = "configId") Integer configId,
                                 @RequestParam(name = "pushStatus") Integer pushStatus) {
        if (pushStatus != PushStatusEnum.OFFLINE.getCode() && pushStatus != PushStatusEnum.ONLINE.getCode()) {
            return ResVo.fail(StatusEnum.ILLEGAL_ARGUMENTS);
        }
        configSettingService.operateConfig(configId, pushStatus);
        return ResVo.ok("ok");
    }

    /**
     * Get a list of configurations.
     *
     * @param req The search request.
     * @return A response containing the list of configurations.
     */
    @PostMapping(path = "list")
    public ResVo<PageVo<ConfigDTO>> list(@RequestBody SearchConfigReq req) {
        PageVo<ConfigDTO> bannerDTOPageVo = configSettingService.getConfigList(req);
        return ResVo.ok(bannerDTOPageVo);
    }
}
