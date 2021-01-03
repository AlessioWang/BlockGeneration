package Tools;

import org.locationtech.jts.geom.*;
import wblut.geom.*;

import java.util.ArrayList;
import java.util.List;

/**
 * @auther Alessio
 * @date 2020/12/24
 **/
public class W_Tools {
    private static GeometryFactory gf = new GeometryFactory();
    private static  WB_GeometryFactory wbgf = new WB_GeometryFactory();

    //计算两点的距离


    static public double getDistance(WB_Point a, WB_Point b) {
        return Math.sqrt((a.xd() - b.xd()) * (a.xd() - b.xd()) + (a.yd() - b.yd()) * (a.yd() - b.yd()));
    }


    //计算quad的最长边
    static public WB_Point getLongestSide(WB_Quad quad) {
        double length1 =
                Math.sqrt((quad.getP1().xd() - quad.getP2().xd()) * (quad.getP1().xd() - quad.getP2().xd()) +
                        (quad.getP1().yd() - quad.getP2().yd()) * (quad.getP1().yd() - quad.getP2().yd()));

        double length2 =
                Math.sqrt((quad.getP2().xd() - quad.getP3().xd()) * (quad.getP2().xd() - quad.getP3().xd()) +
                        (quad.getP2().yd() - quad.getP3().yd()) * (quad.getP2().yd() - quad.getP3().yd()));

        if (length1 > length2) {
            return quad.getP1();
        } else {
            return quad.getP2();
        }
    }


    //以基点及长宽画polygon
    static public WB_Polygon getPolygon(WB_Point pt, double width, double height) {
        WB_Vector vWidth = new WB_Vector(width, 0);
        WB_Vector vHeight = new WB_Vector(0, height);
        WB_Point p1 = pt.add(vWidth);
        WB_Point p2 = pt.add(vHeight).add(vWidth);
        WB_Point p3 = pt.add(vHeight);
        return new WB_Polygon(pt, p1, p2, p3);
    }

    //计算建筑的长宽比
    static public double getProportion(WB_Polygon polygon) {
        WB_AABB aabb = polygon.getAABB();
        return aabb.getWidth() / aabb.getHeight();
    }

    //p2指向p1的单位向量
    static public WB_Vector getUnitVector(WB_Point p1, WB_Point p2) {
        return p1.sub(p2).div(p1.getDistance(p2));
    }

    //polygon判断是否相交（相交是false，相交是true）
    public static boolean checkInRedLine(WB_Polygon a, WB_Polygon b) {
        List<WB_Segment> as = a.toSegments();
        List<WB_Segment> bs = b.toSegments();
        for (WB_Segment s : as) {
            for (WB_Segment s1 : bs) {
                if (WB_GeometryOp2D.getIntersection2D(s, s1).intersection)
                    return false;
            }
        }
        return true;
    }

    public static WB_Polygon getBuffer(WB_Polygon boundary, double redLineDis) {
        List<WB_Polygon> list = wbgf.createBufferedPolygons(boundary, redLineDis,0);
        return list.get(0);
    }

    //数组翻转
    public static WB_Coord[] reserve( WB_Coord[] arr ){
        WB_Coord[] arr1 = new WB_Coord[arr.length];
        for( int x=0;x<arr.length;x++ ){
            arr1[x] = arr[arr.length-x-1];
        }
        return arr1 ;
    }

    //Jts与Hemesh转化工具
//    public static LineString WB_SegmentToJtsLineString(final WB_Segment seg) {
//        Coordinate[] coords = new Coordinate[2];
//        coords[0] = new Coordinate(seg.getOrigin().xd(), seg.getOrigin().yd(), seg.getOrigin().zd());
//        coords[1] = new Coordinate(seg.getEndpoint().xd(), seg.getEndpoint().yd(), seg.getEndpoint().zd());
//        return wbgf.createLineString(coords);
//    }

    public static WB_PolyLine JtsLineStringToWB_PolyLine(final LineString p) {
        WB_Coord[] points = new WB_Point[p.getNumPoints()];
        for (int i = 0; i < p.getNumPoints(); i++) {
            points[i] = new WB_Point(p.getCoordinates()[i].x, p.getCoordinates()[i].y, p.getCoordinates()[i].z);
        }
        return new WB_PolyLine(points);
    }

    public static WB_Polygon JtsPolygonToWB_Polygon(final Polygon p) {
        WB_Coord[] points = new WB_Point[p.getNumPoints()];
        for (int i = 0; i < p.getNumPoints(); i++) {
            points[i] = new WB_Point(p.getCoordinates()[i].x, p.getCoordinates()[i].y, p.getCoordinates()[i].z);
        }
        return new WB_Polygon(points).getSimplePolygon();
    }

