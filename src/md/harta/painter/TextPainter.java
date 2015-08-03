package md.harta.painter;

import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Shape;
import java.awt.font.GlyphVector;
import java.util.List;
import md.harta.drawer.AbstractDrawer;
import md.harta.geometry.Bounds;
import md.harta.geometry.Circle;
import md.harta.geometry.GeometryUtil;
import md.harta.geometry.Intersection;
import md.harta.geometry.Label;
import md.harta.geometry.Line;
import md.harta.geometry.XYPoint;
import md.harta.osm.Highway;
import md.harta.osm.OsmNode;
import md.harta.projector.AbstractProjector;
import md.harta.tile.TilePalette;

/**
 * Created by sergpank on 07.07.15.
 */
public class TextPainter extends AbstractPainter
{
  public TextPainter(AbstractProjector projector, Bounds bounds)
  {
    super(projector, bounds);
  }

  public void paintHighwayLabel(AbstractDrawer drawer, Label label)
  {
    Highway highway = label.getHighway();
    if (highway != null && highway.getName() != null)
    {
      drawer.setFillColor(TilePalette.FONT_COLOR);
      drawTiltString(drawer, label);
    }
  }

  private void drawTiltString(AbstractDrawer drawer, Label label)
  {
    Font font = new Font(Font.MONOSPACED, Font.PLAIN, 15);
    FontMetrics fontMetrics = drawer.getFontMetrics(font);

    String text = label.getText();
    int labelLength = label.getText().length();

    int height = fontMetrics.getHeight();
    // There is one pixel distance between characters
    int width = fontMetrics.charWidth('A') * labelLength + labelLength;

    // XYPoint center = getIntersectionPoint(label.getHighway(), label.getCenter());
    XYPoint roadStartPoint = getRoadStartPoint(label);
    int xShift = (int) roadStartPoint.getX();
    int yShift = (int) roadStartPoint.getY() + height / 2;
    double roadLength = GeometryUtil.getHighwayLength(label.getHighway(), projector);

    System.out.printf("%s - %d : %d\n", text, xShift, yShift);
    System.out.printf("Start at: %f : %f\n", roadStartPoint.getX(), roadStartPoint.getY());
    System.out.printf("Width = %d; Height = %d\n", width, height);

    Bounds bounds = label.getHighway().getBounds();
    XYPoint minXY = shiftPoint(projector.getXY(bounds.getMinLat(), bounds.getMinLon()));
    XYPoint maxXY = shiftPoint(projector.getXY(bounds.getMaxLat(), bounds.getMaxLon()));
    System.out.printf("Road bounding box = {(%s); (%s)}\n", minXY, maxXY);

    System.out.println("Road length = " + roadLength + "\n");

    GlyphVector glyphVector = font.createGlyphVector(drawer.getFontRenderContext(), text);

    int numGlyphs = glyphVector.getNumGlyphs();
    if (roadLength > labelLength)
    {
      for (int i = 0; i < numGlyphs; i++)
      {
        double curGlyphX = roadStartPoint.getX() + labelLength / numGlyphs * i + i;
        XYPoint curGlyphPoint = new XYPoint(curGlyphX, roadStartPoint.getY());
        Intersection intersection = getIntersectionPoint(label.getHighway(), curGlyphPoint, roadStartPoint);
        System.out.println(label.getText().charAt(i) + " -> " + intersection);
//        double angle = getIntersectionAngle(label.getHighway(), intersectionPoint);

        drawer.translate((int)intersection.getPoint().getX(), (int)intersection.getPoint().getY());
        drawer.rotate(intersection.getAngle());

        Shape outline = glyphVector.getGlyphOutline(i);
        drawer.fill(outline);

        drawer.rotate(-(intersection.getAngle()));
        drawer.translate(-(int)intersection.getPoint().getX(), -(int)intersection.getPoint().getY());
      }
    }
  }

