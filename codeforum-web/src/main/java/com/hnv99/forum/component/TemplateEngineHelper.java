package com.hnv99.forum.component;

import com.hnv99.forum.core.util.MapUtils;
import com.hnv99.forum.global.GlobalInitService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring5.SpringTemplateEngine;


/**
 * TemplateEngineHelper class for template rendering.
 */
@Component
public class TemplateEngineHelper {
    @Autowired
    private SpringTemplateEngine springTemplateEngine;

    @Autowired
    private GlobalInitService globalInitService;

    /**
     * Render template.
     *
     * @param template The template.
     * @param attrName The name of the attribute.
     * @param attrVal  The value of the attribute.
     * @param <T>      The type of attribute value.
     * @return The rendered template.
     */
    public <T> String render(String template, String attrName, T attrVal) {
        Context context = new Context();
        context.setVariable(attrName, attrVal);
        context.setVariable("global", globalInitService.globalAttr());
        return springTemplateEngine.process(template, context);
    }

    /**
     * Render template.
     *
     * @param template The template.
     * @param attr     The attribute value.
     * @param <T>      The type of attribute value.
     * @return The rendered template.
     */
    public <T> String render(String template, T attr) {
        return render(template, "vo", attr);
    }

    /**
     * Render template with parameter properties wrapped in the "vo" class.
     *
     * @param template The template.
     * @param second   The actual "data" property.
     * @param val      The parameter.
     * @param <T>      The type of parameter.
     * @return The rendered template.
     */
    public <T> String renderToVo(String template, String second, T val) {
        Context context = new Context();
        context.setVariable("vo", MapUtils.create(second, val));
        return springTemplateEngine.process(template, context);
    }
}

