package md.onemap.harta.db.statistics;

import md.onemap.harta.db.DbHelper;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.List;

public class TileStatistics
{
  private static final String SELECT = "SELECT count FROM statistics.tile_calls_count WHERE level = ? AND x = ? AND y = ?";
  private static final String INSERT = "INSERT INTO statistics.tile_calls_count (level, x, y, count) VALUES (?, ?, ?, 1)";
  private static final String INCREMENT = "UPDATE statistics.tile_calls_count SET count = count + 1 WHERE level = ? AND x = ? AND y = ?";

  private final JdbcTemplate jdbcTemplate;

  public TileStatistics()
  {
    jdbcTemplate = new JdbcTemplate(DbHelper.getDataSource());
  }

  public void incrementTileCalls(int level, int x, int y)
  {
    List<Long> data = jdbcTemplate.queryForList(SELECT, Long.class, level, x, y);

    if (data.isEmpty())
    {
      jdbcTemplate.update(INSERT, level, x, y);
    }
    else
    {
      jdbcTemplate.update(INCREMENT, level, x, y);
    }
  }
}
