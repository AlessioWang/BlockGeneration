package Tools;

import org.locationtech.jts.geom.*;
import org.locationtech.jts.operation.polygonize.Polygonizer;
import wblut.geom.*;

import java.util.*;


/**
 * JTS和HeMesh数据形式变换的类
 *
 * @author Naturalpowder
 */
public class TransTool {


    public TransTool() {

    }

    /**
     * 将点按一定宽度和高度拓展为矩形
     *
     * @param p      中心点
     * @param height 高度
     * @param width  宽度
     * @return 所得矩形区域的HeMesh版
     */
    public static WB_Polygon toRegionVersion(WB_Point p, double height, double width) {
        WB_Point origin = p.add(-width / 2, -height / 2);
        return rectangle(origin, width, height);
    }

    /**
     * @param polygon   待获取边界矩形的多边形
     * @param lineWidth 偏移宽度
     * @param endStyle  BufferParameters.CAP_SQUARE
     * @return 边界多边形
     */
    public static WB_Polygon toWidthVersion(WB_Polygon polygon, double lineWidth, int subdiv, int endStyle) {
        lineWidth /= 2;
        LinearRing linearRing = TransTool.toJTS_LinearRing(polygon);
        Geometry geometry = linearRing.buffer(lineWidth, subdiv, endStyle);
        List<WB_Polygon> interiors = TransTool.toWB_PolygonInterior((Polygon) geometry);
        List<WB_Point> pts = new ArrayList<>();
        for (WB_Polygon interior : interiors) {
            for (int i = 0; i < interior.getNumberOfPoints(); i++) {
                pts.add(interior.getPoint(i));
            }
        }
        WB_Polygon exterior = TransTool.toWB_PolygonExterior((Polygon) geometry);
        Douglas douglas = new Douglas(exterior, .1);
        List<WB_Coord> out_pts = douglas.getTransPolygon().getPoints().toList();
        System.out.println(out_pts);
        return new WB_Polygon(out_pts, pts);
    }

    /**
     * @param line      待赋予线宽多段线
     * @param lineWidth 线宽
     * @param endStyle  BufferParameters.CAP_SQUARE
     * @return 赋予线宽后多边形
     */
    public static WB_Polygon toWidthVersion(WB_PolyLine line, double lineWidth, int subdiv, int endStyle) {
        lineWidth /= 2;
        LineString lineString = TransTool.toJTS_LineString(line);
        Geometry geometry = lineString.buffer(lineWidth, subdiv, endStyle);
        WB_Polygon polygon = TransTool.toWB_PolygonExterior((Polygon) geometry);
        Douglas douglas = new Douglas(polygon, .1);
        return douglas.getTransPolygon();
    }

    /**
     * 将多边形转化为JTS_LinearRing
     *
     * @param polygon 多边形
     * @return JTS_LinearRing
     */
    public static LinearRing toJTS_LinearRing(WB_Polygon polygon) {
        GeometryFactory gf = new GeometryFactory();
        Coordinate[] coords = getCoords(polygon);
        return gf.createLinearRing(coords);
    }

    /**
     * 将WB_PolyLine转化为JTS_LineString
     *
     * @param line 多段线
     * @return JTS_LineString
     */
    public static LineString toJTS_LineString(WB_PolyLine line) {
        GeometryFactory gf = new GeometryFactory();
        Coordinate[] coords = getCoords(line);
        return gf.createLineString(coords);
    }

    /**
     * 获取多段线line的JTS表达
     *
     * @param line 多段线
     * @return JTS坐标
     */
    public static Coordinate[] getCoords(WB_PolyLine line) {
        int num = line.getNumberOfPoints();
        Coordinate[] coords = new Coordinate[num];
        for (int i = 0; i < num; i++) {
            WB_Point p = line.getPoint(i);
            coords[i] = new Coordinate(p.xd(), p.yd(), p.zd());
        }
        return coords;
    }

    /**
     * 将WB_Polygon转化为JTS_Polygon
     *
     * @param p HeMesh版
     * @return JTS版
     */
    public static Polygon toJTS_Polygon(WB_Polygon p) {
        GeometryFactory gf = new GeometryFactory();
        Coordinate[] coords = getCoords(p);
        return gf.createPolygon(coords);
    }

    /**
     * 获取多边形p坐标的JTS表达
     *
     * @param p 多边形
     * @return JTS坐标数组
     */
    public static Coordinate[] getCoords(WB_Polygon p) {
        WB_CoordCollection collection = p.getPoints();
        Coordinate[] coords = new Coordinate[collection.size() + 1];
        for (int i = 0; i < collection.size(); i++) {
            WB_Coord coord = collection.get(i);
            coords[i] = new Coordinate(coord.xd(), coord.yd(), coord.zd());
        }
        coords[coords.length - 1] = coords[0];
        return coords;
    }


