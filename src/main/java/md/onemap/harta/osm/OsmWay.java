package md.onemap.harta.osm;

import md.onemap.harta.geometry.BoundsLatLon;

import java.util.List;

/**
 * Created by sergpank on 06.02.2015.
 */
public class OsmWay {
    protected long id;
    protected List<OsmNode> nodes;
    protected BoundsLatLon bounds;

    public OsmWay(long id, List<OsmNode> nodes) {
        this.id = id;
        this.nodes = nodes;
        double minLat = Double.MAX_VALUE, maxLat = Double.MIN_VALUE, minLon = Double.MAX_VALUE, maxLon = Double.MIN_VALUE;
        for (int i = 0; i < nodes.size(); i++)
        {
            OsmNode node = nodes.get(i);
            double lat = node.getLat();
            double lon = node.getLon();
            if (minLat > lat)
            {
                minLat = lat;
            }
            if (maxLat < lat)
            {
                maxLat = lat;
            }
            if (minLon > lon)
            {
                minLon = lon;
            }
            if (maxLon < lon)
            {
                maxLon = lon;
            }
        }
        this.bounds = new BoundsLatLon(minLat, minLon, maxLat, maxLon);
    }

    public OsmWay(Long id, List<OsmNode> nodes, Double minLat, Double minLon, Double maxLat, Double maxLon)
    {
        this.id = id;
        this.nodes = nodes;
        this.bounds = new BoundsLatLon(minLat, minLon, maxLat, maxLon);
    }

    public long getId() {
        return id;
    }

    public List<OsmNode> getNodes() {
        return nodes;
    }

    public BoundsLatLon getBounds()
    {
        return bounds;
    }

    @Override
    public String toString() {
        return "OsmWay{" +
                "id=" + id +
                ", nodes=" + nodes +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        OsmWay osmWay = (OsmWay) o;

        if (id != osmWay.id) return false;
        if (nodes != null ? !nodes.equals(osmWay.nodes) : osmWay.nodes != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = (int) (id ^ (id >>> 32));
        result = 31 * result + (nodes != null ? nodes.hashCode() : 0);
        return result;
    }
}
