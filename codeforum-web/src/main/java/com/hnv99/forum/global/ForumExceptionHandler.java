package com.hnv99.forum.global;

import com.hnv99.forum.api.model.context.ReqInfoContext;
import com.hnv99.forum.api.model.exception.ForumException;
import com.hnv99.forum.api.model.vo.ResVo;
import com.hnv99.forum.api.model.vo.Status;
import com.hnv99.forum.api.model.vo.constants.StatusEnum;
import com.hnv99.forum.core.util.JsonUtil;
import com.hnv99.forum.core.util.SpringUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.core.NestedRuntimeException;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.MediaType;
import org.springframework.messaging.handler.annotation.support.MethodArgumentTypeMismatchException;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.context.request.async.AsyncRequestTimeoutException;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * ForumExceptionHandler class for handling global exceptions.
 */
@Slf4j
@Order(Ordered.HIGHEST_PRECEDENCE)
public class ForumExceptionHandler implements HandlerExceptionResolver {

    @Override
    public ModelAndView resolveException(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        Status errStatus = buildToastMsg(ex);

        if (restResponse(request, response)) {
            if (response.isCommitted()) {
                return new ModelAndView();
            }

            try {
                response.reset();
                response.setContentType(MediaType.APPLICATION_JSON_UTF8_VALUE);
                response.setHeader("Cache-Control", "no-cache, must-revalidate");
                response.getWriter().println(JsonUtil.toStr(ResVo.fail(errStatus)));
                response.getWriter().flush();
                response.getWriter().close();
                return new ModelAndView();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        String view = getErrorPage(errStatus, response);
        ModelAndView mv = new ModelAndView(view);
        response.setContentType(MediaType.TEXT_HTML_VALUE);
        mv.getModel().put("global", SpringUtil.getBean(GlobalInitService.class).globalAttr());
        mv.getModel().put("res", ResVo.fail(errStatus));
        mv.getModel().put("toast", JsonUtil.toStr(ResVo.fail(errStatus)));
        return mv;
    }

    private Status buildToastMsg(Exception ex) {
        if (ex instanceof ForumException) {
            return ((ForumException) ex).getStatus();
        } else if (ex instanceof AsyncRequestTimeoutException) {
            return Status.newStatus(StatusEnum.UNEXPECT_ERROR, "Timeout without login");
        } else if (ex instanceof HttpMediaTypeNotAcceptableException) {
            return Status.newStatus(StatusEnum.RECORDS_NOT_EXISTS, ExceptionUtils.getStackTrace(ex));
        } else if (ex instanceof HttpRequestMethodNotSupportedException || ex instanceof MethodArgumentTypeMismatchException || ex instanceof IOException) {
            return Status.newStatus(StatusEnum.ILLEGAL_ARGUMENTS, ExceptionUtils.getStackTrace(ex));
        } else if (ex instanceof NestedRuntimeException) {
            log.error("Unexpected NestedRuntimeException error! {}", ReqInfoContext.getReqInfo(), ex);
            return Status.newStatus(StatusEnum.UNEXPECT_ERROR, ex.getMessage());
        } else {
            log.error("Unexpected error! {}", ReqInfoContext.getReqInfo(), ex);
            return Status.newStatus(StatusEnum.UNEXPECT_ERROR, ExceptionUtils.getStackTrace(ex));
        }
    }

    private String getErrorPage(Status status, HttpServletResponse response) {
        if (StatusEnum.is5xx(status.getCode())) {
            response.setStatus(500);
            return "error/500";
        } else if (StatusEnum.is403(status.getCode())) {
            response.setStatus(403);
            return "error/403";
        } else {
            response.setStatus(404);
            return "error/404";
        }
    }

    private boolean restResponse(HttpServletRequest request, HttpServletResponse response) {
        if (request.getRequestURI().startsWith("/api/admin/") || request.getRequestURI().startsWith("/admin/")) {
            return true;
        }

        if (request.getRequestURI().startsWith("/image/upload")) {
            return true;
        }

        if (response.getContentType() != null && response.getContentType().contains(MediaType.APPLICATION_JSON_VALUE)) {
            return true;
        }

        if (isAjaxRequest(request)) {
            return true;
        }

        AntPathMatcher pathMatcher = new AntPathMatcher();
        if (pathMatcher.match("/**/api/**", request.getRequestURI())) {
            return true;
        }
        return false;
    }

    private boolean isAjaxRequest(HttpServletRequest request) {
        String requestedWith = request.getHeader("X-Requested-With");
        return "XMLHttpRequest".equals(requestedWith);
    }
}
