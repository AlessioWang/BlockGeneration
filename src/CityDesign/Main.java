package CityDesign;

import processing.core.PApplet;
import wblut.geom.WB_Point;
import wblut.geom.WB_Polygon;

import java.util.ArrayList;
import java.util.List;

/**
 * @auther Alessio
 * @date 2020/12/24
 **/
public class Main extends PApplet{
    public static void main(String[] args) {
        PApplet.main("CityDesign.Main");
    }

    List<WB_Point> points = new ArrayList<>();
    BlockBoundary boundary;
    CrossDivideBlock crossDivideBlockX;
    CrossDivideBlock crossDivideBlockJING;
    List<BuildingGeneration> list1 = new ArrayList<>();

    double xx1 = 0.4;
    double xx2 = 0.4;
    double yy1 = 0.5;
    double yy2 = 0.5;

    double x1 = 0.3;
    double x2 = 0.6;
    double x3 = 0.3;
    double x4 = 0.6;
    double y1 = 0.4;
    double y2 = 0.7;
    double y3 = 0.4;
    double y4 = 0.7;

    public void settings(){
        size(500,500,P3D);
    }

    public void setup(){
        points = BlockBoundary.setBoundaryPoint();
        boundary = new BlockBoundary(points,this);
        crossDivideBlockX = new CrossDivideBlock(boundary.outline,xx1,xx2,yy1,yy2,this);
        crossDivideBlockJING = new CrossDivideBlock(boundary.outline,x1,x2,x3,x4,y1,y2,y3,y4,this);
        for(WB_Polygon boundary: crossDivideBlockX.crossDividedBlock){
            BuildingGeneration building1 = new BuildingGeneration(boundary,1,120,50,30,30,this);
            list1.add(building1);
        }
    }

    public void draw(){
        background(255);
        crossDivideBlockX.display();
//        crossDivideBlockJING.display();
        for(BuildingGeneration b1:list1){
            b1.display();
        }
        boundary.display();
    }


}
