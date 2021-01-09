package CityDesign;

import Block.Display;
import processing.core.PApplet;
import wblut.geom.WB_Point;
import wblut.geom.WB_Polygon;
import wblut.processing.WB_Render;

import java.util.ArrayList;
import java.util.List;

/**
 * @auther Alessio
 * @date 2020/12/24
 **/
public class BlockBoundary implements Display {
    WB_Point p1, p2, p3, p4;
    List<WB_Point> pointList;
    WB_Polygon outline;
    WB_Render wb_render;
    PApplet app;

    //添加任意四个点来定义boundary
    public BlockBoundary(WB_Point point1, WB_Point point2, WB_Point point3, WB_Point point4) {
        this.p1 = point1;
        this.p2 = point2;
        this.p3 = point3;
        this.p4 = point4;
        pointList = new ArrayList<>();
        pointList.add(point1);
        pointList.add(point2);
        pointList.add(point3);
        pointList.add(point4);
        outline = new WB_Polygon(point1, point2, point3, point4);
//        wb_render = new WB_Render(this);
    }

    //传入一个list来定义
    public BlockBoundary(List<WB_Point> points, PApplet applet) {
        wb_render = new WB_Render(applet);
        pointList = points;
        this.app = applet;
        outline = new WB_Polygon(points);
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
            outline = new WB_Polygon(pointList);
        }
    }

    public static List<WB_Point> setBoundaryPoint(){
        WB_Point p1 = new WB_Point(50, 50);
        WB_Point p2 = new WB_Point(50, 400);
        WB_Point p3 = new WB_Point(400, 400);
        WB_Point p4 = new WB_Point(400, 80);
        List<WB_Point> list = new ArrayList<>();
        list.add(p1);
        list.add(p2);
        list.add(p3);
        list.add(p4);
        return list;
    }

    public static List<WB_Point> set1000BoundaryPoint(){
        WB_Point p1 = new WB_Point(50, 50);
        WB_Point p2 = new WB_Point(50, 2000);
        WB_Point p3 = new WB_Point(2000, 3500);
        WB_Point p4 = new WB_Point(3500, -200);
        List<WB_Point> list = new ArrayList<>();
        list.add(p1);
        list.add(p2);
        list.add(p3);
        list.add(p4);
        return list;
        }

    public static List<WB_Point> setCommercialBoundaryPoint(){
        WB_Point p1 = new WB_Point(200, 250);
        WB_Point p2 = new WB_Point(50, 1200);
        WB_Point p3 = new WB_Point(700, 1800);
        WB_Point p4 = new WB_Point(1500, 1700);
        WB_Point p5 = new WB_Point(1400, 0);
        List<WB_Point> list = new ArrayList<>();
        list.add(p1);
        list.add(p2);
        list.add(p3);
        list.add(p4);
        list.add(p5);
        return list;
    }


    @Override
    public void display() {
        app.pushStyle();
        app.noFill();
        app.stroke(0,0,0);
        app.strokeWeight(2);
        wb_render.drawPolygonEdges(this.outline);
        app.popStyle();
    }
}
