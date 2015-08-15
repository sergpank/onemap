package md.harta.geometry;

import com.sun.javafx.tk.Toolkit;
import java.util.ArrayList;
import java.util.List;
import javafx.scene.text.Font;
import junit.framework.TestCase;
import md.harta.osm.Highway;
import md.harta.osm.OsmNode;
import md.harta.projector.SimpleProjector;
import md.harta.tile.TilePalette;
import org.junit.Test;

/**
 * Created by sergpank on 11.08.15.
 */
public class RoadLabelIntersectorTest extends TestCase
{
  @Test
  public void testDirectLineToSegments()
  {
    RoadLabelIntersector intersector = new RoadLabelIntersector(new Bounds(10, 10, 1000, 1000), 5);
    SimpleProjector projector = new SimpleProjector(1);

    List<OsmNode> nodes = new ArrayList<>();
    nodes.add(new OsmNode(1, 10, 10));
    nodes.add(new OsmNode(2, 20, 20));
    nodes.add(new OsmNode(3, 30, 30));

    Highway highway = new Highway(1l, "1", "no_type", nodes, projector);
    List<Line> lines = intersector.highwayToLines(highway, projector);

    assertEquals(2, lines.size());
    assertTrue(GeometryUtil.getLine(new XYPoint(10, 10), new XYPoint(20, 20)).isIdentical(lines.get(0)));
    assertTrue(GeometryUtil.getLine(new XYPoint(20, 20), new XYPoint(30, 30)).isIdentical(lines.get(1)));
  }

  @Test
  public void testReverseLineToSegments()
  {
    RoadLabelIntersector intersector = new RoadLabelIntersector(new Bounds(10, 10, 1000, 1000), 5);
    SimpleProjector projector = new SimpleProjector(1);

    List<OsmNode> nodes = new ArrayList<>();
    nodes.add(new OsmNode(1, 30, 30));
    nodes.add(new OsmNode(2, 20, 20));
    nodes.add(new OsmNode(3, 10, 10));

    Highway highway = new Highway(1l, "1", "no_type", nodes, projector);
    List<Line> lines = intersector.highwayToLines(highway, projector);

    assertEquals(2, lines.size());
    assertTrue(GeometryUtil.getLine(new XYPoint(10, 10), new XYPoint(20, 20)).isIdentical(lines.get(0)));
    assertTrue(GeometryUtil.getLine(new XYPoint(20, 20), new XYPoint(30, 30)).isIdentical(lines.get(1)));
  }

  @Test
  public void testHorizontalLineToSegments()
  {
    RoadLabelIntersector intersector = new RoadLabelIntersector(new Bounds(10, 10, 1000, 1000), 5);
    SimpleProjector projector = new SimpleProjector(1);

    List<OsmNode> nodes = new ArrayList<>();
    nodes.add(new OsmNode(1, 10, 10));
    nodes.add(new OsmNode(2, 10, 20));
    nodes.add(new OsmNode(3, 10, 30));

    Highway highway = new Highway(1l, "1", "no_type", nodes, projector);
    List<Line> lines = intersector.highwayToLines(highway, projector);

    assertEquals(2, lines.size());
    assertTrue(GeometryUtil.getLine(new XYPoint(10, 10), new XYPoint(20, 10)).isIdentical(lines.get(0)));
    assertTrue(GeometryUtil.getLine(new XYPoint(20, 10), new XYPoint(30, 10)).isIdentical(lines.get(1)));
  }

  @Test
  public void testVerticalLineToSegments()
  {
    RoadLabelIntersector intersector = new RoadLabelIntersector(new Bounds(10, 10, 1000, 1000), 5);
    SimpleProjector projector = new SimpleProjector(1);

    List<OsmNode> nodes = new ArrayList<>();
    nodes.add(new OsmNode(1, 10, 10));
    nodes.add(new OsmNode(2, 20, 10));
    nodes.add(new OsmNode(3, 30, 10));

    Highway highway = new Highway(1l, "1", "no_type", nodes, projector);
    List<Line> lines = intersector.highwayToLines(highway, projector);

    assertEquals(2, lines.size());
    assertTrue(GeometryUtil.getLine(new XYPoint(10, 10), new XYPoint(10, 20)).isIdentical(lines.get(0)));
    assertTrue(GeometryUtil.getLine(new XYPoint(10, 20), new XYPoint(10, 30)).isIdentical(lines.get(1)));
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

    double dx = Math.cos(slope) * 10;
    double dy = Math.sin(slope) * 10;

    assertEquals(0, dx, 0.00001);
    assertEquals(10, dy, 0.00001);
  }

  @Test
  public void testIntersectionPointsDiagonalUp()
  {
    RoadLabelIntersector intersector = new RoadLabelIntersector(new Bounds(10, 10, 1000, 1000), 5);
    SimpleProjector projector = new SimpleProjector(1);

    List<OsmNode> nodes = new ArrayList<>();
    nodes.add(new OsmNode(1, 10, 10));
    nodes.add(new OsmNode(2, 20, 20));
    nodes.add(new OsmNode(3, 30, 30));

    Highway highway = new Highway(1l, "1", "no_type", nodes, projector);
    Label label = new Label("aaa", new XYPoint(100, 100), TilePalette.FONT_NAME, 12);

    List<Intersection> intersections = intersector.getIntersections(highway, label, projector);

    for (Intersection i : intersections)
    {
      System.out.println(i);
    }
  }

