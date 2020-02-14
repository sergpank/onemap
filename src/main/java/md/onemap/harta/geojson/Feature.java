package md.onemap.harta.geojson;

import md.onemap.harta.geojson.geometry.Geometry;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Map;

public class Feature
{
  private static final Logger log = LogManager.getLogger();

  public String type = "Feature";
  public Geometry geometry;
  public Map<String, String> properties;

//  public Feature(String featureText)
//  {
//    this.geometry = GeometryFactory.createGeometry(featureText);
//  }
}
