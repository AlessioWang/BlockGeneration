package CityDesign;

import processing.core.PApplet;
import wblut.geom.WB_Point;
import peasy.*;

import java.util.ArrayList;
import java.util.List;

/**
 * @auther Alessio
 * @date 2020/12/27
 **/

public class CityDesign extends PApplet {
    public static void main(String[] args) {
        PApplet.main("CityDesign.CityDesign");
    }


    PeasyCam cam;
    CameraState state;
    List<WB_Point> points = new ArrayList<>();
    BlockBoundary boundary;
    BlockBoundary commercialBoundary;
    List<WB_Point> pointsCom = new ArrayList<>();
    Residence residence;
    Commercial commercial;

    public void settings() {
        size(1200, 1200, P3D);
    }

    public void setup() {
        cam = new PeasyCam(this, 1200);
        state = cam.getState();
        points = BlockBoundary.set1000BoundaryPoint();
        boundary = new BlockBoundary(points, this);
        pointsCom = BlockBoundary.setCommercialBoundaryPoint();
        commercialBoundary = new BlockBoundary(pointsCom,this);
        residence = new Residence
            (boundary.outline, 100, 500, 150, 1, 30, 100, 10, this);
        commercial = new Commercial
            (commercialBoundary.outline,60,6,300,5,100,this);
    }

    public void draw() {
        background(255);
//        residence.options();  //调用住宅的主方法
//        residence.display();  //显示住宅
//        boundary.display();   //住宅的display

        commercialBoundary.display();
        commercial.display();
    }


}
