package com.hnv99.forum.global;

import com.hnv99.forum.api.model.context.ReqInfoContext;
import com.hnv99.forum.api.model.vo.article.dto.ColumnArticlesDTO;
import com.hnv99.forum.api.model.vo.article.dto.ColumnDTO;
import com.hnv99.forum.api.model.vo.article.dto.TagDTO;
import com.hnv99.forum.api.model.vo.seo.Seo;
import com.hnv99.forum.api.model.vo.seo.SeoTagVo;
import com.hnv99.forum.config.GlobalViewConfig;
import com.hnv99.forum.core.util.DateUtil;
import com.hnv99.forum.front.article.vo.ArticleDetailVo;
import com.hnv99.forum.front.user.vo.UserHomeVo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * SEO injection service for adding SEO tags to different pages.
 * This service provides methods to initialize SEO tags for various pages:
 * - Homepage
 * - Article detail page
 * - User homepage
 * - Column content detail page
 */
@Service
public class SeoInjectService {

    private static final String KEYWORDS = "Tech Community,Open Source Community,Java,Spring Boot,IT,Programmer,Developer,MySQL,Redis,Java Basics,Multithreading,JVM,Virtual Machine,Database,MySQL,Spring,Redis,MyBatis,System Design,Distributed,RPC,High Availability,High Concurrency";
    private static final String DES = "Tech Community is a community system based on the technology stack including Spring Boot, MyBatis-Plus, MySQL, Redis, Elasticsearch, MongoDB, Docker, RabbitMQ, and other technologies. It adopts mainstream Internet technology architecture, features a brand new UI design, supports one-click source code deployment, and has a complete process for publishing, searching, commenting, and statistics of articles and tutorials. The code is completely open source without any secondary encapsulation. It is a modern community project suitable for secondary development and real-world practice. Learn to code with Tech Community!";

    @Resource
    private GlobalViewConfig globalViewConfig;

    /**
     * Initializes SEO tags for article detail page.
     *
     * @param detail Article detail information
     */
    public void initColumnSeo(ArticleDetailVo detail) {
        Seo seo = initBasicSeoTag();
        List<SeoTagVo> list = seo.getOgp();
        Map<String, Object> jsonLd = seo.getJsonLd();

        String title = detail.getArticle().getTitle();
        String description = detail.getArticle().getSummary();
        String authorName = detail.getAuthor().getUserName();
        String updateTime = DateUtil.time2LocalTime(detail.getArticle().getLastUpdateTime()).toString();
        String publishedTime = DateUtil.time2LocalTime(detail.getArticle().getCreateTime()).toString();
        String image = detail.getArticle().getCover();

        // Adding OGP tags
        list.add(new SeoTagVo("og:title", title));
        list.add(new SeoTagVo("og:description", description));
        list.add(new SeoTagVo("og:type", "article"));
        list.add(new SeoTagVo("og:locale", "zh-CN"));
        list.add(new SeoTagVo("og:updated_time", updateTime));

        list.add(new SeoTagVo("article:modified_time", updateTime));
        list.add(new SeoTagVo("article:published_time", publishedTime));
        list.add(new SeoTagVo("article:tag", detail.getArticle().getTags().stream().map(TagDTO::getTag).collect(Collectors.joining(","))));
        list.add(new SeoTagVo("article:section", detail.getArticle().getCategory().getCategory()));
        list.add(new SeoTagVo("article:author", authorName));

        list.add(new SeoTagVo("author", authorName));
        list.add(new SeoTagVo("title", title));
        list.add(new SeoTagVo("description", description));
        list.add(new SeoTagVo("keywords", detail.getArticle().getCategory().getCategory() + "," + detail.getArticle().getTags().stream().map(TagDTO::getTag).collect(Collectors.joining(","))));

        if (StringUtils.isNotBlank(image)) {
            list.add(new SeoTagVo("og:image", image));
            jsonLd.put("image", image);
        }

        // Adding JSON-LD tags
        jsonLd.put("headline", title);
        jsonLd.put("description", description);
        Map<String, Object> author = new HashMap<>();
        author.put("@type", "Person");
        author.put("name", authorName);
        jsonLd.put("author", author);
        jsonLd.put("dateModified", updateTime);
        jsonLd.put("datePublished", publishedTime);

        // Setting SEO information to request context
        if (ReqInfoContext.getReqInfo() != null) {
            ReqInfoContext.getReqInfo().setSeo(seo);
        }
    }

