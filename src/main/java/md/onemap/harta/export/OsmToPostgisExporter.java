package md.onemap.harta.export;

import md.onemap.harta.db.DatabaseCreator;
import md.onemap.harta.db.gis.*;
import md.onemap.harta.loader.OsmLoader;
import md.onemap.harta.osm.*;
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
  protected void createdDb(String dbName)
  {
    DatabaseCreator.createGisDb(dbName);
  }

  @Override
  protected void exportEntities()
  {
    OsmLoader osmLoader = new OsmLoader();
    osmLoader.load(osmFile);

    Map<Long, Building> buildings = osmLoader.getBuildings();
    Map<Long, Highway> highways = osmLoader.getHighways();
    Map<Long, Leisure> leisure = osmLoader.getLeisure();
    Map<Long, Landuse> landuse = osmLoader.getLanduse();
    Map<Long, Natural> nature = osmLoader.getNature();
    Map<Long, Waterway> waterways = osmLoader.getWaterways();

    LOG.info("\n\nSaving buildings ...");
    new BuildingGisDao().saveAll(buildings.values());

    LOG.info("\n\nSaving highways ...");
    new HighwayGisDao().saveAll(highways.values());

    LOG.info("\n\nSaving leisure ...");
    new LeisureGisDao().saveAll(leisure.values());

    LOG.info("\n\nSaving landuse ...");
    new LanduseGisDao().saveAll(landuse.values());

    LOG.info("\n\nSaving waterways ...");
    new WaterwayGisDao().saveAll(waterways.values());

    LOG.info("\n\nSaving nature ...");
    new NatureGisDao().saveAll(nature.values());
  }

  @Override
  protected void normalizeHighwayNames()
  {
    LOG.info("Normalizing highway names ...");
    Collection<Highway> highways = new HighwayGisDao().loadAll();
    new HighwayNameNormalizer().normalize(highways);
  }
}
