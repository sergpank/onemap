package md.harta.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Created by sergpank on 21.04.15.
 */
public class DbHelper
{
  private static Connection connection;
  private static String url = "jdbc:postgresql://localhost:5434/harta";
  private static String login = "postgres";
  private static String password = "jkl123";

  static
  {
    try
    {
      connection = DriverManager.getConnection(url, login, password);
    }
    catch (SQLException e)
    {
      e.printStackTrace();
    }
  }

  public static Connection getConnection()
  {
    return connection;
  }
}
