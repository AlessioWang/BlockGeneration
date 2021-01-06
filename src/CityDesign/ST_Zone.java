package CityDesign;

import Tools.W_Tools;
import org.locationtech.jts.geom.LineString;
import processing.core.PApplet;
import wblut.geom.*;
import wblut.processing.WB_Render;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * @auther Alessio
 * @date 2021/1/6
 **/
public class ST_Zone implements Display {
    WB_Render wb_render;
    PApplet app;
    WB_GeometryFactory gf;
    Random random;
    WB_Polygon boundary;
    WB_Polygon redLine;
    double redLineDis;
    double depth;
    int seed =4;
    WB_Polygon innerPolygon;
    List<WB_Point> ctrlP;
    double divLengthTol = 300;
    double minBuildingArea = 30000;
    double roadWidth = 90;
    double floorHeight = 30;
    int maxGapNum = 2;
    List<WB_PolyLine> divLines;
    List<WB_Polygon> divPolygons;
    List<WB_Point> centers = new ArrayList<>();
    List<WB_Polygon> buildingBoundary;
    List<BuildingVol> buildingVols;
    WB_Polygon greenBoundary;
    double greenDis = 100;
    List<WB_PolyLine> roads;
    Green green;


    public ST_Zone(WB_Polygon boundary, double redLineDis, double depth, PApplet applet) {
        this.app = applet;
        wb_render = new WB_Render(applet);
        gf = new WB_GeometryFactory();
        random = new Random(seed);
        this.boundary = boundary;
        this.redLineDis = redLineDis;
        this.depth = depth;
        this.redLine = getSingleBufferedPolygon(boundary, redLineDis - roadWidth * 0.5);
        this.innerPolygon = getSingleBufferedPolygon(redLine, depth + roadWidth);
        this.ctrlP = getPtInPolygon(innerPolygon, maxGapNum);
        this.divLines = getDivLine(redLine, ctrlP);
        this.divPolygons = getDivPolygon(divLines);
        this.buildingBoundary = getBuildingBoundarys(divPolygons, roadWidth);
        this.buildingVols = initialBuildingVol();
        this.greenBoundary = getSingleBufferedPolygon(innerPolygon, greenDis - roadWidth);
        this.roads = getRoads(greenBoundary, 150);
        this.green = new Green(greenBoundary, roads, 30, 500, app);
        System.out.println("*****" + green.greenZoneWithRoad.size());
    }

    public List<BuildingVol> initialBuildingVol() {
        List<BuildingVol> buildingVols = new ArrayList<>();
        for (WB_Polygon b : buildingBoundary) {
            BuildingVol bvl = new BuildingVol(b, floorHeight, 6, app);
            buildingVols.add(bvl);
        }
        return buildingVols;
    }

    public WB_Polygon getSingleBufferedPolygon(WB_Polygon b, double dis) {
        List<WB_Polygon> list = gf.createBufferedPolygons(b, dis * (-1));
        return list.get(0);
    }

    public List<WB_Point> getPtInPolygon(WB_Polygon polygon, int maxNum) {
        List<WB_Segment> segments = polygon.toSegments();
        List<WB_Point> pts = new ArrayList<>();
        for (WB_Segment segment : segments) {
            if (segment.getLength() > divLengthTol) {
                for (int i = 0; i < random.nextInt(maxNum + 1); i++) {
                    WB_Point p = segment.getPointOnCurve(0.1 + (random.nextInt(8) * 0.1));
                    pts.add(p);
                }
            }
        }
        System.out.println(pts.size());
        return pts;
    }

    public List<WB_PolyLine> getDivLine(WB_Polygon out, List<WB_Point> points) {
        List<WB_PolyLine> lines = new ArrayList<>();
        List<WB_Coord> pts = out.getPoints().toList();
        pts.add(pts.get(0));
        WB_PolyLine ring = new WB_PolyLine(pts);
        for (WB_Point p : points) {
            WB_Point closeP = WB_GeometryOp2D.getClosestPoint2D(p, ring);
            WB_PolyLine line = new WB_PolyLine(p, closeP);
            lines.add(line);
        }
        return lines;
    }

    //选出建筑边界的polygon，经过面积筛选以及随机去建筑
    public List<WB_Polygon> getDivPolygon(List<WB_PolyLine> lines) {
        List<WB_Polygon> polygons = new ArrayList<>();
        List<WB_PolyLine> ls = W_Tools.getShortedPolylines(lines, (-10));
        List<WB_Polygon> ringLines = new ArrayList<>();
        ringLines.add(redLine);
        ringLines.add(innerPolygon);
        List<WB_Polygon> div = W_Tools.splitPolygonWithPolylineList(ringLines, ls);
        System.out.println("@@@" + div.size());
        for (WB_Polygon p : div) {
            centers.add(p.getCenter());
        }
        div = W_Tools.selPolygonsInRingByPoint0(innerPolygon, div);
        for (WB_Polygon p : div) {
            polygons.add(p);
        }
        return polygons;
    }

    public List<WB_Polygon> getBuildingBoundarys(List<WB_Polygon> selPolygons, double roadWid) {
        List<WB_Polygon> out = new ArrayList<>();
        for (WB_Polygon polygon : selPolygons) {
            if (gf.createBufferedPolygons(polygon, (-0.5) * (roadWid), 0).size()>0) {
                WB_Polygon beforeSel = gf.createBufferedPolygons(polygon, (-0.5) * (roadWid), 0).get(0);
                if (Math.abs(beforeSel.getSignedArea()) > minBuildingArea) {
                    out.add(beforeSel);
                }
            }
        }
        return out;
    }

    public List<WB_PolyLine> getRoads(WB_Polygon polygon, double tol) {
        List<WB_Segment> segments = polygon.toSegments();
        List<WB_Point> points = new ArrayList<>();
        List<WB_PolyLine> lines = new ArrayList<>();
        for (WB_Segment seg : segments) {
            if (seg.getLength() > tol) {
                points.add(seg.getCenter());
            }
        }
        for (int i = 0; i < points.size() ; i += 1) {
            WB_Point p0 = points.get(i);
            WB_Point p1 = points.get((i + 2) % points.size());
            lines.add(new WB_PolyLine(p0, p1));
        }
        System.out.println("roadLineNum : " + lines.size());
        return lines;
    }


    @Override
    public void display() {
        app.pushStyle();
        app.noFill();
        app.strokeWeight(2);
        app.stroke(0, 0, 150);
        wb_render.drawPolygonEdges(redLine);
        wb_render.drawPolygonEdges(innerPolygon);
        for (WB_Point p : ctrlP) {
            wb_render.drawPoint(p, 10);
        }
        for (WB_PolyLine p : roads) {
            wb_render.drawPolylineEdges(p);
        }
        for (WB_Polygon p : buildingBoundary) {
            wb_render.drawPolygonEdges(p);
        }
        for (BuildingVol b : buildingVols) {
            b.display();
        }
        green.display();
        app.popStyle();
    }

}
