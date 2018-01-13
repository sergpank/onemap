package md.onemap.harta.util;

import md.onemap.harta.db.DbHelper;
import md.onemap.harta.db.dao.BuildingDao;
import md.onemap.harta.db.dao.HighwayDao;
import md.onemap.harta.db.dao.NodeDao;
import md.onemap.harta.db.dao.WaterwayDao;
import md.onemap.harta.loader.OsmLoader;
import md.onemap.harta.osm.Building;
import md.onemap.harta.osm.Highway;
import md.onemap.harta.osm.OsmNode;
import md.onemap.harta.osm.Waterway;
import org.apache.log4j.xml.DOMConfigurator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.util.Arrays;
import java.util.Map;

public class OsmToPostgresExporter
{
  private static final Logger log = LoggerFactory.getLogger(OsmToPostgresExporter.class);

  public static void main(String[] args)
  {
    DOMConfigurator.configure("log4j.xml");

    OsmLoader osmLoader = new OsmLoader();
    String dbName = "kishinev"; // always lowercase only
    DatabaseCreator.createDb(dbName);
    Connection connection = DbHelper.getNewConnection(dbName);
    for (String osm : Arrays.asList("osm/Кишинёв.osm"))
    {
      osmLoader.load(osm);
      Map<Long, OsmNode> nodes = osmLoader.getNodes();
      Map<Long, Building> buildings = osmLoader.getBuildings();
      Map<Long, Highway> highways = osmLoader.getHighways();
      Map<Long, Waterway> waterways = osmLoader.getWaterways();

      NodeDao nodeDao = new NodeDao(connection);
      BuildingDao buildingDao = new BuildingDao(connection);
      HighwayDao highwayDao = new HighwayDao(connection);
      WaterwayDao waterwayDao = new WaterwayDao(connection);

      log.info("Saving nodes: {}", nodes.size());
      nodes.values().forEach(nodeDao::save);

      log.info("Saving buildings: {}", buildings.size());
      buildings.values().forEach(buildingDao::save);

      log.info("Saving highways: {}", highways.size());
      highways.values().forEach(highwayDao::save);

      log.info("Saving waterways: {}", waterways.size());
      waterways.values().forEach(waterwayDao::save);
    }
  }
}
