package CityDesign;

import Tools.TransTool;
import processing.core.PApplet;
import wblut.geom.WB_Point;
import wblut.geom.WB_PolyLine;
import wblut.geom.WB_Polygon;
import wblut.geom.WB_Vector;
import wblut.processing.WB_Render;

import java.util.ArrayList;
import java.util.List;

/**
 * @auther Alessio
 * @date 2020/12/24
 **/
public class CrossDivideBlock implements Display{
    WB_Polygon boundary;
    double x1;
    double x2;
    double x3;
    double x4;
    double y1;
    double y2;
    double y3;
    double y4;
    List<WB_PolyLine> divideLines = new ArrayList<>();
    PApplet app;
    WB_Render wb_render;
    List<WB_Polygon> crossDividedBlock = new ArrayList<>();

    //十字形分隔地块
    public CrossDivideBlock(WB_Polygon boundary, double x1, double x2, double y1, double y2, PApplet applet){
        this.boundary = boundary;
        this.x1 = x1;
        this.x2 = x2;
        this.y1 = y1;
        this.y2 = y2;
        this.divideLines = getDivideLinesX(boundary,x1,x2,y1,y2);
        this.crossDividedBlock  = getCrossBlock(boundary,this.divideLines);
        this.app = applet;
        wb_render = new WB_Render(applet);
    }

    public CrossDivideBlock(WB_Polygon boundary, double x1, double x2,double x3, double x4, double y1, double y2, double y3, double y4, PApplet applet){
        this.boundary = boundary;
        this.x1 = x1;
        this.x2 = x2;
        this.x3 = x3;
        this.x4 = x4;
        this.y1 = y1;
        this.y2 = y2;
        this.y3 = y3;
        this.y4 = y4;
        this.divideLines = getDivideLinesJING(boundary,x1,x2,x3,x4,y1,y2,y3,y4);
        this.crossDividedBlock  = getCrossBlock(boundary,this.divideLines);
        this.app = applet;
        wb_render = new WB_Render(applet);
    }

    public List<WB_PolyLine> getDivideLinesX(WB_Polygon polygon, double x1, double x2, double y1, double y2){
        List<WB_PolyLine> lines = new ArrayList<>();
        WB_Point p0 = polygon.getPoint(0);
        WB_Point p1 = polygon.getPoint(1);
        WB_Point p2 = polygon.getPoint(2);
        WB_Point p3 = polygon.getPoint(3);
        WB_Vector v0 = new WB_Vector(p1.sub(p0)).mul(x1);
        WB_Vector v1 = new WB_Vector(p2.sub(p3)).mul(x2);
        WB_Vector v2 = new WB_Vector(p3.sub(p0)).mul(y1);
        WB_Vector v3 = new WB_Vector(p2.sub(p1)).mul(y2);
        WB_Point pp1 = p0.add(v0);
        WB_Point pp2 = p3.add(v1);
        WB_Point pp3 = p0.add(v2);
        WB_Point pp4 = p1.add(v3);
        WB_PolyLine l1 = new WB_PolyLine(pp1,pp2);
        WB_PolyLine l2 = new WB_PolyLine(pp3,pp4);
        lines.add(l1);
        lines.add(l2);
        return lines;
    }

    public List<WB_PolyLine> getDivideLinesJING(WB_Polygon polygon,  double x1, double x2,double x3, double x4, double y1, double y2, double y3, double y4){
        List<WB_PolyLine> lines = new ArrayList<>();
        WB_Point p0 = polygon.getPoint(0);
        WB_Point p1 = polygon.getPoint(1);
        WB_Point p2 = polygon.getPoint(2);
        WB_Point p3 = polygon.getPoint(3);
        WB_Vector vx1 = new WB_Vector(p1.sub(p0)).mul(x1);
        WB_Vector vx2 = new WB_Vector(p1.sub(p0)).mul(x2);
        WB_Vector vx3 = new WB_Vector(p2.sub(p3)).mul(x3);
        WB_Vector vx4 = new WB_Vector(p2.sub(p3)).mul(x4);
        WB_Vector vy1 = new WB_Vector(p3.sub(p0)).mul(y1);
        WB_Vector vy2 = new WB_Vector(p3.sub(p0)).mul(y2);
        WB_Vector vy3 = new WB_Vector(p2.sub(p1)).mul(y3);
        WB_Vector vy4 = new WB_Vector(p2.sub(p1)).mul(y4);
        WB_Point px1 = p0.add(vx1);
        WB_Point px2 = p0.add(vx2);
        WB_Point px3 = p3.add(vx3);
        WB_Point px4 = p3.add(vx4);
        WB_Point py1 = p0.add(vy1);
        WB_Point py2 = p0.add(vy2);
        WB_Point py3 = p1.add(vy3);
        WB_Point py4 = p1.add(vy4);
        WB_PolyLine lx1 = new WB_PolyLine(px1,px3);
        WB_PolyLine lx2 = new WB_PolyLine(px2,px4);
        WB_PolyLine ly1 = new WB_PolyLine(py1,py3);
        WB_PolyLine ly2 = new WB_PolyLine(py2,py4);
        lines.add(lx1);
        lines.add(lx2);
        lines.add(ly1);
        lines.add(ly2);
//        System.out.println(lines.size());
//        for(WB_PolyLine l:lines){
//            System.out.println(l.getPoint(0)+" , " + l.getPoint(1));
//        }
        return  lines;
    }





    public List<WB_Polygon> getCrossBlock(WB_Polygon originBlock, List<WB_PolyLine> lines){
//        List<WB_PolyLine> lines = getDivideLinesX(originBlock,x1,x2,y1,y2);
        return TransTool.getSplitRegions(originBlock,lines);
    }

    @Override
    public void display() {
        app.pushStyle();
        app.noFill();
        app.stroke(0,255,0);
        app.strokeWeight(2);
        for(WB_Polygon polygon: crossDividedBlock){
            wb_render.drawPolygonEdges(polygon);
        }
        app.popStyle();
    }
}