    public static WB_Polygon rotate(WB_Polygon polygon, double angle, WB_Point center) {
        List<WB_Point> pts = new ArrayList<>();
        for (int i = 0; i < polygon.getNumberOfPoints(); i++)
            pts.add(polygon.getPoint(i).rotateAboutPoint2D(angle, center));
        return new WB_Polygon(pts);
    }

    /**
     * 获取内部Polygon的HeMesh版本
     *
     * @param p JTS_Polygon
     * @return 多边形内部多边形数组
     */
    public static List<WB_Polygon> toWB_PolygonInterior(Polygon p) {
        List<WB_Polygon> polygons = new ArrayList<>();
        for (int i = 0; i < p.getNumInteriorRing(); i++) {
            LineString line = p.getInteriorRingN(i);
            List<WB_Point> pts = new ArrayList<>();
            for (int j = 0; j < line.getNumPoints(); j++) {
                Point pt = line.getPointN(j);
                pts.add(new WB_Point(pt.getX(), pt.getY(), 0));
            }
            polygons.add(new WB_Polygon(pts));
        }
        return polygons;
    }

    /**
     * 将JTS_Polygon的外部多边形转化为WB_Polygon
     *
     * @param p JTS_Polygon
     * @return HeMesh多边形（外部）
     */
    public static WB_Polygon toWB_PolygonExterior(Polygon p) {
        Coordinate[] coords = p.getExteriorRing().getCoordinates();
        List<WB_Coord> list = new ArrayList<>();
        for (int i = 0; i < coords.length - 1; i++) {
            double z = coords[i].z;
            if (Double.isNaN(z))
                z = 0;
            WB_Coord coordinate = new WB_Point(coords[i].x, coords[i].y, z);
            list.add(coordinate);
        }
        return new WB_Polygon(list);
    }

    /**
     * 将JTS_Polygon转化为WB_Polygon
     *
     * @param line JTS多段线
     * @return HeMesh多段线
     */
    public static WB_Polygon toWB_PolyLine(LineString line) {
        Coordinate[] coords = line.getCoordinates();
        List<WB_Coord> list = new ArrayList<>();
        for (Coordinate pt : coords) {
            double z = pt.z;
            if (Double.isNaN(z))
                z = 0;
            WB_Coord coordinate = new WB_Point(pt.x, pt.y, z);
            list.add(coordinate);
        }
        return new WB_Polygon(list);
    }

    /**
     * 由原点和宽度高度定义矩形
     *
     * @param origin 原点
     * @param width  宽度
     * @param height 高度
     * @return HeMesh矩形
     */
    public static WB_Polygon rectangle(WB_Point origin, double width, double height) {
        WB_Point p1 = origin.add(width, 0);
        WB_Point p2 = p1.add(0, height);
        WB_Point p3 = p2.add(-width, 0);
        return new WB_Polygon(origin, p1, p2, p3);
    }

    /**
     * 获取被直线切割后的区域
     *
     * @param back      背景区域
     * @param polyLines 用于切割的直线
     * @return
     */
    public static List<WB_Polygon> getSplitRegions(WB_Polygon back, List<WB_PolyLine> polyLines) {
        Polygon region = toJTS_Polygon(back);
        LineString[] lineString = new LineString[polyLines.size()];
        for (int i = 0; i < lineString.length; i++)
            lineString[i] = toJTS_LineString(polyLines.get(i));
        Collection lines = new ArrayList(Arrays.asList(lineString));
        lines.add(region.getExteriorRing());
        Iterator iterator = lines.iterator();
        Geometry nodeLineStrings = (LineString) iterator.next();
        while (iterator.hasNext())
            nodeLineStrings = nodeLineStrings.union((LineString) iterator.next());
        lines.clear();
        for (int i = 0; i < nodeLineStrings.getNumGeometries(); i++)
            lines.add(nodeLineStrings.getGeometryN(i));
        Polygonizer polygonizer = new Polygonizer();
        polygonizer.add(lines);
        Collection polys = polygonizer.getPolygons();
//        System.out.println("polys.size() = "+polys.size());
        List<WB_Polygon> list = new ArrayList<>();
        for (Object poly : polys) {
            Douglas douglas = new Douglas(toWB_PolygonExterior((Polygon) poly), .1);
            list.add(douglas.getTransPolygon());
        }
        return list;
    }
}

