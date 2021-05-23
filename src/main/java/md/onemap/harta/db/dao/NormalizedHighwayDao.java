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

public class NormalizedHighwayDao extends Dao<NormalizedHighway> {

  // Идея такая -
  // 1. сначала нахожу все дороги в имени которых есть искомое слово
  // 2. потомн нахожу все релейшны которые содержат эти дороги
  // 3. потом трансформирую эти релейшны в гео ГСОН

  private static final String FIND_SQL =
"""
SELECT t.id, t.value as name,
    json_build_object(
    'type', 'FeatureCollection',
    'features',
        json_agg(json_build_object(
            'type', 'Feature',
            'geometry', st_asgeojson(w.geometry)::json,
            'properties',
             json_build_object(
               'id', nh.id,
               'name', nh.name,
               'name_ru', nh.name_ru,
               'name_old', nh.name_old)))) as geojson
FROM gis.relation_members rm
   JOIN gis.relation r on r.id = rm.relation_id
   JOIN gis.way w ON w.id = rm.member_id
   JOIN normalized_highways nh ON nh.id = w.id
   JOIN gis.tag t ON t.id = rm.relation_id
   WHERE (nh.name ~ ? OR nh.name_ru ~ ? OR nh.name_old ~ ?)
   AND t.key = 'name'
   AND r.type = 'associatedStreet'
   GROUP BY rm.relation_id, t.value, t.id;
""";

  private static final String INSERT_SQL = "INSERT INTO normalized_highways (id, name, name_ru, name_old) VALUES (?, ?, ?, ?)";

  public Collection<NormalizedHighway> findHighwaysByKey(String key) {
    Set<NormalizedHighway> result = new HashSet<>();

    try (Connection con = DbHelper.getConnection()) {
      // replaces all spaces to lookup of any symbols
      // it is useful for cases when street name is typed partially
      // i.e.: stef cel mare, gr vieru, bul gagarin, str a puskin
      key = key.replaceAll(" ", ".+");

      PreparedStatement ps = con.prepareStatement(FIND_SQL);
      ps.setString(1, key);
      ps.setString(2, key);
      ps.setString(3, key);

      ResultSet rs = ps.executeQuery();

      while (rs.next()) {
        long id = rs.getLong("id");
        String name = rs.getString("name");
//        String nameRu = rs.getString("name_ru");
//        String nameOld = rs.getString("name_old");
        String geoJson = rs.getString("geojson");

        result.add(new NormalizedHighway(id, name, null, null, geoJson));
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }

    return result;
  }

  @Override
  public void save(NormalizedHighway entity) {
    try (Connection connection = DbHelper.getConnection()) {
      PreparedStatement pstmt = connection.prepareStatement(INSERT_SQL);
      prepare(entity, pstmt);
      pstmt.execute();
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  @Override
  public void saveAll(Collection<NormalizedHighway> entities) {
    try (Connection connection = DbHelper.getConnection()) {
      PreparedStatement pstmt = connection.prepareStatement(INSERT_SQL);
      int cnt = 0;
      for (NormalizedHighway nh : entities) {
        prepare(nh, pstmt);
        pstmt.addBatch();
        cnt++;
        if (cnt > 99) {
          pstmt.executeBatch();
          cnt = 0;
        }
      }
      if (cnt > 0) {
        pstmt.executeBatch();
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  private void prepare(NormalizedHighway entity, PreparedStatement pstmt) throws SQLException {
    int pos = 1;
    pstmt.setLong(pos++, entity.getId());
    pstmt.setString(pos++, entity.getName());
    pstmt.setString(pos++, entity.getNameRu());
    pstmt.setString(pos++, entity.getNameOld());
  }

  @Override
  public NormalizedHighway load(long id) {
    throw new UnsupportedOperationException();
  }

  @Override
  public Collection<NormalizedHighway> load(int zoomLevel, BoundsLatLon box) {
    throw new UnsupportedOperationException();
  }

  @Override
  public Collection<NormalizedHighway> loadAll() {
    throw new UnsupportedOperationException();
  }
}
