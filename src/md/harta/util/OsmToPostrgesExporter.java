package md.harta.util;

import java.sql.Connection;
import java.util.Arrays;
import java.util.Map;
import md.harta.db.BuildingDao;
import md.harta.db.DbHelper;
import md.harta.db.HighwayDao;
import md.harta.db.NodeDao;
import md.harta.loader.OsmLoader;
import md.harta.osm.Building;
import md.harta.osm.Highway;
import md.harta.osm.OsmNode;

public class OsmToPostrgesExporter
{
  public static void main(String[] args)
  {
    OsmLoader osmLoader = new OsmLoader();
    String dbName = "HanulMorii";
    DatabaseCreator.createDb(dbName);
    Connection connection = DbHelper.getNewConnection(dbName);
    for (String osm : Arrays.asList("osm/HanulMorii.osm"))
    {
      osmLoader.load(osm, null);
      Map<Long, OsmNode> nodes = osmLoader.getNodes();
      Map<Long, Building> buildings = osmLoader.getBuildings(null);
      Map<Long, Highway> highways = osmLoader.getHighways(null);

      NodeDao nodeDao = new NodeDao(connection);
      BuildingDao buildingDao = new BuildingDao(connection);
      HighwayDao highwayDao = new HighwayDao(connection);

      nodes.values().forEach(node -> nodeDao.save(node));
      buildings.values().forEach(building -> buildingDao.save(building));
      highways.values().forEach(highway -> highwayDao.save(highway));
    }
  }
}
