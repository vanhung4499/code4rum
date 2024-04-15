package com.hnv99.forum.config.init;

import com.hnv99.forum.core.util.SpringUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.init.DataSourceInitializer;
import org.springframework.jdbc.datasource.init.DatabasePopulator;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.springframework.util.CollectionUtils;

import javax.sql.DataSource;
import java.net.URI;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Map;
import java.util.Objects;


/**
 * Table initialization, only executed when starting for the first time
 */
@Slf4j
@Configuration
public class ForumDataSourceInitializer {
    @Value("${database.name}")
    private String database;

    @Value("${spring.liquibase.enabled:true}")
    private Boolean liquibaseEnable;

    @Value("${spring.liquibase.change-log}")
    private String liquibaseChangeLog;

    @Bean
    public DataSourceInitializer dataSourceInitializer(final DataSource dataSource) {
        final DataSourceInitializer initializer = new DataSourceInitializer();
        // Set the data source
        initializer.setDataSource(dataSource);
        boolean enable = needInit(dataSource);
        initializer.setEnabled(enable);
        initializer.setDatabasePopulator(databasePopulator(enable));
        return initializer;
    }

    private DatabasePopulator databasePopulator(boolean initEnable) {
        final ResourceDatabasePopulator populator = new ResourceDatabasePopulator();
        // This approach is based on initializing with SQL files; after switching to Liquibase, this approach is no longer used, as Liquibase manages table structure data changes uniformly
        if (initEnable && !liquibaseEnable) {
            // fixme: For the first startup, active initialization is adopted for databases that do not support Liquibase, such as MariaDB
            // fixme: This approach does not support subsequent dynamic updates of table structures and data changes
            populator.addScripts(DbChangeSetLoader.loadDbChangeSetResources(liquibaseChangeLog).toArray(new ClassPathResource[]{}));
            populator.setSeparator(";");
            log.info("For databases not managed by Liquibase, please manually execute database table initialization!");
        }
        return populator;
    }

    /**
     * Check whether tables exist in the database, if so, no initialization is needed; otherwise, initialize tables based on schema-all.sql
     *
     * @param dataSource
     * @return true if initialization is needed; false if no initialization is needed
     */
    private boolean needInit(DataSource dataSource) {
        if (autoInitDatabase()) {
            return true;
        }
        // Check whether initialization is needed based on the existence of tables
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        if (!liquibaseEnable) {
            // For databases not managed by Liquibase, determine whether initialization is required based on the presence of users
            List list = jdbcTemplate.queryForList("SELECT table_name FROM information_schema.TABLES where table_name = 'user_info' and table_schema = '" + database + "';");
            return CollectionUtils.isEmpty(list);
        }

        // For scenarios where Liquibase manages data version control, if code_forum is not the default, it needs to be revised
        List<Map<String, Object>> record = jdbcTemplate.queryForList("select * from DATABASECHANGELOG where ID='00000000000001' limit 1;");
        if (CollectionUtils.isEmpty(record)) {
            // First startup, initialization of library tables is required, directly return
            return true;
        }

        // During non-first startup, check whether the MD5 corresponding to the record is accurate
        if (Objects.equals(record.get(0).get("MD5SUM"), "8:a1a2d9943b746acf58476ae612c292fc")) {
            // This is mainly to solve issue #71
            jdbcTemplate.update("update DATABASECHANGELOG set MD5SUM='8:bb81b67a5219be64eff22e2929fed540' where ID='00000000000001'");
        }
        return false;
    }

    /**
     * When the database does not exist, attempt to create the database
     */
    private boolean autoInitDatabase() {
        // If the query fails, it may be because the database does not exist. Try creating the database and then test again
        URI url = URI.create(SpringUtil.getConfigOrElse("spring.datasource.url", "spring.dynamic.datasource.master.url").substring(5));
        String uname = SpringUtil.getConfigOrElse("spring.datasource.username", "spring.dynamic.datasource.master.username");
        String pwd = SpringUtil.getConfigOrElse("spring.datasource.password", "spring.dynamic.datasource.master.password");
        try (Connection connection = DriverManager.getConnection("jdbc:mysql://" + url.getHost() + ":" + url.getPort() +
                "?useUnicode=true&characterEncoding=UTF-8&useSSL=false", uname, pwd);
             Statement statement = connection.createStatement()) {
            ResultSet set = statement.executeQuery("select schema_name from information_schema.schemata where schema_name = '" + database + "'");
            if (!set.next()) {
                // If the database does not exist, create it
                String createDb = "CREATE DATABASE IF NOT EXISTS " + database;
                connection.setAutoCommit(false);
                statement.execute(createDb);
                connection.commit();
                log.info("Database ({}）created successfully", database);
                if (set.isClosed()) {
                    set.close();
                }
                return true;
            }
            set.close();
            log.info("Database already exists, no need to initialize");
            return false;
        } catch (SQLException e2) {
            throw new RuntimeException(e2);
        }
    }
}
