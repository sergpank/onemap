package md.harta;

import md.harta.projector.CylindricalProjector;
import md.harta.projector.MercatorProjector;
import md.harta.projector.SimpleProjector;
import md.harta.util.OsmLoader;

import javax.swing.*;

public class Main {

    public static void main(String[] args) {
        OsmLoader loader = new OsmLoader();
        //loader.load("C:\\Users\\sergp_000\\Downloads\\map.osm");
        //loader.load("OsmData/Australia.osm");
        loader.load("OsmData/AnAuGl.osm");
//        loader.load("OsmData/Antarctica.osm");

        MapPanel mapPanel = new MapPanel(loader, new MercatorProjector(100, 85));

        JFrame app = new JFrame();

        app.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        app.add(mapPanel);
        app.setSize(800, 400);
        app.setVisible(true);
    }
}
