package md.onemap.harta.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Created by sergpank on 21.04.15.
 */
public class DbHelper
{
  private static Connection connection;
  public static String dbName = "harta";
  private static String url = "jdbc:postgresql://localhost:5432/";
  private static String login = "postgres";
  private static String password = "postgres";

  private static void init()
  {
    try
    {
      if (connection == null)
      {
        connection = DriverManager.getConnection(url + dbName, login, password);
      }
    }
    catch (SQLException e)
    {
      e.printStackTrace();
    }
  }

  public static Connection getConnection()
  {
    init();
    return connection;
  }

  public static Connection getConnection(String dbName)
  {
    DbHelper.dbName = dbName;
    init();
    return connection;
  }

  public static Connection getNewConnection(String dbName)
  {
    DbHelper.dbName = dbName;
    connection = null;
    init();
    return connection;
  }
}
