/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hr.algebra.dal.sql;

import com.microsoft.sqlserver.jdbc.SQLServerDataSource;
import javax.sql.DataSource;

/**
 *
 * @author filip
 */
public class DataSourceSingleton {

    private static final String PWD = "SQL";
    private static final String USER = "sasJava";
    private static final String DB = "ProjektniZadatak";
    private static final String SERVR = "localhost";

    private static DataSource instance;

    private DataSourceSingleton() {
    }

    public static DataSource getInstance() {

        if (instance == null) {
            instance = createInstance();
        }
        return instance;
    }

    private static DataSource createInstance() {
        SQLServerDataSource dataSource = new SQLServerDataSource();

        dataSource.setServerName(SERVR);
        dataSource.setDatabaseName(DB);
        dataSource.setUser(USER);
        dataSource.setPassword(PWD);

        return dataSource;
    }


}
