package CityDesign;

import Tools.W_Tools;
import processing.core.PApplet;
import wblut.geom.*;
import wblut.processing.WB_Render;

import java.util.*;

/**
 * @auther Alessio
 * @date 2020/12/27
 **/
public class Residence implements Display {
    WB_Render wb_render;
    PApplet app;
    WB_GeometryFactory gf;
    WB_Polygon boundary;
    WB_Polygon redLine;
    double redLineDis;
    double buildWidth;
    double buildDepth;
    double sunRate;
    double floorNum;
    double floorHeight;
    double wholeHeight;
    double buildingGap;
    double widthDis;        //横向之间的楼间距
    double angleTol = Math.PI * 0.05;   //转变摆放方式的角度阈值
    WB_PolyLine controlLineNorth;
    WB_PolyLine controlLineSouth;
    List<WB_PolyLine> controlLines;
    int ctrlLineNum;
    //控制线
    List<List<WB_PolyLine>> divCtrlLineCollection;
    List<WB_PolyLine> rotateBuildingCtrlLine;
    List<WB_PolyLine> normalBuildingCtrlLine;
    List<List<WB_PolyLine>> sepDivLine;
    List<WB_Polygon> rotateBuildings;
    List<WB_Polygon> norBuildings;
    List<WB_Polygon> allBuildingBoundarys;
    List<ResidenceBuilding> residenceBuildings;
    WB_Polygon greenOriginPolygon;
    Green green;
    double dis1 = -170;
    double dis2 = 70;
    double roadDistance = 1000;
    List<WB_PolyLine> roadLines;


    public Residence
            (WB_Polygon boundary, double redLineDis, double buildWidth, double buildDepth, double sunRate, double floorHeight, double widthDis, double floorNum, PApplet applet) {
        this.app = applet;
        wb_render = new WB_Render(applet);
        gf = new WB_GeometryFactory();
        this.boundary = boundary;
        this.redLineDis = redLineDis;
        this.buildWidth = buildWidth;
        this.buildDepth = buildDepth;
        this.sunRate = sunRate;
        this.floorHeight = floorHeight;
        this.floorNum = floorNum;
        this.wholeHeight = floorHeight * floorNum;
        this.widthDis = widthDis;
        this.buildingGap = this.wholeHeight * sunRate;
        this.redLine = getRedLine(boundary, redLineDis);
        this.controlLineNorth = getNorthControlLine(this.redLine, this.buildDepth);
        this.controlLineSouth = getSouthControlLine(this.redLine, this.buildDepth);
        this.controlLines = getControlLinesList();
        this.ctrlLineNum = calculateControlNum() + 2;
        this.divCtrlLineCollection = getDivCtrlLineCollection(this.controlLines);
        rotateBuildingCtrlLine = new ArrayList<>();
        normalBuildingCtrlLine = new ArrayList<>();
        this.sepDivLine = sepBuildingType(this.divCtrlLineCollection);
        this.norBuildings = getAllNorBuildings();
        this.rotateBuildings = getRotateBuildings();
        this.allBuildingBoundarys = getAllBuildingBoundarys();
        residenceBuildings = initialResidenceBuildings();
        turnIfInRed(residenceBuildings);
        getRemovedBuilding();
        greenOriginPolygon = new WB_Polygon();
//        initialGreen();
        roadLines = getRoadLine();
    }

    public void options() {
        for (ResidenceBuilding b : residenceBuildings) {
            b.checkBuildingInRedLine();
            b.setCenter();
            b.setCp();
            if (!b.ifInRedLine) {
                b.getInterPts();
                b.getMidP();
                b.setDir();
                b.getDirLine();
            }
        }
        this.creatPolygonWithHoles();
        this.initialGreen();
        this.moveBuildings();
        for (ResidenceBuilding b: residenceBuildings) {
            b.initialVols();
        }
//        this.roadLines = this.getRoadLine();
    }

    public WB_Polygon getRedLine(WB_Polygon boundary, double redLineDis) {
        List<WB_Polygon> list = gf.createBufferedPolygons(boundary, redLineDis * (-1));
        return list.get(0);
    }

