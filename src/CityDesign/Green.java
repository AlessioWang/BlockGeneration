package CityDesign;

import Tools.TransTool;
import Tools.W_Tools;
import org.locationtech.jts.geom.Polygon;
import org.locationtech.jts.simplify.DouglasPeuckerSimplifier;
import processing.core.PApplet;
import wblut.geom.*;
import wblut.hemesh.HEC_Creator;
import wblut.hemesh.HE_Mesh;
import wblut.processing.WB_Render;


import java.lang.reflect.Modifier;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @auther Alessio
 * @date 2021/1/3
 **/
public class Green implements Display {

    WB_Render wb_render;
    PApplet app;
    WB_GeometryFactory gf = new WB_GeometryFactory();
    WB_Polygon originPolygon;
    double dis1;
    double dis2;
    List<WB_Polygon> basicPolygon;
    List<WB_Polygon> simplyPolygons;
    List<WB_Polygon> greenZone;
    List<WB_SimpleMesh> meshes;
    List<WB_PolyLine> roadLines;
    List<WB_Polygon> dividedGreens;
    List<WB_Polygon> greenZoneWithRoad;
    double roadWidth = 30;
    List<WB_Point> allp = new ArrayList<>();

    //住宅草地
    public Green(WB_Polygon originPolygon, double dis1, double dis2, List<WB_PolyLine> roadLines, PApplet applet) {
        this.app = applet;
        wb_render = new WB_Render(applet);
        this.originPolygon = originPolygon;
        this.dis1 = dis1;
        this.dis2 = dis2;
        this.basicPolygon = setBasicPolygon();
        this.simplyPolygons = simply(this.basicPolygon, 30);
        this.greenZone = setGreenZone();
        this.roadLines = roadLines;
        this.dividedGreens = getDividedGreens();
//        this.greenZoneWithRoad = getGreenZoneWithRoad(this.dividedGreens, roadWidth);
        this.allp = getAllP();
//        System.out.println("div : " + dividedGreens.size());
    }


    //商业地块的草地
    public Green(WB_Polygon originPolygon, List<WB_PolyLine> roadLines, double roadWidth, double minArea, PApplet applet) {
        this.app = applet;
        wb_render = new WB_Render(applet);
        this.originPolygon = originPolygon;
        this.roadLines = roadLines;
        this.greenZoneWithRoad = getDivGreens(originPolygon, roadLines, minArea, roadWidth);
    }


    public List<WB_Point> getAllP() {
        List<WB_Point> allp = new ArrayList<>();
        for (WB_Polygon p : dividedGreens) {
            List<WB_Coord> cs = p.getPoints().toList();
            for (WB_Coord c : cs) {
                allp.add((WB_Point) c);
            }
        }
        return allp;
    }


    //商业
    public List<WB_Polygon> getDivGreens(WB_Polygon back, List<WB_PolyLine> cutters, double minArea, double streetWidth) {
        List<WB_Polygon> greensSel = new ArrayList<>();
        List<WB_Polygon> backs = new ArrayList<>();
        backs.add(back);
        List<WB_Polygon> divs = W_Tools.splitPolygonWithPolylineList(backs, cutters);
        for (WB_Polygon p : divs) {
            double area = Math.abs(p.getSignedArea());
            if (area > minArea) {
                greensSel.add(p);
            }
        }
        List<WB_Polygon> polygons = getGreenZoneWithRoad(greensSel, streetWidth);
        return polygons;
    }


    public List<WB_Polygon> setBasicPolygon() {
        return gf.createBufferedPolygons(originPolygon, dis1, 0);
    }

    public List<WB_Polygon> simply(List<WB_Polygon> wb_polygons, double tol) {
        List<WB_Polygon> output = new ArrayList<>();
        for (WB_Polygon wb : wb_polygons) {
            for (int i = 0; i < wb.getNumberOfPoints(); i++) {
//                System.out.println(wb.getPoint(i).toString());
            }
            Polygon polygon = W_Tools.WB_PolygonToJtsPolygon(wb);
            DouglasPeuckerSimplifier dgSim = new DouglasPeuckerSimplifier(polygon);
            dgSim.setDistanceTolerance(tol);
            polygon = (Polygon) dgSim.getResultGeometry();
            WB_Polygon outWbPolygon = W_Tools.JtsPolygonToWB_Polygon(polygon);
            output.add(outWbPolygon);
        }
        return output;
    }

    public List<WB_Polygon> setGreenZone() {
        return gf.createBufferedPolygons(simplyPolygons, dis2, 0);
    }

    public List<WB_SimpleMesh> createMesh() {
        List<WB_SimpleMesh> meshes1 = new ArrayList<>();
        for (WB_Polygon p : greenZone) {
            meshes1.add(gf.createMesh(p, 100));
        }

        return meshes1;
    }

//    private List<WB_Polygon> getDividedGreens() {
//        List<WB_Polygon> allPolygons = new ArrayList<>();
//        roadLines = W_Tools.getShortedPolylines(roadLines, 50);
//        for (WB_Polygon p : greenZone) {
//            List<WB_Polygon> polygons = TransTool.getSplitRegions(p, roadLines);
//            allPolygons.addAll(polygons);
//        }
//        return allPolygons;
//    }

    private List<WB_Polygon> getDividedGreens() {
        List<WB_Polygon> out = new ArrayList<>();
        List<WB_Polygon> allPolygons = new ArrayList<>();
        roadLines = W_Tools.getShortedPolylines(roadLines, 250);
        allPolygons = W_Tools.splitPolygonWithPolylineList(greenZone, roadLines);
        for (WB_Polygon g : allPolygons) {
            if (Math.abs(g.getSignedArea()) > 2000) {
                out.add(g);
            }
        }
        return allPolygons;
    }


    public List<WB_Polygon> getGreenZoneWithRoad(List<WB_Polygon> greens, double width) {
        List<WB_Polygon> out = new ArrayList<>();
        for (WB_Polygon green : greens) {
            if (green != null) {
                List<WB_Polygon> grns = gf.createBufferedPolygons(green, width * (-0.5));
                for (WB_Polygon g : grns) {
                    if (Math.abs(g.getSignedArea()) > 3000) {
                        out.add(g);
                    }
                }
            }
        }
        return out;
    }


    @Override
    public void display() {
        app.pushStyle();
        app.noStroke();
//        app.fill(0, 120, 0, 70);
//        app.strokeWeight(2);
//        for (WB_Polygon polygon : basicPolygon) {
//            wb_render.drawPolygonEdges(polygon);
//        }

        app.noStroke();
        app.fill(174, 192, 167);   //原本
//        app.fill(255);



//        for (WB_PolyLine l : roadLines) {
//            wb_render.drawPolylineEdges(l);
//        }


        if (dividedGreens != null) {
            for (WB_Polygon polygon : dividedGreens) {
                wb_render.drawPolygonEdges(polygon);
            }
        }

        if (greenZoneWithRoad != null) {
            for (WB_Polygon polygon : greenZoneWithRoad) {
                wb_render.drawPolygonEdges(polygon);
            }
        }

//        for (WB_Point p : allp) {
//            wb_render.drawPoint(p,20);
//        }

        app.popStyle();
    }
}
