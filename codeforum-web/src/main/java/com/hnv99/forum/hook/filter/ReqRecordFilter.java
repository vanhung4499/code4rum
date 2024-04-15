package com.hnv99.forum.hook.filter;


import cn.hutool.core.date.StopWatch;
import com.hnv99.forum.api.model.context.ReqInfoContext;
import com.hnv99.forum.core.async.AsyncUtil;
import com.hnv99.forum.core.mdc.MdcUtil;
import com.hnv99.forum.core.util.*;
import com.hnv99.forum.global.GlobalInitService;
import com.hnv99.forum.service.sitemap.service.impl.SitemapServiceImpl;
import com.hnv99.forum.service.statistics.service.StatisticsSettingService;
import com.hnv99.forum.service.user.service.LoginService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLDecoder;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
/**
 * 1. Request parameter logging output filter
 * 2. Determine if the user is logged in
 */
@Slf4j
@WebFilter(urlPatterns = "/*", filterName = "reqRecordFilter", asyncSupported = true)
public class ReqRecordFilter implements Filter {
    private static Logger REQ_LOG = LoggerFactory.getLogger("req");
    /**
     * TraceId returned to the front end for logging tracking
     */
    private static final String GLOBAL_TRACE_ID_HEADER = "g-trace-id";

    @Autowired
    private GlobalInitService globalInitService;

    @Autowired
    private StatisticsSettingService statisticsSettingService;

    @Override
    public void init(FilterConfig filterConfig) {
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        long start = System.currentTimeMillis();
        HttpServletRequest request = null;
        StopWatch stopWatch = new StopWatch("Request Processing Time");
        try {
            stopWatch.start("Building Request Parameters");
            request = this.initReqInfo((HttpServletRequest) servletRequest, (HttpServletResponse) servletResponse);
            stopWatch.stop();
            stopWatch.start("CORS");
            CrossUtil.buildCors(request, (HttpServletResponse) servletResponse);
            stopWatch.stop();
            stopWatch.start("Business Execution");
            filterChain.doFilter(request, servletResponse);
        } finally {
            if (stopWatch.isRunning()) {
                // Avoid situations where doFilter execution fails, causing the above stopWatch to not end. End the last count first here.
                stopWatch.stop();
            }
            stopWatch.start("Output Request Logs");
            buildRequestLog(ReqInfoContext.getReqInfo(), request, System.currentTimeMillis() - start);
            // Clear MDC-related variables (such as GlobalTraceId, user information) after a link request is completed
            MdcUtil.clear();
            ReqInfoContext.clear();
            stopWatch.stop();

            if (!isStaticURI(request) && !EnvUtil.isPro()) {
                log.info("{} - Cost:\n{}", request.getRequestURI(), stopWatch.prettyPrint(TimeUnit.MILLISECONDS));
            }
        }
    }

    @Override
    public void destroy() {
    }

