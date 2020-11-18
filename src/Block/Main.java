package Block;

import processing.core.PApplet;
import wblut.geom.WB_GeometryFactory;
import wblut.geom.WB_Point;
import wblut.geom.WB_PolyLine;
import wblut.geom.WB_Polygon;
import wblut.processing.WB_Render;

import java.util.ArrayList;
import java.util.List;

/**
 * @auther Alessio
 * @date 2020/11/12
 **/
public class Main extends PApplet {
    public static void main(String[] args) {
        PApplet.main("Block.Main");
    }

    Boundary boundary;
    List<WB_Point>allPoint;
    WB_Polygon polygonBoundary = new WB_Polygon();
    WB_Point p1;
    WB_Point p2;
    WB_Point p3;
    WB_Point p4;
    WB_Render wb_render;
    List<Display> things = new ArrayList<>();
    List<WB_Polygon> buffer = new ArrayList<>();
    WB_GeometryFactory gf = new WB_GeometryFactory();
    Buildings buildings;
    int bufferDistance = 20;  //控制线，红线向内部buffer的距离
    WB_PolyLine controlLine;
    List<WB_Point> boundaryControlPoints = new ArrayList<>();
    WB_Point pt = new WB_Point(0, 200);
    WB_PolyLine podiumBuildingBoundary;
    double podWidth = 60;
    List<WB_Point> podBuildingControlPoints = new ArrayList<>();


    public void settings() {
        size(500, 500, P3D);
        smooth(5);
    }

    public void setup() {
        wb_render = new WB_Render(this);
        p1 = new WB_Point(50, 50);
        p2 = new WB_Point(50, 400);
        p3 = new WB_Point(200, 470);
        p4 = new WB_Point(400, 100);
        boundary = new Boundary(p1, p2, p3, p4);
        allPoint = new ArrayList<>();
        things.add(boundary);
        polygonBoundary = boundary.boundary2WB_Polygon();
//        //初始化buildings
//        buildings = new Buildings(polygonBoundary, bufferDistance, removeIndex);
//        buffer = buildings.createBuffer(bufferDistance);
//        controlLine = buildings.createControlLine(buffer);
//        controlPoints = buildings.getControlPoints(controlLine);
//        System.out.println("num of controlPoint :" + controlPoints.size());
////        things.add((Display) controlLine);
////        buffer = gf.createBufferedPolygons(polygonBoundary, -50);

        //用点的距离来初始化buildings
        buildings = new Buildings(polygonBoundary, bufferDistance, podWidth);
        buffer = buildings.createBuffer(bufferDistance);

    }

    public void draw() {
        controlLine = buildings.createControlLineInfluncedByPoint(buffer, pt);
        boundaryControlPoints = buildings.getControlPoints(controlLine);
        podiumBuildingBoundary = buildings.createPodPolyline(pt, podWidth);
        podBuildingControlPoints = buildings.getControlPoints(podiumBuildingBoundary);
        background(255);
        drawBoundary();
        drawControlLineAndPoint(); //渲染控制线和点
        drawPt();
        drawPodBoundary();
    }

    //------------------------------------------------------------------------
    public void drawBoundary() {
        //画地块边界
        pushStyle();
        stroke(255, 0, 0);
        strokeWeight(3);
        for (Display d : things) {
            d.display(wb_render);
        }
        popStyle();
    }

    public void drawControlLineAndPoint() {
        pushStyle();
        fill(0, 0, 255);
        wb_render.drawPolyLine(controlLine);
        allPoint.addAll( boundaryControlPoints);
        allPoint.addAll(podBuildingControlPoints);
        for (WB_Point p : allPoint) {
            wb_render.drawPoint(p, 10);
        }
        popStyle();
    }

    public void drawPt() {
        pushStyle();
        noStroke();
        fill(0, 255, 0);
        ellipse(pt.xf(), pt.yf(), 10, 10);
        popStyle();
    }

    public void drawPodBoundary() {
        pushStyle();
        noFill();
        stroke(0, 0, 150);
        wb_render.drawPolylineEdges(podiumBuildingBoundary);
        popStyle();
    }

    public void mousePressed() {
        pt = new WB_Point(mouseX, mouseY);
    }

}
