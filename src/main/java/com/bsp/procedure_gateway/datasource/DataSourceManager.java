package com.bsp.procedure_gateway.datasource;

import javax.sql.DataSource;

import com.bsp.procedure_gateway.entity.DatabaseMaster;

public interface DataSourceManager {

    DataSource getDataSource(DatabaseMaster databaseMaster);

    void refreshDataSource(Long databaseId);

    void removeDataSource(Long databaseId);

}