package com.hnv99.forum.front.notice.view;

import com.hnv99.forum.api.model.context.ReqInfoContext;
import com.hnv99.forum.api.model.enums.NotifyTypeEnum;
import com.hnv99.forum.api.model.vo.PageParam;
import com.hnv99.forum.core.permission.Permission;
import com.hnv99.forum.core.permission.UserRole;
import com.hnv99.forum.front.notice.vo.NoticeResVo;
import com.hnv99.forum.global.BaseViewController;
import com.hnv99.forum.service.notify.service.NotifyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import java.util.Map;

/**
 * Controller for handling notifications.
 * Handles the display of notification messages based on the specified type.
 **/
@Controller
@Permission(role = UserRole.LOGIN)
@RequestMapping(path = "notice")
public class NoticeViewController extends BaseViewController {

    @Autowired
    private NotifyService notifyService;

    /**
     * Displays the list of notification messages based on the specified type.
     * If no type is specified, it displays the messages with unread counts for the first available category.
     *
     * @param type   The type of notification messages to display.
     * @param model  The model to add attributes to for rendering the view.
     * @return The view name for rendering the notification messages.
     */
    @RequestMapping({"/{type}", "/"})
    public String list(@PathVariable(name = "type", required = false) String type, Model model) {
        Long loginUserId = ReqInfoContext.getReqInfo().getUserId();
        Map<String, Integer> unreadCountMap = notifyService.queryUnreadCounts(loginUserId);

        NotifyTypeEnum typeEnum = type == null ? null : NotifyTypeEnum.typeOf(type);
        if (typeEnum == null) {
            // If no specific notification type is specified, select one with unread messages
            typeEnum = unreadCountMap.entrySet().stream()
                    .filter(entry -> entry.getValue() > 0)
                    .map(entry -> NotifyTypeEnum.typeOf(entry.getKey()))
                    .findAny()
                    .orElse(NotifyTypeEnum.COMMENT);
        }

        NoticeResVo vo = new NoticeResVo();
        vo.setList(notifyService.queryUserNotices(loginUserId, typeEnum, PageParam.newPageInstance()));
        vo.setSelectType(typeEnum.name().toLowerCase());
        vo.setUnreadCountMap(unreadCountMap);

        model.addAttribute("vo", vo);
        return "views/notice/index";
    }
}

