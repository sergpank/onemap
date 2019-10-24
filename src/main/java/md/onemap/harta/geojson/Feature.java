package md.onemap.harta.geojson;

import md.onemap.harta.geojson.geometry.Geometry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public class Feature
{
  private static final Logger log = LoggerFactory.getLogger(Feature.class);

  public String type = "Feature";
  public Geometry geometry;
  public Map<String, String> properties;

//  public Feature(String featureText)
//  {
//    this.geometry = GeometryFactory.createGeometry(featureText);
//  }
}
