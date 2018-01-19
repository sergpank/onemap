package md.onemap.harta.geometry;

import md.onemap.harta.osm.Highway;
import md.onemap.harta.osm.OsmNode;
import md.onemap.harta.projector.AbstractProjector;
import md.onemap.harta.util.TextUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sergpank on 11.08.15.
 */
public class RoadLabelIntersector
{
  private static final Logger LOG = LoggerFactory.getLogger(RoadLabelIntersector.class);

  private BoundsXY bounds;
  private String fontName;
  private int fontSize;

  /**
   * Constructor
   * @param bounds Bounds of area that is being rendered (in pixels)
   */
  public RoadLabelIntersector(BoundsXY bounds, String fontName, int fontSize)
  {
    this.bounds = bounds;
    this.fontName = fontName;
    this.fontSize = fontSize;
  }

  public List<Intersection> getIntersections(Highway highway, Label label, AbstractProjector projector)
  {
    List<Intersection> intersections = new ArrayList<>();

    List<Line> segments = highwayToSegments(highway, projector);

    for (Line segment : segments) {
      double segLength = GeometryUtil.getDistanceBetweenPoints(segment.getLeftPoint(), segment.getRightPoint());
      if (segLength >= label.getWidth()) {
        int repeats = (int) (Math.ceil(segLength / (label.getWidth() * 3)));
        double offset = (segLength - (label.getWidth() * repeats)) / (repeats + 1);

        for (int i = 0; i < repeats; i++) {
          double shift = offset + (label.getWidth() * i) + (offset * i);
          intersections.addAll(calculateIntersections(label, segment, shift));
        }
      }
    }
    return intersections;
  }

  private List<Intersection> calculateIntersections(Label label, Line segment, double shift)
  {
    List<Intersection> intersections = new ArrayList<>();

    for (int i = 0; i < label.getText().length(); i++)
    {
      char ch = label.getText().charAt(i);
      int charWidth = calcCharWidth(ch);

      XYPoint intersectionPoint = calcIntersectionPoint(segment, shift, label.getHeight());

      shift += charWidth + 1;

      Intersection intersection = new Intersection(intersectionPoint, segment.getSlope());
      intersections.add(intersection);
    }

    return intersections;
  }

  private int calcCharWidth(char ch) {
    return (int) TextUtil.getCharWidth(ch, fontName, fontSize);
  }

  private XYPoint calcIntersectionPoint(Line line, double shift, float labelHeight)
  {
    double dx = Math.cos(line.getSlope()) * shift;
    double dy = Math.sin(line.getSlope()) * shift;
    // need to drop label down by half its height
    // ^necessary to adjust labels position with the road center line
    // ^otherwise label lays on the road center line
    double centrationCoeffiecient = Math.abs(labelHeight * Math.sin(line.getSlope()));

    XYPoint intersection = new XYPoint(line.getLeftPoint().getX() + dx, line.getLeftPoint().getY() + dy + centrationCoeffiecient);
    return intersection;
  }

  protected List<Line> highwayToSegments(Highway highway, AbstractProjector projector)
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
    return new XYPoint(point.getX() - bounds.getXmin(), point.getY() - bounds.getYmin());
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
