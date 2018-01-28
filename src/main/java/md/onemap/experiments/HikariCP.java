package md.onemap.experiments;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

public class HikariCP
{
  public static void main(String[] args)
  {
    Properties props = new Properties();
    props.setProperty("dataSourceClassName", "org.postgresql.ds.PGSimpleDataSource");
    props.setProperty("dataSource.user", "postgres");
    props.setProperty("dataSource.password", "postgres");
    props.setProperty("dataSource.databaseName", "botanica");
    props.setProperty("jdbcUrl", "jdbc:postgresql://localhost:5432/");
//    props.put("dataSource.logWriter", new PrintWriter(System.out));

    HikariConfig config = new HikariConfig(props);
    HikariDataSource hikariDs = new HikariDataSource(config);


    try (Connection connection = hikariDs.getConnection())
    {
      Statement statement = connection.createStatement();
      ResultSet resultSet = statement.executeQuery("select * from normalized_highways");
      while (resultSet.next())
      {
        long id = resultSet.getLong("id");
        String name = resultSet.getString("name");
        String nameOld = resultSet.getString("name_old");

        System.out.printf("%d - %s - %s\n", id, name, nameOld);
      }
    }
    catch (SQLException e)
    {
      e.printStackTrace();
    }
  }
}
