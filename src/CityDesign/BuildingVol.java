package CityDesign;

import processing.core.PApplet;
import wblut.geom.*;
import wblut.processing.WB_Render;

import java.sql.SQLOutput;
import java.util.ArrayList;
import java.util.List;

/**
 * @auther Alessio
 * @date 2021/1/4
 **/
public class BuildingVol implements Display{
    WB_Render wb_render;
    PApplet app;
    WB_GeometryFactory gf = new WB_GeometryFactory();
    WB_Polygon outline;
    double floorHeight;
    double floorNum;
    List<WB_Polygon> walls;
    List<WB_Polygon> floors;
    WB_Polygon roof;

    public BuildingVol(WB_Polygon outline, double floorHeight,double floorNum,PApplet applet){
        this.app = applet;
        wb_render = new WB_Render(applet);
        this.outline = outline;
        this.floorHeight =  floorHeight;
        this.floorNum = floorNum;
        this.walls = getWalls();
        this.roof = getRoof();
        this.floors = getFloors();
    }

    public List<WB_Polygon> getWalls (){
        List<WB_Polygon> allWalls = new ArrayList<>();
        List<WB_Segment> segments = outline.toSegments();
        WB_Vector v = new WB_Vector(0,0,floorHeight*floorNum);
        for(WB_Segment seg : segments){
            WB_Point p0 = new WB_Point(seg.getOrigin());
            WB_Point p1 = new WB_Point(seg.getEndpoint());
            WB_Point p2 = p1.add(v);
            WB_Point p3 = p0.add(v);
            WB_Polygon polygon  = new WB_Polygon(p0,p1,p2,p3);
            allWalls.add(polygon);
        }
        return allWalls;
    }

    public List<WB_Polygon> getFloors(){
        List<WB_Polygon> allFloors = new ArrayList<>();
        List<WB_Coord> coords = outline.getPoints().toList();
        for(int i=0; i<floorNum; i++){
            WB_Vector v = new WB_Vector(0,0,floorHeight*i);
            List<WB_Point> pts = new ArrayList<>();
            for (WB_Coord p : coords){
                WB_Point pt = new WB_Point(p.xd(),p.yd(),p.zd());
                pt = pt.add(v);
                pts.add(pt);
            }
            WB_Polygon polygon = new WB_Polygon(pts);
            allFloors.add(polygon);
        }
        return allFloors;
    }

    public WB_Polygon getRoof(){
        List<WB_Coord> points = outline.getPoints().toList();
        WB_Vector v = new WB_Vector(0,0,floorHeight*floorNum);
        List<WB_Point> pts = new ArrayList<>();
        for (WB_Coord p : points){
            WB_Point pt = new WB_Point(p.xd(),p.yd(),p.zd());
            pt = pt.add(v);
            pts.add(pt);
        }
        return new WB_Polygon(pts);
    }

    @Override
    public void display() {
        app.pushStyle();
        app.strokeWeight(1);
        app.fill(150,80);
        wb_render.drawPolygon(outline);
        for(WB_Polygon p : walls){
            wb_render.drawPolygonEdges(p);
        }
        app.fill(10,10,100);
        for(WB_Polygon p : floors){
            wb_render.drawPolygonEdges(p);
        }
        app.fill(10,100,120);
        wb_render.drawPolygonEdges(roof);
        app.popStyle();
    }
}
