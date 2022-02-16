package Demo;

import DxfReader.DXFImporter;
import wblut.geom.WB_Polygon;

import java.util.ArrayList;
import java.util.List;

/**
 * @auther Alessio
 * @date 13/7/2021
 **/
public class MyImporter {

    public static final String GBK = "gbk", UTF_8 = "utf-8";
    DXFImporter dxfImporter;
    String path ;

    List<WB_Polygon> originLand;
    List<WB_Polygon> green;
    List<WB_Polygon> pod;
    List<WB_Polygon> tower;

    public MyImporter(String path) {
        this.path = path;
        dxfImporter = new DXFImporter(path, UTF_8);
        getElementsFromDxf();
    }

    private void getElementsFromDxf(){
        originLand = dxfImporter.getPolygons("originLand");
        green = dxfImporter.getPolygons("green");
        pod = dxfImporter.getPolygons("pod");
        tower = dxfImporter.getPolygons("tower");
    }

    public List<WB_Polygon> getOriginLand() {
        return originLand;
    }

    public List<WB_Polygon> getGreen() {
        return green;
    }

    public List<WB_Polygon> getPod() {
        return pod;
    }

    public List<WB_Polygon> getTower() {
        return tower;
    }
}
