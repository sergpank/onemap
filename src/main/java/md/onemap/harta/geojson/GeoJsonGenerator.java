package md.onemap.harta.geojson;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

public class GeoJsonGenerator
{
  /*
   * A GeoJSON object may represent:
   * a region of space (Geometry)
   * a spatially bounded entity (Feature)
   * a list of Features (FeatureCollection)
   *
   * Following geometry types are supported:
   * 1. Point
   * 2. LineString
   * 3. Polygon
   * 4. MultiPoint
   * 5. MultiLineString,
   * 6. MultiPolygon
   * 7. GeometryCollection
   */

  /*
   A GeoJSON FeatureCollection:

    {
       "type":"FeatureCollection",
       "features":
       [
          {
             "type":"Feature",
             "geometry":
             {
                "type":"Point",
                "coordinates":[102.0, 0.5]
             },
             "properties":
             {
                "prop0":"value0"
             }
          },
          {
             "type":"Feature",
             "geometry":
             {
                "type":"LineString",
                "coordinates":
                [
                   [102.0, 0.0],
                   [103.0, 1.0],
                   [104.0, 0.0],
                   [105.0, 1.0]
                ]
             },
             "properties":
             {
                "prop0":"value0",
                "prop1":0.0
             }
          },
          {
             "type":"Feature",
             "geometry":
             {
                "type":"Polygon",
                "coordinates":
                [
                   [
                      [100.0, 0.0],
                      [ 101.0, 0.0],
                      [101.0, 1.0],
                      [100.0, 1.0],
                      [100.0,0.0]
                   ]
                ]
             },
             "properties":
             {
                "prop0":"value0",
                "prop1":{"this":"that"}
             }
          }
       ]
    }
   */

//  26184655	highway	LINESTRING(28.8698675 46.9980298,28.8706374 46.9972598,28.8712621 46.9966336)
//  26184648	highway	LINESTRING(28.8706374 46.9972598,28.8714128 46.9975884)


  public static String generate(String ... features)
  {
    List<Feature> featureList = new ArrayList<>();

//    for (String featureText : features)
//    {
//      Feature feature = new Feature(featureText);
//      featureList.add(feature);
//    }
    FeatureCollection featureCollection  = new FeatureCollection();

    Gson gson = new Gson();
    String json = gson.toJson(featureCollection);

    System.out.println(json);

    return json;
  }

}
