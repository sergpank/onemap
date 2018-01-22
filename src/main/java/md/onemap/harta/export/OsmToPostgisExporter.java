package md.onemap.harta.export;

import md.onemap.harta.db.DbHelper;
import md.onemap.harta.db.gis.BuildingGisDao;
import md.onemap.harta.db.gis.HighwayGisDao;
import md.onemap.harta.loader.OsmLoader;
import md.onemap.harta.osm.Building;
import md.onemap.harta.osm.Highway;
import org.apache.log4j.xml.DOMConfigurator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.Map;

import static md.onemap.harta.properties.OsmExporterProperties.getDbName;
import static md.onemap.harta.properties.OsmExporterProperties.getOsmPath;

public class OsmToPostgisExporter extends OsmExporter
{
  private static final Logger LOG = LoggerFactory.getLogger(OsmToPostgisExporter.class);

  public static void main(String[] args)
  {
    DOMConfigurator.configure("log4j.xml");
    new OsmToPostgisExporter().export();
  }

  @Override
  protected void exportEntities()
  {
    OsmLoader osmLoader = new OsmLoader();
    osmLoader.load(getOsmPath());

    Map<Long, Building> buildings = osmLoader.getBuildings();
    Map<Long, Highway> highways = osmLoader.getHighways();

    LOG.info("Saving buildings ...");
    BuildingGisDao buildingDao = new BuildingGisDao(DbHelper.getConnection(getDbName()));
    buildingDao.saveAll(buildings.values());

    LOG.info("Saving highways ...");
    HighwayGisDao highwayDao = new HighwayGisDao(DbHelper.getConnection(getDbName()));
    highwayDao.saveAll(highways.values());
  }

  @Override
  protected void normalizeHighwayNames()
  {
    LOG.info("Normalizing highway names ...");
    String dbName = getDbName();
    Collection<Highway> highways = new HighwayGisDao(DbHelper.getConnection(dbName)).loadAll();
    new HighwayNameNormalizer().normalize(highways, dbName);
  }
}
