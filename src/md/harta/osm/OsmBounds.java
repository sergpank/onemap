package md.harta.osm;

/**
 * Created by sergpank on 06.02.2015.
 */
public class OsmBounds {
    private double minLat;
    private double minLon;
    private double maxLat;
    private double maxLon;

    public OsmBounds(double minLat, double minLon, double maxLat, double maxLon) {
        this.minLat = minLat;
        this.minLon = minLon;
        this.maxLat = maxLat;
        this.maxLon = maxLon;
    }

    public double getMinLat() {
        return minLat;
    }

    public double getMinLon() {
        return minLon;
    }

    public double getMaxLat() {
        return maxLat;
    }

    public double getMaxLon() {
        return maxLon;
    }

    @Override
    public String toString() {
        return String.format("%.7f:%.7f %.7f:%.7f", minLat, minLon, maxLat, maxLon);
    }
}
