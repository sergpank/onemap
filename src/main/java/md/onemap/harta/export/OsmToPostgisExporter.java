package md.onemap.harta.export;

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
    osmLoader.load(osmFile);

    Map<Long, Building> buildings = osmLoader.getBuildings();
    Map<Long, Highway> highways = osmLoader.getHighways();

    LOG.info("Saving buildings ...");
    new BuildingGisDao().saveAll(buildings.values());

    LOG.info("Saving highways ...");
    new HighwayGisDao().saveAll(highways.values());
  }

  @Override
  protected void normalizeHighwayNames()
  {
    LOG.info("Normalizing highway names ...");
    Collection<Highway> highways = new HighwayGisDao().loadAll();
    new HighwayNameNormalizer().normalize(highways);
  }
}