    /**
     * Initialize SEO tags for tutorial details
     *
     * @param detail The details of the tutorial
     * @param column The column to which the tutorial belongs
     */
    public void initColumnSeo(ColumnArticlesDTO detail, ColumnDTO column) {
        Seo seo = initBasicSeoTag();
        List<SeoTagVo> list = seo.getOgp();
        Map<String, Object> jsonLd = seo.getJsonLd();

        String title = detail.getArticle().getTitle();
        String description = detail.getArticle().getSummary();
        String authorName = column.getAuthorName();
        String updateTime = DateUtil.time2LocalTime(detail.getArticle().getLastUpdateTime()).toString();
        String publishedTime = DateUtil.time2LocalTime(detail.getArticle().getCreateTime()).toString();
        String image = column.getCover();

        list.add(new SeoTagVo("og:title", title));
        list.add(new SeoTagVo("og:description", description));
        list.add(new SeoTagVo("og:type", "article"));
        list.add(new SeoTagVo("og:locale", "zh-CN"));

        list.add(new SeoTagVo("og:updated_time", updateTime));
        list.add(new SeoTagVo("og:image", image));

        list.add(new SeoTagVo("article:modified_time", updateTime));
        list.add(new SeoTagVo("article:published_time", publishedTime));
        list.add(new SeoTagVo("article:tag", detail.getArticle().getTags().stream().map(TagDTO::getTag).collect(Collectors.joining(","))));
        list.add(new SeoTagVo("article:section", column.getColumn()));
        list.add(new SeoTagVo("article:author", authorName));

        list.add(new SeoTagVo("author", authorName));
        list.add(new SeoTagVo("title", title));
        list.add(new SeoTagVo("description", detail.getArticle().getSummary()));
        list.add(new SeoTagVo("keywords", detail.getArticle().getCategory().getCategory() + "," + detail.getArticle().getTags().stream().map(TagDTO::getTag).collect(Collectors.joining(","))));

        jsonLd.put("headline", title);
        jsonLd.put("description", description);
        Map<String, Object> author = new HashMap<>();
        author.put("@type", "Person");
        author.put("name", authorName);
        jsonLd.put("author", author);
        jsonLd.put("dateModified", updateTime);
        jsonLd.put("datePublished", publishedTime);
        jsonLd.put("image", image);

        if (ReqInfoContext.getReqInfo() != null) ReqInfoContext.getReqInfo().setSeo(seo);
    }

    /**
     * Initialize SEO tags for user homepage
     *
     * @param user The user's homepage information
     */
    public void initUserSeo(UserHomeVo user) {
        Seo seo = initBasicSeoTag();
        List<SeoTagVo> list = seo.getOgp();
        Map<String, Object> jsonLd = seo.getJsonLd();

        String title = "Tech Community | " + user.getUserHome().getUserName() + "'s Homepage";
        list.add(new SeoTagVo("og:title", title));
        list.add(new SeoTagVo("og:description", user.getUserHome().getProfile()));
        list.add(new SeoTagVo("og:type", "article"));
        list.add(new SeoTagVo("og:locale", "zh-CN"));

        list.add(new SeoTagVo("article:tag", "Backend, Frontend, Java, Spring, Computer Science"));
        list.add(new SeoTagVo("article:section", "Homepage"));
        list.add(new SeoTagVo("article:author", user.getUserHome().getUserName()));

        list.add(new SeoTagVo("author", user.getUserHome().getUserName()));
        list.add(new SeoTagVo("title", title));
        list.add(new SeoTagVo("description", user.getUserHome().getProfile()));
        list.add(new SeoTagVo("keywords", KEYWORDS));

        jsonLd.put("headline", title);
        jsonLd.put("description", user.getUserHome().getProfile());
        Map<String, Object> author = new HashMap<>();
        author.put("@type", "Person");
        author.put("name", user.getUserHome().getUserName());
        jsonLd.put("author", author);

        if (ReqInfoContext.getReqInfo() != null) ReqInfoContext.getReqInfo().setSeo(seo);
    }

    public Seo defaultSeo() {
        Seo seo = initBasicSeoTag();
        List<SeoTagVo> list = seo.getOgp();
        list.add(new SeoTagVo("og:title", "Tech Community"));
        list.add(new SeoTagVo("og:description", DES));
        list.add(new SeoTagVo("og:type", "article"));
        list.add(new SeoTagVo("og:locale", "zh-CN"));

        list.add(new SeoTagVo("article:tag", "Backend, Frontend, Java, Spring, Computer Science"));
        list.add(new SeoTagVo("article:section", "Open Source Community"));
        list.add(new SeoTagVo("article:author", "Tech Community"));

        list.add(new SeoTagVo("author", "Tech Community"));
        list.add(new SeoTagVo("title", "Tech Community"));
        list.add(new SeoTagVo("description", DES));
        list.add(new SeoTagVo("keywords", KEYWORDS));

        Map<String, Object> jsonLd = seo.getJsonLd();
        jsonLd.put("@context", "https://schema.org");
        jsonLd.put("@type", "Article");
        jsonLd.put("headline", "Tech Community");
        jsonLd.put("description", DES);

        if (ReqInfoContext.getReqInfo() != null) {
            ReqInfoContext.getReqInfo().setSeo(seo);
        }
        return seo;
    }

    private Seo initBasicSeoTag() {

        List<SeoTagVo> list = new ArrayList<>();
        Map<String, Object> map = new HashMap<>();

        HttpServletRequest request =
                ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
        String url = globalViewConfig.getHost() + request.getRequestURI();

        list.add(new SeoTagVo("og:url", url));
        map.put("url", url);

        return Seo.builder().jsonLd(map).ogp(list).build();
    }

}