    private HttpServletRequest initReqInfo(HttpServletRequest request, HttpServletResponse response) {
        if (isStaticURI(request)) {
            // Directly allow static resources
            return request;
        }

        StopWatch stopWatch = new StopWatch("Building Request Parameters");
        try {
            stopWatch.start("TraceId");
            // Add the traceId for the entire link
            MdcUtil.addTraceId();
            stopWatch.stop();

            stopWatch.start("Request Basic Information");
            // Manually write a session to implement real-time statistics of online users with the help of OnlineUserCountListener
            request.getSession().setAttribute("latestVisit", System.currentTimeMillis());

            ReqInfoContext.ReqInfo reqInfo = new ReqInfoContext.ReqInfo();
            reqInfo.setHost(request.getHeader("host"));
            reqInfo.setPath(request.getPathInfo());
            if (reqInfo.getPath() == null) {
                String url = request.getRequestURI();
                int index = url.indexOf("?");
                if (index > 0) {
                    url = url.substring(0, index);
                }
                reqInfo.setPath(url);
            }
            reqInfo.setReferer(request.getHeader("referer"));
            reqInfo.setClientIp(IpUtil.getClientIp(request));
            reqInfo.setUserAgent(request.getHeader("User-Agent"));
            reqInfo.setDeviceId(getOrInitDeviceId(request, response));

            request = this.wrapperRequest(request, reqInfo);
            stopWatch.stop();

            stopWatch.start("Login User Information");
            // Initialize login information
            globalInitService.initLoginUser(reqInfo);
            stopWatch.stop();

            ReqInfoContext.addReqInfo(reqInfo);
            stopWatch.start("PV/UV Site Statistics");
            // Update uv/pv count
            AsyncUtil.execute(() -> SpringUtil.getBean(SitemapServiceImpl.class).saveVisitInfo(reqInfo.getClientIp(), reqInfo.getPath()));
            stopWatch.stop();

            stopWatch.start("Write TraceId back");
            // Record traceId in the response header
            response.setHeader(GLOBAL_TRACE_ID_HEADER, Optional.ofNullable(MdcUtil.getTraceId()).orElse(""));
            stopWatch.stop();
        } catch (Exception e) {
            log.error("Error initializing reqInfo!", e);
        } finally {
            if (!EnvUtil.isPro()) {
                log.info("{} -> Request construction time: \n{}", request.getRequestURI(), stopWatch.prettyPrint(TimeUnit.MILLISECONDS));
            }
        }

        return request;
    }

    private void buildRequestLog(ReqInfoContext.ReqInfo req, HttpServletRequest request, long costTime) {
        if (req == null || isStaticURI(request)) {
            return;
        }

        StringBuilder msg = new StringBuilder();
        msg.append("method=").append(request.getMethod()).append("; ");
        if (StringUtils.isNotBlank(req.getReferer())) {
            msg.append("referer=").append(URLDecoder.decode(req.getReferer())).append("; ");
        }
        msg.append("remoteIp=").append(req.getClientIp());
        msg.append("; agent=").append(req.getUserAgent());

        if (req.getUserId() != null) {
            // Print user information
            msg.append("; user=").append(req.getUserId());
        }

        msg.append("; uri=").append(request.getRequestURI());
        if (StringUtils.isNotBlank(request.getQueryString())) {
            msg.append('?').append(URLDecoder.decode(request.getQueryString()));
        }

        msg.append("; payload=").append(req.getPayload());
        msg.append("; cost=").append(costTime);
        REQ_LOG.info("{}", msg);

        // Save request count
        statisticsSettingService.saveRequestCount(req.getClientIp());
    }


    private HttpServletRequest wrapperRequest(HttpServletRequest request, ReqInfoContext.ReqInfo reqInfo) {
        if (!HttpMethod.POST.name().equalsIgnoreCase(request.getMethod())) {
            return request;
        }

        BodyReaderHttpServletRequestWrapper requestWrapper = new BodyReaderHttpServletRequestWrapper(request);
        reqInfo.setPayload(requestWrapper.getBodyString());
        return requestWrapper;
    }

    private boolean isStaticURI(HttpServletRequest request) {
        return request == null
                || request.getRequestURI().endsWith("css")
                || request.getRequestURI().endsWith("js")
                || request.getRequestURI().endsWith("png")
                || request.getRequestURI().endsWith("ico")
                || request.getRequestURI().endsWith("svg")
                || request.getRequestURI().endsWith("min.js.map")
                || request.getRequestURI().endsWith("min.css.map");
    }


    /**
     * Initialize device id
     *
     * @return
     */
    private String getOrInitDeviceId(HttpServletRequest request, HttpServletResponse response) {
        String deviceId = request.getParameter("deviceId");
        if (StringUtils.isNotBlank(deviceId) && !"null".equalsIgnoreCase(deviceId)) {
            return deviceId;
        }

        Cookie device = SessionUtil.findCookieByName(request, LoginService.USER_DEVICE_KEY);
        if (device == null) {
            deviceId = UUID.randomUUID().toString();
            if (response != null) {
                response.addCookie(SessionUtil.newCookie(LoginService.USER_DEVICE_KEY, deviceId));
            }
            return deviceId;
        }
        return device.getValue();
    }
}

