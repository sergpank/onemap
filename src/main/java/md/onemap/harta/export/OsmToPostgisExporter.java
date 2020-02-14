package md.onemap.harta.export;

import md.onemap.harta.db.DatabaseCreator;
import md.onemap.harta.db.gis.NodeGisDao;
import md.onemap.harta.db.gis.RelationGisDao;
import md.onemap.harta.db.gis.WayGisDao;
import md.onemap.harta.db.gis.entity.Way;
import md.onemap.harta.loader.OsmLoader;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.text.DecimalFormatSymbols;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

public class OsmToPostgisExporter extends OsmExporter
{
  private static final Logger LOG = LogManager.getLogger();

  public static void main(String[] args)
  {
    System.out.printf("Format: %f\n", 123.45);
    System.out.println("Concat: " + 123.45);

    System.out.println("Decimal Separator: " + DecimalFormatSymbols.getInstance().getDecimalSeparator());
    RuntimeMXBean runtimeBean = ManagementFactory.getRuntimeMXBean();

    Map<String, String> systemProperties = runtimeBean.getSystemProperties();
    Set<String> keys = systemProperties.keySet();

    System.out.println("LOCALE_USER_DEFAULT: " + systemProperties.get("LOCALE_USER_DEFAULT"));
    System.out.println("LOCALE_SDECIMAL: " + systemProperties.get("LOCALE_SDECIMAL"));

    System.out.println("SYSTEM PROPERTIES:");
    for (String key : keys) {
      String value = systemProperties.get(key);
      System.out.printf("[%s] = %s.\n", key, value);
    }
    System.out.println();

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
    LOG.info("\n\nReading {} ...", osmFile);
    osmLoader.load(osmFile);

    LOG.info("\n\nSaving nodes ...");
    new NodeGisDao().saveAll(osmLoader.getNodes().values());

    LOG.info("\n\nSaving ways ...");
    new WayGisDao().saveAll(osmLoader.getWays().values());

    LOG.info("\n\nSaving relations ...");
    new RelationGisDao(osmLoader).saveAll(osmLoader.getRelations().values());
  }

  @Override
  protected void normalizeHighwayNames()
  {
    LOG.info("Normalizing highway names ... ");
    Collection<Way> highways = new WayGisDao().loadAllHighways();
    new HighwayNameNormalizer().normalize(highways);
  }
}
