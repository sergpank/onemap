package md.onemap.harta.db.statistics;

import md.onemap.harta.db.DbHelper;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.List;

public class TileStatistics
{
  public static final String TABLE_NAME = "statistics.tile_calls_count";

  private static final String SELECT = "SELECT count FROM " + TABLE_NAME + " WHERE level = ? AND x = ? AND y = ?";
  private static final String INSERT = "INSERT INTO " + TABLE_NAME + " (level, x, y, count) VALUES (?, ?, ?, 1)";
  private static final String INCREMENT = "UPDATE " + TABLE_NAME + " SET count = count + 1 WHERE level = ? AND x = ? AND y = ?";

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
