package me.sharuru.jchv.frontend.config;

import lombok.extern.slf4j.Slf4j;
import org.hibernate.boot.model.naming.Identifier;
import org.hibernate.engine.jdbc.env.spi.JdbcEnvironment;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.orm.jpa.hibernate.SpringPhysicalNamingStrategy;
import org.springframework.context.annotation.Configuration;
import org.thymeleaf.util.StringUtils;

import java.io.Serializable;

/**
 * Hibernate table name strategy class.
 * <p>
 * For multiple sub-systems.
 * When prefix is not configured, it will use 'meta_data' as the business table.
 */
@Configuration
@Slf4j
public class TableNameStrategy extends SpringPhysicalNamingStrategy implements Serializable {

    @Value("${jchv.table-prefix:}")
    private String tableNamePrefix;

    @Override
    public Identifier toPhysicalTableName(Identifier name, JdbcEnvironment context) {
        log.info("Current table name prefix is: {}.", tableNamePrefix);
        String tableName = StringUtils.isEmpty(tableNamePrefix) ? name.getText() : tableNamePrefix.concat("_").concat(name.getText());
        return new Identifier(tableName, name.isQuoted());
    }

}