    public WB_PolyLine getNorthControlLine(WB_PolyLine redLine, double buildHeight) {
        WB_Point p0 = redLine.getPoint(0);
        WB_Point p1 = redLine.getPoint(1);
        WB_Point p2 = redLine.getPoint(2);
        WB_Point p3 = redLine.getPoint(3);
        WB_Vector v1 = W_Tools.getUnitVector(p3, p0);
        WB_Vector v2 = W_Tools.getUnitVector(p2, p1);
        WB_Vector vv1 = v1.mul(buildHeight).mul(0.5);
        WB_Vector vv2 = v2.mul(buildHeight).mul(0.5);
        WB_Point pp1 = p0.add(vv1);
        WB_Point pp2 = p1.add(vv2);
        return new WB_PolyLine(pp1, pp2);
    }

    public WB_PolyLine getSouthControlLine(WB_PolyLine redLine, double buildHeight) {
        WB_Point p0 = redLine.getPoint(0);
        WB_Point p1 = redLine.getPoint(1);
        WB_Point p2 = redLine.getPoint(2);
        WB_Point p3 = redLine.getPoint(3);
        WB_Vector v1 = W_Tools.getUnitVector(p3, p0);
        WB_Vector v2 = W_Tools.getUnitVector(p2, p1);
        WB_Vector vv1 = v1.mul(buildHeight).mul(-0.5);
        WB_Vector vv2 = v2.mul(buildHeight).mul(-0.5);
        WB_Point pp1 = p3.add(vv1);
        WB_Point pp2 = p2.add(vv2);
        return new WB_PolyLine(pp1, pp2);
    }

    //得到纵向比较小的一个边
    public double getLeftDis() {
        double dis1 = this.redLine.getPoint(0).getDistance(this.redLine.getPoint(3)) - 2 * this.buildDepth;
        double dis2 = this.redLine.getPoint(1).getDistance(this.redLine.getPoint(2)) - 2 * this.buildDepth;
        return Math.min(dis1, dis2);
    }

    //计算剩余位置可以产生的控制线数目
    public int calculateControlNum() {
        return (int) ((getLeftDis() - this.buildingGap) / (this.buildingGap + this.buildingGap));
    }

    public double calculateGapDis() {
        return (getLeftDis() - (calculateControlNum() * this.buildDepth)) / (calculateControlNum() + 1);
    }

    //左侧控制线控制线之间的距离
    public double getGapDis1() {
        double dis1 = this.redLine.getPoint(0).getDistance(this.redLine.getPoint(3)) - 2 * this.buildDepth;
        return (dis1 - (calculateControlNum() * this.buildDepth)) / (calculateControlNum() + 1);
    }

    //右侧控制线控制线之间的距离
    public double getGapDis2() {
        double dis2 = this.redLine.getPoint(1).getDistance(this.redLine.getPoint(2)) - 2 * this.buildDepth;
        return (dis2 - (calculateControlNum() * this.buildDepth)) / (calculateControlNum() + 1);
    }

    //第n条线控制线（去掉第一条和最后一条）
    public WB_PolyLine getCtrlLine(int n) {
        WB_Point p0 = this.controlLineNorth.getPoint(0);
        WB_Point p1 = this.controlLineNorth.getPoint(1);
        double dis1 = (getGapDis1() + this.buildDepth) * n;
        double dis2 = (getGapDis2() + this.buildDepth) * n;
        WB_Point pp0 = redLine.getPoint(0);
        WB_Point pp1 = redLine.getPoint(1);
        WB_Point pp2 = redLine.getPoint(2);
        WB_Point pp3 = redLine.getPoint(3);
        WB_Vector v1 = W_Tools.getUnitVector(pp3, pp0);
        WB_Vector v2 = W_Tools.getUnitVector(pp2, pp1);
        return new WB_PolyLine(p0.add(v1.mul(dis1)), p1.add(v2.mul(dis2)));
    }

    public List<WB_PolyLine> getAllCtrlLines() {
        List<WB_PolyLine> lines = new ArrayList<>();
        for (int n = 0; n < calculateControlNum(); n++) {
            WB_PolyLine l = getCtrlLine(n + 1);
            lines.add(l);
        }
        return lines;
    }

    public List<WB_PolyLine> getControlLinesList() {
        WB_PolyLine ctrlLineN = getNorthControlLine(this.redLine, this.buildDepth);
        WB_PolyLine ctrlLineS = getSouthControlLine(this.redLine, this.buildDepth);
        List<WB_PolyLine> otherLines = getAllCtrlLines();
        List<WB_PolyLine> lines = new ArrayList<>();
        lines.add(ctrlLineN);
        lines.addAll(otherLines);
        lines.add(ctrlLineS);
        return lines;
    }


