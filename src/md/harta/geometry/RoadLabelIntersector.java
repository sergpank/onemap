package md.harta.geometry;

import java.util.ArrayList;
import java.util.List;
import md.harta.osm.Highway;
import md.harta.osm.OsmNode;
import md.harta.projector.AbstractProjector;

/**
 * Created by sergpank on 11.08.15.
 */
public class RoadLabelIntersector
{
  private Bounds bounds;
  private int charWidth;

  public RoadLabelIntersector(Bounds bounds, int charWidth)
  {
    this.bounds = bounds;
    this.charWidth = charWidth;
  }

  public List<Intersection> getIntersections(Highway highway, Label label, AbstractProjector projector)
  {
    double highwayLength = GeometryUtil.getHighwayLength(highway, projector);

    int labelWidth = label.getText().length() * charWidth;
    System.out.println(label.getText());
    System.out.println("charWidth: " + charWidth);
    System.out.println("labelWidth: " + labelWidth);
    if (highwayLength < labelWidth)
    {
      System.err.printf("Road length (%f) < label length (%f)", highwayLength, labelWidth);
      return null;
    }
    List<Line> segments = highwayToLines(highway, projector);
    List<Intersection> intersections = calculateIntersections(label, segments);
    return intersections;
  }

  private List<Intersection> calculateIntersections(Label label, List<Line> segments)
  {
    List<Intersection> intersections = new ArrayList<>();

    int segmentIndex = 0;
    double shift = 0;

    for (int i = 0; i < label.getText().length(); i++)
    {
      Line line = segments.get(segmentIndex);

      XYPoint intersectionPoint = calcIntersectionPoint(line, shift);

      if (intersectionPoint.getX() > line.getRightPoint().getX())
      {
        XYPoint previousIntersection = intersections.get(intersections.size() - 1).getPoint();
        double distanceFromThePreviousIntersectionToTheEndOfPreviousSegment = GeometryUtil.getDistanceBetweenPoints(previousIntersection, line.getRightPoint());
        shift = charWidth - distanceFromThePreviousIntersectionToTheEndOfPreviousSegment;

        ++segmentIndex;
        if (segmentIndex < segments.size())
        {
          Line nextLine = segments.get(segmentIndex);
          intersectionPoint = calcIntersectionPoint(nextLine, shift);
        }
        else
        {
          intersectionPoint = new XYPoint(0, 0);
          --segmentIndex;
        }
      }

      shift += charWidth;

      Intersection intersection = new Intersection(intersectionPoint, line.getSlope());
//      System.out.printf("%s :: %.2f:%.2f ---> {%.2f;%.2f - %.2f;%.2f}\n", label.getText().charAt(i),
//          intersection.getPoint().getX(), intersection.getPoint().getY(),
//          line.getLeftPoint().getX(), line.getLeftPoint().getY(),
//          line.getRightPoint().getX(), line.getRightPoint().getY());
      intersections.add(intersection);
    }

    return intersections;
  }

  private XYPoint calcIntersectionPoint(Line line, double shift)
  {
    double dx = Math.cos(line.getSlope()) * shift;
    double dy = Math.sin(line.getSlope()) * shift;

    return new XYPoint(line.getLeftPoint().getX() + dx, line.getLeftPoint().getY() + dy);
  }

  protected List<Line> highwayToLines(Highway highway, AbstractProjector projector)
  {
    int numNodes = highway.getNodes().size();
    OsmNode firstNode = highway.getNodes().get(0);
    OsmNode lastNode = highway.getNodes().get(numNodes - 1);

    if (firstNode.getLon() < lastNode.getLon())
    {
      return getSegmentsDirect(highway.getNodes(), projector);
    }
    else if (firstNode.getLon() > lastNode.getLon())
    {
      return getSegmentsReverse(highway.getNodes(), projector);
    }
    else
    {
      // In this case this is a vertical line, label should be written from bottom to top
      if (firstNode.getLat() < lastNode.getLat())
      {
        return getSegmentsDirect(highway.getNodes(), projector);
      }
      else
      {
        return getSegmentsReverse(highway.getNodes(), projector);
      }
    }
  }

  protected XYPoint shiftPoint(XYPoint point)
  {
    return new XYPoint(point.getX() - bounds.getxMin(), point.getY() - bounds.getyMin());
  }

  private List<Line> getSegmentsDirect(List<OsmNode> nodes, AbstractProjector projector)
  {
    ArrayList<Line> segments = new ArrayList<>();
    int step = 1;
    for (int i = 0; i < nodes.size() - 1; i++)
    {
      addSegment(nodes, projector, segments, step, i);
    }
    return segments;
  }

  private List<Line> getSegmentsReverse(List<OsmNode> nodes, AbstractProjector projector)
  {
    ArrayList<Line> segments = new ArrayList<>();
    int step = -1;
    for (int i = nodes.size() - 1; i > 0; i--)
    {
      addSegment(nodes, projector, segments, step, i);
    }
    return segments;
  }

  private void addSegment(List<OsmNode> nodes, AbstractProjector projector, ArrayList<Line> segments, int step, int i)
  {
    OsmNode nodeA = nodes.get(i);
    OsmNode nodeB = nodes.get(i + step);
    XYPoint pointA = shiftPoint(projector.getXY(nodeA.getLat(), nodeA.getLon()));
    XYPoint pointB = shiftPoint(projector.getXY(nodeB.getLat(), nodeB.getLon()));
    segments.add(new Line(pointA, pointB));
  }
}
