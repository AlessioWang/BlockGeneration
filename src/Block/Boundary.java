package Block;

import processing.core.PApplet;
import wblut.geom.WB_Point;
import wblut.geom.WB_Polygon;
import wblut.processing.WB_Render;
import wblut.processing.WB_Render3D;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * @auther Alessio
 * @date 2020/11/12
 **/
public class Boundary implements Display{
    WB_Point p1;
    WB_Point p2;
    WB_Point p3;
    WB_Point p4;
    List<WB_Point> pointList;
    WB_Polygon boundary;
    WB_Render wb_render;

    public Boundary(WB_Point point1,WB_Point point2, WB_Point point3, WB_Point point4){
        this.p1 = point1;
        this.p2 = point2;
        this.p3 = point3;
        this.p4 = point4;
        pointList = new ArrayList<>();
        pointList.add(point1);
        pointList.add(point2);
        pointList.add(point3);
        pointList.add(point4);
        boundary = new WB_Polygon(point1,point2,point3,point4);
//        wb_render = new WB_Render(this);
    }

    public WB_Polygon boundary2WB_Polygon(){
        return new WB_Polygon(pointList);
    }


    public void drawBoundary( WB_Polygon polygon){
        wb_render.drawPolygonEdges(polygon);
    }




    @Override
    public void display(WB_Render render) {
        this.wb_render = render;
        drawBoundary(boundary);
    }

    @Override
    public void display(PApplet applet) {

    }
}
