package md.onemap.harta.export;

import md.onemap.harta.db.dao.NormalizedHighwayDao;
import md.onemap.harta.db.gis.entity.Way;
import md.onemap.harta.osm.NormalizedHighway;

import java.util.ArrayList;
import java.util.Collection;

public class HighwayNameNormalizer
{
  public void normalize(Collection<Way> highways) {
    Collection<NormalizedHighway> normalizedHighways = new ArrayList<>();

    for (Way h : highways)
    {
      NormalizedHighway nh = normalizeHighway(h);
      if (nh != null)
      {
        normalizedHighways.add(nh);
      }
    }

    new NormalizedHighwayDao().saveAll(normalizedHighways);
  }

  private NormalizedHighway normalizeHighway(Way h)
  {
    String name = h.getTags().get("name");
    if (name == null || name.isEmpty())
    {
      return null;
    }

    String nameRu = h.getTags().get("name:ru");
    String nameOld = h.getTags().get("old_name");
    String nameOldRu = h.getTags().get("old_name:ru");

    String normalizedName = normalizeName(name);
    String normalizedNameRu = normalizeName(nameRu);
    String normalizedNameOld = normalizeName(nameOldRu != null ? nameOldRu : nameOld);

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
