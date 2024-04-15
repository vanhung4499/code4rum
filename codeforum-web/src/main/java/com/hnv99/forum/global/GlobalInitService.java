package com.hnv99.forum.global;

import com.hnv99.forum.api.model.context.ReqInfoContext;
import com.hnv99.forum.api.model.vo.seo.Seo;
import com.hnv99.forum.api.model.vo.user.dto.BaseUserInfoDTO;
import com.hnv99.forum.config.GlobalViewConfig;
import com.hnv99.forum.core.util.NumUtil;
import com.hnv99.forum.core.util.SessionUtil;
import com.hnv99.forum.global.vo.GlobalVo;
import com.hnv99.forum.service.notify.service.NotifyService;
import com.hnv99.forum.service.sitemap.service.SitemapService;
import com.hnv99.forum.service.statistics.service.UserStatisticService;
import com.hnv99.forum.service.user.service.LoginService;
import com.hnv99.forum.service.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import cn.hutool.json.JSONUtil;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.time.LocalDate;
import java.util.Optional;

import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.CollectionUtils;

/**
 * GlobalInitService class for initializing global attributes and user information.
 */
@Slf4j
@Service
public class GlobalInitService {
    @Value("${env.name}")
    private String env;

    @Autowired
    private UserService userService;

    @Resource
    private GlobalViewConfig globalViewConfig;

    @Resource
    private NotifyService notifyService;

    @Resource
    private SeoInjectService seoInjectService;

    @Resource
    private UserStatisticService userStatisticService;

    @Resource
    private SitemapService sitemapService;

    /**
     * Initialize global attributes.
     */
    public GlobalVo globalAttr() {
        GlobalVo vo = new GlobalVo();
        vo.setEnv(env);
        vo.setSiteInfo(globalViewConfig);
        vo.setOnlineCnt(userStatisticService.getOnlineUserCnt());
        vo.setSiteStatisticInfo(sitemapService.querySiteVisitInfo(null, null));
        vo.setTodaySiteStatisticInfo(sitemapService.querySiteVisitInfo(LocalDate.now(), null));

        if (ReqInfoContext.getReqInfo() == null || ReqInfoContext.getReqInfo().getSeo() == null || CollectionUtils.isEmpty(ReqInfoContext.getReqInfo().getSeo().getOgp())) {
            Seo seo = seoInjectService.defaultSeo();
            vo.setOgp(seo.getOgp());
            vo.setJsonLd(JSONUtil.toJsonStr(seo.getJsonLd()));
        } else {
            Seo seo = ReqInfoContext.getReqInfo().getSeo();
            vo.setOgp(seo.getOgp());
            vo.setJsonLd(JSONUtil.toJsonStr(seo.getJsonLd()));
        }

        try {
            if (ReqInfoContext.getReqInfo() != null && NumUtil.upZero(ReqInfoContext.getReqInfo().getUserId())) {
                vo.setIsLogin(true);
                vo.setUser(ReqInfoContext.getReqInfo().getUser());
                vo.setMsgNum(ReqInfoContext.getReqInfo().getMsgNum());
            } else {
                vo.setIsLogin(false);
            }

            HttpServletRequest request =
                    ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
            if (request.getRequestURI().startsWith("/column")) {
                vo.setCurrentDomain("column");
            } else if (request.getRequestURI().startsWith("/chat")) {
                vo.setCurrentDomain("chat");
            } else {
                vo.setCurrentDomain("article");
            }
        } catch (Exception e) {
            log.error("loginCheckError:", e);
        }
        return vo;
    }

    /**
     * Initialize user information.
     *
     * @param reqInfo
     */
    public void initLoginUser(ReqInfoContext.ReqInfo reqInfo) {
        HttpServletRequest request =
                ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
        if (request.getCookies() == null) {
            return;
        }
        Optional.ofNullable(SessionUtil.findCookieByName(request, LoginService.SESSION_KEY))
                .ifPresent(cookie -> initLoginUser(cookie.getValue(), reqInfo));
    }

    public void initLoginUser(String session, ReqInfoContext.ReqInfo reqInfo) {
        BaseUserInfoDTO user = userService.getAndUpdateUserIpInfoBySessionId(session, null);
        reqInfo.setSession(session);
        if (user != null) {
            reqInfo.setUserId(user.getUserId());
            reqInfo.setUser(user);
            reqInfo.setMsgNum(notifyService.queryUserNotifyMsgCount(user.getUserId()));
        }
    }
}
