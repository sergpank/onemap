package md.onemap.harta.geometry;

import junit.framework.TestCase;
import md.onemap.harta.db.gis.entity.Node;
import md.onemap.harta.osm.Highway;
import md.onemap.harta.projector.SimpleProjector;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sergpank on 11.08.15.
 */
public class RoadLabelIntersectorTest extends TestCase
{
  int charHeight = 10;

  @Test
  public void testDirectLineToSegmentsUp()
  {
    RoadLabelIntersector intersector = new RoadLabelIntersector(new BoundsXY(10, 10, 1000, 1000), "", 1);
    SimpleProjector projector = new SimpleProjector(1);

    List<Node> nodes = new ArrayList<>();
    nodes.add(new Node(1, 10, 10));
    nodes.add(new Node(2, 20, 20));
    nodes.add(new Node(3, 30, 30));

    Highway highway = new Highway(1l, "1", "1-ru", "old", "no_type", nodes);
    List<Line> lines = intersector.highwayToSegments(projector, highway.getNodes());

    assertEquals(2, lines.size());
    assertTrue(new Line(new XYPoint(180, 70), new XYPoint(190, 60)).isIdentical(lines.get(0)));
    assertTrue(new Line(new XYPoint(190, 60), new XYPoint(200, 50)).isIdentical(lines.get(1)));
  }

  @Test
  public void testDirectLineToSegmentsDown()
  {
    RoadLabelIntersector intersector = new RoadLabelIntersector(new BoundsXY(10, 10, 1000, 1000), "", 1);
    SimpleProjector projector = new SimpleProjector(1);

    List<Node> nodes = new ArrayList<>();
    nodes.add(new Node(1, 10, 30));
    nodes.add(new Node(2, 20, 20));
    nodes.add(new Node(3, 30, 10));

    Highway highway = new Highway(1l, "1", "1-ru", "old", "no_type", nodes);
    List<Line> lines = intersector.highwayToSegments(projector, highway.getNodes());

    assertEquals(2, lines.size());
    assertTrue(new Line(new XYPoint(190, 60), new XYPoint(200, 70)).isIdentical(lines.get(0)));
    assertTrue(new Line(new XYPoint(200, 70), new XYPoint(210, 80)).isIdentical(lines.get(1)));
  }

  @Test
  public void testReverseLineToSegmentsUp()
  {
    RoadLabelIntersector intersector = new RoadLabelIntersector(new BoundsXY(10, 10, 1000, 1000), "", 1);
    SimpleProjector projector = new SimpleProjector(1);

    List<Node> nodes = new ArrayList<>();
    nodes.add(new Node(1, 30, 30));
    nodes.add(new Node(2, 20, 20));
    nodes.add(new Node(3, 10, 10));

    Highway highway = new Highway(1l, "1", "1-ru", "old", "no_type", nodes);
    List<Line> lines = intersector.highwayToSegments(projector, highway.getNodes());

    assertEquals(2, lines.size());
    assertTrue(new Line(new XYPoint(180, 70), new XYPoint(190, 60)).isIdentical(lines.get(0)));
    assertTrue(new Line(new XYPoint(190, 60), new XYPoint(200, 50)).isIdentical(lines.get(1)));
  }

  @Test
  public void testReverseLineToSegmentsDown()
  {
    RoadLabelIntersector intersector = new RoadLabelIntersector(new BoundsXY(10, 10, 1000, 1000), "", 1);
    SimpleProjector projector = new SimpleProjector(1);

    List<Node> nodes = new ArrayList<>();
    nodes.add(new Node(1, 30, 10));
    nodes.add(new Node(2, 20, 20));
    nodes.add(new Node(3, 10, 30));

    Highway highway = new Highway(1l, "1", "1-ru", "old", "no_type", nodes);
    List<Line> lines = intersector.highwayToSegments(projector, highway.getNodes());

    assertEquals(2, lines.size());
    assertTrue(new Line(new XYPoint(190, 60), new XYPoint(200, 70)).isIdentical(lines.get(0)));
    assertTrue(new Line(new XYPoint(200, 70), new XYPoint(210, 80)).isIdentical(lines.get(1)));
  }

  @Test
  public void testHorizontalLineToSegments()
  {
    RoadLabelIntersector intersector = new RoadLabelIntersector(new BoundsXY(10, 10, 1000, 1000),  "", 1);
    SimpleProjector projector = new SimpleProjector(1);

    List<Node> nodes = new ArrayList<>();
    nodes.add(new Node(1, 10, 10));
    nodes.add(new Node(2, 20, 10));
    nodes.add(new Node(3, 30, 10));

    Highway highway = new Highway(1l, "1", "1-ru", "old", "no_type", nodes);
    List<Line> lines = intersector.highwayToSegments(projector, highway.getNodes());

    assertEquals(2, lines.size());
    assertTrue(new Line(new XYPoint(180, 70), new XYPoint(190, 70)).isIdentical(lines.get(0)));
    assertTrue(new Line(new XYPoint(190, 70), new XYPoint(200, 70)).isIdentical(lines.get(1)));
  }

