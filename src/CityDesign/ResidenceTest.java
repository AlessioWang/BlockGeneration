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

public class ResidenceTest extends PApplet {
    public static void main(String[] args) {
        PApplet.main("CityDesign.ResidenceTest");
    }


    PeasyCam cam;
    CameraState state;
    List<WB_Point> points = new ArrayList<>();
    BlockBoundary boundary;
    Residence residence;
    public void settings() {
        size(1200, 1200, P3D);
    }

    public void setup() {
        cam = new PeasyCam(this, 1200);
        state = cam.getState();
        points = BlockBoundary.set1000BoundaryPoint();
        boundary = new BlockBoundary(points, this);
        residence = new Residence
                (boundary.outline, 150, 600, 170, 1, 30, 100, 6, this);
    }

    public void draw() {
        background(255);
        residence.options();
//        for (ResidenceBuilding b : residence.residenceBuildings) {
//            b.checkBuildingInRedLine();
//            b.setCenter();
//            b.setCp();
//            if(!b.ifInRedLine) {
//                b.getInterPts();
//                b.getMidP();
//                b.setDir();
//                b.getDirLine();
//            }
//        }
//        residence.creatPolygonWithHoles();
//        residence.initialGreen();
//        residence.moveBuildings();
//        residence.roadLines = residence.getRoadLine();
        residence.display();
        boundary.display();
    }


}
