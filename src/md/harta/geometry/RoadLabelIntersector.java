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
  private int charHeight;

  public RoadLabelIntersector(Bounds bounds, int charWidth, int charHeight)
  {
    this.bounds = bounds;
    this.charWidth = charWidth;
    this.charHeight = charHeight;
  }

  public List<Intersection> getIntersections(Highway highway, Label label, AbstractProjector projector)
  {
    double highwayLength = GeometryUtil.getHighwayLength(highway, projector);

    int labelWidth = label.getText().length() * charWidth + label.getText().length();
    System.out.println(label.getText());
    System.out.println("charWidth: " + charWidth);
    System.out.println("labelWidth: " + labelWidth);
    if (highwayLength < labelWidth)
    {
      System.err.printf("Road length (%f) < label length (%d)", highwayLength, labelWidth);
      return null;
    }
    List<Line> segments = highwayToLines(highway, projector);

    int repeats = (int) (Math.ceil(highwayLength / (labelWidth * 3)));
    double distance = (highwayLength - (labelWidth * repeats)) / (repeats + 1);

    List<Intersection> intersections = new ArrayList<>();
    for (int i = 0; i < repeats; i++)
    {
      double shift = distance + (labelWidth * i) + (distance * i);
      intersections.addAll(calculateIntersections(label, segments, shift));
    }
    return intersections;
  }

  private List<Intersection> calculateIntersections(Label label, List<Line> segments, double shift)
  {
    List<Intersection> intersections = new ArrayList<>();

    int segmentIndex = 0;

    for (int i = 0; i < label.getText().length(); i++)
    {
      Line line = segments.get(segmentIndex);

      XYPoint intersectionPoint = calcIntersectionPoint(line, shift);

      if (intersectionPoint.getX() > line.getRightPoint().getX())
      {
        if (intersections.size() == 0)
        {
          for (; segmentIndex < segments.size(); segmentIndex++)
          {
            line = segments.get(segmentIndex);
            double segmentLength = GeometryUtil.getDistanceBetweenPoints(line.getLeftPoint(), line.getRightPoint());
            if (segmentLength < shift)
            {
              shift -= segmentLength;
            }
            else
            {
              Line nextLine = segments.get(segmentIndex);
              intersectionPoint = calcIntersectionPoint(nextLine, shift);
              break;
            }
          }
        }
        else
        {
          XYPoint previousIntersection = intersections.get(intersections.size() - 1).getPoint();
          // Расстояние от последнего пересечения до конца предыдущего сегмента
          double rest = GeometryUtil.getDistanceBetweenPoints(previousIntersection, line.getRightPoint());
          shift = charWidth - rest;

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
      }

      shift += charWidth + 1;

      Intersection intersection = new Intersection(intersectionPoint, line.getSlope());
      intersections.add(intersection);
    }

    return intersections;
  }

  private XYPoint calcIntersectionPoint(Line line, double shift)
  {
    double dx = Math.cos(line.getSlope()) * shift;
    double dy = Math.sin(line.getSlope()) * shift;

    XYPoint intersection = new XYPoint(line.getLeftPoint().getX() + dx, line.getLeftPoint().getY() + dy);
    return intersection;
//    Line perpendicular = GeometryUtil.getPerpendicular(line, intersection);
//    return GeometryUtil.getLineCircleIntersection(perpendicular, new Circle(intersection, charHeight / 2))[1];
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
