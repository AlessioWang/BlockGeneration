package Block;

import processing.core.PApplet;
import wblut.geom.WB_Point;
import wblut.geom.WB_Polygon;
import wblut.hemesh.HE_Mesh;
import wblut.processing.WB_Render;

import java.util.ArrayList;
import java.util.List;

/**
 * @auther Alessio
 * @date 2020/11/12
 **/
public class Boundary implements Display {
    WB_Point p1, p2, p3, p4;
    List<WB_Point> pointList;
    WB_Polygon boundary;
    WB_Render wb_render;
    PApplet app;

    public Boundary(WB_Point point1, WB_Point point2, WB_Point point3, WB_Point point4) {
        this.p1 = point1;
        this.p2 = point2;
        this.p3 = point3;
        this.p4 = point4;
        pointList = new ArrayList<>();
        pointList.add(point1);
        pointList.add(point2);
        pointList.add(point3);
        pointList.add(point4);
        boundary = new WB_Polygon(point1, point2, point3, point4);
//        wb_render = new WB_Render(this);
    }

    public Boundary(List<WB_Point> points, PApplet applet) {
        wb_render = new WB_Render(applet);
        pointList = points;
        this.app = applet;
        boundary = new WB_Polygon(points);
    }

    public WB_Polygon boundary2WB_Polygon() {
        return new WB_Polygon(pointList);
    }

    public void drawBoundary(WB_Polygon polygon) {
        wb_render.drawPolygonEdges(polygon);
    }

    public void addPointByClick() {
        WB_Point pt = new WB_Point(app.mouseX, app.mouseY);
        pointList.add(pt);
        if (pointList.size() >= 3) {
            boundary = new WB_Polygon(pointList);
        }
    }


    @Override
    public void display() {
        app.pushStyle();
        app.noFill();
        app.stroke(255,0,0);
        app.strokeWeight(3);
        wb_render.drawPolygonEdges(this.boundary);
        app.popStyle();
    }
}
