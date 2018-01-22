package md.onemap.harta.db;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by sergpank on 21.04.15.
 */
public class DbHelper
{
  private static final Logger LOG = LoggerFactory.getLogger(DbHelper.class);

  private static Map<String, Connection> connectionMap = new HashMap<>();
  private static String url = "jdbc:postgresql://localhost:5432/";
  private static String login = "postgres";
  private static String password = "postgres";

  private static Connection init(String dbName)
  {
    Connection connection = connectionMap.get(dbName);
    try
    {
      if (connection == null || connection.isClosed())
      {
        connection = DriverManager.getConnection(url + dbName, login, password);
        connectionMap.put(dbName, connection);
        LOG.info("Connection initialized ...");
      }
    }
    catch (SQLException e)
    {
      LOG.error(e.getMessage());
    }

    return connection;
  }

  public static Connection getConnection(String dbName)
  {
    return init(dbName);
  }
}
