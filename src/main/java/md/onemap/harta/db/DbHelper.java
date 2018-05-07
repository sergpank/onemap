package md.onemap.harta.db;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import md.onemap.harta.properties.Props;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Created by sergpank on 21.04.15.
 */
public class DbHelper
{
  private static final Logger LOG = LoggerFactory.getLogger(DbHelper.class);

  private static final String DATASOURCE_CLASS_NAME = "org.postgresql.ds.PGSimpleDataSource";

  private static HikariDataSource dataSource;

  private static void init()
  {
    Properties props = new Properties();

    props.setProperty("dataSourceClassName", DATASOURCE_CLASS_NAME);
    props.setProperty("dataSource.databaseName", Props.dbName());
    props.setProperty("dataSource.user", Props.dbLogin());
    props.setProperty("dataSource.password", Props.dbPassword());
//    props.setProperty("jdbcUrl", Props.dbUrl());
//    props.put("dataSource.logWriter", new PrintWriter(System.out));

    HikariConfig config = new HikariConfig(props);
    dataSource = new HikariDataSource(config);
  }

  /**
   * @return Connection to DB configured in application.properties
   */
  public static Connection getConnection()
  {
    Connection connection = null;
    if (dataSource == null)
    {
      init();
    }

    try
    {
      connection = dataSource.getConnection();
    }
    catch (SQLException e)
    {
      LOG.error(e.getMessage());
    }

    return connection;
  }

  public static Connection getConnection(String dbName)
  {
    Connection connection = null;
    try
    {
      String url = Props.dbUrl() + dbName;
      connection = DriverManager.getConnection(url, Props.dbLogin(), Props.dbPassword());
    }
    catch (SQLException e)
    {
      e.printStackTrace();
    }

    return connection;
  }
}
