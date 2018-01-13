package md.onemap.harta.util;

import md.onemap.harta.db.*;
import md.onemap.harta.db.gis.BuildingGisDao;
import md.onemap.harta.db.gis.HighwayGisDao;
import md.onemap.harta.loader.OsmLoader;
import md.onemap.harta.osm.Building;
import md.onemap.harta.osm.Highway;

import java.sql.Connection;
import java.util.Arrays;
import java.util.Map;

public class OsmToPostgisExporter
{
  public static void main(String[] args)
  {
    OsmLoader osmLoader = new OsmLoader();
    String dbName = "chisinau";
    DatabaseCreator.createDb(dbName);
    Connection connection = DbHelper.getNewConnection(dbName);
    for (String osm : Arrays.asList("osm/chisinau.osm"))
    {
      osmLoader.load(osm);
      Map<Long, Building> buildings = osmLoader.getBuildings();
      Map<Long, Highway> highways = osmLoader.getHighways();

      BuildingGisDao buildingDao = new BuildingGisDao(connection);
      HighwayGisDao highwayDao = new HighwayGisDao(connection);

      System.out.println("Saving buildings ...");
      buildings.values().forEach(building -> buildingDao.save(building));

      System.out.println("Saving highways ...");
      highways.values().forEach(highway -> highwayDao.save(highway));

      //--46.9957461;46.9988352;28.8679837;28.8736379
//      buildingDao.load(15, new BoundsLatLon(null, 46.9957461, 28.8679837, 46.9988352, 28.8736379), null, new MercatorProjector(15));
    }
  }
}
