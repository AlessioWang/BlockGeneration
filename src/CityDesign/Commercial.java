package CityDesign;

import Tools.W_Tools;
import org.apache.batik.dom.util.DOMUtilities;
import processing.core.PApplet;
import wblut.geom.*;
import wblut.processing.WB_Render;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * @auther Alessio
 * @date 2021/1/4
 **/

public class Commercial implements Display {
    WB_Render wb_render;
    PApplet app;
    WB_GeometryFactory gf;
    WB_Polygon boundary;
    WB_Polygon redLine;
    double podiumHeight;
    double podFloorNum;
    WB_Polygon originRing;  //基础分割的带洞多边形
    WB_Polygon innerPolygon;
    List<WB_Point> controlP;    //控制点
    double roadWidth = 20;
    double depth = 170;
    double podMinWidth = 200;  //最小裙楼单元的宽度
    double roadRandom = 0.05;  //小路的个数
    double buildingRandom = 0.22;
    double minBuildingArea = 20000;
    double minGreenArea = 5000;
    int roadNum = 4;
    List<WB_PolyLine> divLine;
    List<WB_Point> cloP = new ArrayList<>();
    List<WB_Polygon> divPolygon = new ArrayList<>();
    List<WB_PolyLine> test = new ArrayList<>();
    List<WB_Point> centers = new ArrayList<>();
    List<WB_Polygon> buildingBoundarys;
    WB_Polygon greenBoundary;
    List<WB_PolyLine> greenRoadLines;
    Green green;
    List<BuildingVol> buildingVols;
    int seed =5;
    int randomWidthLength = 50;
    Random random = new Random(seed);


    public Commercial(WB_Polygon boundary, double redLineDis, double podH, double podN, PApplet applet) {
        this.app = applet;
        wb_render = new WB_Render(applet);
        gf = new WB_GeometryFactory();
        this.boundary = boundary;
        setPara(boundary);
        this.redLine = getSingleBufferedPolygon(boundary, (redLineDis - roadWidth * 0.5));
        this.podiumHeight = podH;
        this.podFloorNum = podN;
//        this.originRing = W_Tools.getPolygonWithHoles(redLine, depth);
        this.innerPolygon = getSingleBufferedPolygon(redLine, depth);
        this.controlP = getAllCtrlP(innerPolygon, podMinWidth);
        this.divLine = getDivLine(redLine, controlP);
        this.divPolygon = getDivPolygon(divLine);
        this.buildingBoundarys = getBuildingBoundarys(divPolygon, roadWidth);
        this.greenBoundary =  getSingleBufferedPolygon(innerPolygon, roadWidth * 1.2);
        this.greenRoadLines = getGreenRoadLines(buildingBoundarys, greenBoundary, roadNum);
        this.green = new Green(greenBoundary, greenRoadLines, 15, minGreenArea, app);
        this.buildingVols = initialBuildingVol();
    }

    public void setPara(WB_Polygon b) {
        double area = Math.abs(b.getSignedArea());
        if (area < 350000) {
            depth = 150;
            roadWidth = 17;
            minGreenArea = 2500;
            minBuildingArea = 18000;
            roadNum = 2;
        } else if (area >= 350000 && area < 55000) {
            depth = 160;
            roadWidth = 17;
            minGreenArea = 3000;
            minBuildingArea = 18000;
            roadNum = 3;
        } else if (area >= 550000 && area < 80000) {
            depth = 170;
            roadWidth = 18;
            minGreenArea = 3000;
            minBuildingArea = 20000;
            roadNum = 4;
        } else if (area >= 800000 && area < 110000) {
            depth = 180;
            roadWidth = 20;
            minGreenArea = 4000;
            minBuildingArea = 20000;
            roadNum = 5;
        } else if (area >= 110000 && area < 150000) {
            depth = 190;
            roadWidth = 20;
            minGreenArea = 4000;
            minBuildingArea = 25000;
            roadNum = 6;
        }else {
            depth = 200;
            roadWidth = 20;
            minGreenArea = 4000;
            minBuildingArea = 30000;
            roadNum = 7;
        }

    }

    public List<BuildingVol> initialBuildingVol() {
        List<BuildingVol> buildingVols = new ArrayList<>();
        for (WB_Polygon b : buildingBoundarys) {
            BuildingVol bvl = new BuildingVol(b, setHeight(), 6, 0, app);
            buildingVols.add(bvl);
        }
        return buildingVols;
    }

    public Integer setHeight() {
        List<Integer> heightList = new ArrayList<>();
        heightList.add(30);
        heightList.add(36);
        heightList.add(42);
        heightList.add(39);
        int height = heightList.get((int) (Math.random() * heightList.size()));
        return height;
    }

    public WB_Polygon getSingleBufferedPolygon(WB_Polygon b, double dis) {
        List<WB_Polygon> list = gf.createBufferedPolygons(b, dis * (-1));
        return list.get(0);
    }

