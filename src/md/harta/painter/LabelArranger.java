package md.harta.painter;

import md.harta.geometry.Bounds;
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
      System.out.println(this.labels.get(i).getBounds().getyMin());
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
    Bounds boundsA = currentLabel.getBounds();
    Bounds boundsB = nextLabel.getBounds();
    if ( (boundsA.getxMax() <= boundsB.getxMin()) || (boundsA.getxMin() >= boundsB.getxMax()) )
    {
      return 0.0;
    }
//    else
//    {
//      // TODO: Need to shift horizontally
//      return boundsA.getyMax() - boundsB.getyMin();
//    }

    if ( (boundsA.getyMax() <= boundsB.getyMin()) || (boundsA.getyMin() >= boundsB.getyMax()) )
    {
      return 0.0;
    }
//    else
//    {
//      // TODO: Need to shift vertically
//      return boundsA.getyMax() - boundsB.getyMin();
//    }
    return boundsB.getyMin() - boundsA.getyMax();
  }
}
