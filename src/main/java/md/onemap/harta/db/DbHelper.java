package md.onemap.harta.db;

import md.onemap.harta.properties.Props;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Created by sergpank on 21.04.15.
 */
public class DbHelper
{
  private static final Logger LOG = LogManager.getLogger();

  private static final String DATA_SOURCE_CLASS_NAME = "org.postgresql.ds.PGSimpleDataSource";

  private static HikariDataSource dataSource;

  private static JdbcTemplate jdbcTemplate;

  private static void init()
  {
    Properties props = new Properties();

    props.setProperty("dataSourceClassName", DATA_SOURCE_CLASS_NAME);
    props.setProperty("dataSource.databaseName", Props.dbName());
    props.setProperty("dataSource.user", Props.dbLogin());
    props.setProperty("dataSource.password", Props.dbPassword());
//    props.setProperty("jdbcUrl", Props.dbUrl());
//    props.put("dataSource.logWriter", new PrintWriter(System.out));

    HikariConfig config = new HikariConfig(props);
    dataSource = new HikariDataSource(config);
    jdbcTemplate = new JdbcTemplate(dataSource);
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

  /**
   * Plain JDBC Connection
   *
   * @param dbName Name of the database to create to
   * @return Connection to Database. NOTE: this connection is not pooled, so don't forget
   * to close it when the work is done.
   */
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

  public static DataSource getDataSource()
  {
    if (dataSource == null)
    {
      init();
    }
    return dataSource;
  }

  public static JdbcTemplate getJdbcTemplate()
  {
    if (jdbcTemplate == null)
    {
      init();
    }
    return jdbcTemplate;
  }
}
