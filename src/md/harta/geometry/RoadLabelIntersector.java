package md.harta.geometry;

import md.harta.osm.Highway;
import md.harta.osm.OsmNode;
import md.harta.projector.AbstractProjector;
import md.harta.util.TextUtil;
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
   * @param bounds Bounds of are that is being rendered (in pixels)
   */
  public RoadLabelIntersector(BoundsXY bounds, String fontName, int fontSize)
  {
    this.bounds = bounds;
    this.fontName = fontName;
    this.fontSize = fontSize;
  }

  public List<Intersection> getIntersections(Highway highway, Label label, AbstractProjector projector)
  {
    double highwayLength = GeometryUtil.getHighwayLength(highway, projector);

//    LOG.info("Highway length: " + highwayLength);

    if (highwayLength < label.getWidth())
    {
//      System.err.printf("Road length (%f) < label length (%f)", highwayLength, label.getWidth());
      return null;
    }

    List<Line> segments = highwayToLines(highway, projector);

    int repeats = (int) (Math.ceil(highwayLength / (label.getWidth() * 3)));
    double offset = (highwayLength - (label.getWidth() * repeats)) / (repeats + 1);

    List<Intersection> intersections = new ArrayList<>();
    for (int i = 0; i < repeats; i++)
    {
      double shift = offset + (label.getWidth() * i) + (offset * i);
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
      char ch = label.getText().charAt(i);
      int charWidth = calcCharWidth(ch);
      Line line = segments.get(segmentIndex);

      XYPoint intersectionPoint = calcIntersectionPoint(line, shift, label.getHeight());

      if ((line.calcLength() - shift) <= (charWidth))
      {
        if (intersections.size() == 0)
        {
          for (; segmentIndex < segments.size(); segmentIndex++)
          {
            line = segments.get(segmentIndex);
            double segmentLength = line.calcLength();
            if (segmentLength < shift)
            {
              shift -= segmentLength;
            }
            else
            {
              Line nextLine = segments.get(segmentIndex);
              intersectionPoint = calcIntersectionPoint(nextLine, shift, label.getHeight());
              break;
            }
          }
        }
        else
        {
          XYPoint previousIntersection = intersections.get(intersections.size() - 1).getPoint();
          // Расстояние от последнего пересечения до конца предыдущего сегмента
          double rest = GeometryUtil.getDistanceBetweenPoints(previousIntersection, line.getRightPoint());
          int previousCharWidth = 0;//calcCharWidth(label.getText().charAt(i - 1));
          shift = charWidth - rest - previousCharWidth;

          ++segmentIndex;
          if (segmentIndex < segments.size())
          {
            Line nextLine = segments.get(segmentIndex);
            intersectionPoint = calcIntersectionPoint(nextLine, shift, label.getHeight());
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

  private int calcCharWidth(char ch) {
    String s = Character.toString(ch);
    return (int) TextUtil.getStringWidth(s, fontName, fontSize);
  }

  private XYPoint calcIntersectionPoint(Line line, double shift, float labelHeight)
  {
    double dx = Math.cos(line.getSlope()) * shift;
    double dy = Math.sin(line.getSlope()) * shift;
    double centrationCoeffiecient = Math.abs(labelHeight * Math.sin(line.getSlope()));

    XYPoint intersection = new XYPoint(line.getLeftPoint().getX() + dx, line.getLeftPoint().getY() + dy + centrationCoeffiecient);
    return intersection;
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
