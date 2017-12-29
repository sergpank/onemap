package md.harta.painter;

import md.harta.geometry.BoundsXY;
import md.harta.geometry.Label;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Created by sergpank on 11.05.15.
 */
public class LabelArranger
{

  private final ArrayList<Label> labels;

  public LabelArranger(Set<Label> labels)
  {
    this.labels = new ArrayList<>(labels);
    for (int i = 0; i < this.labels.size(); i++)
    {
      System.out.println(this.labels.get(i).getBounds().getYmin());
    }
  }

  public List<Label> arrangeLabels()
  {
    //TODO попробовать упорядочить сначала по вертикали а потом по горизонтали и снова расставить

    for (int i = 0; i < labels.size(); i++)
    {
      int j = i + 1;
      if (j >= labels.size())
      {
        break;
      }
      double intersectionHeight;
      while ((intersectionHeight = labelsIntersect(labels.get(i), labels.get(j))) < 0)
      {
        labels.get(i).liftUp(intersectionHeight);
        j++;
      }
    }

    return labels;
  }

  private double labelsIntersect(Label currentLabel, Label nextLabel)
  {
    BoundsXY boundsA = currentLabel.getBounds();
    BoundsXY boundsB = nextLabel.getBounds();
    if ( (boundsA.getXmax() <= boundsB.getXmin()) || (boundsA.getXmin() >= boundsB.getXmax()) )
    {
      return 0.0;
    }
//    else
//    {
//      // TODO: Need to shift horizontally
//      return boundsA.getYmax() - boundsB.getYmin();
//    }

    if ( (boundsA.getYmax() <= boundsB.getYmin()) || (boundsA.getYmin() >= boundsB.getYmax()) )
    {
      return 0.0;
    }
//    else
//    {
//      // TODO: Need to shift vertically
//      return boundsA.getYmax() - boundsB.getYmin();
//    }
    return boundsB.getYmin() - boundsA.getYmax();
  }
}