  @Test
  public void testReverseHorizontalLineToSegments()
  {
    RoadLabelIntersector intersector = new RoadLabelIntersector(new BoundsXY(10, 10, 1000, 1000),  "", 1);
    SimpleProjector projector = new SimpleProjector(1);

    List<Node> nodes = new ArrayList<>();
    nodes.add(new Node(1, 30, 10));
    nodes.add(new Node(2, 20, 10));
    nodes.add(new Node(3, 10, 10));

    Highway highway = new Highway(1l, "1", "1-ru", "old", "no_type", nodes);
    List<Line> lines = intersector.highwayToSegments(projector, highway.getNodes());

    assertEquals(2, lines.size());
    assertTrue(new Line(new XYPoint(180, 70), new XYPoint(190, 70)).isIdentical(lines.get(0)));
    assertTrue(new Line(new XYPoint(190, 70), new XYPoint(200, 70)).isIdentical(lines.get(1)));
  }

  @Test
  public void testVerticalLineToSegments()
  {
    RoadLabelIntersector intersector = new RoadLabelIntersector(new BoundsXY(10, 10, 1000, 1000), "", 1);
    SimpleProjector projector = new SimpleProjector(1);

    List<Node> nodes = new ArrayList<>();
    nodes.add(new Node(1, 10, 10));
    nodes.add(new Node(2, 10, 20));
    nodes.add(new Node(3, 10, 30));

    Highway highway = new Highway(1l, "1", "1-ru", "old", "no_type", nodes);
    List<Line> lines = intersector.highwayToSegments(projector, highway.getNodes());

    assertEquals(2, lines.size());
    assertTrue(new Line(new XYPoint(180, 70), new XYPoint(180, 60)).isIdentical(lines.get(0)));
    assertTrue(new Line(new XYPoint(180, 60), new XYPoint(180, 50)).isIdentical(lines.get(1)));
  }

  @Test
  public void testReverseVerticalLineToSegments()
  {
    RoadLabelIntersector intersector = new RoadLabelIntersector(new BoundsXY(10, 10, 1000, 1000), "", 1);
    SimpleProjector projector = new SimpleProjector(1);

    List<Node> nodes = new ArrayList<>();
    nodes.add(new Node(1, 10, 30));
    nodes.add(new Node(2, 10, 20));
    nodes.add(new Node(3, 10, 10));

    Highway highway = new Highway(1l, "1", "1-ru", "old", "no_type", nodes);
    List<Line> lines = intersector.highwayToSegments(projector, highway.getNodes());

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

//  @Test
//  public void testIntersectionRoadShorterThanLabel() {
//    RoadLabelIntersector intersector = new RoadLabelIntersector(new BoundsXY(0, 0, 20, 20), Font.MONOSPACED, 15);
//    List<Node> nodes = Arrays.asList(
//        new Node(1l, 10, 10),
//        new Node(2l, 10.0001, 10.0001));
//    String roadName = "abcdefghijklmnopuvw";
//    Highway highway = new Highway(1l, roadName, "ru", "old", "test", nodes);
//    Label label = new Label(roadName, new XYPoint(10, 10), 10, 100, highway.getNodes());
//    AbstractProjector projector = new MercatorProjector(17);
//    List<Intersection> intersections = intersector.getIntersections(label, projector, highway.getNodes());
//    assertEquals(0, intersections.size());
//  }

//  @Test
//  public void testIntersectionDiagonal_1() {
//    AbstractProjector projector = new MercatorProjector(17);
//
//    List<Node> nodes = Arrays.asList(
//        new Node(1l, 10, 10),
//        new Node(2l, 10.001, 10.0005));
//
//    String roadName = "ABCD";
//    Highway highway = new Highway(1l, roadName, "ru", "old", "test", nodes);
//
//    Font font = new Font(Font.MONOSPACED, Font.PLAIN, 15);
//    new Graphics2D()
//    float lineHeight = fontMetrics.getLineHeight();
//    float lineWidth = fontMetrics.computeStringWidth(roadName) + roadName.length();
//
//    Label label = new Label(roadName, new XYPoint(10, 10), lineHeight, lineWidth, highway.getNodes());
//
//    RoadLabelIntersector intersector = new RoadLabelIntersector(new BoundsXY(0, 0, 200, 200), Font.MONOSPACED, 15);
//    List<Intersection> intersections = intersector.getIntersections(label, projector, highway.getNodes());
//
//    intersections.forEach(System.out::println);
//
//    assertEquals(4, intersections.size());
//    assertTrue(isPrettySame(new Intersection(new XYPoint(1.770930983167526E7, 1.5840374675626924E7), -0.46979950686796507), intersections.get(0)));
//    assertTrue(isPrettySame(new Intersection(new XYPoint(1.7709319639925044E7, 1.5840369695844172E7), -0.46979950686796507), intersections.get(1)));
//    assertTrue(isPrettySame(new Intersection(new XYPoint(1.770932855651575E7, 1.5840365168768942E7), -0.46979950686796507), intersections.get(2)));
//    assertTrue(isPrettySame(new Intersection(new XYPoint(1.770933836476553E7, 1.584036018898619E7), -0.46979950686796507), intersections.get(3)));
//  }

  private boolean isPrettySame(Intersection expected, Intersection actual)
  {
    assertEquals(expected.getPoint().getX(), actual.getPoint().getX(), 1E-5);
    assertEquals(expected.getPoint().getY(), actual.getPoint().getY(), 1E-5);
    assertEquals(expected.getAngle(), actual.getAngle(), 1E-5);

    return true;
  }
}