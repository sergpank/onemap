package md.onemap.harta.db.dao;

import md.onemap.harta.db.DbHelper;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;
import java.sql.*;
import java.util.List;

public class TileCallsCounter
{
  // Это должно делаться через Hibernate

  // Разобраться как пркрутить connection-pool к Hibernate
  // https://stackoverflow.com/questions/20899455/configuring-hibernate-with-hikaricp

  // При запросе тайла в тайл генераторе нужно икрементировать счетчик для каждого тайла
  // Сделать составной индекс по 'х' и 'у'

  // также добавить свойство tile.counter.enabled=true/false, чтобы отключать эту функцию
  // т.к. она будет тормозить генерацию тайлов

  public void increment(int level, int x, int y)
  {
    try (Connection connection = DbHelper.getConnection())
    {
      String sql = "SELECT count FROM tile_calls_count WHERE level = ? AND x = ? AND y = ?";
      PreparedStatement ps = connection.prepareStatement(sql);
      ps.setInt(1, level);
      ps.setInt(2, x);
      ps.setInt(3, y);

      ResultSet rs = ps.executeQuery();

      if (rs.next())
      {
        long count = rs.getLong(1);
        updateTileCallsCount(connection, count + 1, level, x, y);
      }
      else
      {
        initTileCallsCount(connection, level, x, y);
      }
    }
    catch (SQLException e)
    {
      e.printStackTrace();
    }

  }

  private void updateTileCallsCount(Connection connection, long callsCount, int level, int x, int y)
  throws SQLException
  {
    String sql = String.format("UPDATE tile_calls_count SET count = %d " +
        "WHERE level = %d AND x = %d AND y = %d", callsCount, level, x, y);
    Statement statement = connection.createStatement();
    statement.execute(sql);
  }

  private void initTileCallsCount(Connection connection, int level, int x, int y)
  throws SQLException
  {
    String sql = String.format("INSERT INTO tile_calls_count (level, x, y, count) " +
        "VALUES (%d, %d, %d, %d)", level, x, y, 1);
    Statement statement = connection.createStatement();
    statement.execute(sql);
  }

  public static void main(String[] args)
  {
    DataSource dataSource = DbHelper.getDataSource();
    JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);

//    Integer cnt = jdbcTemplate.queryForObject("SELECT count FROM tile_calls_count WHERE level = ? AND x = ? AND y = ?",
//        Integer.class, 1, 1, 2);

    List<Integer> integers = jdbcTemplate.queryForList("SELECT count FROM tile_calls_count WHERE level = ? AND x = ? AND y = ?",
        Integer.class, 1, 1, 1);

//    System.out.println(cnt);
    System.out.println(integers);

  }
}
