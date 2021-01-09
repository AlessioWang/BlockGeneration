package CityDesign;

import processing.core.PApplet;
import wblut.geom.WB_GeometryFactory;
import wblut.geom.WB_Polygon;
import wblut.processing.WB_Render;

import java.util.List;

/**
 * @auther Alessio
 * @date 2021/1/9
 **/
public class Roof implements Display {
    WB_Render wb_render;
    PApplet app;
    WB_GeometryFactory gf;
    WB_Polygon boundary;
    double height;
    List<WB_Polygon> roofCombine;

    public Roof(WB_Polygon boundary, double height, PApplet applet) {
        this.app = applet;
        wb_render = new WB_Render(applet);
        gf = new WB_GeometryFactory();
        this.boundary = boundary;
        this.height = height;
    }





    @Override
    public void display() {

    }
}
