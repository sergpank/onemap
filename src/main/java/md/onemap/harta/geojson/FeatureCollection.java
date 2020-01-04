package md.onemap.harta.geojson;

import java.util.ArrayList;
import java.util.List;

public class FeatureCollection
{
  public static final String type = "FeatureCollection";
  private List<Feature> features = new ArrayList<>();

  public void addFeature(Feature feature)
  {
    features.add(feature);
  }
}
