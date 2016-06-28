package md.harta.util;

import md.harta.db.*;
import md.harta.geometry.Bounds;
import md.harta.loader.OsmLoader;
import md.harta.osm.Building;
import md.harta.osm.Highway;
import md.harta.osm.OsmNode;
import md.harta.projector.MercatorProjector;

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
      osmLoader.load(osm, null);
      Map<Long, Building> buildings = osmLoader.getBuildings(null);
      Map<Long, Highway> highways = osmLoader.getHighways(null);

      BuildingGisDao buildingDao = new BuildingGisDao(connection);
      HighwayGisDao highwayDao = new HighwayGisDao(connection);

      System.out.println("Saving buildings ...");
      buildings.values().forEach(building -> buildingDao.save(building));

      System.out.println("Saving highways ...");
      highways.values().forEach(highway -> highwayDao.save(highway));

      //--46.9957461;46.9988352;28.8679837;28.8736379
//      buildingDao.load(15, new Bounds(null, 46.9957461, 28.8679837, 46.9988352, 28.8736379), null, new MercatorProjector(15));
    }
  }
}