    public static Polygon WB_PolygonToJtsPolygon(final WB_Polygon wbp) {
        if (wbp.getNumberOfHoles() == 0) {
            if (wbp.getPoint(0).equals(wbp.getPoint(wbp.getNumberOfPoints() - 1))) {
                Coordinate[] coords = new Coordinate[wbp.getNumberOfPoints()];
                for (int i = 0; i < wbp.getNumberOfPoints(); i++) {
                    coords[i] = new Coordinate(wbp.getPoint(i).xd(), wbp.getPoint(i).yd(), wbp.getPoint(i).zd());
                }
                return gf.createPolygon(coords);
            } else {
                Coordinate[] coords = new Coordinate[wbp.getNumberOfPoints() + 1];
                for (int i = 0; i < wbp.getNumberOfPoints(); i++) {
                    coords[i] = new Coordinate(wbp.getPoint(i).xd(), wbp.getPoint(i).yd(), wbp.getPoint(i).zd());
                }
                coords[wbp.getNumberOfPoints()] = coords[0];
                return gf.createPolygon(coords);
            }
        } else {
            // exterior
            List<Coordinate> exteriorCoords = new ArrayList<>();
            for (int i = 0; i < wbp.getNumberOfShellPoints(); i++) {
                exteriorCoords.add(new Coordinate(wbp.getPoint(i).xd(), wbp.getPoint(i).yd(), wbp.getPoint(i).zd()));
            }
            if (exteriorCoords.get(0).equals3D(exteriorCoords.get(exteriorCoords.size() - 1))) {
                exteriorCoords.add(exteriorCoords.get(0));
            }
            LinearRing exteriorLinearRing = gf.createLinearRing(exteriorCoords.toArray(new Coordinate[0]));

            // interior
            final int[] npc = wbp.getNumberOfPointsPerContour();
            int index = npc[0];
            LinearRing[] interiorLinearRings = new LinearRing[wbp.getNumberOfHoles()];
            for (int i = 0; i < wbp.getNumberOfHoles(); i++) {
                List<Coordinate> contour = new ArrayList<>();
                for (int j = 0; j < npc[i + 1]; j++) {
                    contour.add(new Coordinate(wbp.getPoint(index).xd(), wbp.getPoint(index).yd(), wbp.getPoint(index).zd()));
                    index++;
                }
                if (!contour.get(0).equals3D(contour.get(contour.size() - 1))) {
                    contour.add(contour.get(0));
                }
                interiorLinearRings[i] = gf.createLinearRing(contour.toArray(new Coordinate[0]));
            }

            return gf.createPolygon(exteriorLinearRing, interiorLinearRings);
        }
    }

    /**
     * 将Polygon转换为WB_Polygon（支持带洞）
     *
     * @param p input Polygon
     * @return wblut.geom.WB_Polygon
     */
    public static WB_Polygon jtsPolygonToWB_Polygon(final Polygon p) {
        if (p.getNumInteriorRing() == 0) {
            WB_Coord[] points = new WB_Point[p.getNumPoints()];
            for (int i = 0; i < p.getNumPoints(); i++) {
                points[i] = new WB_Point(p.getCoordinates()[i].x, p.getCoordinates()[i].y, p.getCoordinates()[i].z);
            }
            return new WB_Polygon(points).getSimplePolygon();
        } else {
            // exterior
            WB_Coord[] exteriorPoints = new WB_Point[p.getExteriorRing().getNumPoints()];
            for (int i = 0; i < p.getExteriorRing().getNumPoints(); i++) {
                exteriorPoints[i] = new WB_Point(p.getCoordinates()[i].x, p.getCoordinates()[i].y, p.getCoordinates()[i].z);
            }
            // interior
            int index = p.getExteriorRing().getNumPoints();
            WB_Coord[][] interiorHoles = new WB_Point[p.getNumInteriorRing()][];
            for (int i = 0; i < p.getNumInteriorRing(); i++) {
                LineString curr = p.getInteriorRingN(i);
                WB_Coord[] holePoints = new WB_Point[curr.getNumPoints()];
                for (int j = 0; j < curr.getNumPoints(); j++) {
                    WB_Point point = new WB_Point(curr.getCoordinates()[j].x, curr.getCoordinates()[j].y, curr.getCoordinates()[j].z);
                    holePoints[j] = point;
                }
                interiorHoles[i] = holePoints;
            }
            return new WB_Polygon(exteriorPoints, interiorHoles);
        }
    }


}