    public int calculateBuildNumIn1Line(WB_PolyLine controlLine) {
        double length = W_Tools.getDistance(controlLine.getPoint(0), controlLine.getPoint(1));
        return (int) ((length + this.widthDis) / (buildWidth + widthDis));
    }

    //计算横向楼与楼之间的间距
    public double realWidthDis(WB_PolyLine controlLine) {
        double length = W_Tools.getDistance(controlLine.getPoint(0), controlLine.getPoint(1));
        int n = calculateBuildNumIn1Line(controlLine);
        return (length - n * buildWidth) / (n - 1);
    }

    //通过分成的段数与楼间距来得到polyline的集合列表
    public List<WB_PolyLine> getPolylineListFromNum(WB_PolyLine controlLine) {
        int num = calculateBuildNumIn1Line(controlLine);
        double disReal = realWidthDis(controlLine);         //实际的楼间距
        WB_Vector unitVector = W_Tools.getUnitVector(controlLine.getPoint(1), controlLine.getPoint(0));
        WB_Vector vAdd = unitVector.mul(this.buildWidth + disReal);
        List<WB_PolyLine> lines = new ArrayList<>();
        WB_Point pOrigin = controlLine.getPoint(0);
        for (int n = 0; n < num - 1; n++) {
            WB_Point p0 = pOrigin.add(vAdd.mul(n));
            WB_Point p1 = pOrigin.add(vAdd.mul(n + 1));
            WB_PolyLine polyLine = new WB_PolyLine(p0, p1);
            lines.add(polyLine);
        }
        //计算最后一段(只有建筑宽度)
        WB_Point pEnd = controlLine.getPoint(1);
        WB_Point pp = pEnd.sub(unitVector.mul(buildWidth));
        WB_PolyLine lastLine = new WB_PolyLine(pp, pEnd);
        lines.add(lastLine);
        return lines;
    }

    public List<List<WB_PolyLine>> getDivCtrlLineCollection(List<WB_PolyLine> rawCtrlLines) {
        List<List<WB_PolyLine>> lineCollection = new ArrayList<>();
        for (WB_PolyLine ctrlLine : rawCtrlLines) {
            List<WB_PolyLine> singleList = getPolylineListFromNum(ctrlLine);
            lineCollection.add(singleList);
        }
        return lineCollection;
    }

    public List<WB_Point> getDivPoint() {
        List<WB_Point> pointList = new ArrayList<>();
        for (List<WB_PolyLine> lines : this.divCtrlLineCollection) {
            for (WB_PolyLine line : lines) {
                pointList.add(line.getPoint(0));
                pointList.add(line.getPoint(1));
            }
        }
        return pointList;
    }

    public WB_Point getCtrlPoint(WB_PolyLine controlLine) {
        WB_Point p = controlLine.getPoint(0);
        WB_Vector v = new WB_Vector(0, buildDepth * 0.5);
        return new WB_Point(p.sub(v));
    }

    public double getRotateAngle(WB_PolyLine controlLine) {
        WB_Point p0 = controlLine.getPoint(0);
        WB_Point p1 = controlLine.getPoint(1);
        WB_Vector direction = W_Tools.getUnitVector(p1, p0);
        WB_Vector origin = new WB_Vector(1, 0);
        WB_Vector v = direction.cross(origin);
        if (v.zd() > 0) {
            return direction.getAngle(origin) * (-1);
        } else {
            return direction.getAngle(origin);
        }
    }

    public WB_Polygon getRotatedBuilding(WB_PolyLine controlLine, double width, double depth) {
        double angle = getRotateAngle(controlLine);
        WB_Polygon rawBuilding = W_Tools.getPolygon(getCtrlPoint(controlLine), width, depth);
        WB_Transform2D transform2D = new WB_Transform2D();
        transform2D.addRotateAboutPoint(angle, controlLine.getPoint(0));
        rawBuilding.apply2DSelf(transform2D);
        return rawBuilding;
    }

    public List<WB_Polygon> getRotateBuildings() {
        List<WB_Polygon> rotateBuildings = new ArrayList<>();
        for (WB_PolyLine polyLine : rotateBuildingCtrlLine) {
            WB_Polygon building = getRotatedBuilding(polyLine, buildWidth, buildDepth);
            rotateBuildings.add(building);
        }
        return rotateBuildings;
    }


