package com.hnv99.forum.global;

import com.hnv99.forum.api.model.vo.PageParam;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * BaseViewController class for configuring global properties.
 */
public class BaseViewController {
    @Autowired
    protected GlobalInitService globalInitService;

    /**
     * Build page parameters with default values if necessary.
     *
     * @param page The page number.
     * @param size The page size.
     * @return The constructed page parameters.
     */
    public PageParam buildPageParam(Long page, Long size) {
        if (page <= 0) {
            page = PageParam.DEFAULT_PAGE_NUM;
        }
        if (size == null || size > PageParam.DEFAULT_PAGE_SIZE) {
            size = PageParam.DEFAULT_PAGE_SIZE;
        }
        return PageParam.newPageInstance(page, size);
    }

    // Recommended for use instead of setting global attributes in GlobalViewInterceptor
    // /**
    //  * Configure global attributes.
    //  *
    //  * @param model The model.
    //  */
    // @ModelAttribute
    // public void globalAttr(Model model) {
    //     model.addAttribute("global", globalInitService.globalAttr());
    // }
}

