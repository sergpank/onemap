package md.harta.projector;

import md.harta.geometry.GeometryUtil;
import md.harta.geometry.LatLonPoint;
import md.harta.geometry.XYPoint;
import org.junit.Assert;
import org.junit.Test;

public class MercatorProjectorTest {

  @Test
  public void testGetXYPoint_0_0() throws Exception {
    MercatorProjector projector = new MercatorProjector(AbstractProjector.EARTH_RADIUS_M / 1000, 85);
    XYPoint xyPoint = projector.getXY(0, 0);
    Assert.assertEquals(20015.0868, xyPoint.getX(), 0.0001);
    Assert.assertEquals(19949.5207, xyPoint.getY(), 0.0001);
  }

  @Test
  public void testGetXYPoint_10_10() throws Exception {
    MercatorProjector projector = new MercatorProjector(AbstractProjector.EARTH_RADIUS_M / 1000, 85);
    XYPoint xyPoint = projector.getXY(10, 10);

    Assert.assertEquals(21127.03606, xyPoint.getX(), 0.0001);
    Assert.assertEquals(18831.88282, xyPoint.getY(), 0.0001);
  }

  @Test
  public void testGetXYPoint_m10_10() throws Exception {
    MercatorProjector projector = new MercatorProjector(AbstractProjector.EARTH_RADIUS_M / 1000, 85);
    XYPoint xyPoint = projector.getXY(-10, 10);

    Assert.assertEquals(21127.03606, xyPoint.getX(), 0.0001);
    Assert.assertEquals(21067.15874, xyPoint.getY(), 0.0001);
  }

  @Test
  public void testGetXYPoint_m10_m10() throws Exception {
    MercatorProjector projector = new MercatorProjector(AbstractProjector.EARTH_RADIUS_M / 1000, 85);
    XYPoint xyPoint = projector.getXY(-10, -10);

    Assert.assertEquals(18903.13753, xyPoint.getX(), 0.0001);
    Assert.assertEquals(21067.15874, xyPoint.getY(), 0.0001);
  }


  @Test
  public void testGetXYPoint_10_m10() throws Exception {
    MercatorProjector projector = new MercatorProjector(AbstractProjector.EARTH_RADIUS_M / 1000, 85);
    XYPoint xyPoint = projector.getXY(10, -10);

    Assert.assertEquals(18903.13753, xyPoint.getX(), 0.0001);
    Assert.assertEquals(18831.88282, xyPoint.getY(), 0.0001);
  }

  @Test
  public void testGetLonLat_0_0() throws Exception {
    MercatorProjector projector = new MercatorProjector(AbstractProjector.EARTH_RADIUS_M / 1000, 85);
    LatLonPoint point = projector.getLatLon(20015.0868, 19949.5207);
    Assert.assertEquals(0, point.getLon(), 0.0001);
    Assert.assertEquals(0, point.getLat(), 0.0001);
  }

  @Test
  public void testGetLonLat_10_10() throws Exception {
    MercatorProjector projector = new MercatorProjector(AbstractProjector.EARTH_RADIUS_M / 1000, 85);
    LatLonPoint point = projector.getLatLon(21127.03606, 18831.88282);

    Assert.assertEquals(10, point.getLon(), 0.0001);
    Assert.assertEquals(10, point.getLat(), 0.0001);
  }

  @Test
  public void testLonLat_m10_10() throws Exception {
    MercatorProjector projector = new MercatorProjector(AbstractProjector.EARTH_RADIUS_M / 1000, 85);
    LatLonPoint point = projector.getLatLon(21127.03606, 21067.15874);

    Assert.assertEquals(-10, point.getLat(), 0.0001);
    Assert.assertEquals(10, point.getLon(), 0.0001);
  }

  @Test
  public void testGetLonLat_m10_m10() throws Exception {
    MercatorProjector projector = new MercatorProjector(AbstractProjector.EARTH_RADIUS_M / 1000, 85);
    LatLonPoint point = projector.getLatLon(18903.13753, 21067.15874);

    Assert.assertEquals(-10, point.getLon(), 0.0001);
    Assert.assertEquals(-10, point.getLat(), 0.0001);
  }

  @Test
  public void testGetLonLat_10_m10() throws Exception {
    MercatorProjector projector = new MercatorProjector(AbstractProjector.EARTH_RADIUS_M / 1000, 85);
    LatLonPoint point = projector.getLatLon(18903.13753, 18831.88282);

    Assert.assertEquals(10, point.getLat(), 0.0001);
    Assert.assertEquals(-10, point.getLon(), 0.0001);
  }

