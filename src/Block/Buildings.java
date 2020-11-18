package Block;


import org.locationtech.jts.geom.*;
import org.locationtech.jts.operation.linemerge.LineMerger;
import processing.core.PApplet;
import wblut.geom.*;
import wblut.processing.WB_Render;

import java.util.*;

/**
 * @auther Alessio
 * @date 2020/11/12
 **/
public class Buildings implements Display {
    private static GeometryFactory jtsGf;
    //渲染器
    PApplet applet;
    WB_Render buildingRender;
    //图形参数
    WB_Polygon redLine;
    WB_PolyLine controlLine;
    List<WB_Point> controlPoints;
    List<WB_Polygon> buffer;
    WB_GeometryFactory gf;
    WB_Point influencePt;
    WB_Polygon podiumBuildingBoundary;
    List<LineString> mergedLineStrings;
    double podWidth;
    WB_Point pt;

    public Buildings(WB_Polygon boundary, int distance, double inputPodWidth) {
        mergedLineStrings = new ArrayList<>();
        influencePt = new WB_Point();
        gf = new WB_GeometryFactory();
        jtsGf = new GeometryFactory();
        this.redLine = boundary;
        this.podWidth = inputPodWidth;
//        this.lineIndex = index;
        buffer = new ArrayList<>();
        buffer = this.createBuffer(distance);
        controlLine = this.createControlLineInfluncedByPoint(buffer, influencePt);
        //获得控制点
        controlPoints = new ArrayList<>();
        controlPoints = this.getControlPoints(controlLine);

        System.out.println("chushihua" + mergedLineStrings);


    }

    public List<WB_Polygon> createBuffer(double distance) {
        buffer = gf.createBufferedPolygons(redLine, distance * (-1));
        return buffer;
    }

//    //取所有的首点与最后一个的末点
//    public WB_PolyLine createControlLine(List<WB_Polygon> buffer) {
//        WB_Polygon bufferSeparate = buffer.get(0);
//        List<WB_Segment> segments = bufferSeparate.toSegments();
//        List<WB_Coord> nodeList = new ArrayList<>();
//        segments.remove(lineIndex);
//        for (WB_Segment segment : segments) {
//            WB_Coord node = segment.getEndpoint();
//            nodeList.add(node);
//        }
//        //添加起始点
//        nodeList.add(segments.get(0).getOrigin());
//        //以点集创建polyLine作为controlLine
//        return new WB_PolyLine(nodeList);
//    }

//    public WB_PolyLine createControlLine(List<WB_Polygon> buffer) {
//        WB_Polygon bufferSeparate = buffer.get(0);
//        List<WB_Segment> segments = bufferSeparate.toSegments();
//        List<WB_Coord> nodeList = new ArrayList<>();
//        segments.remove(lineIndex);
//        for (WB_Segment segment : segments) {
//            WB_Coord node1 = segment.getOrigin();
//            nodeList.add(node1);
//            WB_Coord node2 = segment.getEndpoint();
//            nodeList.add(node2);
//        }
//        List<WB_Coord> pureNodeList = removeRepeatingCoord(nodeList);
//        return new WB_PolyLine(pureNodeList);
//    }

    public List<WB_Coord> removeRepeatingCoord(List<WB_Coord> coords) {
        for (int i = 0; i < coords.size() - 1; i++) {
            for (int j = 1; j < coords.size() - 1; j++) {
                if (coords.get(j).equals(coords.get(i))) {
                    coords.remove(j);
                }
            }
        }
        return coords;
    }


    public List<WB_Point> getControlPoints(WB_PolyLine controlLine) {
        WB_CoordCollection pts = controlLine.getPoints();
        List<WB_Point> pointList = new ArrayList<>();
        int num = pts.size();
        for (int i = 0; i < num; i++) {
            WB_Point p = controlLine.getPoint(i);
            pointList.add(p);
        }
        return pointList;
    }

    public WB_Segment getClosestSegment(List<WB_Segment> segments, WB_Point pt) {
        double distance = Integer.MAX_VALUE;
        WB_Segment chosenSeg = new WB_Segment();
        for (WB_Segment segment : segments) {
            double d = WB_GeometryOp2D.getDistance2D(pt, segment);
            if (d < distance) {
                distance = d;
                chosenSeg = segment;
            }
        }
        return chosenSeg;
    }

    public List<WB_Segment> removeOneSegmentFromSegments(List<WB_Segment> segments, WB_Segment segmentRemove) {
        segments.removeIf(seg -> seg.equals(segmentRemove));
        return segments;
    }

