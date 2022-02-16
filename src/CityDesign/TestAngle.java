package CityDesign;

import Demo.MyImporter;
import gzf.gui.CameraController;
import processing.core.PApplet;
import wblut.geom.*;
import wblut.processing.WB_Render;

import java.security.PrivateKey;
import java.util.*;

/**
 * @auther Alessio
 * @date 6/9/2021
 **/
public class TestAngle extends PApplet {

    public static final String GBK = "gbk", UTF_8 = "utf-8";
    String path = "E:\\INST.AAA\\paperPic\\angleTest.dxf";
    MyImporter myImporter;
    WB_Render render;
    CameraController guoCam;

    WB_Polygon origin;

    List<WB_Circle> showPoints;

    public static void main(String[] args) {
        PApplet.main("CityDesign.TestAngle");
    }

    public void settings() {
        size(800, 800, P3D);
    }

    public void setup() {
        guoCam = new CameraController(this, 20000);
        guoCam.top();
        background(255);
        myImporter = new MyImporter(path);
        render = new WB_Render(this);
        origin = myImporter.getOriginLand().get(0);
//        System.out.println(selAngle(0.5));

        List<WB_Coord[]> coordsList = cutPolyEdgeCoords(origin, selAngle(0.3), 2000);
        showPoints = getCirFromCoords(coordsList, 300);

    }

    public void draw() {
        background(255);

        strokeWeight(3);
        stroke(0);
        render.drawPolygonEdges(origin);

        pushStyle();
        fill(200,0,0);
        for(WB_Circle circle: showPoints){
            render.drawCircle(circle);
        }
        popStyle();

    }

    private List<Integer> selAngle(double tol) {
        List<Integer> coords = new ArrayList<>();
        List<WB_Coord> pts = origin.getPoints().toList();
        pts.remove(0);
        int size = pts.size();
        System.out.println(size);
        for (int a = 0; a < size; a++) {
            // 前一个点
            int b = (a + size - 1) % size;
            // 后一个点
            int c = (a + 1) % size;
            System.out.println(b + "," + a + "," + c);

            WB_Coord pta = pts.get(a);
            WB_Coord ptb = pts.get(b);
            WB_Coord ptc = pts.get(c);

            WB_Vector vec1 = new WB_Vector(pta, ptb);
            WB_Vector vec2 = new WB_Vector(pta, ptc);

            double angle = vec1.getAngle(vec2);
            if (angle < tol) {
                coords.add(a);
            }
        }
        return coords;
    }

    private List<WB_Coord[]> cutPolyEdgeCoords(WB_Polygon polygon, List<Integer> indexList, double dis) {
        List<WB_Coord> pts = origin.getPoints().toList();

        List<WB_Coord> selPts = new ArrayList<>();
        List<WB_Coord[]> coordsPairList = new ArrayList<>();

        for (int i : indexList) {
            selPts.add(pts.get(i));
            coordsPairList.add(getFormerAndLaterPts(polygon, i));
        }

        Map coordsMap = new HashMap();
        for (int i = 0; i < selPts.size(); i++) {
            coordsMap.put(selPts.get(i), coordsPairList.get(i));
        }

        List<WB_Coord[]> target = new ArrayList<>();
        for (WB_Coord coord : selPts) {
            WB_Coord[] pair = (WB_Coord[]) coordsMap.get(coord);
            WB_Vector vec0 = new WB_Vector(coord, pair[0]);
            WB_Vector vec1 = new WB_Vector(coord, pair[1]);
            vec0.normalizeSelf();
            vec1.normalizeSelf();

            WB_Coord p0 = ((WB_Point) coord).add(vec0.mul(dis));
            WB_Coord p1 = ((WB_Point) coord).add(vec1.mul(dis));

            WB_Coord[] newPair = new WB_Coord[2];
            newPair[0] = p0;
            newPair[1] = p1;
            target.add(newPair);
        }

        return target;
    }

    /**
     * 得到指定标号顶点前一个和后一个的顶点，[0]是前一个，[1]是后一个
     *
     * @param polygon
     * @param index
     * @return
     */
    private WB_Coord[] getFormerAndLaterPts(WB_Polygon polygon, int index) {
        List<WB_Coord> coordsList = polygon.getPoints().toList();
        int size = coordsList.size();

        // 前一个点
        int former = (index + size - 1) % size;
        // 后一个点
        int later = (index + 1) % size;

        WB_Coord[] coords = new WB_Coord[2];
        coords[0] = coordsList.get(former);
        coords[1] = coordsList.get(later);
        return coords;
    }

    private List<WB_Circle> getCirFromCoords(List<WB_Coord[]> coordsList, double rad) {
        List<WB_Circle> circles = new ArrayList<>();

        for (WB_Coord[] coords : coordsList) {
            circles.add(new WB_Circle(coords[0], rad));
            circles.add(new WB_Circle(coords[1], rad));
        }

        return circles;
    }

}
