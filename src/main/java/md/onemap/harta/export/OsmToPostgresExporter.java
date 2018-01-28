package md.onemap.harta.export;

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

import java.util.Collection;
import java.util.Map;

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
    osmLoader.load(osmFile);

    Map<Long, OsmNode> nodes = osmLoader.getNodes();
    Map<Long, Building> buildings = osmLoader.getBuildings();
    Map<Long, Highway> highways = osmLoader.getHighways();
    Map<Long, Waterway> waterways = osmLoader.getWaterways();

    NodeDao nodeDao = new NodeDao();
    BuildingDao buildingDao = new BuildingDao();
    HighwayDao highwayDao = new HighwayDao();
    WaterwayDao waterwayDao = new WaterwayDao();

    LOG.info("Saving nodes: {}", nodes.size());
    nodeDao.saveAll(nodes.values());

    LOG.info("Saving buildings: {}", buildings.size());
    buildingDao.saveAll(buildings.values());

    LOG.info("Saving highways: {}", highways.size());
    highwayDao.saveAll(highways.values());

    LOG.info("Saving waterways: {}", waterways.size());
   waterwayDao.saveAll(waterways.values());
  }

  @Override
  protected void normalizeHighwayNames()
  {
    LOG.info("Normalizing highway names ...");
    Collection<Highway> highways = new HighwayDao().loadAll();
    new HighwayNameNormalizer().normalize(highways);
  }
}
