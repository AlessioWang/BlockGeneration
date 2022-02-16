package CityDesign;

import processing.core.PApplet;
import wblut.geom.WB_Polygon;

/**
 * @auther Alessio
 * @date 18/7/2021
 **/
public class BuildingNew extends BuildingVol{

    public BuildingNew(WB_Polygon outline, double floorHeight, double floorNum, double distance, PApplet applet) {
        super(outline, floorHeight, floorNum, distance, applet);
    }

    public void display(){
        app.pushStyle();
        app.strokeWeight(1);
        app.noStroke();
        if (this.dis == 0) {
            wb_render.drawPolygon(outline);
            //墙面颜色
            app.fill(153, 157, 174);
//            app.fill(255, 103, 104,70);

            for (WB_Polygon p : walls) {
                wb_render.drawPolygonEdges(p);
            }

//            app.fill(10, 10, 100, 20);
//            for (WB_Polygon p : floors) {
//                wb_render.drawPolygonEdges(p);
//            }

            app.fill(217, 245, 241);  //白色屋顶
//            app.fill(122, 125, 139);  //灰色屋顶

//            app.strokeWeight(1);
//            app.stroke(0);
//            app.fill(100);  //黑色屋顶
            wb_render.drawPolygonEdges(roof);
        }
        if (this.dis != 0) {
            for (WB_Polygon p : scaledVol) {
                wb_render.drawPolygonEdges(p);
            }
        }
        app.popStyle();
    }


}
