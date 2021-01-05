package CityDesign;

import Tools.TransTool;
import Tools.W_Tools;
import org.locationtech.jts.geom.Polygon;
import org.locationtech.jts.simplify.DouglasPeuckerSimplifier;
import processing.core.PApplet;
import wblut.geom.*;
import wblut.hemesh.HEC_Creator;
import wblut.hemesh.HE_Mesh;
import wblut.processing.WB_Render;


import java.lang.reflect.Modifier;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.List;

/**
 * @auther Alessio
 * @date 2021/1/3
 **/
public class Green implements Display {

    WB_Render wb_render;
    PApplet app;
    WB_GeometryFactory gf = new WB_GeometryFactory();
    WB_Polygon originPolygon;
    double dis1;
    double dis2;
    List<WB_Polygon> basicPolygon;
    List<WB_Polygon> simplyPolygons;
    List<WB_Polygon> greenZone;
    List<WB_SimpleMesh> meshes;
    List<WB_PolyLine> roadLines;
    List<WB_Polygon> dividedGreens;
    List<WB_Polygon> greenZoneWithRoad;
    double roadWidth = 15;




    public Green(WB_Polygon originPolygon, double dis1, double dis2, List<WB_PolyLine> roadLines, PApplet applet) {
        this.app = applet;
        wb_render = new WB_Render(applet);
        this.originPolygon = originPolygon;
        this.dis1 = dis1;
        this.dis2 = dis2;
        this.basicPolygon = setBasicPolygon();
        this.simplyPolygons = simply(this.basicPolygon, 20);
        this.greenZone = setGreenZone();
        this.roadLines = roadLines;
        this.dividedGreens = getDividedGreens();
        this.greenZoneWithRoad = getGreenZoneWithRoad(this.dividedGreens,roadWidth);

    }

    public List<WB_Polygon> setBasicPolygon() {
        return gf.createBufferedPolygons(originPolygon, dis1, 0);
    }

    public List<WB_Polygon> simply(List<WB_Polygon> wb_polygons, double tol) {
        List<WB_Polygon> output = new ArrayList<>();
        for (WB_Polygon wb : wb_polygons) {
            Polygon polygon = W_Tools.WB_PolygonToJtsPolygon(wb);
            DouglasPeuckerSimplifier dgSim = new DouglasPeuckerSimplifier(polygon);
            dgSim.setDistanceTolerance(tol);
            polygon = (Polygon) dgSim.getResultGeometry();
            WB_Polygon outWbPolygon = W_Tools.JtsPolygonToWB_Polygon(polygon);
            output.add(outWbPolygon);
        }
        return output;
    }

    public List<WB_Polygon> setGreenZone() {
        return gf.createBufferedPolygons(simplyPolygons, dis2, 0);
    }

    public List<WB_SimpleMesh> createMesh() {
        List<WB_SimpleMesh> meshes1 = new ArrayList<>();
        for (WB_Polygon p : greenZone) {
            meshes1.add(gf.createMesh(p, 100));
        }

        return meshes1;
    }

    private List<WB_Polygon> getDividedGreens() {
        List<WB_Polygon> allPolygons = new ArrayList<>();
        roadLines = W_Tools.getShortedPolylines(roadLines, 50);
        for (WB_Polygon p : greenZone) {
            List<WB_Polygon> polygons = TransTool.getSplitRegions(p, roadLines);
            allPolygons.addAll(polygons);
        }
//        List<WB_Polygon> polygons = TransTool.getSplitRegions(greenZone.get(0), roadLines);
//        allPolygons.addAll(polygons);
//        System.out.println("green Num : " + allPolygons.size());
        return allPolygons;
    }

    public List<WB_Polygon> getGreenZoneWithRoad(List<WB_Polygon> greens, double width) {
        List<WB_Polygon> out = new ArrayList<>();
        for (WB_Polygon green : greens) {
            out.addAll(gf.createBufferedPolygons(green, width * (-0.5)));
        }
        return out;
    }


    @Override
    public void display() {
        app.pushStyle();
        app.noStroke();
        app.fill(0, 120, 0, 70);
        app.strokeWeight(2);
//        for (WB_Polygon polygon : basicPolygon) {
//            wb_render.drawPolygonEdges(polygon);
//        }
        app.noFill();
        app.stroke(10, 150, 10);
        app.fill(0, 255, 10, 50);
//        for (WB_Polygon polygon : greenZone) {
//            wb_render.drawPolygonEdges(polygon);
//        }
        for (WB_Polygon polygon : greenZoneWithRoad) {
            wb_render.drawPolygonEdges(polygon);
        }
        for (WB_PolyLine polygon : roadLines) {
            wb_render.drawPolylineEdges(polygon);
        }
        app.popStyle();
    }
}