    public static LineString WB_SegmentToJtsLineString(final WB_Segment seg) {
        Coordinate[] coords = new Coordinate[2];
        coords[0] = new Coordinate(seg.getOrigin().xd(), seg.getOrigin().yd(), seg.getOrigin().zd());
        coords[1] = new Coordinate(seg.getEndpoint().xd(), seg.getEndpoint().yd(), seg.getEndpoint().zd());
        return jtsGf.createLineString(coords);
    }

    public static WB_PolyLine JtsLineStringToWB_PolyLine(final LineString p) {
        WB_Coord[] points = new WB_Point[p.getNumPoints()];
        for (int i = 0; i < p.getNumPoints(); i++) {
            points[i] = new WB_Point(p.getCoordinates()[i].x, p.getCoordinates()[i].y, p.getCoordinates()[i].z);
        }
        return new WB_PolyLine(points);
    }


    //把segments合成一个polyline（若不是收尾相连，就是多个polyline）
    public List<WB_PolyLine> segments2polyline(List<WB_Segment> segments) {
        List<WB_PolyLine> polyLines = new ArrayList<>();
        LineMerger lineMerger = new LineMerger();
        List<LineString> lineStrings = new ArrayList<>();
        for (WB_Segment segment : segments) {
            lineStrings.add(WB_SegmentToJtsLineString(segment));
        }
        lineMerger.add(lineStrings);
        mergedLineStrings = (List<LineString>) lineMerger.getMergedLineStrings();
        for (Object string : mergedLineStrings) {
            polyLines.add(JtsLineStringToWB_PolyLine((LineString) string));
        }
//        System.out.println("@@@"+ mergedLineStrings.size());
        return polyLines;
    }

    public WB_PolyLine createControlLineInfluncedByPoint(List<WB_Polygon> buffer, WB_Point pt) {
        WB_Polygon bufferSingle = buffer.get(0);
        List<WB_Segment> segments = bufferSingle.toSegments();
        WB_Segment closestSeg = getClosestSegment(segments, pt);
        segments = removeOneSegmentFromSegments(segments, closestSeg);
        List<WB_PolyLine> controlLines = segments2polyline(segments);
        return controlLines.get(0);
    }

    public WB_PolyLine createPodPolyline(WB_Point pt, double width) {
        List<WB_Polygon> baseLineBuffers = gf.createBufferedPolygons(buffer, width * (-1),0);
        WB_Polygon baseLineBuffer = baseLineBuffers.get(0);
        List<WB_Segment> segments = baseLineBuffer.toSegments();
        WB_Segment closestSeg = getClosestSegment(segments, pt);
        segments = removeOneSegmentFromSegments(segments, closestSeg);
        List<WB_PolyLine> PodPolyline = segments2polyline(segments);
        return PodPolyline.get(0);
    }

//    public WB_Polygon getPodiumBuilding(double width) {
//        List<LineString> jtsControlLines = new ArrayList<>();
//        System.out.println("###" + mergedLineStrings.size());
//        for (Object string : mergedLineStrings) {
//            jtsControlLines.add((LineString) string);
//        }
//        System.out.println(mergedLineStrings.size());
//        System.out.println(jtsControlLines.size());
//        LineString jtsControlLine = jtsControlLines.get(0);
//        Geometry boundary = jtsControlLine.buffer(width);
//
////        return JtsLineStringToWB_PolyLine((LineString) boundary);
//    }

    public void drawPolygon(WB_Polygon polygon) {
        buildingRender.drawPolygonEdges2D(polygon);
    }

    public void drawPolyline(WB_PolyLine polyLine) {
        buildingRender.drawPolyLine(polyLine);
    }

    public void drawPoint(WB_Point point) {
        buildingRender.drawPoint(point);
    }

    public void drawCircle(PApplet applet, WB_Point point, float rad) {
        applet.pushStyle();
        applet.noFill();
        applet.stroke(0, 255, 0);
        applet.strokeWeight(3);
        applet.ellipse(point.xf(), point.yf(), rad, rad);
        applet.popStyle();
    }

    @Override
    public void display(WB_Render render) {
//        this.buildingRender = render;
//        drawPolyline(controlLine);                //画控制线
//        for (WB_Point p : controlPoints) {        //画控制点
//            drawPoint(p);
//        }
    }

    @Override
    public void display(PApplet applet) {
//        for (WB_Point p : controlPoints) {
//            drawCircle(applet, p,10);
//        }
    }
}
