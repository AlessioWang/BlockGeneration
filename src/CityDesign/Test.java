package CityDesign;

import Tools.TransTool;
import peasy.CameraState;
import peasy.PeasyCam;
import processing.core.PApplet;
import wblut.geom.WB_Point;
import wblut.geom.WB_PolyLine;
import wblut.geom.WB_Polygon;
import wblut.processing.WB_Render;

import java.util.ArrayList;
import java.util.List;

/**
 * @auther Alessio
 * @date 2020/12/29
 **/
public class Test extends PApplet {

    public static void main(String[] args) {
        PApplet.main("CityDesign.Test");
    }

    PeasyCam cam;
    CameraState state;
    WB_Polygon polygon;
    WB_Render render ;
    List<WB_PolyLine> lines = new ArrayList<>();
    WB_PolyLine l1;
    WB_PolyLine l2;
    List<WB_Polygon> polygons = new ArrayList<>();

    List<WB_Point> pts1 =new ArrayList<>();
    List<WB_Point> pts2 =new ArrayList<>();



    public void settings(){
        size(1200,1200, P3D);
    }

    public void setup(){
        cam = new PeasyCam(this,1200);
        state = cam.getState();
        render = new WB_Render(this);
        WB_Point p1 = new WB_Point(50, 50);
        WB_Point p2 = new WB_Point(50, 400);
        WB_Point p3 = new WB_Point(400, 400);
        WB_Point p4 = new WB_Point(400, 80);
        List<WB_Point> list = new ArrayList<>();
        list.add(p1);
        list.add(p2);
        list.add(p3);
        list.add(p4);
        polygon = new WB_Polygon(list);


        WB_Point pp1 = new WB_Point(0, 0);
        WB_Point pp2 = new WB_Point(500, 300);
        pts1.add(pp1);
        pts1.add(pp2);

        WB_Point pp3 = new WB_Point(200, 0);
        WB_Point pp4 = new WB_Point(200, 500);
        pts2.add(pp3);
        pts2.add(pp4);


        l1 = new WB_PolyLine(pts1);
        System.out.println(l1.getPoint(0));
        l2 = new WB_PolyLine(pts2);
        lines.add(l1);
        lines.add(l2);

        polygons = TransTool.getSplitRegions(polygon,lines);
    }

    public void draw(){
        background(255);
        render.drawPolygonEdges(polygon);
        for(WB_Polygon p: polygons ){
            render.drawPolygonEdges(p);
        }
    }

}
