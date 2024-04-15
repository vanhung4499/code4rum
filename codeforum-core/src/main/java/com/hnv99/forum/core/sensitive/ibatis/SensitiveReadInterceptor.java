package com.hnv99.forum.core.sensitive.ibatis;

import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.hnv99.forum.core.sensitive.SensitiveService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ClassUtils;
import org.apache.ibatis.executor.resultset.ResultSetHandler;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Plugin;
import org.apache.ibatis.plugin.Signature;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.reflection.SystemMetaObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.reflect.Proxy;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Properties;

import static com.google.common.collect.Lists.newArrayList;

/**
 * Sensitive word replacement interceptor, mainly for sensitive word processing of data read from the database
 * (If you need to desensitize or encrypt when writing to the database, you can also use a similar method to implement it)
 */
@Intercepts({
        @Signature(type = ResultSetHandler.class, method = "handleResultSets", args = {java.sql.Statement.class})
})
@Component
@Slf4j
public class SensitiveReadInterceptor implements Interceptor {

    private static final String MAPPED_STATEMENT = "mappedStatement";

    @Autowired
    private SensitiveService sensitiveService;

    @SuppressWarnings("unchecked")
    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        final List<Object> results = (List<Object>) invocation.proceed();

        if (results.isEmpty()) {
            return results;
        }

        final ResultSetHandler statementHandler = realTarget(invocation.getTarget());
        final MetaObject metaObject = SystemMetaObject.forObject(statementHandler);
        final MappedStatement mappedStatement = (MappedStatement) metaObject.getValue(MAPPED_STATEMENT);

        Optional<Object> firstOpt = results.stream().filter(Objects::nonNull).findFirst();
        if (!firstOpt.isPresent()) {
            return results;
        }
        Object firstObject = firstOpt.get();
        // Find member information of database entities that need sensitive word replacement
        SensitiveObjectMeta sensitiveObjectMeta = findSensitiveObjectMeta(firstObject);

        // Perform replacement of sensitive words
        replaceSensitiveResults(results, mappedStatement, sensitiveObjectMeta);
        return results;
    }

    /**
     * Perform specific sensitive word replacement
     *
     * @param results
     * @param mappedStatement
     * @param sensitiveObjectMeta
     */
    private void replaceSensitiveResults(Collection<Object> results, MappedStatement mappedStatement, SensitiveObjectMeta sensitiveObjectMeta) {
        for (Object obj : results) {
            if (sensitiveObjectMeta.getSensitiveFieldMetaList() == null) {
                continue;
            }

            final MetaObject objMetaObject = mappedStatement.getConfiguration().newMetaObject(obj);
            sensitiveObjectMeta.getSensitiveFieldMetaList().forEach(i -> {
                Object value = objMetaObject.getValue(StringUtils.isBlank(i.getBindField()) ? i.getName() : i.getBindField());
                if (value == null) {
                    return;
                } else if (value instanceof String) {
                    String strValue = (String) value;
                    String processVal = sensitiveService.replace(strValue);
                    objMetaObject.setValue(i.getName(), processVal);
                } else if (value instanceof Collection) {
                    Collection listValue = (Collection) value;
                    if (CollectionUtils.isNotEmpty(listValue)) {
                        Optional firstValOpt = listValue.stream().filter(Objects::nonNull).findFirst();
                        if (firstValOpt.isPresent()) {
                            SensitiveObjectMeta valSensitiveObjectMeta = findSensitiveObjectMeta(firstValOpt.get());
                            if (Boolean.TRUE.equals(valSensitiveObjectMeta.getEnabledSensitiveReplace()) && CollectionUtils.isNotEmpty(valSensitiveObjectMeta.getSensitiveFieldMetaList())) {
                                replaceSensitiveResults(listValue, mappedStatement, valSensitiveObjectMeta);
                            }
                        }
                    }
                } else if (!ClassUtils.isPrimitiveOrWrapper(value.getClass())) {
                    // For non-basic types, sensitive word replacement needs to be performed internally
                    SensitiveObjectMeta valSensitiveObjectMeta = findSensitiveObjectMeta(value);
                    if (Boolean.TRUE.equals(valSensitiveObjectMeta.getEnabledSensitiveReplace()) && CollectionUtils.isNotEmpty(valSensitiveObjectMeta.getSensitiveFieldMetaList())) {
                        replaceSensitiveResults(newArrayList(value), mappedStatement, valSensitiveObjectMeta);
                    }
                }
            });
        }
    }

    /**
     * Query objects with @SensitiveField annotations and perform sensitive word replacement
     *
     * @param firstObject Object to be queried
     * @return Metadata of the object's sensitive words
     */
    private SensitiveObjectMeta findSensitiveObjectMeta(Object firstObject) {
        SensitiveMetaCache.computeIfAbsent(firstObject.getClass().getName(), s -> {
            Optional<SensitiveObjectMeta> sensitiveObjectMetaOpt = SensitiveObjectMeta.buildSensitiveObjectMeta(firstObject);
            return sensitiveObjectMetaOpt.orElse(null);
        });

        return SensitiveMetaCache.get(firstObject.getClass().getName());
    }

    @Override
    public Object plugin(Object o) {
        return Plugin.wrap(o, this);
    }

    @Override
    public void setProperties(Properties properties) {
    }

    public static <T> T realTarget(Object target) {
        if (Proxy.isProxyClass(target.getClass())) {
            MetaObject metaObject = SystemMetaObject.forObject(target);
            return realTarget(metaObject.getValue("h.target"));
        }
        return (T) target;
    }
}
