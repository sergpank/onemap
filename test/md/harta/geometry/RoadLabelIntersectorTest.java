package md.harta.geometry;

import junit.framework.TestCase;
import md.harta.osm.Highway;
import md.harta.osm.OsmNode;
import md.harta.projector.SimpleProjector;
import md.harta.tile.TilePalette;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sergpank on 11.08.15.
 */
public class RoadLabelIntersectorTest extends TestCase
{
  int charHeight = 10;

//  @Test
//  public void testGreekStreetOdessa()
//  {
//    BoundsLatLon bounds = new BoundsLatLon(3.9284041629477546E7, 2.374463388867807E7, 3.928453098358557E7, 2.374468367487468E7);
//    new Label("Грецька вулиця", )
//    RoadLabelIntersector intersector = new RoadLabelIntersector(bounds, );
//
//
//    List<Intersection> intersections = intersector.getIntersections();
//  }

  @Test
  public void testDirectLineToSegments()
  {
    RoadLabelIntersector intersector = new RoadLabelIntersector(new BoundsXY(10, 10, 1000, 1000), 5, charHeight);
    SimpleProjector projector = new SimpleProjector(1);

    List<OsmNode> nodes = new ArrayList<>();
    nodes.add(new OsmNode(1, 10, 10));
    nodes.add(new OsmNode(2, 20, 20));
    nodes.add(new OsmNode(3, 30, 30));

    Highway highway = new Highway(1l, "1", "no_type", nodes);
    List<Line> lines = intersector.highwayToLines(highway, projector);

    assertEquals(2, lines.size());
    assertTrue(new Line(new XYPoint(180, 70), new XYPoint(190, 60)).isIdentical(lines.get(0)));
    assertTrue(new Line(new XYPoint(190, 60), new XYPoint(200, 50)).isIdentical(lines.get(1)));
  }

  @Test
  public void testReverseLineToSegments()
  {
    RoadLabelIntersector intersector = new RoadLabelIntersector(new BoundsXY(10, 10, 1000, 1000), 5, charHeight);
    SimpleProjector projector = new SimpleProjector(1);

    List<OsmNode> nodes = new ArrayList<>();
    nodes.add(new OsmNode(1, 30, 30));
    nodes.add(new OsmNode(2, 20, 20));
    nodes.add(new OsmNode(3, 10, 10));

    Highway highway = new Highway(1l, "1", "no_type", nodes);
    List<Line> lines = intersector.highwayToLines(highway, projector);

    assertEquals(2, lines.size());
    assertTrue(new Line(new XYPoint(180, 70), new XYPoint(190, 60)).isIdentical(lines.get(0)));
    assertTrue(new Line(new XYPoint(190, 60), new XYPoint(200, 50)).isIdentical(lines.get(1)));
  }

  @Test
  public void testHorizontalLineToSegments()
  {
    RoadLabelIntersector intersector = new RoadLabelIntersector(new BoundsXY(10, 10, 1000, 1000), 5, charHeight);
    SimpleProjector projector = new SimpleProjector(1);

    List<OsmNode> nodes = new ArrayList<>();
    nodes.add(new OsmNode(1, 10, 10));
    nodes.add(new OsmNode(2, 10, 20));
    nodes.add(new OsmNode(3, 10, 30));

    Highway highway = new Highway(1l, "1", "no_type", nodes);
    List<Line> lines = intersector.highwayToLines(highway, projector);

    assertEquals(2, lines.size());
    assertTrue(new Line(new XYPoint(180, 70), new XYPoint(190, 70)).isIdentical(lines.get(0)));
    assertTrue(new Line(new XYPoint(190, 70), new XYPoint(200, 70)).isIdentical(lines.get(1)));
  }

  @Test
  public void testVerticalLineToSegments()
  {
    RoadLabelIntersector intersector = new RoadLabelIntersector(new BoundsXY(10, 10, 1000, 1000), 5, charHeight);
    SimpleProjector projector = new SimpleProjector(1);

    List<OsmNode> nodes = new ArrayList<>();
    nodes.add(new OsmNode(1, 10, 10));
    nodes.add(new OsmNode(2, 20, 10));
    nodes.add(new OsmNode(3, 30, 10));

    Highway highway = new Highway(1l, "1", "no_type", nodes);
    List<Line> lines = intersector.highwayToLines(highway, projector);

    assertEquals(2, lines.size());
    assertTrue(new Line(new XYPoint(180, 70), new XYPoint(180, 60)).isIdentical(lines.get(0)));
    assertTrue(new Line(new XYPoint(180, 60), new XYPoint(180, 50)).isIdentical(lines.get(1)));
  }
  
  @Test
  public void testdxdy45()
  {
    Line line = new Line(new XYPoint(10, 10), new XYPoint(20, 20));
    double slope = line.getSlope();
    double dx = Math.cos(slope) * 10;
    double dy = Math.sin(slope) * 10;

    assertEquals(10.0 / Math.sqrt(2), dx, 0.00001);
    assertEquals(10.0 / Math.sqrt(2), dy, 0.00001);
  }