    public WB_Polygon getNorBuilding(WB_Point pt, double width, double depth) {
        WB_Vector v = new WB_Vector(0, depth);
        WB_Point p = pt.sub(v.mul(0.5));
        return new WB_Polygon(W_Tools.getPolygon(p, width, depth));
    }

    public List<WB_Polygon> getAllNorBuildings() {
        List<WB_Polygon> norBuildings = new ArrayList<>();
        for (WB_PolyLine l : normalBuildingCtrlLine) {
            WB_Point p = l.getPoint(0);
            WB_Polygon building = getNorBuilding(p, buildWidth, buildDepth);
            norBuildings.add(building);
        }
        return norBuildings;
    }

    //筛选出两种建筑的控制线，添加进一个list内（添加顺序：先旋转，后普通）
    public List<List<WB_PolyLine>> sepBuildingType(List<List<WB_PolyLine>> allDiv) {
        List<List<WB_PolyLine>> allB = new ArrayList<>();
//        List<WB_PolyLine> rotateB = new ArrayList<>();
//        List<WB_PolyLine> norB = new ArrayList<>();
        for (List<WB_PolyLine> ctrlLines : allDiv) {
            for (WB_PolyLine div : ctrlLines) {
                WB_Vector v = new WB_Vector(div.getPoint(0), div.getPoint(1));
                WB_Vector unitV = new WB_Vector(1, 0);
                double angle = v.getAngle(unitV);
                if (angle <= angleTol) {
                    rotateBuildingCtrlLine.add(div);
                } else {
                    normalBuildingCtrlLine.add(div);
                }
            }
        }
        allB.add(rotateBuildingCtrlLine);
        allB.add(normalBuildingCtrlLine);
        return allB;
    }

    public List<WB_Polygon> getAllBuildingBoundarys() {
        List<WB_Polygon> all = new ArrayList<>();
        all.addAll(rotateBuildings);
        all.addAll(norBuildings);
        return all;
    }

    public List<WB_Polygon> selInterSecBuildings(List<List<WB_Polygon>> allBuildings) {
        List<WB_Polygon> selBuildings = new ArrayList<>();
        List<List<WB_Point>> interPsList = new ArrayList<>();
        List<WB_Polygon> buildings = new ArrayList<>();
        for (List<WB_Polygon> bs : allBuildings) {
            buildings.addAll(bs);
        }
        for (WB_Polygon bui : buildings) {
            List<WB_Segment> segs = bui.toSegments();
            List<WB_Segment> redSegs = redLine.toSegments();
            List<WB_Point> interPs = new ArrayList<>();
            for (WB_Segment seg : segs) {
                for (WB_Segment reSeg : redSegs) {
                    WB_IntersectionResult interP = WB_GeometryOp.getIntersection2D(seg, reSeg);
                    WB_Point p = (WB_Point) interP.getObject();
                    interPs.add(p);
                }
            }
            if (interPs != null) {
                selBuildings.add(bui);
                interPsList.add(interPs);
            }
        }
        return selBuildings;
    }

    public WB_Polygon findClosestBoundary(WB_Polygon boundary) {
        WB_Polygon closestBoundary = new WB_Polygon();
        WB_Point center = boundary.getCenter();
        double disTemp = Integer.MAX_VALUE;
        for (WB_Polygon b : allBuildingBoundarys) {
            WB_Point p = WB_GeometryOp2D.getClosestPoint2D(center, (WB_PolyLine) b);
            double dis = p.getDistance2D(center);
            if (dis > 10 && dis < disTemp) {
                disTemp = dis;
                closestBoundary = b;
            }
        }
        return closestBoundary;
    }

    public List<ResidenceBuilding> initialResidenceBuildings() {
        List<ResidenceBuilding> residenceBuildingList = new ArrayList<>();
        for (int i = 0; i < this.allBuildingBoundarys.size(); i++) {
            WB_Polygon thisBuilding = allBuildingBoundarys.get(i);
            WB_Polygon closestB = findClosestBoundary(thisBuilding);
            System.out.println(closestB.getSignedArea());
            ResidenceBuilding building = new ResidenceBuilding
                    (i, this.allBuildingBoundarys.get(i), floorNum, floorHeight, redLine, buildingGap, app);
            residenceBuildingList.add(building);
        }
        return residenceBuildingList;
    }

