package Block;

import processing.core.PApplet;
import wblut.geom.*;
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

    //基础参数，定义地块以及基本渲染器
    Boundary boundary;
    List<WB_Point> allPoint = new ArrayList<>();
    WB_Polygon polygonBoundary = new WB_Polygon();
    WB_Point p1, p2,p3,p4;
    WB_Render wb_render;
    List<Display> things = new ArrayList<>();

    // 影响入口的影响点以及塔楼的影响点
    WB_Point entrancePoint = new WB_Point(0, 200);
    WB_Point towerPoint = new WB_Point(0, 0);

    //裙楼参数
    List<WB_Polygon> buffer = new ArrayList<>();
    Buildings buildings;
    double bufferDistance = 20;  //退线距离
    WB_PolyLine controlLine;
    List<WB_Point> boundaryControlPoints = new ArrayList<>();
    WB_PolyLine podiumBuildingBoundary;
    double podWidth = 70;
    List<WB_Point> podBuildingControlPoints = new ArrayList<>();
    WB_Polygon podWB_Polygon = new WB_Polygon();

    // 塔楼参数
    double rad = 35;
    WB_Circle towerCircleBoundary = new WB_Circle();

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
        double realBufferDistance = podWidth * 0.5 + bufferDistance;
        things.add(boundary);
        polygonBoundary = boundary.boundary2WB_Polygon();
        buildings = new Buildings(polygonBoundary, realBufferDistance, podWidth);
        //获得最原始的裙楼退线
        buffer = buildings.createBuffer(realBufferDistance);
    }

    public void draw() {
        //获得建筑的控制线（外边缘线）
        controlLine = buildings.createControlLineInfluncedByPoint(buffer, entrancePoint);
        //创建双向buffer的pod基础线
        podWB_Polygon = buildings.getPodiumBuilding(podWidth / 2);
        //裙楼退线的控制点
        boundaryControlPoints = buildings.getControlPoints(controlLine);
        towerCircleBoundary = buildings.getCircleTower(rad, towerPoint);

        //渲染方法
        background(255);
        drawBoundary();
        drawControlLineAndPoint(); //渲染控制线和点
        drawPt();
        drawPod();
        drawTower();
//        drawPodBoundary();
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
        noFill();
        stroke(0, 0, 255);
        wb_render.drawPolyLine(controlLine);
        allPoint.addAll(boundaryControlPoints);
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
        ellipse(entrancePoint.xf(), entrancePoint.yf(), 10, 10);
        fill(150, 150, 0);
        ellipse(towerPoint.xf(), towerPoint.yf(), 10, 10);
        popStyle();
    }

    public void drawPod() {
        pushStyle();
        fill(10, 100, 150);
        wb_render.drawPolygonEdges(podWB_Polygon);
        popStyle();
    }

    public void drawTower() {
        pushStyle();
        fill(0, 150, 150);
        wb_render.drawCircle(towerCircleBoundary);
        popStyle();
    }


    public void mousePressed() {
        if (keyPressed && key == '1') {
            entrancePoint = new WB_Point(mouseX, mouseY);
        }
        if (keyPressed && key == '2') {
            towerPoint = new WB_Point(mouseX, mouseY);
        }
        if (keyPressed && key == '3') {
            boundary.addPointByClick();
        }

    }

}
