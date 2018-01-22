package md.onemap.harta.export;

import md.onemap.harta.db.DbHelper;
import md.onemap.harta.db.dao.NormalizedHighwayDao;
import md.onemap.harta.osm.Highway;
import md.onemap.harta.osm.NormalizedHighway;

import java.util.ArrayList;
import java.util.Collection;

public class HighwayNameNormalizer
{
  public void normalize(Collection<Highway> highways, String dbName) {
    Collection<NormalizedHighway> normalizedHighways = new ArrayList<>();

    for (Highway h : highways)
    {
      NormalizedHighway nh = normalizeHighway(h);
      if (nh != null)
      {
        normalizedHighways.add(nh);
      }
    }

    new NormalizedHighwayDao(DbHelper.getConnection(dbName)).saveAll(normalizedHighways);
  }

  private NormalizedHighway normalizeHighway(Highway h)
  {
    if (h.getName() == null || h.getName().isEmpty())
    {
      return null;
    }

    String normalizedName = normalizeName(h.getName());
    String normalizedNameRu = normalizeName(h.getNameRu());
    String normalizedNameOld = normalizeName(h.getNameOld());

    return new NormalizedHighway(h.getId(), normalizedName, normalizedNameRu, normalizedNameOld);
  }

  public String normalizeName(String name)
  {
    if (name == null || name.isEmpty())
    {
      return null;
    }

    String normalized = name.toLowerCase();

    normalized = normalized.replaceAll("ё", "е");
    normalized = normalized.replaceAll("î", "i");
    normalized = normalized.replaceAll("â", "a");
    normalized = normalized.replaceAll("ă", "a");
    normalized = normalized.replaceAll("ș", "s");
    normalized = normalized.replaceAll("ş", "s");
    normalized = normalized.replaceAll("ț", "t");
    normalized = normalized.replaceAll("ţ", "t");

    return normalized;
  }
}
