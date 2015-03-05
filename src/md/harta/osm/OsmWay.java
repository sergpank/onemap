package md.harta.osm;

import java.util.List;

/**
 * Created by sergpank on 06.02.2015.
 */
public class OsmWay {
    long id;
    List<OsmNode> nodes;

    public OsmWay(long id, List<OsmNode> nodes) {
        this.id = id;
        this.nodes = nodes;
    }

    public long getId() {
        return id;
    }

    public List<OsmNode> getNodes() {
        return nodes;
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
