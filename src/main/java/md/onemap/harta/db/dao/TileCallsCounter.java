package md.onemap.harta.db.dao;

import md.onemap.harta.db.DbHelper;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.List;

public class TileCallsCounter
{
  private static final String SELECT = "SELECT count FROM tile_calls_count WHERE level = ? AND x = ? AND y = ?";
  private static final String INSERT = "INSERT INTO tile_calls_count (level, x, y, count) VALUES (?, ?, ?, 1)";
  private static final String UPDATE = "UPDATE tile_calls_count SET count = ? WHERE level = ? AND x = ? AND y = ?";

  private final JdbcTemplate jdbcTemplate;

  public TileCallsCounter()
  {
    jdbcTemplate = new JdbcTemplate(DbHelper.getDataSource());
  }

  public void increment(int level, int x, int y)
  {
    List<Long> data = jdbcTemplate.queryForList(SELECT, Long.class, level, x, y);

    if (data.isEmpty())
    {
      jdbcTemplate.update(INSERT, level, x, y);
    }
    else
    {
      long count = data.get(0) + 1;
      jdbcTemplate.update(UPDATE, count, level, x, y);
    }
  }
}
