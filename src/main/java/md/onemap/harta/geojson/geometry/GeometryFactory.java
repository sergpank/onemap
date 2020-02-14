package md.onemap.harta.geojson.geometry;

import md.onemap.harta.geojson.FeatureType;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class GeometryFactory
{
  private static final Logger log = LogManager.getLogger();

//  public static Geometry createGeometry(String geometryText)
//  {
//    Geometry geometry = null;
//
//    FeatureType featureType = getFeatureType(featureText);
//
//    switch (featureType)
//    {
//      case POINT:
//      {
//        geometry = new Point();
//        ((Point) geometry).coordinates =
//      }
//      case LINESTRING:
//      {
//
//      }
//      case POLYGON:
//      {
//
//      }
//      default:
//      {
//        log.error("Feature type is not supported : {}", featureType);
//      }
//    }
//
//    return geometry;
//  }

  private static FeatureType getFeatureType(String featureText)
  {
    String featureString = featureText.substring(0, featureText.indexOf('('));
    log.debug("Found feature : {}", featureString);

    FeatureType featureType = FeatureType.valueOf(featureString);
    log.debug("Detected feature : {}", featureType);

    return featureType;
  }

}
