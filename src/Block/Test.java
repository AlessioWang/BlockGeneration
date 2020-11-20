package Block;

import processing.core.PApplet;
import wblut.geom.WB_Point;
import wblut.math.WB_Peak;
import wblut.processing.WB_Render;

import java.util.ArrayList;
import java.util.List;

/**
 * @auther Alessio
 * @date 2020/11/19
 **/
public class Test extends PApplet {
    public static void main(String[] args) {
        PApplet.main("Block.Test");
    }

    Boundary boundary;
    List<WB_Point> pointsList = new ArrayList<>();
    WB_Render wb_render;

    public void settings() {
        size(500, 500, P3D);
    }

    public void setup() {
        boundary = new Boundary(pointsList, this);
        wb_render = new WB_Render(this);
    }

    public void draw() {
            boundary.display(this);
    }

    public void mousePressed(){
        if (keyPressed && key == '1') {
            boundary.addPointByClick();
        }
    }

}
