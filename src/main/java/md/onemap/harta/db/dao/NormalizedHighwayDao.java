package md.onemap.harta.db.dao;

import md.onemap.harta.db.DbHelper;
import md.onemap.harta.geometry.BoundsLatLon;
import md.onemap.harta.osm.NormalizedHighway;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class NormalizedHighwayDao extends Dao<NormalizedHighway>
{
  private static final String FIND_SQL = "SELECT * FROM normalized_highways WHERE name ~ ? OR name_ru ~ ? OR name_old ~ ?";

  private static final String INSERT_SQL = "INSERT INTO normalized_highways (id, name, name_ru, name_old) VALUES (?, ?, ?, ?)";

  public Collection<NormalizedHighway> findHighwaysByKey(String key)
  {
    Set<NormalizedHighway> result = new HashSet<>();

    try (Connection con = DbHelper.getConnection())
    {
      // replaces all spaces to lookup of any symbols
      // it is useful for cases when street name is typed partially
      // i.e.: stef cel mare, gr vieru, bul gagarin, str a puskin
      key = key.replaceAll(" ", ".+");

      PreparedStatement ps = con.prepareStatement(FIND_SQL);
      ps.setString(1, key);
      ps.setString(2, key);
      ps.setString(3, key);

      ResultSet rs = ps.executeQuery();

      while (rs.next())
      {
        long id = rs.getLong("id");
        String name = rs.getString("name");
        String nameRu = rs.getString("name_ru");
        String nameOld = rs.getString("name_old");

        result.add(new NormalizedHighway(id, name, nameRu, nameOld));
      }
    }
    catch (SQLException e)
    {
      e.printStackTrace();
    }

    return result;
  }

  @Override
  public void save(NormalizedHighway entity)
  {
    try (Connection connection = DbHelper.getConnection())
    {
      PreparedStatement pstmt = connection.prepareStatement(INSERT_SQL);
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
    try (Connection connection = DbHelper.getConnection())
    {
      PreparedStatement pstmt = connection.prepareStatement(INSERT_SQL);
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
