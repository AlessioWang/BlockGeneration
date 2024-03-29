package CityDesign;

import DxfReader.DXFImporter;
import processing.core.PApplet;
import wblut.geom.WB_Point;
import peasy.*;
import wblut.geom.WB_Polygon;

import java.util.ArrayList;
import java.util.List;

/**
 * @auther Alessio
 * @date 2020/12/27
 **/

public class CityDesignTest extends PApplet {
    public static void main(String[] args) {
        PApplet.main("CityDesign.CityDesignTest");
    }

    public static final String GBK = "gbk", UTF_8 = "utf-8";
    PeasyCam cam;
    CameraState state;
    List<WB_Point> points = new ArrayList<>();
    BlockBoundary boundary;
    BlockBoundary commercialBoundary;
    List<WB_Point> pointsCom = new ArrayList<>();
    Residence residence;
    Commercial commercial;
    ST_Zone st_zone;
    ST_Zone st_zoneWithTower;
    DXFImporter dxfImporter;

    public void settings() {
        size(1000, 1000, P3D);
    }

    public void setup() {
        cam = new PeasyCam(this, 1200);
        state = cam.getState();
        dxfImporter = new DXFImporter("E:\\INST.AAA\\Term-1\\CAD\\finalBlocks_0107.dxf", UTF_8);
        List<WB_Polygon> commercialPolygon = dxfImporter.getPolygons("3_commercial");


        points = BlockBoundary.set1000BoundaryPoint();
        boundary = new BlockBoundary(points, this);
        pointsCom = BlockBoundary.setCommercialBoundaryPoint();
        commercialBoundary = new BlockBoundary(pointsCom, this);
        System.out.println(boundary.outline.getNormal().toString());
        residence = new Residence
                (boundary.outline, 100, 500, 150, 1, 30, 100, 10, this);
        commercial = new Commercial
                (commercialBoundary.outline, 60, 6, 300, this);
        st_zone = new ST_Zone
                (commercialBoundary.outline, 80, 400, 6,4, this);
        st_zoneWithTower = new ST_Zone
                (commercialBoundary.outline, 80, 400, 6, 500, 400, 23,3, this);

    }

    public void draw() {
        background(255);
//-----------显示住宅-----------------
//        residence.options();  //调用住宅的主方法
//        residence.display();  //显示住宅
//        boundary.display();   //住宅的boundary
//-----------显示商业-----------------
        commercialBoundary.display();
        commercial.display();
//-----------显示科技园区-----------------
//        st_zone.display();
//        st_zoneWithTower.display();
    }


}