  @Test
  public void testWidthRadius100(){
    MercatorProjector projector = new MercatorProjector(100, 85);
    Assert.assertEquals(628.3185307, projector.getWidth(), 0.0001);
    Assert.assertEquals(626.2602663, projector.getHeight(), 0.0001);
  }

  @Test
  public void testWidthRadius200(){
    MercatorProjector projector = new MercatorProjector(200, 85);
    Assert.assertEquals(1256.637061, projector.getWidth(), 0.0001);
    Assert.assertEquals(1252.520533, projector.getHeight(), 0.0001);
  }

  @Test
  public void testWidthEarthRadius(){
    MercatorProjector projector = new MercatorProjector(6371, 85);
    Assert.assertEquals(40030.17359, projector.getWidth(), 0.0001);
    Assert.assertEquals(39899.04157, projector.getHeight(), 0.0001);
  }

  @Test
  public void test1Meter(){
    MercatorProjector projector = new MercatorProjector(AbstractProjector.EARTH_RADIUS_M, 85);
    Assert.assertEquals(40030173, projector.getWidth(), 1);

    LatLonPoint pointA = new LatLonPoint(45, 0);
    LatLonPoint pointB = new LatLonPoint(45 + GeometryUtil.DEGREES_IN_METER, 0);
    double distanceOrtodroma = GeometryUtil.getDistanceOrtodroma(pointA, pointB);
    Assert.assertEquals(1, distanceOrtodroma, 0.1);

    XYPoint pointAxy = projector.getXY(pointA);
    XYPoint pointBxy = projector.getXY(pointB);

    Assert.assertEquals(pointA.getLat(), projector.getLatLon(pointAxy).getLat(), 1e-12);
    Assert.assertEquals(pointA.getLon(), projector.getLatLon(pointAxy).getLon(), 1e-12);
    Assert.assertEquals(pointB.getLat(), projector.getLatLon(pointBxy).getLat(), 1e-12);
    Assert.assertEquals(pointB.getLon(), projector.getLatLon(pointBxy).getLon(), 1e-12);
  }

  ///////////// THIS TEST SHOWS MAX TOLERANCE OF CURRENT APPROACH
  ///////////// AND THIS TOLERANCE = ~0,2M
//  @Test
//  public void testXYtolerance(){
//    MercatorProjector projector = new MercatorProjector(AbstractProjector.EARTH_RADIUS_M, 85);
//
////    Assert.assertEquals(projector.getLatLon(projector.getXY(45, 45)), new LatLonPoint(45, 45));
////    Assert.assertEquals(projector.getLatLon(projector.getXY(45.1, 45.1)), new LatLonPoint(45.1, 45.1));
////    Assert.assertEquals(projector.getLatLon(projector.getXY(45.01, 45.01)), new LatLonPoint(45.01, 45.01));
////    Assert.assertEquals(projector.getLatLon(projector.getXY(45.001, 45.001)), new LatLonPoint(45.001, 45.001));
////    Assert.assertEquals(projector.getLatLon(projector.getXY(45.0001, 45.0001)), new LatLonPoint(45.0001, 45.0001));
////    Assert.assertEquals(projector.getLatLon(projector.getXY(45.00001, 45.00001)), new LatLonPoint(45.00001, 45.00001));
////    Assert.assertEquals(projector.getLatLon(projector.getXY(45.000001, 45.000001)), new LatLonPoint(45.000001, 45.000001));
////    Assert.assertEquals(projector.getLatLon(projector.getXY(45.0000001, 45.0000001)), new LatLonPoint(45.0000001, 45.0000001));
////    Assert.assertEquals(projector.getLatLon(projector.getXY(45.00000001, 45.00000001)), new LatLonPoint(45.00000001, 45.00000001));
////    Assert.assertEquals(projector.getLatLon(projector.getXY(45.000000001, 45.000000001)), new LatLonPoint(45.000000001, 45.000000001));
////    Assert.assertEquals(projector.getLatLon(projector.getXY(45.0000000001, 45.0000000001)), new LatLonPoint(45.0000000001, 45.0000000001));
////    Assert.assertEquals(projector.getLatLon(projector.getXY(45.00000000001, 45.00000000001)), new LatLonPoint(45.00000000001, 45.00000000001));
//    Assert.assertEquals(new LatLonPoint(45.000000000001, 45.000000000001), projector.getLatLon(projector.getXY(45.000000000001, 45.000000000001)));
//  }
}