package com.bsp.procedure_gateway.datasource;


import com.bsp.procedure_gateway.entity.DatabaseMaster;
import com.bsp.procedure_gateway.util.PasswordEncryptor;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class DataSourceManagerImpl implements DataSourceManager {
	
	private final PasswordEncryptor passwordEncoderUtil;

    public DataSourceManagerImpl(
    		PasswordEncryptor passwordEncoderUtil) {
        this.passwordEncoderUtil = passwordEncoderUtil;
    }

    private final Map<Long, DataSource> dataSourceCache =
            new ConcurrentHashMap<>();

    @Override
    public DataSource getDataSource(DatabaseMaster databaseMaster) {

        return dataSourceCache.computeIfAbsent(

                databaseMaster.getDatabaseId(),

                id -> createDataSource(databaseMaster)

        );

    }

    @Override
    public void refreshDataSource(Long databaseId) {

        removeDataSource(databaseId);

    }

    @Override
    public void removeDataSource(Long databaseId) {

        DataSource dataSource =
                dataSourceCache.remove(databaseId);

        if (dataSource instanceof HikariDataSource hikari) {

            hikari.close();

        }

    }

    private DataSource createDataSource(
            DatabaseMaster databaseMaster) {

        HikariConfig config = new HikariConfig();

        config.setDriverClassName(
                "oracle.jdbc.OracleDriver"
        );

        String jdbcUrl;

        if (databaseMaster.getServiceName() != null 
                && !databaseMaster.getServiceName().isBlank()) {

            jdbcUrl = "jdbc:oracle:thin:@//"
                    + databaseMaster.getHost()
                    + ":"
                    + databaseMaster.getPort()
                    + "/"
                    + databaseMaster.getServiceName();

        } else {

            jdbcUrl = "jdbc:oracle:thin:@"
                    + databaseMaster.getHost()
                    + ":"
                    + databaseMaster.getPort()
                    + ":"
                    + databaseMaster.getSid();
        }


        config.setJdbcUrl(jdbcUrl);

        config.setUsername(
                databaseMaster.getUsername()
        );

        config.setPassword(
                passwordEncoderUtil.decode(
                        databaseMaster.getPassword()
                )
        );

        config.setMaximumPoolSize(10);

        config.setMinimumIdle(2);

        config.setPoolName(
                databaseMaster.getDatabaseName()
        );

        return new HikariDataSource(config);
    }

}
