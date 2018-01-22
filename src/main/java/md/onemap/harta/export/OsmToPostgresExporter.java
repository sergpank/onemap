package md.onemap.harta.export;

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
import java.util.Collection;
import java.util.Map;

import static md.onemap.harta.properties.OsmExporterProperties.getDbName;
import static md.onemap.harta.properties.OsmExporterProperties.getOsmPath;

public class OsmToPostgresExporter extends OsmExporter
{
  private static final Logger LOG = LoggerFactory.getLogger(OsmToPostgresExporter.class);

  public static void main(String[] args)
  {
    DOMConfigurator.configure("log4j.xml");
    new OsmToPostgresExporter().export();
  }

  @Override
  protected void exportEntities()
  {
    OsmLoader osmLoader = new OsmLoader();
    osmLoader.load(getOsmPath());

    Map<Long, OsmNode> nodes = osmLoader.getNodes();
    Map<Long, Building> buildings = osmLoader.getBuildings();
    Map<Long, Highway> highways = osmLoader.getHighways();
    Map<Long, Waterway> waterways = osmLoader.getWaterways();

    Connection connection = DbHelper.getConnection(getDbName());
    NodeDao nodeDao = new NodeDao(connection);
    BuildingDao buildingDao = new BuildingDao(connection);
    HighwayDao highwayDao = new HighwayDao(connection);
    WaterwayDao waterwayDao = new WaterwayDao(connection);

    LOG.info("Saving nodes: {}", nodes.size());
    nodes.values().forEach(nodeDao::save);

    LOG.info("Saving buildings: {}", buildings.size());
    buildings.values().forEach(buildingDao::save);

    LOG.info("Saving highways: {}", highways.size());
    highways.values().forEach(highwayDao::save);

    LOG.info("Saving waterways: {}", waterways.size());
    waterways.values().forEach(waterwayDao::save);
  }

  @Override
  protected void normalizeHighwayNames()
  {
    LOG.info("Normalizing highway names ...");
    String dbName = getDbName();
    Collection<Highway> highways = new HighwayDao(DbHelper.getConnection(dbName)).loadAll();
    new HighwayNameNormalizer().normalize(highways, dbName);
  }
}
