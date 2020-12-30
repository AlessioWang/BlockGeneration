package Tools;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.LineString;
import org.locationtech.jts.geom.Polygon;
import wblut.geom.*;

import java.util.List;

/**
 * @auther Alessio
 * @date 2020/12/24
 **/
public class W_Tools {
    private static GeometryFactory jtsGf;

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
    static public WB_Polygon getPolygon(WB_Point pt , double width, double height){
        WB_Vector vWidth = new WB_Vector(width,0);
        WB_Vector vHeight = new WB_Vector(0,height);
        WB_Point p1 = pt.add(vWidth);
        WB_Point p2 = pt.add(vHeight).add(vWidth);
        WB_Point p3 = pt.add(vHeight);
        return new WB_Polygon(pt,p1,p2,p3);
    }

    //计算建筑的长宽比
    static public double getProportion(WB_Polygon polygon){
        WB_AABB aabb = polygon.getAABB();
        return aabb.getWidth()/aabb.getHeight();
    }

    //p2指向p1的单位向量
    static public  WB_Vector getUnitVector(WB_Point p1, WB_Point p2){
        return p1.sub(p2).div(p1.getDistance(p2));
    }

    //Jts与Hemesh转化工具
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

    public static WB_Polygon JtsPolygonToWB_Polygon(final Polygon p) {
        WB_Coord[] points = new WB_Point[p.getNumPoints()];
        for (int i = 0; i < p.getNumPoints(); i++) {
            points[i] = new WB_Point(p.getCoordinates()[i].x, p.getCoordinates()[i].y, p.getCoordinates()[i].z);
        }
        return new WB_Polygon(points).getSimplePolygon();
    }
}
