package CityDesign;

import DxfReader.DXFImporter;
import Tools.Exporter;
import Tools.W_Tools;
import gzf.gui.CameraController;
import gzf.gui.Vec_Guo;
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
 * @date 2020/12/27
 **/

public class CityDesign extends PApplet {
    public static void main(String[] args) {
        PApplet.main("CityDesign.CityDesign");
    }

    public static final String GBK = "gbk", UTF_8 = "utf-8";
    CameraController guoCam;
    WB_Render render;

    DXFImporter dxfImporter;
    List<WB_PolyLine> allBoundary = new ArrayList<>();
    List<WB_PolyLine> blocksAxis = new ArrayList<>();
    List<WB_PolyLine> roadCenter = new ArrayList<>();
    List<WB_Polygon> landscape = new ArrayList<>();
    List<WB_PolyLine> landscapeLine = new ArrayList<>();
    List<WB_Polygon> lake = new ArrayList<>();
    List<WB_Polygon> commercialPolygon = new ArrayList<>();
    List<WB_Polygon> residentPolygon = new ArrayList<>();
    List<WB_Polygon> residentTest = new ArrayList<>();
    List<WB_Polygon> towerBoundary = new ArrayList<>();
    List<WB_Polygon> stZoneBoundary = new ArrayList<>();
    List<WB_Polygon> pointResidenceBoundary = new ArrayList<>();
    List<WB_Polygon> publicPlace = new ArrayList<>();

    //建筑图元
    List<Commercial> commercialList = new ArrayList<>();
    List<Residence> residenceList = new ArrayList<>();
    List<Residence> pointResidences = new ArrayList<>();
    List<ST_Zone> towers = new ArrayList<>();
    List<ST_Zone> stZones = new ArrayList<>();

    Exporter e = new Exporter();


    public void settings() {
        size(1000, 1000, P3D);
    }

    public void setup() {
        guoCam = new CameraController(this, 3000);
        render = new WB_Render(this);
        //        commercialPolygon = dxfImporter.getPolygons("test");
        dxfImporter = new DXFImporter("E:\\INST.AAA\\Term-1\\CAD\\CityDesign.dxf", UTF_8);
        allBoundary = dxfImporter.getPolyLines("0edge");
        blocksAxis = dxfImporter.getPolyLines("3_blocksaxis");
        lake = dxfImporter.getPolygons("0lake");
        commercialPolygon = dxfImporter.getPolygons("commercial");
        roadCenter = dxfImporter.getPolyLines("0road_center");
        landscape = dxfImporter.getPolygons("3_landscape");
        landscapeLine = dxfImporter.getPolyLines("3_landscape");
        residentPolygon = dxfImporter.getPolygons("resident");
        residentTest = dxfImporter.getPolygons("residentTest");
        towerBoundary = dxfImporter.getPolygons("tower");
        stZoneBoundary = dxfImporter.getPolygons("stZone");
        pointResidenceBoundary = dxfImporter.getPolygons("point");
        publicPlace = dxfImporter.getPolygons("3_public");


        for (int i = 0; i < commercialPolygon.size(); i++) {
            WB_Polygon p = W_Tools.polygonFaceDown(commercialPolygon.get(i));
//            System.out.println("index : " + i);
            Commercial commercialSingle = new Commercial(p, 50, 300, 6, this);
            commercialList.add(commercialSingle);
        }

        for (int i = 0; i < residentTest.size(); i++) {
            WB_Polygon p = W_Tools.polygonFaceDown(residentTest.get(i));
            Residence residence = new Residence(p, 80, 550, 150, 0.8, 30, 90, 12, this);
            residenceList.add(residence);
        }

        for (int i = 0; i < pointResidenceBoundary.size(); i++) {
            WB_Polygon p = W_Tools.polygonFaceDown(pointResidenceBoundary.get(i));
            Residence residence = new Residence(p, 80, 250, 180, 0.6, 30, 90, 15, this);
            pointResidences.add(residence);
        }

        for (WB_Polygon p : towerBoundary) {
            WB_Polygon p1 = W_Tools.polygonFaceDown(p);
            ST_Zone tower = new ST_Zone(p1, 80, 300, 6, 500, 350, 23, 3, this);
            towers.add(tower);
        }

        for (WB_Polygon p : stZoneBoundary) {
            WB_Polygon p1 = W_Tools.polygonFaceDown(p);
            ST_Zone zone = new ST_Zone(p1, 50, 160, 6, 4, this);
            stZones.add(zone);
        }

    }

