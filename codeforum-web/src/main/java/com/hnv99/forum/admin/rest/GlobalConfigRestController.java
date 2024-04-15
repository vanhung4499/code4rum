package com.hnv99.forum.admin.rest;

import com.hnv99.forum.api.model.vo.PageVo;
import com.hnv99.forum.api.model.vo.ResVo;
import com.hnv99.forum.api.model.vo.config.GlobalConfigReq;
import com.hnv99.forum.api.model.vo.config.SearchGlobalConfigReq;
import com.hnv99.forum.api.model.vo.config.dto.GlobalConfigDTO;
import com.hnv99.forum.core.permission.Permission;
import com.hnv99.forum.core.permission.UserRole;
import com.hnv99.forum.service.config.service.GlobalConfigService;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * Backend for global configurations.
 * Manages global configuration settings.
 */
@RestController
@Permission(role = UserRole.LOGIN)
@Api(value = "Global configuration management controller", tags = "Global Configuration")
@RequestMapping(path = {"api/admin/global/config/", "admin/global/config/"})
public class GlobalConfigRestController {

    @Autowired
    private GlobalConfigService globalConfigService;

    /**
     * Save a global configuration.
     *
     * @param req The global configuration request.
     * @return A response indicating the success of the operation.
     */
    @Permission(role = UserRole.ADMIN)
    @PostMapping(path = "save")
    public ResVo<String> save(@RequestBody GlobalConfigReq req) {
        globalConfigService.save(req);
        return ResVo.ok("ok");
    }

    /**
     * Delete a global configuration.
     *
     * @param id The ID of the global configuration to delete.
     * @return A response indicating the success of the operation.
     */
    @Permission(role = UserRole.ADMIN)
    @GetMapping(path = "delete")
    public ResVo<String> delete(@RequestParam(name = "id") Long id) {
        globalConfigService.delete(id);
        return ResVo.ok("ok");
    }

    /**
     * Get a list of global configurations.
     *
     * @param req The search request.
     * @return A response containing the list of global configurations.
     */
    @PostMapping(path = "list")
    @Permission(role = UserRole.ADMIN)
    public ResVo<PageVo<GlobalConfigDTO>> list(@RequestBody SearchGlobalConfigReq req) {
        PageVo<GlobalConfigDTO> page = globalConfigService.getList(req);
        return ResVo.ok(page);
    }
}

