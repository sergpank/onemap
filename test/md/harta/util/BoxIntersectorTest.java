package md.harta.util;

import junit.framework.TestCase;
import md.harta.geometry.BoundsXY;
import md.harta.geometry.BoundsXY;
import org.junit.Assert;

/**
 * Created by sergpank on 27.05.15.
 */
public class BoxIntersectorTest extends TestCase
{

  public void testIntersectXY1() throws Exception
  {
    BoundsXY boundsA = new BoundsXY(10, 10, 20, 20);
    BoundsXY boundsB = new BoundsXY(30, 30, 40, 40);
    Assert.assertFalse(BoxIntersector.intersectXY(boundsA, boundsB));
  }

  public void testIntersectXY2() throws Exception
  {
    BoundsXY boundsA = new BoundsXY(10, 10, 20, 20);
    BoundsXY boundsB = new BoundsXY(20, 20, 40, 40);
    Assert.assertTrue(BoxIntersector.intersectXY(boundsA, boundsB));
  }

  public void testIntersectXY3() throws Exception
  {
    BoundsXY boundsA = new BoundsXY(10, 10, 20, 20);
    BoundsXY boundsB = new BoundsXY(15, 15, 40, 40);
    Assert.assertTrue(BoxIntersector.intersectXY(boundsA, boundsB));
  }

  public void testIntersectXY4() throws Exception
  {
    BoundsXY boundsA = new BoundsXY(10, 10, 20, 20);
    BoundsXY boundsB = new BoundsXY(5, 5, 25, 25);
    Assert.assertTrue(BoxIntersector.intersectXY(boundsA, boundsB));
  }

  public void testIntersectXY5() throws Exception
  {
    BoundsXY boundsA = new BoundsXY(10, 10, 20, 20);
    BoundsXY boundsB = new BoundsXY(20, 10, 30, 20);
    Assert.assertTrue(BoxIntersector.intersectXY(boundsA, boundsB));
  }

  public void testIntersectXY6() throws Exception
  {
    BoundsXY boundsA = new BoundsXY(10, 10, 20, 20);
    BoundsXY boundsB = new BoundsXY(21, 10, 30, 20);
    Assert.assertFalse(BoxIntersector.intersectXY(boundsA, boundsB));
  }

  public void testIntersectLatLon() throws Exception
  {

  }
}