  @Test
  public void testdxdy60()
  {
    Line line = new Line(new XYPoint(10, 10), new XYPoint(20, 10 + 10 * Math.sqrt(3)));
    double slope = line.getSlope();

    double dx = Math.cos(slope) * 10;
    double dy = Math.sin(slope) * 10;

    assertEquals(5, dx, 0.00001);
    assertEquals(5 * Math.sqrt(3), dy, 0.00001);
  }

  @Test
  public void testdxdy30()
  {
    Line line = new Line(new XYPoint(10, 10), new XYPoint(10 + 10 * Math.sqrt(3), 20));
    double slope = line.getSlope();

    double dx = Math.cos(slope) * 10;
    double dy = Math.sin(slope) * 10;

    assertEquals(5 * Math.sqrt(3), dx, 0.00001);
    assertEquals(5, dy, 0.00001);
  }

  @Test
  public void testdxdy0()
  {
    Line line = new Line(new XYPoint(10, 10), new XYPoint(20, 10));
    double slope = line.getSlope();

    double dx = Math.cos(slope) * 10;
    double dy = Math.sin(slope) * 10;

    assertEquals(10, dx, 0.00001);
    assertEquals(0, dy, 0.00001);
  }

  @Test
  public void testdxdy90()
  {
    Line line = new Line(new XYPoint(10, 10), new XYPoint(10, 20));
    double slope = line.getSlope();

    double dx = Math.cos(slope);
    double dy = Math.sin(slope);

    assertEquals(0, dx, 0.00001);
    assertEquals(1, dy, 0.00001);
  }

  @Test
  public void testIntersectionDiagonalUp2() {
    Line line = new Line(new XYPoint(170, 100), new XYPoint(190, 80));
    Circle circle = new Circle(new XYPoint(180, 90), 5 * Math.sqrt(2));
    XYPoint[] intersection = GeometryUtil.getLineCircleIntersection(line, circle);

    assertEquals(175, intersection[0].getX(), 0.001);
    assertEquals(95, intersection[0].getY(), 0.001);

    assertEquals(185, intersection[1].getX(), 0.001);
    assertEquals(85, intersection[1].getY(), 0.001);
  }

  @Test
  public void testIntersectionDiagonalDown() {
    Line line = new Line(new XYPoint(170, 80), new XYPoint(190, 100));
    Circle circle = new Circle(new XYPoint(180, 90), 5 * Math.sqrt(2));
    XYPoint[] intersection = GeometryUtil.getLineCircleIntersection(line, circle);

    assertEquals(175, intersection[0].getX(), 0.001);
    assertEquals(85, intersection[0].getY(), 0.001);

    assertEquals(185, intersection[1].getX(), 0.001);
    assertEquals(95, intersection[1].getY(), 0.001);
  }

  public void testIntersectionHorizontal() {
    Line line = new Line(new XYPoint(170, 90), new XYPoint(190, 90));
    Circle circle = new Circle(new XYPoint(180, 90), 5.0);
    XYPoint[] intersection = GeometryUtil.getLineCircleIntersection(line, circle);

    assertEquals(175, intersection[0].getX(), 0.001);
    assertEquals(90, intersection[0].getY(), 0.001);

    assertEquals(185, intersection[1].getX(), 0.001);
    assertEquals(90, intersection[1].getY(), 0.001);
  }

  public void testIntersectionVertical() {
    Line line = new Line(new XYPoint(180, 70), new XYPoint(180, 100));
    Circle circle = new Circle(new XYPoint(180, 90), 5.0);
    XYPoint[] intersection = GeometryUtil.getLineCircleIntersection(line, circle);

    assertEquals(180, intersection[0].getX(), 0.001);
    assertEquals(85, intersection[0].getY(), 0.001);

    assertEquals(180, intersection[1].getX(), 0.001);
    assertEquals(95, intersection[1].getY(), 0.001);
  }

  @Test
  public void testIntersectionPointsDiagonalDown()
  {
    RoadLabelIntersector intersector = new RoadLabelIntersector(new BoundsXY(10, 10, 1000, 1000), 5, charHeight);
    SimpleProjector projector = new SimpleProjector(1);

    List<OsmNode> nodes = new ArrayList<>();
    nodes.add(new OsmNode(1, 30, 10));
    nodes.add(new OsmNode(2, 20, 20));
    nodes.add(new OsmNode(3, 10, 30));

    Highway highway = new Highway(1l, "1", "no_type", nodes);
    Label label = new Label("aaa", new XYPoint(200, 70), TilePalette.FONT_NAME, 12);

    List<Intersection> intersections = intersector.getIntersections(highway, label, projector);

    for (Intersection i : intersections)
    {
      System.out.println(i);
    }
  }
}