package com.hnv99.forum.hook.interceptor;

import com.baomidou.mybatisplus.core.toolkit.ObjectUtils;
import com.hnv99.forum.api.model.context.ReqInfoContext;
import com.hnv99.forum.api.model.vo.ResVo;
import com.hnv99.forum.api.model.vo.constants.StatusEnum;
import com.hnv99.forum.core.permission.Permission;
import com.hnv99.forum.core.permission.UserRole;
import com.hnv99.forum.core.util.JsonUtil;
import com.hnv99.forum.core.util.SpringUtil;
import com.hnv99.forum.global.GlobalInitService;
import com.hnv99.forum.service.rank.service.UserActivityRankService;
import com.hnv99.forum.service.rank.service.model.ActivityScoreBo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.messaging.handler.HandlerMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.AsyncHandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Injects global configuration information:
 * - Thymeleaf site information, basic information, injected here
 */
@Slf4j
@Component
public class GlobalViewInterceptor implements AsyncHandlerInterceptor {
    @Autowired
    private GlobalInitService globalInitService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (handler instanceof HandlerMethod) {
            HandlerMethod handlerMethod = (HandlerMethod) handler;
            Permission permission = handlerMethod.getMethod().getAnnotation(Permission.class);
            if (permission == null) {
                permission = handlerMethod.getBeanType().getAnnotation(Permission.class);
            }

            if (permission == null || permission.role() == UserRole.ALL) {
                if (ReqInfoContext.getReqInfo() != null) {
                    // Update user activity
                    SpringUtil.getBean(UserActivityRankService.class).addActivityScore(ReqInfoContext.getReqInfo().getUserId(), new ActivityScoreBo().setPath(ReqInfoContext.getReqInfo().getPath()));
                }
                return true;
            }

            if (ReqInfoContext.getReqInfo() == null || ReqInfoContext.getReqInfo().getUserId() == null) {
                if (handlerMethod.getMethod().getAnnotation(ResponseBody.class) != null
                        || handlerMethod.getMethod().getDeclaringClass().getAnnotation(RestController.class) != null) {
                    // Accessing a REST interface that requires login
                    response.setContentType(MediaType.APPLICATION_JSON_UTF8_VALUE);
                    response.getWriter().println(JsonUtil.toStr(ResVo.fail(StatusEnum.FORBID_NOTLOGIN)));
                    response.getWriter().flush();
                    return false;
                } else if (request.getRequestURI().startsWith("/api/admin/") || request.getRequestURI().startsWith("/admin/")) {
                    response.sendRedirect("/admin");
                } else {
                    // Accessing a page that requires login, redirect to the login page
                    response.sendRedirect("/");
                }
                return false;
            }
            if (permission.role() == UserRole.ADMIN && !UserRole.ADMIN.name().equalsIgnoreCase(ReqInfoContext.getReqInfo().getUser().getRole())) {
                // Set to no permission
                response.setStatus(HttpStatus.FORBIDDEN.value());
                return false;
            }
        }
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        if (!ObjectUtils.isEmpty(modelAndView)) {
            if (response.getStatus() != HttpStatus.OK.value()) {
                try {
                    ReqInfoContext.ReqInfo reqInfo = new ReqInfoContext.ReqInfo();
                    // FIXME When redirecting to /error due to an exception, the login information will be lost, to be resolved
                    globalInitService.initLoginUser(reqInfo);
                    ReqInfoContext.addReqInfo(reqInfo);
                    modelAndView.getModel().put("global", globalInitService.globalAttr());
                } finally {
                    ReqInfoContext.clear();
                }
            } else {
                modelAndView.getModel().put("global", globalInitService.globalAttr());
            }
        }

    }
}

