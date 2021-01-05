package CityDesign;

import Block.Boundary;
import processing.core.PApplet;
import wblut.geom.WB_GeometryFactory;
import wblut.geom.WB_Polygon;
import wblut.processing.WB_Render;

import java.util.List;

/**
 * @auther Alessio
 * @date 2021/1/4
 **/
public class Commercial implements Display{
    WB_Render wb_render;
    PApplet app;
    WB_GeometryFactory gf;
    WB_Polygon boundary;
    WB_Polygon redLine;
    double podiumHeight;
    double podFloorNum;
    double towerHeight;
    double towerFloorNum;

    public Commercial(WB_Polygon boundary, double redLineDis,double podH, double podN,double towH, double towN, PApplet applet){
        this.app = applet;
        wb_render = new WB_Render(applet);
        gf = new WB_GeometryFactory();
        this.boundary = boundary;
        this.redLine = getRedLine(boundary,redLineDis);
        this.podiumHeight = podH;
        this.podFloorNum = podN;
        this.towerHeight = towH;
        this.towerFloorNum = towN;

    }

    public WB_Polygon getRedLine(WB_Polygon boundary, double redLineDis) {
        List<WB_Polygon> list = gf.createBufferedPolygons(boundary, redLineDis * (-1));
        return list.get(0);
    }



    @Override
    public void display() {
        app.pushStyle();
        app.noFill();
        app.stroke(255, 0, 0);
        app.strokeWeight(1);
        wb_render.drawPolygonEdges(redLine);
//        wb_render.drawPolylineEdges(Boundary);
        app.popStyle();
    }
}
