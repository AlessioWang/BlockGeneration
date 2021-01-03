package CityDesign;

import Tools.W_Tools;
import org.locationtech.jts.geom.Polygon;
import org.locationtech.jts.simplify.DouglasPeuckerSimplifier;
import processing.core.PApplet;
import wblut.geom.WB_GeometryFactory;
import wblut.geom.WB_Polygon;
import wblut.processing.WB_Render;


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


    public Green(WB_Polygon originPolygon, double dis1, double dis2, PApplet applet) {
        this.app = applet;
        wb_render = new WB_Render(applet);
        this.originPolygon = originPolygon;
        this.dis1 = dis1;
        this.dis2 = dis2;
        this.basicPolygon = setBasicPolygon();
        this.simplyPolygons = simply(this.basicPolygon,20);
        this.greenZone = setGreenZone();
    }

    public List<WB_Polygon> setBasicPolygon() {
        return gf.createBufferedPolygons(originPolygon, dis1,0);
    }

    public List<WB_Polygon> simply(List<WB_Polygon> wb_polygons,double tol){
        List<WB_Polygon> output = new ArrayList<>();
        for(WB_Polygon wb:wb_polygons){
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
//        List<WB_Polygon> output = new ArrayList<>();
//        for(WB_Polygon polygon: simplyPolygons){
//            WB_Polygon p = gf.createBufferedPolygons()
//        }
        return gf.createBufferedPolygons(simplyPolygons, dis2,0);
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
        app.stroke(10,150,10);
        for (WB_Polygon polygon : simplyPolygons) {
            wb_render.drawPolygonEdges(polygon);
        }
        app.fill(0,255,10,50);
        for (WB_Polygon polygon : greenZone) {
            wb_render.drawPolygonEdges(polygon);
        }
        app.popStyle();
    }
}