  @Test
  public void testIntersectionPointsDiagonalDown()
  {
    RoadLabelIntersector intersector = new RoadLabelIntersector(new Bounds(10, 10, 1000, 1000), 5);
    SimpleProjector projector = new SimpleProjector(1);

    List<OsmNode> nodes = new ArrayList<>();
    nodes.add(new OsmNode(1, 30, 10));
    nodes.add(new OsmNode(2, 20, 20));
    nodes.add(new OsmNode(3, 10, 30));

    Highway highway = new Highway(1l, "1", "no_type", nodes, projector);
    Label label = new Label("aaa", new XYPoint(100, 100), TilePalette.FONT_NAME, 12);

    List<Intersection> intersections = intersector.getIntersections(highway, label, projector);

    for (Intersection i : intersections)
    {
      System.out.println(i);
    }
  }

  @Test
  public void testIntersectionPointsHorizontal()
  {
    RoadLabelIntersector intersector = new RoadLabelIntersector(new Bounds(10, 10, 1000, 1000), 5);
    SimpleProjector projector = new SimpleProjector(1);

    List<OsmNode> nodes = new ArrayList<>();
    nodes.add(new OsmNode(1, 10, 10));
    nodes.add(new OsmNode(2, 10, 20));
    nodes.add(new OsmNode(3, 10, 40));

    Highway highway = new Highway(1l, "1", "no_type", nodes, projector);
    Label label = new Label("aaa", new XYPoint(100, 100), TilePalette.FONT_NAME, 12);

    List<Intersection> intersections = intersector.getIntersections(highway, label, projector);

    for (Intersection i : intersections)
    {
      System.out.println(i);
    }
  }

  @Test
  public void testIntersectionPointsHorizontalReverse()
  {
    int symbolWidth = (int)Math.ceil(getSymbolWidth(12, TilePalette.FONT_NAME));
    RoadLabelIntersector intersector = new RoadLabelIntersector(new Bounds(10, 10, 1000, 1000), symbolWidth);
    SimpleProjector projector = new SimpleProjector(1);

    List<OsmNode> nodes = new ArrayList<>();
    nodes.add(new OsmNode(1, 10, 40));
    nodes.add(new OsmNode(2, 10, 20));
    nodes.add(new OsmNode(3, 10, 10));

    Highway highway = new Highway(1l, "1", "no_type", nodes, projector);
    Label label = new Label("aaa", new XYPoint(100, 100), TilePalette.FONT_NAME, 12);

    List<Intersection> actual = intersector.getIntersections(highway, label, projector);


    XYPoint startPoint = projector.getXY(10, 10);

    List<Intersection> expected = new ArrayList<>();
    expected.add(new Intersection(startPoint, 0));
    expected.add(new Intersection(new XYPoint(startPoint.getX() + symbolWidth, startPoint.getY()), 0));
    expected.add(new Intersection(new XYPoint(startPoint.getX() + symbolWidth * 2, startPoint.getY()), 0));

    for (int i = 0; i < actual.size(); i++)
    {
      assertEquals(expected.get(i), actual.get(i));
    }
  }

  @Test
  public void testIntersectionPointsVertical()
  {
    RoadLabelIntersector intersector = new RoadLabelIntersector(new Bounds(10, 10, 1000, 1000), 5);
    SimpleProjector projector = new SimpleProjector(1);

    List<OsmNode> nodes = new ArrayList<>();
    nodes.add(new OsmNode(1, 10, 10));
    nodes.add(new OsmNode(2, 20, 10));
    nodes.add(new OsmNode(3, 40, 10));

    Highway highway = new Highway(1l, "1", "no_type", nodes, projector);
    int fontSize = 12;
    Label label = new Label("aaa", new XYPoint(100, 100), TilePalette.FONT_NAME, fontSize);

    List<Intersection> actual = intersector.getIntersections(highway, label, projector);

    double symbolWidth = getSymbolWidth(fontSize, TilePalette.FONT_NAME);
    XYPoint startPoint = projector.getXY(10, 10);

    List<Intersection> expected = new ArrayList<>();
    expected.add(new Intersection(startPoint, 0));
    expected.add(new Intersection(new XYPoint(startPoint.getX() + symbolWidth, startPoint.getY()), 0));
    expected.add(new Intersection(new XYPoint(startPoint.getX() + symbolWidth * 2, startPoint.getY()), 0));

    for (int i = 0; i < actual.size(); i++)
    {
      assertEquals(expected.get(i), actual.get(i));
    }
  }

  @Test
  public void testIntersectionPointsVerticalReverse()
  {
    RoadLabelIntersector intersector = new RoadLabelIntersector(new Bounds(10, 10, 1000, 1000), 5);
    SimpleProjector projector = new SimpleProjector(1);

    List<OsmNode> nodes = new ArrayList<>();
    nodes.add(new OsmNode(1, 40, 10));
    nodes.add(new OsmNode(2, 20, 10));
    nodes.add(new OsmNode(3, 10, 10));

    Highway highway = new Highway(1l, "1", "no_type", nodes, projector);
    Label label = new Label("aaa", new XYPoint(100, 100), TilePalette.FONT_NAME, 12);

    List<Intersection> intersections = intersector.getIntersections(highway, label, projector);

    for (Intersection i : intersections)
    {
      System.out.println(i);
    }
  }

  private double getSymbolWidth(int fontSize, String fontName)
  {
    Font font = new Font(fontName, fontSize);
    return Toolkit.getToolkit().getFontLoader().getFontMetrics(font).computeStringWidth("A");
  }
}