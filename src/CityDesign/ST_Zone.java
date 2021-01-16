package CityDesign;

import Tools.W_Tools;
import processing.core.PApplet;
import wblut.geom.*;
import wblut.processing.WB_Render;

import java.awt.*;
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
    WB_Polygon outPolygon;
    double redLineDis;
    double depth;
    WB_Polygon innerPolygon;
    List<WB_Point> ctrlP;
    int seed = 4;
    double divLengthTol = 300;
    double minBuildingArea = 20000;
    double roadWidth = 80;
    double floorHeight = 30;
    double towerLength = 500;
    double towerDepth = 400;
    double towerTol = 600;
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
    int type;
    double towerBufferDis = 20;
    List<WB_Polygon> towersBoundaryRaw = new ArrayList<>();
    List<WB_Polygon> towersBoundaryCut = new ArrayList<>();
    List<BuildingVol> towerVols;
    int podFloorNum;
    int towerNum;


    public ST_Zone(WB_Polygon boundary, double redLineDis, double depth, int podFloorNum, int gapNum, PApplet applet) {
        this.app = applet;
        wb_render = new WB_Render(applet);
        gf = new WB_GeometryFactory();
        this.type = type;
        setPara(boundary);
        this.maxGapNum = gapNum;
        random = new Random(seed);
        this.boundary = boundary;
        this.redLineDis = redLineDis;
        this.redLine = getSingleBufferedPolygon(boundary, redLineDis);
        this.depth = depth;
        this.outPolygon = getSingleBufferedPolygon(boundary, redLineDis - roadWidth * 0.5);
        this.innerPolygon = getSingleBufferedPolygon(outPolygon, depth + roadWidth);
        this.ctrlP = getPtInPolygon(innerPolygon, gapNum);
        this.divLines = getDivLine(outPolygon, ctrlP);
        this.divPolygons = getDivPolygon(divLines);
        this.buildingBoundary = getBuildingBoundarys(divPolygons, roadWidth);
        this.buildingVols = initialBuildingVol(buildingBoundary, this.podFloorNum, 0);
        this.greenBoundary = getSingleBufferedPolygon(innerPolygon, greenDis - roadWidth);
        this.roads = getRoads(greenBoundary, 300);
        this.green = new Green(greenBoundary, roads, 15, 500, app);

    }

    public ST_Zone(WB_Polygon boundary, double redLineDis, double depth, int podFloorNum, double towerLength, double towerDepth, int towerFloorNum, int gapNum, PApplet applet) {
        this.app = applet;
        wb_render = new WB_Render(applet);
        gf = new WB_GeometryFactory();
        this.type = type;
        setTowerPara(boundary);
        random = new Random(seed);
        this.maxGapNum = gapNum;
        this.boundary = boundary;
        this.redLineDis = redLineDis;
        this.redLine = getSingleBufferedPolygon(boundary, redLineDis);
        this.depth = depth;
        this.outPolygon = getSingleBufferedPolygon(boundary, redLineDis - roadWidth * 0.5);
        this.innerPolygon = getSingleBufferedPolygon(outPolygon, depth + roadWidth);
        this.ctrlP = getPtInPolygon(innerPolygon, maxGapNum);
        this.divLines = getDivLine(outPolygon, ctrlP);
        this.divPolygons = getDivPolygon(divLines);
        this.buildingBoundary = getBuildingBoundarys(divPolygons, roadWidth);
        this.buildingVols = initialBuildingVol(buildingBoundary, podFloorNum, 0);
        this.greenBoundary = getSingleBufferedPolygon(innerPolygon, greenDis - roadWidth);
        this.roads = getRoads(greenBoundary, 300);
        this.green = new Green(greenBoundary, roads, 30, 500, app);
        this.towerLength = towerLength;
        this.towerDepth = towerDepth;
        this.towersBoundaryRaw = getTowerBoundary(redLine, towerTol, towerDepth, towerLength);
        this.towersBoundaryCut = cutTower(towersBoundaryRaw, buildingBoundary);
        this.towerVols = initialBuildingVol(towersBoundaryCut, this.towerNum+ random.nextInt(8), 0);
    }


    public void setPara(WB_Polygon b) {
        double area = Math.abs(b.getSignedArea());
        if (area < 500000) {
            depth = 100;
            roadWidth = 50;
            minBuildingArea = 18000;
            podFloorNum = 4;
        } else if (area < 800000 && area >= 500000) {
            depth = 150;
            roadWidth = 50;
            minBuildingArea = 20000;
            podFloorNum = 6;
        } else {
            depth = 200;
            roadWidth = 80;
            minBuildingArea = 22000;
            podFloorNum = 8;
        }
    }

    public void setTowerPara(WB_Polygon b) {
        double area = Math.abs(b.getSignedArea());
        if (area < 800000) {
            depth = 200;
            towerLength = 300;
            towerDepth = 250;
            towerTol = 600;
            towerNum = 15;
        } else if (area >= 80000 && area < 1100000) {
            depth = 270;
            towerLength = 350;
            towerDepth = 300;
            towerTol = 700;
            towerNum = 20;
        } else {
            towerNum = 25;
        }
    }


    public List<BuildingVol> initialBuildingVol(List<WB_Polygon> buildingOutline, int floor, double scale) {
        List<BuildingVol> buildingVols = new ArrayList<>();
        for (WB_Polygon b : buildingOutline) {
            BuildingVol bvl = new BuildingVol(b, floorHeight, floor, scale, app);
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
//        System.out.println(pts.size());
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
        ringLines.add(outPolygon);
        ringLines.add(innerPolygon);
        List<WB_Polygon> div = W_Tools.splitPolygonWithPolylineList(ringLines, ls);
//        System.out.println("region number : " + div.size());
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
            if (gf.createBufferedPolygons(polygon, (-0.5) * (roadWid), 0).size() > 0) {
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
        for (int i = 0; i < points.size(); i += 1) {
            WB_Point p0 = points.get(i);
            WB_Point p1 = points.get((i + 2) % points.size());
//            System.out.println(p0 + " , " + p1);
            lines.add(new WB_PolyLine(p0, p1));
        }
//        System.out.println("roadLineNum : " + lines.size());
        return lines;
    }

    public List<WB_Polygon> getTowerBoundary(WB_Polygon polygon, double tol, double depth, double length) {
        List<WB_Coord> allPt = polygon.getPoints().toList();
        List<WB_Point> pts = W_Tools.wb_pointsList2CoordList(allPt);
        List<WB_Polygon> rects = new ArrayList<>();     //记录还未被旋转的方块
        List<WB_Polygon> output = new ArrayList<>();
        for (WB_Point p : pts) {
            double tempDis = Integer.MAX_VALUE;
            for (WB_Point other : pts) {
                double dis = p.getDistance2D(other);
                if (dis < tempDis && dis > 10) {
                    tempDis = dis;
                }
            }
            if (tempDis > tol) {
                WB_Polygon rec = W_Tools.getPolygon(p, length, depth);
                rects.add(rec);
            }
        }
        for (WB_Polygon b : rects) {
            WB_Polygon rotatedPolygon = getRotatePolygon(b, redLine);
            output.add(rotatedPolygon);
        }
        return output;
    }

    public WB_Segment selRotateBasedSeg(WB_Polygon rect, WB_Polygon boundary) {
        WB_Segment line = rect.getSegment(0);    //基准线
        WB_Point p = (WB_Point) line.getEndpoint();
        List<WB_Segment> segs = boundary.toSegments();
        WB_Segment selSeg = new WB_Segment();
        double tempD = Integer.MAX_VALUE;
        for (WB_Segment seg : segs) {
            double dis = WB_GeometryOp2D.getDistance2D(p, seg);
            if (tempD > dis) {
                tempD = dis;
                selSeg = seg;
            }
        }
//        System.out.println(selSeg.getEndpoint());
        return selSeg;
    }

    public WB_Polygon getRotatePolygon(WB_Polygon rect, WB_Polygon boundary) {
        WB_Segment basedSeg = selRotateBasedSeg(rect, boundary);
//        System.out.println("segP0 : " + basedSeg.getOrigin());
        WB_Segment line = rect.getSegment(0);    //基准线
        WB_Vector v0 = W_Tools.getUnitVector((WB_Point) basedSeg.getEndpoint(), (WB_Point) basedSeg.getOrigin());
        WB_Vector v1 = W_Tools.getUnitVector((WB_Point) line.getEndpoint(), (WB_Point) line.getOrigin());
//        System.out.println("v0 : " + v0);
//        System.out.println("v1 : " + v1);
        WB_Vector vector = v0.cross(v1);
        double angle = 0;
        if (vector.zd() > 0) {
            angle = v0.getAngle(v1);
        } else {
            angle = v0.getAngle(v1) * (-1);
        }
        WB_Transform2D transform2D = new WB_Transform2D();
        WB_Point point = new WB_Point();
        double dis1 = ((WB_Point) basedSeg.getOrigin()).getDistance2D(rect.getCenter());
        double dis2 = ((WB_Point) basedSeg.getEndpoint()).getDistance2D(rect.getCenter());
        if (dis1 < dis2) {
            point = (WB_Point) basedSeg.getOrigin();
        } else {
            point = (WB_Point) basedSeg.getEndpoint();
        }

        //旋转
        transform2D.addRotateAboutPoint((-1) * angle, point);
        rect.apply2DSelf(transform2D);
        if (WB_GeometryOp.contains2D(rect.getCenter(), boundary)) {
            return rect;
        } else {
            List<WB_Coord> pts = rect.getPoints().toList();
            List<WB_Point> points = new ArrayList<>();
            for (WB_Coord c : pts) {
                WB_Point p = ((WB_Point) c).sub(v0.mul(towerLength));
                points.add(p);
            }
            rect = new WB_Polygon(points);
            return rect;
        }
    }

    public List<WB_Polygon> cutTower(List<WB_Polygon> towers, List<WB_Polygon> outlines) {
        List<WB_PolyLine> lines = new ArrayList<>();
        for (WB_Polygon polygon : outlines) {
            List<WB_Segment> segments = polygon.toSegments();
            for (WB_Segment segment : segments) {
                WB_PolyLine l = new WB_PolyLine(segment.getOrigin(), segment.getEndpoint());
                lines.add(l);
            }
        }
        List<WB_Polygon> polygons = W_Tools.splitPolygonWithPolylineList(towers, W_Tools.getShortedPolylines(lines, -20));
        List<WB_Polygon> selPolygons = new ArrayList<>();
        for (WB_Polygon p : polygons) {
            for (WB_Polygon tower : towers) {
                if (WB_GeometryOp2D.contains2D(p.getCenter(), tower)) {
                    selPolygons.add(p);
                }
            }
        }

        //筛选面积
        List<WB_Polygon> out = new ArrayList<>();
        for (WB_Polygon b : selPolygons) {
            if (Math.abs(b.getSignedArea()) > 120000) {
//                System.out.println("Area :" + Math.abs(b.getSignedArea()));
                WB_Polygon sc = gf.createBufferedPolygons(b, -towerBufferDis).get(0);
                out.add(sc);
            }
        }

        //随机塔楼数量
        List<WB_Polygon> realSel = new ArrayList<>();
        int num = out.size();
        if (num > 0) {
            int n = random.nextInt(num) + 1;
            for (int i = 0; i < n; i++) {
                realSel.add(out.get(i));
            }
        }

        return realSel;
    }


    @Override
    public void display() {
        app.pushStyle();
        app.noFill();
        app.strokeWeight(2);
        app.noStroke();

//        wb_render.drawPolygonEdges(outPolygon);
//        wb_render.drawPolygonEdges(innerPolygon);

//        //控制点
//        for (WB_Point p : ctrlP) {
//            wb_render.drawPoint(p, 10);
//        }


        for (WB_PolyLine p : roads) {
            wb_render.drawPolylineEdges(p);
        }

        for (WB_Polygon p : buildingBoundary) {
            wb_render.drawPolygonEdges(p);
        }
//        app.stroke(255, 0, 0);
//        for (WB_Polygon p : divPolygons) {
//            wb_render.drawPolygonEdges(p);
//        }

//        wb_render.drawPolygonEdges(redLine);
        app.fill(10, 150, 100);

        for (WB_Polygon p : towersBoundaryCut) {
            wb_render.drawPolygonEdges(p);
        }

        for (BuildingVol b : buildingVols) {
            b.display();
        }

        if (towerVols != null) {
            for (BuildingVol b : towerVols) {
                b.display();
            }
        }

        green.display();
        app.popStyle();
    }

}
