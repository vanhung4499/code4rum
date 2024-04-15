package com.hnv99.forum.front.notice.rest;

import com.hnv99.forum.api.model.context.ReqInfoContext;
import com.hnv99.forum.api.model.enums.NotifyTypeEnum;
import com.hnv99.forum.api.model.exception.ExceptionUtil;
import com.hnv99.forum.api.model.vo.NextPageHtmlVo;
import com.hnv99.forum.api.model.vo.PageListVo;
import com.hnv99.forum.api.model.vo.PageParam;
import com.hnv99.forum.api.model.vo.ResVo;
import com.hnv99.forum.api.model.vo.constants.StatusEnum;
import com.hnv99.forum.api.model.vo.notify.dto.NotifyMsgDTO;
import com.hnv99.forum.component.TemplateEngineHelper;
import com.hnv99.forum.core.permission.Permission;
import com.hnv99.forum.core.permission.UserRole;
import com.hnv99.forum.front.notice.vo.NoticeResVo;
import com.hnv99.forum.service.notify.service.NotifyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller for handling notification messages.
 * Provides endpoints for retrieving notification message lists and rendering HTML for pagination.
 **/
@RestController
@Permission(role = UserRole.LOGIN)
@RequestMapping(path = "notice/api")
public class NoticeRestController {

    @Autowired
    private TemplateEngineHelper templateEngineHelper;

    private NotifyService notifyService;

    public NoticeRestController(NotifyService notifyService) {
        this.notifyService = notifyService;
    }

    /**
     * Retrieves the list of notification messages.
     *
     * @param type     The type of notification messages to retrieve.
     * @param page     The page number.
     * @param pageSize The number of items per page.
     * @return A response containing the list of notification messages.
     */
    @RequestMapping(path = "list")
    public ResVo<PageListVo<NotifyMsgDTO>> list(@RequestParam(name = "type") String type,
                                                @RequestParam("page") Long page,
                                                @RequestParam(name = "pageSize", required = false) Long pageSize) {
        return ResVo.ok(listItems(type, page, pageSize));
    }

    /**
     * Retrieves the HTML for rendering the pagination.
     *
     * @param type     The type of notification messages.
     * @param page     The page number.
     * @param pageSize The number of items per page.
     * @return A response containing the HTML for pagination.
     */
    @RequestMapping(path = "items")
    public ResVo<NextPageHtmlVo> listForView(@RequestParam(name = "type") String type,
                                             @RequestParam("page") Long page,
                                             @RequestParam(name = "pageSize", required = false) Long pageSize) {
        type = type.toLowerCase().trim();
        PageListVo<NotifyMsgDTO> list = listItems(type, page, pageSize);
        NoticeResVo vo = new NoticeResVo();
        vo.setList(list);
        vo.setSelectType(type);
        String html = templateEngineHelper.render("views/notice/tab/notify-" + type, vo);
        return ResVo.ok(new NextPageHtmlVo(html, list.getHasMore()));
    }

    /**
     * Retrieves the list of notification messages.
     *
     * @param type     The type of notification messages.
     * @param page     The page number.
     * @param pageSize The number of items per page.
     * @return The list of notification messages.
     */
    private PageListVo<NotifyMsgDTO> listItems(String type, Long page, Long pageSize) {
        NotifyTypeEnum typeEnum = NotifyTypeEnum.typeOf(type);
        if (typeEnum == null) {
            throw ExceptionUtil.of(StatusEnum.ILLEGAL_ARGUMENTS_MIXED, "type" + type + "非法");
        }
        if (pageSize == null) {
            pageSize = PageParam.DEFAULT_PAGE_SIZE;
        }
        return notifyService.queryUserNotices(ReqInfoContext.getReqInfo().getUserId(),
                typeEnum, PageParam.newPageInstance(page, pageSize));
    }
}

