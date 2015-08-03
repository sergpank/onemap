package md.harta.util;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.CoordinateSequence;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.Polygon;
import com.vividsolutions.jts.geom.PrecisionModel;
import com.vividsolutions.jts.geom.impl.CoordinateArraySequence;
import com.vividsolutions.jts.operation.polygonize.Polygonizer;
import com.vividsolutions.jts.operation.union.CascadedPolygonUnion;
import java.util.Arrays;
import java.util.Collection;
import md.harta.geometry.XYPoint;

/**
 * Created by sergpank on 05.07.15.
 */
public class JtsUtil
{
  public static Polygon createPolygon(GeometryFactory factory, double[] xPoints, double[] yPoints)
  {
    Coordinate[] coordinates = new Coordinate[xPoints.length];
    for (int i = 0; i < xPoints.length; i++)
    {
      coordinates[i] = new Coordinate(xPoints[i], yPoints[i]);
    }
    return factory.createPolygon(coordinates);
  }

  public static LineString createLine(GeometryFactory geometryFactory, XYPoint ... points)
  {
    CoordinateSequence cs1 = createCoordinateSequence(points);
    return new LineString(cs1, geometryFactory);
  }

  public static CoordinateSequence createCoordinateSequence(XYPoint ... coordinates)
  {
    return new CoordinateArraySequence(toCoordinate(coordinates));
  }

  public static Coordinate[] toCoordinate(XYPoint ... xy)
  {
    Coordinate[] coordinates = new Coordinate[xy.length];
    for (int i = 0; i < xy.length; i++)
    {
      coordinates[i] = new Coordinate(xy[i].getX(), xy[i].getY());
    }
    return coordinates;
  }

  public static double[] getXPoints(Geometry g)
  {
    Coordinate[] coordinates = g.getCoordinates();
    double[] xPoints = new double[coordinates.length];
    for (int i = 0; i < coordinates.length; i++)
    {
      xPoints[i] = coordinates[i].x;
    }
    return xPoints;
  }

  public static double[] getYPoints(Geometry g)
  {
    Coordinate[] coordinates = g.getCoordinates();
    double[] yPoints = new double[coordinates.length];
    for (int i = 0; i < coordinates.length; i++)
    {
      yPoints[i] = coordinates[i].y;
    }
    return yPoints;
  }

  public static Geometry createCircle(double x, double y, double radius, GeometryFactory factory)
  {
    Coordinate[] coordinates = new Coordinate[25];
    for (int i = 0; i < 24; i++){
      int angle = 360 / 24 * i;
//      System.out.print(i + " : " + angle + " : ");
      double sin = Math.sin(Math.toRadians(angle));
      double cos = Math.cos(Math.toRadians(angle));
      double xi = radius * cos;
      double yi = radius * sin;

//      System.out.println(xi + "," + yi);
      coordinates[i] = new Coordinate(x + xi, y + yi);
    }
    coordinates[24] = coordinates[0];
    Polygon polygon = factory.createPolygon(coordinates);
    return polygon;
  }

  public static void main(String[] args)
  {
    GeometryFactory factory = new GeometryFactory(new PrecisionModel(PrecisionModel.FLOATING));

    Polygon polygon1 = createPolygon(
        factory,
        new double[]{10, 10, 12, 12, 10},
        new double[]{10, 20, 20, 10, 10});

    Polygon polygon2 = createPolygon(
        factory,
        new double[]{ 9, 21, 21,  9, 9},
        new double[]{11, 11, 13, 13, 11});

    Polygon polygon3 = createPolygon(
        factory,
        new double[]{20, 20, 18, 18, 20},
        new double[]{10, 20, 20, 10, 10});

    Polygon polygon4 = createPolygon(
        factory,
        new double[]{10, 20, 20, 10, 10},
        new double[]{20, 20, 18, 18, 20});

    Geometry union1 = CascadedPolygonUnion.union(Arrays.asList(new Geometry[]{polygon1, polygon2, polygon3, polygon4}));

    Polygonizer polygonizer = new Polygonizer();

    addLines(factory, polygon1, polygonizer);
    addLines(factory, polygon2, polygonizer);
    addLines(factory, polygon3, polygonizer);
    addLines(factory, polygon4, polygonizer);

    Collection polygons = polygonizer.getPolygons();
    Collection dangles = polygonizer.getInvalidRingLines();
    System.out.println("Dangles");
    for (Object dangle : dangles)
    {
      System.out.println(dangle);
    }

    for (Object polygon : polygons)
    {
      System.out.println("Polygon");
      for (Coordinate c : ((Polygon)polygon).getCoordinates())
      {
        System.out.println(c.x + "," + c.y);
      }
    }

//    JtsExperiments.scanGeometry(union1, factory);


//    System.out.println("Boundary");
//    JtsExperiments.scanGeometry(boundary, factory);
  }

  private static void addLines(GeometryFactory factory, Polygon polygon, Polygonizer polygonizer)
  {
    for (int i = 0; i < polygon.getCoordinates().length - 1; i++)
    {
      Coordinate c1 = polygon.getCoordinates()[i];
      Coordinate c2 = polygon.getCoordinates()[i + 1];
      LineString line = factory.createLineString(new Coordinate[]{c1, c2});
      polygonizer.add(line);
    }
  }
}
