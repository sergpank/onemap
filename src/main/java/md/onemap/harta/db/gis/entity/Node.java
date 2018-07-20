package md.onemap.harta.db.gis.entity;

import java.util.Map;

/**
 * Created by sergpank on 03.02.2015.
 */
public class Node extends Unit
{
    private double lat;
    private double lon;
    private Map<String, String> tags;

    public Node(long id, double lon, double lat) {
        this(id, lon, lat, null);
    }

    public Node(long id, double lon, double lat, Map<String, String> tags) {
        super(id, UnitType.NODE);
        this.lon = lon;
        this.lat = lat;
        this.tags = tags;
    }

    public long getId() {
        return id;
    }

    public void setId(long id)
    {
        this.id = id;
    }

    public double getLat() {
        return lat;
    }

    public double getLon() {
        return lon;
    }

    public Map<String, String> getTags()
    {
        return tags;
    }

    @Override
    public String toString() {
        return "Node{" +
                "id=" + id +
                ", lat=" + lat +
                ", lon=" + lon +
                ", tags=" + tags +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Node node = (Node) o;

        if (id != node.id) return false;
        if (Double.compare(node.lat, lat) != 0) return false;
        if (Double.compare(node.lon, lon) != 0) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        result = (int) (id ^ (id >>> 32));
        temp = Double.doubleToLongBits(lat);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(lon);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        return result;
    }
}
