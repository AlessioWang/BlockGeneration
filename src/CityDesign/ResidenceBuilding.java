package CityDesign;

import Tools.W_Tools;
import processing.core.PApplet;
import wblut.geom.*;
import wblut.processing.WB_Render;

import javax.swing.text.Segment;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * @auther Alessio
 * @date 2020/12/30
 **/
public class ResidenceBuilding implements Display {

    WB_Render wb_render;
    PApplet app;
    WB_GeometryFactory gf;
    WB_Polygon boundary;
    double floorNum;
    double floorHeight;
    int index;
    boolean ifInRedLine;
    WB_Polygon redLine;
    WB_Point center;
    WB_Point cp;
    double step = 1;
    WB_Vector dir;
    WB_PolyLine line;
    WB_Point cpInBuilding;
    List<WB_Point> interPts;
    WB_Point midP;
    WB_Polygon buffer;
    boolean ifFullDis;
    WB_Polygon others;
    double dis2others;


    public ResidenceBuilding(int index, WB_Polygon boundary, double floorNum, double floorHeight, WB_Polygon redLine, double dis2others, PApplet applet) {
        this.app = applet;
        gf = new WB_GeometryFactory();
        wb_render = new WB_Render(applet);
        this.index = index;
        this.boundary = boundary;
        this.floorNum = floorNum;
        this.floorHeight = floorHeight;
        this.redLine = redLine;
        this.interPts = new ArrayList<>();
        getBuffer(boundary, dis2others * 0.5);
        this.others = others;
        this.dis2others = dis2others;
        setCenter();
    }

    public void moveDir(WB_Vector direction, double step) {
        WB_Vector v = direction.div(direction.getLength());
        WB_Transform2D t = new WB_Transform2D().addTranslate2D(v.mul(step));
        this.boundary.apply2DSelf(t);
    }

    public void moveRandom(double step) {
        double random = Math.random();
        WB_Vector v;
        if (random < 0.25) {
            v = new WB_Vector(0, 1);
        } else if (random < 0.5 && random > 0.25) {
            v = new WB_Vector(0, -1);
        } else if (random < 0.75 && random > 0.5) {
            v = new WB_Vector(1, 0);
        } else {
            v = new WB_Vector(-1, 0);
        }
        WB_Transform2D t = new WB_Transform2D().addTranslate2D(v.mul(step));
        this.boundary.apply2DSelf(t);
    }

    //找出红线上最接近多边形每个顶点的点
    public void setCp() {
        List<WB_Coord> pts = this.boundary.getPoints().toList();   //多边形上的所有顶点
        WB_Point closeP = new WB_Point();
        double d = Integer.MAX_VALUE;
        for (WB_Coord pt : pts) {
            WB_Point p = WB_GeometryOp2D.getClosestPoint2D(pt, (WB_PolyLine) this.redLine);
            double dis = p.getDistance2D(pt);
            if (dis < d) {
                d = dis;
                closeP = p;
            }
        }
        this.cp = closeP;
    }

    public void setCpInBuilding() {
        cpInBuilding = WB_GeometryOp2D.getClosestPoint2D(this.cp, this.boundary);
    }


    public void setCenter() {
        this.center = boundary.getCenter();
    }

    public void checkBuildingInRedLine() {
        this.ifInRedLine = W_Tools.checkInRedLine(this.boundary, this.redLine);
    }

    public void setDir() {
//        this.dir = W_Tools.getUnitVector(center, midP);
        this.dir = W_Tools.getUnitVector(redLine.getCenter(), center);
    }

    public void buildingMove() {
        this.moveDir(dir, step);
    }

    public void disMove() {
        WB_Vector v = W_Tools.getUnitVector( center,others.getCenter());
        this.moveDir(v, step);
    }

    public void getDirLine() {
        this.line = new WB_PolyLine(cp, center);
    }

    public void getInterPts() {
        interPts.clear();
        if (!this.ifInRedLine) {
            List<WB_Segment> buildSegs = this.boundary.toSegments();
            List<WB_Segment> redLineSegs = this.redLine.toSegments();
            for (WB_Segment buiSeg : buildSegs) {
                for (WB_Segment redSeg : redLineSegs) {
                    WB_IntersectionResult result = WB_GeometryOp2D.getIntersection2D(buiSeg, redSeg);
                    if (result.intersection) {
                        WB_Point p = (WB_Point) result.getObject();
                        interPts.add(p);
                    }
                }
            }
        }
    }

    public void getMidP() {
        WB_Point p1 = this.interPts.get(0);
        WB_Point p2 = this.interPts.get(interPts.size() - 1);
        this.midP = new WB_Point(((p1.xd() + p2.xd()) * 0.5), (p1.yd() + p2.yd() * 0.5));
    }

    public void getBuffer(WB_Polygon boundary, double redLineDis) {
        List<WB_Polygon> list = gf.createBufferedPolygons(boundary, redLineDis);
        this.buffer = list.get(0);
    }

    public void checkHaveFullDis() {
        ifFullDis = W_Tools.checkInRedLine(this.buffer, W_Tools.getBuffer(this.others, dis2others * 0.5));
    }


    @Override
    public void display() {
        app.pushStyle();
        app.noFill();
        app.stroke(0, 0, 50, 70);
        app.strokeWeight(2);
        wb_render.drawPolygonEdges(this.boundary);
        wb_render.drawPoint(center, 10);
        app.fill(255, 0, 0);
        wb_render.drawPoint(cp, 7);
//        wb_render.drawPolylineEdges(line);
        app.popStyle();
    }
}
