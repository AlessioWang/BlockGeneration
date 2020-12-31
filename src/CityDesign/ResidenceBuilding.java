package CityDesign;

import Tools.W_Tools;
import processing.core.PApplet;
import wblut.geom.*;
import wblut.processing.WB_Render;

import java.util.List;

/**
 * @auther Alessio
 * @date 2020/12/30
 **/
public class ResidenceBuilding implements Display{

    WB_Render wb_render;
    PApplet app;
    WB_Polygon boundary;
    double floorNum;
    double floorHeight;
    int index;
    boolean ifInRedLine;
    WB_Polygon redLine;

    public ResidenceBuilding(int index, WB_Polygon boundary,double floorNum, double floorHeight,WB_Polygon redLine, boolean ifInRedLine, PApplet applet){
        this.app = applet;
        wb_render = new WB_Render(applet);
        this.index = index;
        this.boundary = boundary;
        this.floorNum = floorNum;
        this.floorHeight = floorHeight;
        this.ifInRedLine = ifInRedLine;
        this.redLine = redLine;
    }

    public void moveDir(WB_Vector direction, double step){
        WB_Vector v= direction.div(direction.getLength());
        WB_Transform2D t = new WB_Transform2D().addTranslate2D(v.mul(step));
        this.boundary.apply2DSelf(t);
    }

    public void moveRandom(double step){
        double random = Math.random();
        WB_Vector v;
        if(random<0.25){
            v = new WB_Vector(0,1);
        }else if(random<0.5 && random >0.25){
            v = new WB_Vector(0,-1);
        }else if(random<0.75 && random >0.5){
            v = new WB_Vector(1,0);
        }else {
            v = new WB_Vector(-1,0);
        }
        WB_Transform2D t = new WB_Transform2D().addTranslate2D(v.mul(step));
        this.boundary.apply2DSelf(t);
    }

    public void ifInRedLine(ResidenceBuilding building){
        WB_Polygon boundary = building.boundary;
        WB_Point center = boundary.getCenter();
        WB_Point cp = WB_GeometryOp.getClosestPoint3D(center,redLine);
        double dis = center.getDistance2D(boundary.getPoint(0));
        double dis2red = center.getDistance2D(cp);
        if(dis2red<dis){
            building.ifInRedLine = false;
        }
    }

    public void turnIfInRed(List<ResidenceBuilding> buildings){
        for(ResidenceBuilding building: buildings){
            ifInRedLine(building);
        }
    }





    @Override
    public void display() {
        app.pushStyle();
        app.noFill();
        app.stroke(0, 0, 50,70);
        app.strokeWeight(2);
        wb_render.drawPolygonEdges(this.boundary);
        app.popStyle();
    }
}
