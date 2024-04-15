package com.hnv99.forum.core.dal;

import com.alibaba.druid.pool.DruidPooledPreparedStatement;
import com.baomidou.mybatisplus.core.MybatisParameterHandler;
import com.hnv99.forum.core.util.DateUtil;
import com.mysql.cj.MysqlConnection;
import com.zaxxer.hikari.pool.HikariProxyConnection;
import com.zaxxer.hikari.pool.HikariProxyPreparedStatement;
import io.github.classgraph.utils.ReflectionUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.ParameterMapping;
import org.apache.ibatis.mapping.ParameterMode;
import org.apache.ibatis.plugin.*;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.scripting.defaults.DefaultParameterHandler;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.ResultHandler;
import org.springframework.util.CollectionUtils;

import java.sql.Connection;
import java.sql.Date;
import java.sql.Statement;
import java.util.List;
import java.util.Properties;
import java.util.regex.Matcher;


/**
 * MyBatis interceptor. Outputs SQL execution details.
 */
@Slf4j
@Intercepts({@Signature(type = StatementHandler.class, method = "query", args = {Statement.class, ResultHandler.class}), @Signature(type = StatementHandler.class, method = "update", args = {Statement.class})})
public class SqlStateInterceptor implements Interceptor {
    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        long time = System.currentTimeMillis();
        StatementHandler statementHandler = (StatementHandler) invocation.getTarget();
        String sql = buildSql(statementHandler);
        Object[] args = invocation.getArgs();
        String uname = "";
        if (args[0] instanceof HikariProxyPreparedStatement) {
            HikariProxyConnection connection = (HikariProxyConnection) ((HikariProxyPreparedStatement) invocation.getArgs()[0]).getConnection();
            uname = connection.getMetaData().getUserName();
        } else if (DruidCheckUtil.hasDuridPkg()) {
            if (args[0] instanceof DruidPooledPreparedStatement) {
                Connection connection = ((DruidPooledPreparedStatement) args[0]).getStatement().getConnection();
                if (connection instanceof MysqlConnection) {
                    Properties properties = ((MysqlConnection) connection).getProperties();
                    uname = properties.getProperty("user");
                }
            }
        }

        Object rs;
        try {
            rs = invocation.proceed();
        } catch (Throwable e) {
            log.error("error sql: " + sql, e);
            throw e;
        } finally {
            long cost = System.currentTimeMillis() - time;
            sql = this.replaceContinueSpace(sql);
            // Total execution time of this method
            log.info("\n\n ============= \nsql ----> {}\nuser ----> {}\ncost ----> {}\n ============= \n", sql, uname, cost);
        }

        return rs;
    }

    /**
     * Build SQL statement
     *
     * @param statementHandler
     * @return
     */
    private String buildSql(StatementHandler statementHandler) {
        BoundSql boundSql = statementHandler.getBoundSql();
        Configuration configuration = null;
        if (statementHandler.getParameterHandler() instanceof DefaultParameterHandler) {
            DefaultParameterHandler handler = (DefaultParameterHandler) statementHandler.getParameterHandler();
            configuration = (Configuration) ReflectionUtils.getFieldVal(handler, "configuration", false);
        } else if (statementHandler.getParameterHandler() instanceof MybatisParameterHandler) {
            MybatisParameterHandler paramHandler = (MybatisParameterHandler) statementHandler.getParameterHandler();
            configuration = ((MappedStatement) ReflectionUtils.getFieldVal(paramHandler, "mappedStatement", false)).getConfiguration();
        }

        if (configuration == null) {
            return boundSql.getSql();
        }

        return getSql(boundSql, configuration);
    }

    /**
     * Generate the SQL command to execute
     *
     * @param boundSql
     * @param configuration
     * @return
     */
    private String getSql(BoundSql boundSql, Configuration configuration) {
        String sql = boundSql.getSql();
        Object parameterObject = boundSql.getParameterObject();
        List<ParameterMapping> parameterMappings = boundSql.getParameterMappings();
        if (CollectionUtils.isEmpty(parameterMappings) || parameterObject == null) {
            return sql;
        }

        MetaObject mo = configuration.newMetaObject(boundSql.getParameterObject());
        for (ParameterMapping parameterMapping : parameterMappings) {
            if (parameterMapping.getMode() == ParameterMode.OUT) {
                continue;
            }

            // Parameter value
            Object value;
            // Get parameter name
            String propertyName = parameterMapping.getProperty();
            if (boundSql.hasAdditionalParameter(propertyName)) {
                // Get parameter value
                value = boundSql.getAdditionalParameter(propertyName);
            } else if (configuration.getTypeHandlerRegistry().hasTypeHandler(parameterObject.getClass())) {
                // If it's a single value, assign directly
                value = parameterObject;
            } else {
                value = mo.getValue(propertyName);
            }
            String param = Matcher.quoteReplacement(getParameter(value));
            sql = sql.replaceFirst("\\?", param);
        }
        sql += ";";
        return sql;
    }

    public String getParameter(Object parameter) {
        if (parameter instanceof String) {
            return "'" + parameter + "'";
        } else if (parameter instanceof Date) {
            // Date formatting
            return "'" + DateUtil.format(DateUtil.DB_FORMAT, ((Date) parameter).getTime()) + "'";
        } else if (parameter instanceof java.util.Date) {
            // Date formatting
            return "'" + DateUtil.format(DateUtil.DB_FORMAT, ((java.util.Date) parameter).getTime()) + "'";
        }
        return parameter.toString();
    }

    /**
     * Replace consecutive spaces
     *
     * @param str
     * @return
     */
    private String replaceContinueSpace(String str) {
        StringBuilder builder = new StringBuilder(str.length());
        boolean preSpace = false;
        for (int i = 0, len = str.length(); i < len; i++) {
            char ch = str.charAt(i);
            boolean isSpace = Character.isWhitespace(ch);
            if (preSpace && isSpace) {
                continue;
            }

            if (preSpace) {
                // Previous character is a space, and current one is not
                preSpace = false;
                builder.append(ch);
            } else if (isSpace) {
                // Current character is a space, and the previous one is not
                preSpace = true;
                builder.append(" ");
            } else {
                // Both previous and current characters are not spaces
                builder.append(ch);
            }
        }
        return builder.toString();
    }

    @Override
    public Object plugin(Object o) {
        return Plugin.wrap(o, this);
    }

    @Override
    public void setProperties(Properties properties) {
    }
}

