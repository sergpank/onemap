package md.onemap.harta.db.statistics;

import md.onemap.harta.db.DbHelper;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.List;

public class VisitorStatistics
{
  public static final String TABLE_NAME = "statistics.visitors";

  private static final String SELECT = "SELECT tile_cnt FROM " + TABLE_NAME + " WHERE ip = ?";
  private static final String INSERT = "INSERT INTO " + TABLE_NAME + " VALUES(?, now(), 1)";
  private static final String INCREMENT = "UPDATE " + TABLE_NAME + " set tile_cnt = tile_cnt + 1 WHERE ip = ?";

  private final JdbcTemplate jdbcTemplate;

  public VisitorStatistics()
  {
    jdbcTemplate = new JdbcTemplate(DbHelper.getDataSource());
  }

  public void incrementTileCalls(String ip)
  {
    List<Integer> requestedTileCount = jdbcTemplate.queryForList(SELECT, Integer.class, ip);

    if (requestedTileCount.isEmpty())
    {
      jdbcTemplate.update(INSERT, ip);
    }
    else
    {
      jdbcTemplate.update(INCREMENT, ip);
    }
  }
}
