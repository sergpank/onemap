package md.onemap.harta.web;

import md.onemap.harta.properties.Props;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

public class DbTestServlet extends HttpServlet
{
  private static final Logger LOG = LoggerFactory.getLogger(DbTestServlet.class);

  @Override
  protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException
  {
    try
    {
      Class.forName("org.postgresql.Driver");
    }
    catch (ClassNotFoundException e)
    {
      LOG.error(e.getMessage());
      e.printStackTrace();
    }

    try (Connection connection = DriverManager.getConnection(
        Props.dbUrl() + Props.dbName(), Props.dbLogin(), Props.dbPassword()))
    {
      Statement statement = connection.createStatement();
      ResultSet resultSet = statement.executeQuery("SELECT * FROM test");

      PrintWriter writer = resp.getWriter();
      while (resultSet.next()){
        int id = resultSet.getInt("id");
        String value = resultSet.getString("value");
        writer.write(id + " :: " + value + "\n");
      }
      writer.close();
    }
    catch (Exception e)
    {
      LOG.error(e.getMessage());
      e.printStackTrace();
      throw new RuntimeException(e);
    }
  }
}