    public List<WB_Point> getPointsInPolyline(WB_PolyLine line, double width) {
        List<WB_Point> points = new ArrayList<>();
        WB_Point p0 = line.getPoint(0);
        WB_Point p1 = line.getPoint(1);
        double length = p0.getDistance(p1);
        System.out.println(length);
        int n = (int) (length / width);
        WB_Point originP = line.getPoint(0);
        WB_Vector originV = W_Tools.getUnitVector(line.getPoint(1), line.getPoint(0));
        for (int i = 0; i < (n + 1); i++) {
            WB_Point p = originP.add(originV.mul(i * (width + (int) random.nextInt(randomWidthLength))));
            points.add(p);
        }
        points.add(line.getPoint(1));
        return points;
    }

    public List<WB_Point> getAllCtrlP(WB_Polygon polygon, double width) {
        List<WB_Segment> segments = polygon.toSegments();
        List<WB_Point> allP = new ArrayList<>();
        for (WB_Segment segment : segments) {
            WB_PolyLine line = new WB_PolyLine(segment.getOrigin(), segment.getEndpoint());
            allP.addAll(getPointsInPolyline(line, width));
        }
        return allP;
    }

    public List<WB_PolyLine> getDivLine(WB_Polygon out, List<WB_Point> points) {
        List<WB_PolyLine> lines = new ArrayList<>();
        List<WB_Coord> pts = out.getPoints().toList();
        pts.add(pts.get(0));
        WB_PolyLine ring = new WB_PolyLine(pts);
        for (WB_Point p : points) {
            WB_Point closeP = WB_GeometryOp2D.getClosestPoint2D(p, ring);
            cloP.add(closeP);
            WB_PolyLine line = new WB_PolyLine(p, closeP);
            lines.add(line);
        }
        return lines;
    }

    //选出建筑边界的polygon，经过面积筛选以及随机去建筑
    public List<WB_Polygon> getDivPolygon(List<WB_PolyLine> lines) {
        List<WB_Polygon> polygons = new ArrayList<>();
        List<WB_PolyLine> ls = W_Tools.getShortedPolylines(lines, (-80));
        test.addAll(ls);
        List<WB_Polygon> ringLines = new ArrayList<>();
        ringLines.add(redLine);
        ringLines.add(innerPolygon);
        List<WB_Polygon> div = W_Tools.splitPolygonWithPolylineList(ringLines, ls);
        for (WB_Polygon p : div) {
            centers.add(p.getCenter());
        }
        div = W_Tools.selPolygonsInRingByCenter(innerPolygon, div);
        for (WB_Polygon p : div) {
            if (Math.abs(p.getSignedArea()) > minBuildingArea) {
                if (random.nextFloat() > buildingRandom) {
                    polygons.add(p);
                }
            }
        }
        return polygons;
    }

    public List<WB_Polygon> getBuildingBoundarys(List<WB_Polygon> selPolygons, double roadWid) {
        List<WB_Polygon> out = new ArrayList<>();
        for (WB_Polygon polygon : selPolygons) {
            out.addAll(gf.createBufferedPolygons(polygon, (-0.5) * (roadWid), 0));
        }
        return out;
    }

    public List<WB_PolyLine> getGreenRoadLines(List<WB_Polygon> buildings, WB_Polygon green, int num) {
        List<WB_PolyLine> greenLines = new ArrayList<>();
        List<WB_Point> clP = new ArrayList<>();
        List<WB_Coord> pts = green.getPoints().toList();
        pts.add(pts.get(0));
        WB_PolyLine newGreen = new WB_PolyLine(pts);
        for (WB_Polygon b : buildings) {
            WB_Point p = b.getCenter();
            WB_Point cp = WB_GeometryOp2D.getClosestPoint2D(p, newGreen);
            clP.add(cp);
        }
        for (int i = 0; i < clP.size(); i++) {
            for (int j = 1; j < clP.size(); j++) {
                WB_PolyLine line = new WB_PolyLine(clP.get(i), clP.get(j));
//                if (random.nextFloat() < roadRandom) {
//                    greenLines.add(line);
//                }
                greenLines.add(line);
            }
        }
        List<WB_PolyLine> ls = new ArrayList<>();
        for (int i = 0; i < num; i++) {
            ls.add(greenLines.get(random.nextInt(greenLines.size())));
        }

        return ls;
    }


    @Override
    public void display() {
        app.pushStyle();
        app.noFill();
        app.stroke(255, 0, 0);
        app.strokeWeight(1);
        wb_render.drawPolygonEdges(redLine);
        app.stroke(0, 0, 150);
//        wb_render.drawPolygonEdges(originRing);
//        for (WB_Point p : controlP) {
//            wb_render.drawPoint(p, 5);
//        }
//        for (WB_Point p : cloP) {
//            wb_render.drawPoint(p, 5);
//        }
//        app.fill(0, 0, 100, 50);
        for (WB_Polygon p : buildingBoundarys) {
            wb_render.drawPolygonEdges(p);
        }
        app.fill(0, 120, 0, 30);
        green.display();
        for (BuildingVol bvl : buildingVols) {
            bvl.display();
        }
        app.popStyle();
    }
}
