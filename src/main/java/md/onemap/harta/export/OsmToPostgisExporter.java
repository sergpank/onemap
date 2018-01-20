package md.onemap.harta.export;

import md.onemap.harta.db.DatabaseCreator;
import md.onemap.harta.db.DbHelper;
import md.onemap.harta.db.gis.BuildingGisDao;
import md.onemap.harta.db.gis.HighwayGisDao;
import md.onemap.harta.loader.OsmLoader;
import md.onemap.harta.osm.Building;
import md.onemap.harta.osm.Highway;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.util.Map;

public class OsmToPostgisExporter
{
  private static final Logger LOG = LoggerFactory.getLogger(OsmToPostgisExporter.class);

  public static void main(String[] args)
  {
    String dbName = "botanica";
    String osm = "osm/botanica.osm";

    DatabaseCreator.createDb(dbName);
    Connection connection = DbHelper.getNewConnection(dbName);

    OsmLoader osmLoader = new OsmLoader();
    osmLoader.load(osm);

    Map<Long, Building> buildings = osmLoader.getBuildings();
    Map<Long, Highway> highways = osmLoader.getHighways();

    BuildingGisDao buildingDao = new BuildingGisDao(connection);
    HighwayGisDao highwayDao = new HighwayGisDao(connection);

    LOG.info("Saving buildings ...");
    buildings.values().forEach(building -> buildingDao.save(building));

    LOG.info("Saving highways ...");
    highways.values().forEach(highway -> highwayDao.save(highway));

    LOG.info("Normalizing highway names ...");

  }
}
