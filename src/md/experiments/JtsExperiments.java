package md.experiments;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.CoordinateSequence;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryCollection;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.LinearRing;
import com.vividsolutions.jts.geom.MultiLineString;
import com.vividsolutions.jts.geom.MultiPolygon;
import com.vividsolutions.jts.geom.Polygon;
import com.vividsolutions.jts.geom.PrecisionModel;
import com.vividsolutions.jts.geom.impl.CoordinateArraySequence;
import com.vividsolutions.jts.operation.buffer.BufferParameters;

/**
 * Created by sergpank on 04.07.15.
 */
public class JtsExperiments
{
  public static void main(String[] args)
  {
    GeometryFactory factory = new GeometryFactory(new PrecisionModel(PrecisionModel.FLOATING));

    LineString ls1 = createLine(factory, new Coordinate(10, 10), new Coordinate(20, 20));
    LineString ls2 = createLine(factory, new Coordinate(20, 10), new Coordinate(10, 20));
    LineString ls3 = createLine(factory, new Coordinate(30, 10), new Coordinate(30, 30));

    GeometryCollection gc = factory.createGeometryCollection(new Geometry[]{ls1, ls2, ls3});
    Geometry buffer = gc.buffer(5, 4, BufferParameters.CAP_ROUND);

    scanGeometry(buffer, factory);
  }

  public static void scanGeometry(Geometry g, GeometryFactory factory)
  {
    String type = g.getGeometryType();
    if (type == new MultiPolygon(null, factory).getGeometryType()
        || type == new MultiLineString(null, factory).getGeometryType())
    {
      for (int i = 0; i < g.getNumGeometries(); i++)
      {
        scanGeometry(g.getGeometryN(i), factory);
      }
    }
    else if (type == new Polygon(null, null, factory).getGeometryType()
        || type == new LineString(null, factory).getGeometryType()
        || type == new LinearRing(null, factory).getGeometryType())
    {
      System.out.println(type + " points:");
      for (Coordinate c : g.getCoordinates())
      {
        System.out.printf("%f,%f\n", c.x, c.y);
      }
      System.out.println();
    }
    else
    {
      throw new IllegalArgumentException("Unsupported geometry type: " + type);
    }
  }

  private static LineString createLine(GeometryFactory geometryFactory, Coordinate ... coordinates)
  {
    CoordinateSequence cs1 = createCoordinateSequence(coordinates);
    return new LineString(cs1, geometryFactory);
  }

  private static CoordinateSequence createCoordinateSequence(Coordinate ... coordinates)
  {
    return new CoordinateArraySequence(coordinates);
  }
}
