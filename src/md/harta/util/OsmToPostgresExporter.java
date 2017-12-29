package md.harta.util;

import java.sql.Connection;
import java.util.Arrays;
import java.util.Map;

import md.harta.db.*;
import md.harta.db.dao.BuildingDao;
import md.harta.db.dao.HighwayDao;
import md.harta.db.dao.NodeDao;
import md.harta.db.dao.WaterwayDao;
import md.harta.loader.OsmLoader;
import md.harta.osm.Building;
import md.harta.osm.Highway;
import md.harta.osm.OsmNode;
import md.harta.osm.Waterway;
import org.apache.log4j.xml.DOMConfigurator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OsmToPostgresExporter
{
  private static final Logger log = LoggerFactory.getLogger(OsmToPostgresExporter.class);

  public static void main(String[] args)
  {
    DOMConfigurator.configure("log4j.xml");

    OsmLoader osmLoader = new OsmLoader();
    String dbName = "botanica";
    DatabaseCreator.createDb(dbName);
    Connection connection = DbHelper.getNewConnection(dbName);
    for (String osm : Arrays.asList("osm/botanica.osm"))
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

      log.info("Saving nodes:");
      nodes.values().forEach(nodeDao::save);

      log.info("Saving buildings:");
      buildings.values().forEach(buildingDao::save);

      log.info("Saving highways:");
      highways.values().forEach(highwayDao::save);

      log.info("Saving waterways:");
      waterways.values().forEach(waterwayDao::save);
    }
  }
}
