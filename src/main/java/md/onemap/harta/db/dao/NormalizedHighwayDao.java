package md.onemap.harta.db.dao;

import md.onemap.harta.geometry.BoundsLatLon;
import md.onemap.harta.osm.NormalizedHighway;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Collection;

public class NormalizedHighwayDao extends Dao<NormalizedHighway>
{
  private static final String INSERT_SQL = "INSERT into normalized_highways (id, name, name_ru, name_old) VALUES (?, ?, ?, ?)";

  public NormalizedHighwayDao(Connection connection)
  {
    super(connection);
  }

  @Override
  public void save(NormalizedHighway entity)
  {
    try (PreparedStatement pstmt = connection.prepareStatement(INSERT_SQL))
    {
      prepare(entity, pstmt);
      pstmt.execute();
    }
    catch (SQLException e)
    {
      e.printStackTrace();
    }
  }

  @Override
  public void saveAll(Collection<NormalizedHighway> entities)
  {
    try (PreparedStatement pstmt = connection.prepareStatement(INSERT_SQL))
    {
      int cnt = 0;
      for (NormalizedHighway nh : entities)
      {
        prepare(nh, pstmt);
        pstmt.addBatch();
        cnt++;
        if (cnt > 99)
        {
          pstmt.executeBatch();
          cnt = 0;
        }
      }
      if (cnt > 0)
      {
        pstmt.executeBatch();
      }
    }
    catch (SQLException e)
    {
      e.printStackTrace();
    }
  }

  private void prepare(NormalizedHighway entity, PreparedStatement pstmt) throws SQLException
  {
    int pos = 1;
    pstmt.setLong(pos++, entity.getId());
    pstmt.setString(pos++, entity.getName());
    pstmt.setString(pos++, entity.getNameRu());
    pstmt.setString(pos++, entity.getNameOld());
  }

  @Override
  public NormalizedHighway load(long id)
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public Collection load(int zoomLevel, BoundsLatLon box)
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public Collection<NormalizedHighway> loadAll()
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public BoundsLatLon getBounds()
  {
    throw new UnsupportedOperationException();
  }
}
