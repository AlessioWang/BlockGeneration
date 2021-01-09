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
public class BuildingVol implements Display {
    WB_Render wb_render;
    PApplet app;
    WB_GeometryFactory gf = new WB_GeometryFactory();
    WB_Polygon outline;
    double floorHeight;
    double floorNum;
    List<WB_Polygon> walls;
    List<WB_Polygon> floors;
    WB_Polygon roof;
    List<WB_Polygon> scaledVol;
    double dis;

//    public BuildingVol(WB_Polygon outline, double floorHeight, double floorNum, PApplet applet) {
//        this.app = applet;
//        wb_render = new WB_Render(applet);
//        this.outline = outline;
//        this.floorHeight = floorHeight;
//        this.floorNum = floorNum;
//
//    }

    public BuildingVol(WB_Polygon outline, double floorHeight, double floorNum, double distance, PApplet applet) {
        this.app = applet;
        wb_render = new WB_Render(applet);
        this.outline = outline;
        this.floorHeight = floorHeight;
        this.floorNum = floorNum;
        this.dis = distance;
        if (distance == 0) {
            this.walls = getWalls();
            this.roof = getRoof(outline);
            this.floors = getFloors();
        }
        if (distance != 0) {
            this.scaledVol = getVolWithScale(outline, distance);
        }
    }


//    public List<WB_Polygon> getVolWithScale(WB_Polygon outline, double dis) {
//        List<WB_Polygon> vols = new ArrayList<>();
//        WB_Polygon rawRoof = getRoof(outline);
//        WB_Transform3D transform3D = new WB_Transform3D();
//        transform3D.addTranslate(rawRoof.getCenter());
//        transform3D.addScale(0.5);
//        WB_Polygon roof = rawRoof.applySelf(transform3D);
////        WB_Polygon roof = gf.createBufferedPolygons(rawRoof, -dis).get(0);
//        List<WB_Coord> roofPoints = roof.getPoints().toList();
//        List<WB_Coord> outLinePoints = outline.getPoints().toList();
//        for (int i = 0; i <roofPoints.size(); i++) {
//            for (int j = 0; j < outLinePoints.size(); j++) {
//                WB_Point pr = (WB_Point) roofPoints.get(i);
//                WB_Point prn = (WB_Point) roofPoints.get((i + 1) % roofPoints.size());
//                WB_Point prL = (WB_Point) roofPoints.get(roofPoints.size()-1);
//                WB_Point po = (WB_Point) outLinePoints.get(j);
//                WB_Point pon = (WB_Point) outLinePoints.get((j + 1) % outLinePoints.size());
//                if (i == j) {
//                    WB_Polygon wall = new WB_Polygon(po, pon, prn, pr);
//                    vols.add(wall);
//                }
//                else {
//                    WB_Polygon wall = new WB_Polygon(po, pon, prL);
//                    vols.add(wall);
//                }
//            }
//        }
//        vols.add(roof);
//        return vols;
//    }

    public List<WB_Polygon> getVolWithScale(WB_Polygon outline, double dis) {
        List<WB_Polygon> vols = new ArrayList<>();
        WB_Polygon rawRoof = getRoof(outline);
        List<WB_Coord> roofPoints = rawRoof.getPoints().toList();
        roofPoints.remove(roofPoints.get(roofPoints.size()-1));
        List<WB_Coord> outLinePoints = outline.getPoints().toList();
        for (int i = 0; i <roofPoints.size(); i++) {
            for (int j = 0; j < outLinePoints.size(); j++) {
                WB_Point pr = (WB_Point) roofPoints.get(i);
                WB_Point prn = (WB_Point) roofPoints.get((i + 1) % roofPoints.size());
                WB_Point prL = (WB_Point) roofPoints.get(roofPoints.size()-1);
                WB_Point po = (WB_Point) outLinePoints.get(j);
                WB_Point pon = (WB_Point) outLinePoints.get((j + 1) % outLinePoints.size());
                if (i == j) {
                    WB_Polygon wall = new WB_Polygon(po, pon, prn, pr);
                    vols.add(wall);
                }
                else {
                    WB_Polygon wall = new WB_Polygon(po, pon, prL);
                    vols.add(wall);
                }
            }
        }
        vols.add(roof);
        return vols;
    }


    public List<WB_Polygon> getWalls() {
        List<WB_Polygon> allWalls = new ArrayList<>();
        List<WB_Segment> segments = outline.toSegments();
        WB_Vector v = new WB_Vector(0, 0, floorHeight * floorNum);
        for (WB_Segment seg : segments) {
            WB_Point p0 = new WB_Point(seg.getOrigin());
            WB_Point p1 = new WB_Point(seg.getEndpoint());
            WB_Point p2 = p1.add(v);
            WB_Point p3 = p0.add(v);
            WB_Polygon polygon = new WB_Polygon(p0, p1, p2, p3);
            allWalls.add(polygon);
        }
        return allWalls;
    }

    public List<WB_Polygon> getFloors() {
        List<WB_Polygon> allFloors = new ArrayList<>();
        List<WB_Coord> coords = outline.getPoints().toList();
        for (int i = 0; i < floorNum; i++) {
            WB_Vector v = new WB_Vector(0, 0, floorHeight * i);
            List<WB_Point> pts = new ArrayList<>();
            for (WB_Coord p : coords) {
                WB_Point pt = new WB_Point(p.xd(), p.yd(), p.zd());
                pt = pt.add(v);
                pts.add(pt);
            }
            WB_Polygon polygon = new WB_Polygon(pts);
            allFloors.add(polygon);
        }
        return allFloors;
    }

    public WB_Polygon getRoof(WB_Polygon outline) {
        List<WB_Coord> points = outline.getPoints().toList();
        WB_Vector v = new WB_Vector(0, 0, floorHeight * floorNum);
        List<WB_Point> pts = new ArrayList<>();
        for (WB_Coord p : points) {
            WB_Point pt = new WB_Point(p.xd(), p.yd(), p.zd());
            pt = pt.add(v);
            pts.add(pt);
        }
        return new WB_Polygon(pts);
    }

    @Override
    public void display() {
        app.pushStyle();
        app.strokeWeight(1);
        app.fill(150);
        if (this.dis == 0) {
            wb_render.drawPolygon(outline);
            for (WB_Polygon p : walls) {
                wb_render.drawPolygonEdges(p);
            }
            app.fill(10, 10, 100);
            for (WB_Polygon p : floors) {
                wb_render.drawPolygonEdges(p);
            }
            app.fill(10, 100, 120);
            wb_render.drawPolygonEdges(roof);
        }
        if (this.dis != 0) {
            for (WB_Polygon p : scaledVol) {
                wb_render.drawPolygonEdges(p);
            }
        }
        app.popStyle();
    }
}