    public void draw() {
        background(255);
        //地块总边界
        pushStyle();
        stroke(255, 0, 0);
        strokeWeight(5);
        for (WB_PolyLine p : allBoundary) {
            render.drawPolylineEdges(p);
        }

        //地块轴线
        stroke(100);
        strokeWeight(1);
        for (WB_PolyLine p : blocksAxis) {
            render.drawPolylineEdges(p);
        }

        for (WB_Polygon p : residentTest) {
            render.drawPolygonEdges(p);
        }
        //水体
        stroke(0, 0, 200);
        for (WB_PolyLine p : lake) {
            render.drawPolylineEdges(p);
        }
        stroke(100);
        for (WB_PolyLine p : roadCenter) {
            render.drawPolylineEdges(p);
        }
        fill(0, 200, 0);
        for (WB_Polygon p : landscape) {
            render.drawPolygonEdges(p);
        }
        stroke(0, 200, 0);
        fill(0, 200, 0);
        for (WB_PolyLine p : landscapeLine) {
            render.drawPolylineEdges(p);
        }

        fill(20, 100, 150, 40);
        for (WB_Polygon p : publicPlace) {
            render.drawPolygonEdges(p);
        }
        popStyle();

        for (Commercial c : commercialList) {
            c.display();
        }

        for (Residence c : residenceList) {
            c.options();  //调用住宅的主方法
            c.display();
        }

        for (Residence c : pointResidences) {
            c.options();  //调用住宅的主方法
            c.display();
        }

        for (ST_Zone z : towers) {
            z.display();
        }

        for (ST_Zone z : stZones) {
            z.display();
        }
    }

    public void keyPressed() {
        if (keyPressed && key == 's') {
            List<WB_Polygon> greenSave = new ArrayList<>();
            List<WB_Polygon> commercialSave = new ArrayList<>();
            for (Commercial c : commercialList) {
                for (BuildingVol b : c.buildingVols) {
                    commercialSave.addAll(b.walls);
//                    commercialSave.addAll(b.floors);
                    commercialSave.add(b.roof);
                }
                if (c.green.greenZoneWithRoad != null) {
                    greenSave.addAll(c.green.greenZoneWithRoad);
                }
            }

            List<WB_Polygon> podSave = new ArrayList<>();
            List<WB_Polygon> towerSave = new ArrayList<>();
            for (ST_Zone c : towers) {
                for (BuildingVol b : c.buildingVols) {
                    podSave.addAll(b.walls);
//                    towerSave.addAll(b.floors);
                    podSave.add(b.roof);
                }
                for(BuildingVol b: c.towerVols){
                    towerSave.addAll(b.walls);
                    towerSave.add(b.roof);
                }
                if (c.green.greenZoneWithRoad != null) {
                    greenSave.addAll(c.green.greenZoneWithRoad);
                }
            }

            List<WB_Polygon> stzoneSave = new ArrayList<>();
            for (ST_Zone c : stZones) {
                for (BuildingVol b : c.buildingVols) {
                    podSave.addAll(b.walls);
//                    towerSave.addAll(b.floors);
                    podSave.add(b.roof);
                }
                if (c.green.greenZoneWithRoad != null) {
                    greenSave.addAll(c.green.greenZoneWithRoad);
                }
            }

            List<WB_Polygon> residenceSave = new ArrayList<>();
            for (Residence c : residenceList) {
                for (ResidenceBuilding bi : c.residenceBuildings) {
                    residenceSave.addAll(bi.buildingVols.walls);
                    residenceSave.add(bi.buildingVols.roof);
//                    residenceSave.addAll(bi.buildingVols.floors);
                }
                if (c.green.greenZoneWithRoad != null) {
                    greenSave.addAll(c.green.greenZoneWithRoad);
                }
            }

            List<WB_Polygon> pointSave = new ArrayList<>();
            List<WB_Polygon> pointGreenSave = new ArrayList<>();
            for (Residence c : pointResidences) {
                for (ResidenceBuilding bi : c.residenceBuildings) {
                    pointSave.addAll(bi.buildingVols.walls);
                    pointSave.add(bi.buildingVols.roof);
                }
                if (c.green.dividedGreens != null) {
                    pointGreenSave.addAll(c.green.dividedGreens);
                }
            }



//            e.savePolygons(commercialSave, "commerical");
//            e.savePolygons(towerSave, "tower");
//            e.savePolygons(podSave, "pod");
//            e.savePolygons(stzoneSave, "stZone");
//            e.savePolygons(residenceSave, "residence");
//            e.savePolygons(greenSave, "green");
//            e.savePolygons(allBoundary, "boundary");
//            e.WB_PolyLineToICurve(allBoundary.get(0));
            e.savePolygons(pointSave,"point");
            e.savePolygons(pointGreenSave,"pointGreen");


            e.save("E:\\INST.AAA\\Term-1\\iGeoExport\\pointResidence.3dm");
        }


    }


}
