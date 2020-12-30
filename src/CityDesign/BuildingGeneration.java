package CityDesign;

import Block.Buildings;
import Tools.W_Tools;
import com.vividsolutions.jts.geom.GeometryFactory;
import processing.core.PApplet;
import wblut.geom.*;
import wblut.processing.WB_Render;

/**
 * @auther Alessio
 * @date 2020/12/25
 **/
public class BuildingGeneration implements Display{

    PApplet app;
    WB_Polygon redLine;
    WB_Render wb_render;
    WB_Polygon buildingBoundary;
    int type ;
    double width;      //水平向的宽度
    double height;     //竖直向的宽度
    double disX;
    double disY;


    public BuildingGeneration(WB_Polygon boundary,int type, double width, double height, double disX, double disY, PApplet applet){
        wb_render = new WB_Render(applet);
        this.app = applet;
        this.redLine = boundary;
        this.type = type;
        this.width = width;
        this.height = height;
        this.disX = disX;
        this.disY = disY;

        if(type == 1){
            buildingBoundary = buildingType1(boundary,width,height,disX,disY);
        }
    }

    public WB_Polygon buildingType1 (WB_Polygon boundary, double width, double height, double disX, double disY){
        WB_Point pto = boundary.getPoint(0);
        WB_Vector v = new WB_Vector(disX,disY);
        WB_Point pt = pto.add(v);
        return W_Tools.getPolygon(pt,width,height);
    }

//    public WB_Polygon rotateBuilding(WB_Polygon building, double angle){
//        WB_Polygon outputBuilding =
//    }


    @Override
    public void display() {
        app.pushStyle();
        app.fill(0,0,100,50);
        app.stroke(0,0,0);
        app.strokeWeight(1);
        wb_render.drawPolygonEdges(this.buildingBoundary);
        app.popStyle();
    }
}