  /**
   * Road start point is upper left of lower left point
   * Because road name is drawn from left to right
   */
  private XYPoint getRoadStartPoint(Label label)
  {
    List<OsmNode> nodes = label.getHighway().getNodes();
    OsmNode firstNode = nodes.get(0);
    OsmNode lastNode = nodes.get(nodes.size() - 1);

    if (firstNode.getLon() < lastNode.getLon())
    {
      return shiftPoint(projector.getXY(firstNode.getLat(), firstNode.getLon()));
    }
    else
    {
      return shiftPoint(projector.getXY(lastNode.getLat(), lastNode.getLon()));
    }
  }

  private double getIntersectionAngle(Highway highway, XYPoint intersectionPoint)
  {
    Bounds bounds = highway.getBounds();
    Line centerLine = GeometryUtil.getLine(
        new XYPoint(intersectionPoint.getX(), bounds.getyMin()),
        new XYPoint(intersectionPoint.getX(), bounds.getyMax()));
    XYPoint intersection = null;
    Line segment = null;
    for (int i = 1; i < highway.getNodes().size(); i++)
    {
      OsmNode startNode = highway.getNodes().get(i - 1);
      OsmNode endNode = highway.getNodes().get(i);
      XYPoint startPoint = shiftPoint(projector.getXY(startNode.getLat(), startNode.getLon()));
      XYPoint endPoint = shiftPoint(projector.getXY(endNode.getLat(), endNode.getLon()));
      segment = GeometryUtil.getLine(startPoint, endPoint);
      intersection = GeometryUtil.getLineIntersection(centerLine, segment, startPoint, endPoint, true);
      if (intersection != null)
      {
        break;
      }
    }
    if (intersection == null)
    {
//      throw new IllegalArgumentException("Center does not intersect the road");
      System.err.println("WARNING intersection is not found!");
      return 0;
    }
    return Math.atan((-segment.getA()) / segment.getB());
  }

  /**
   * @return Intersection of the highway with vertical point that intersect glyphPoint point
   */
  private Intersection getIntersectionPoint(Highway highway, XYPoint glyphPoint, XYPoint roadStartPoint)
  {
    /* TODO: вычислять точку пересечения следующи образом:
       1. Создать уравнение окружности центр которой находится в начале дороги а радиус равен расстоянию до глифа
       2. Вычислить точку пересечения окружности и дороги

       Это необходимо, из-за того что для сильно наклонённых дорог вертикаль из точки глифа не пересекается с дорогой,
       из-за чего вычислить угол наклона для некоторых глифов не представляется возможным.
    */
    Bounds bounds = highway.getBounds();

    XYPoint leftPoint = new XYPoint(glyphPoint.getX(), bounds.getyMin());
    XYPoint rightPoint = new XYPoint(glyphPoint.getX(), bounds.getyMax());
//    Line centerLine = GeometryUtil.getLine(leftPoint, rightPoint);
    Circle circle = GeometryUtil.getCircle(roadStartPoint, glyphPoint);
//    System.out.println("Radius = " + GeometryUtil.getDistanceBetweenPoints(roadStartPoint, glyphPoint));

    XYPoint intersectionPoint = null;
    double intersectionAngle = 0;
    for (int i = 1; i < highway.getNodes().size(); i++)
    {
      OsmNode startNode = highway.getNodes().get(i - 1);
      OsmNode endNode = highway.getNodes().get(i);
      XYPoint startPoint = shiftPoint(projector.getXY(startNode.getLat(), startNode.getLon()));
      XYPoint endPoint = shiftPoint(projector.getXY(endNode.getLat(), endNode.getLon()));
      Line segment = GeometryUtil.getLine(startPoint, endPoint);
      intersectionPoint = GeometryUtil.getLineCircleIntersection(segment, circle, startPoint, endPoint);
      if (intersectionPoint != null)
      {
        intersectionAngle = Math.atan((-segment.getA()) / segment.getB());
        break;
      }
    }
    if (intersectionPoint == null)
    {
//      throw new IllegalArgumentException("Center does not intersect the road");
      System.err.println("WARNING intersection is not found!");
      return new Intersection(new XYPoint(0, 0), 0);
    }
    return new Intersection(intersectionPoint, intersectionAngle);
  }
}