    public void initialGreen() {
        green = new Green(greenOriginPolygon, dis1, dis2, roadLines, app);
    }

    public void turnIfInRed(List<ResidenceBuilding> buildings) {
        for (ResidenceBuilding building : buildings) {
            building.checkBuildingInRedLine();
        }
    }

    public void moveBuildings() {
        for (ResidenceBuilding building : residenceBuildings) {
            if (!building.ifInRedLine) {
                building.buildingMove();
            }
//            if(!building.ifFullDis){
//                building.disMove();
//            }
        }
    }


    public void getRemovedBuilding() {
        double wholeDistance = Integer.MAX_VALUE;
        ResidenceBuilding removeBuilding = null;
        System.out.println("size ; " + residenceBuildings.size());
        for (int j = 0; j < residenceBuildings.size(); j++) {
            double disTemp = 0;
            for (int i = 0; i < residenceBuildings.size(); i++) {
                double distance = residenceBuildings.get(j).boundary.getCenter().getDistance(residenceBuildings.get(i).boundary.getCenter());
                disTemp += distance;
            }
            if (disTemp < wholeDistance) {
                wholeDistance = disTemp;
                removeBuilding = residenceBuildings.get(j);
            }
        }
        residenceBuildings.remove(removeBuilding);
    }

    public void creatPolygonWithHoles() {
        WB_Coord[] shell = redLine.getPoints().toArray();
        greenOriginPolygon = new WB_Polygon(gf.createPolygonWithHoles(shell, getHolesList()));
    }


    public WB_Coord[][] getHolesList() {
        WB_Coord[][] holesList = new WB_Coord[residenceBuildings.size()][residenceBuildings.get(0).boundary.getPoints().size()];
        for (int i = 0; i < residenceBuildings.size(); i++) {
            WB_Coord[] hole = residenceBuildings.get(i).boundary.getPoints().toArray();
            WB_Coord[] reverse = W_Tools.reserve(hole);
            holesList[i] = reverse;
        }
        return holesList;
    }

    public List<WB_PolyLine> getRoadLine() {
        List<WB_PolyLine> lines = new ArrayList<>();
        for (ResidenceBuilding b : residenceBuildings) {
            List<WB_Point> closePts = new ArrayList<>();
            for (ResidenceBuilding bo : residenceBuildings) {
                double dis = b.center.getDistance2D(bo.center);
                if (dis <= roadDistance && dis > 10) {
                    closePts.add(bo.center);
                }
            }
            for (WB_Point p : closePts) {
                WB_PolyLine l = new WB_PolyLine(b.center, p);
                lines.add(l);
            }
        }
        return lines;
    }

//    public List<WB_PolyLine> getRoadLine() {
//        List<WB_PolyLine> lines = new ArrayList<>();
//        for (ResidenceBuilding b : residenceBuildings) {
//            List<WB_Point> closePts = new ArrayList<>();
//            for (WB_Polygon bo : allBuildingBoundarys) {
//                double dis = b.center.getDistance2D(bo.getCenter());
//                if (dis <= roadDistance && dis > 10) {
//                    closePts.add(bo.getCenter());
//                }
//            }
//            for (WB_Point p : closePts) {
//                WB_PolyLine l = new WB_PolyLine(b.center, p);
//                lines.add(l);
//            }
//        }
//        return lines;
//    }

    @Override
    public void display() {
        app.pushStyle();
        app.noFill();
        app.stroke(255, 0, 0);
        app.strokeWeight(2);
        wb_render.drawPolygonEdges(this.redLine);
        app.stroke(100, 0, 0, 50);
        //画控制线
        for (WB_PolyLine l : this.controlLines) {
            wb_render.drawPolyLine(l);
        }
        //画点
        app.noStroke();
        app.fill(0, 0, 100);

        for (ResidenceBuilding building : residenceBuildings) {
            building.display();
        }
//        app.stroke(0, 255, 0);
//        app.noFill();
//        wb_render.drawPolygonEdges(greenOriginPolygon);


//        app.strokeWeight(1);
//        app.stroke(255, 0, 100);
//        for (WB_PolyLine l : this.roadLines) {
//            wb_render.drawPolyLine(l);
//        }
        green.display();
        app.popStyle();
    }
}
