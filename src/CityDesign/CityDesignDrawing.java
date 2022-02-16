package CityDesign;

import Demo.MyImporter;
import DxfReader.DXFImporter;
import Tools.Exporter;
import Tools.W_Tools;
import gzf.gui.CameraController;
import gzf.gui.Vec_Guo;
import processing.core.PApplet;
import wblut.geom.WB_Coord;
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

public class CityDesignDrawing extends PApplet {
    public static void main(String[] args) {
        PApplet.main("CityDesign.CityDesignDrawing");
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
    List<WB_Polygon> river = new ArrayList<>();
    List<WB_Polygon> commercialPolygon = new ArrayList<>();
    List<WB_Polygon> residentPolygon = new ArrayList<>();
    List<WB_Polygon> residentTest = new ArrayList<>();
    List<WB_Polygon> towerBoundary = new ArrayList<>();
    List<WB_Polygon> stZoneBoundary = new ArrayList<>();
    List<WB_Polygon> pointResidenceBoundary = new ArrayList<>();
    List<WB_Polygon> publicPlace = new ArrayList<>();
    List<WB_Polygon> road = new ArrayList<>();
    List<WB_PolyLine> subRoad = new ArrayList<>();
    List<WB_Polygon> greenRoad = new ArrayList<>();

    //建筑图元
    List<Commercial> commercialList = new ArrayList<>();
    List<Residence> residenceList = new ArrayList<>();
    List<Residence> pointResidences = new ArrayList<>();
    List<ST_Zone> towers = new ArrayList<>();
    List<ST_Zone> stZones = new ArrayList<>();

    Exporter e = new Exporter();

    MyImporter importer;

    List<WB_Polygon> originLand;
    List<WB_Polygon> green;
    List<WB_Polygon> pod;
    List<WB_Polygon> tower;

    List<BuildingVol> podVol1 = new ArrayList<>();
    List<BuildingVol> towerVol1 = new ArrayList<>();


    public void settings() {
        size(1500, 1500, P3D);

//        size(3500, 2000, P3D);  //城市轴侧
//        size(2000, 2500, P3D);    //竖平面

//        size(3000, 2500, P3D);  //局部轴侧
//        size(2000, 3000, P3D);  //平面

    }


    public void setup() {
        guoCam = new CameraController(this, 3000);
        guoCam.getCamera().setFovy(1.2);
        importer = new MyImporter("E:\\INST.AAA\\Term-1\\CAD\\paperLowView.dxf");

//        //城市平面
//        guoCam.getCamera().setPosition(new Vec_Guo(5154.196385771471, -9036.546300268086, 47589.27891514476));
//        guoCam.getCamera().setLookAt(new Vec_Guo(5154.196385771471, -9036.546300268086, 0.0));
//        guoCam.getCamera().setPerspective(false);


////        城市轴侧
//        guoCam.getCamera().setPosition(new Vec_Guo(-8791.772617944645, -20417.95198473546, 20808.232047125413));
//        guoCam.getCamera().setLookAt(new Vec_Guo(4765.655940529182, -7197.047596612056, 139.34128403086953));
//        guoCam.getCamera().setPerspective(false);


////        局部轴侧
        guoCam.getCamera().setPosition(new Vec_Guo(-11285.491455612022, 1845.9151618297778, 14786.324767495662));
        guoCam.getCamera().setLookAt(new Vec_Guo( -395.9342379843291, 6078.444505928476, 223.9412963695952));
        guoCam.getCamera().setPerspective(false);

//        //局部平面
//        guoCam.getCamera().setPosition(new Vec_Guo(654.1434069102174, 10073.001312550077, 14602.378231382067));
//        guoCam.getCamera().setLookAt(new Vec_Guo( 1015.7522037062921, 7018.0563993559, -862.5148746319613));
//        guoCam.getCamera().setPerspective(false);

        render = new WB_Render(this);
//        dxfImporter = new DXFImporter("E:\\INST.AAA\\Term-1\\CAD\\CityDesignDrawing.dxf", UTF_8);   //中强度街区
//        dxfImporter = new DXFImporter("E:\\INST.AAA\\Term-1\\CAD\\diqiangduCityDesignDrawing.dxf", UTF_8);   //低强度街区
//        dxfImporter = new DXFImporter("E:\\INST.AAA\\Term-1\\CAD\\highBlock.dxf", UTF_8);   //高强度街区
        dxfImporter = new DXFImporter("E:\\INST.AAA\\Term-1\\CAD\\paperLowView.dxf", UTF_8);   //超高强度街区


//        dxfImporter = new DXFImporter("E:\\INST.AAA\\Term-1\\CAD\\CityDesign.dxf", UTF_8);
//        dxfImporter = new DXFImporter("E:\\INST.AAA\\Term-1\\CAD\\CityDesignHigh.dxf", UTF_8);
//        dxfImporter = new DXFImporter("E:\\INST.AAA\\Term-1\\CAD\\CityDesignLow.dxf", UTF_8);

        allBoundary = dxfImporter.getPolyLines("0edge");
        blocksAxis = dxfImporter.getPolyLines("3_blocksaxis");
        lake = dxfImporter.getPolygons("0lake");
        commercialPolygon = dxfImporter.getPolygons("commercial");
        roadCenter = dxfImporter.getPolyLines("0road_center");
        landscape = dxfImporter.getPolygons("3_landscape");
        landscapeLine = dxfImporter.getPolyLines("3_landscape");
        river = dxfImporter.getPolygons("0river");
        residentPolygon = dxfImporter.getPolygons("resident");
        residentTest = dxfImporter.getPolygons("residentTest");
        towerBoundary = dxfImporter.getPolygons("tower");
        stZoneBoundary = dxfImporter.getPolygons("stZone");
        pointResidenceBoundary = dxfImporter.getPolygons("point");
        publicPlace = dxfImporter.getPolygons("3_public");
        road = dxfImporter.getPolygons("road");
        subRoad = dxfImporter.getPolyLines("subRoad");
        greenRoad = dxfImporter.getPolygons("green");

        for (int i = 0; i < commercialPolygon.size(); i++) {
            WB_Polygon p = W_Tools.polygonFaceDown(commercialPolygon.get(i));
            Commercial commercialSingle = new Commercial(p, 50, 300, 5, this);
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

//        for (WB_Polygon p : towerBoundary) {
//            WB_Polygon p1 = W_Tools.polygonFaceDown(p);
//            ST_Zone tower = new ST_Zone(p1, 80, 300, 6, 500, 350, 23, 4, this);
//            towers.add(tower);
//        }

        for (WB_Polygon p : stZoneBoundary) {
            WB_Polygon p1 = W_Tools.polygonFaceDown(p);
            ST_Zone zone = new ST_Zone(p1, 50, 160, 1, 5, this);
            stZones.add(zone);
        }

        iniElements();
        iniBuildingVol();
    }



    public void draw() {
        background(255);
//        background(220);

//        lights();
        ambientLight(180, 180, 180);
        directionalLight(150, 150, 150, -0.1f, 0.4f, -0.6f);

        //地块总边界
        pushStyle();

//        stroke(255, 0, 0);
//        strokeWeight(2);
//        for (WB_PolyLine p : allBoundary) {
//            render.drawPolylineEdges(p);
//        }

        //地块轴线
        stroke(100, 50);
        strokeWeight(1);
        for (WB_PolyLine p : blocksAxis) {
            render.drawPolylineEdges(p);
        }

        //水体
        fill(85, 149, 159, 50);
        stroke(85, 149, 159);
        for (WB_Polygon p : lake) {
            render.drawPolygonEdges(p);
        }
        for (WB_Polygon p : river) {
            render.drawPolygonEdges(p);
        }

        //路
//        stroke(220);
//        strokeWeight(10);

        stroke(210);
        strokeWeight(6);
        for (WB_PolyLine p : roadCenter) {
            render.drawPolylineEdges(p);
        }

        stroke(230);
        strokeWeight(5);
        for (WB_PolyLine p : subRoad) {
            render.drawPolylineEdges(p);
        }

        //草
        fill(174, 192, 167);
        stroke(246, 231, 200);
        strokeWeight(4);
        for (WB_Polygon p : landscape) {
            render.drawPolygonEdges(p);
        }
        //外围草
        noStroke();
        for (WB_Polygon p : greenRoad) {
            render.drawPolygonEdges(p);
        }

        strokeWeight(1);
        stroke(145, 159, 114);
        fill(64, 87, 27);
        for (WB_PolyLine p : landscapeLine) {
            render.drawPolylineEdges(p);
        }

        fill(181, 85, 94, 50);
        noStroke();
        for (WB_Polygon p : publicPlace) {
            render.drawPolygonEdges(p);
        }


//        fill(255, 226, 148);
//        for (WB_Polygon p : road) {
//            render.drawPolygonEdges(p);
//        }

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


        for (BuildingVol vol : podVol1) {
            vol.display();
        }

        for (BuildingVol vol : towerVol1) {
            vol.display();
        }

        //场地范围
        landDrawing(originLand);

        //草
        greenDrawing(green);
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
                for (BuildingVol b : c.towerVols) {
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
            System.out.println("%%%%%%%" + pointResidences.size());


//            e.savePolygons(commercialSave, "commerical");
//            e.savePolygons(towerSave, "tower");
//            e.savePolygons(podSave, "pod");
//            e.savePolygons(stzoneSave, "stZone");
//            e.savePolygons(residenceSave, "residence");
//            e.savePolygons(greenSave, "green");
//            e.savePolygons(allBoundary, "boundary");
//            e.WB_PolyLineToICurve(allBoundary.get(0));
            e.savePolygons(pointSave, "point");
            e.savePolygons(pointGreenSave, "pointGreen");


            e.save("E:\\INST.AAA\\Term-1\\iGeoExport\\pointResidence.3dm");
        }
        if (keyPressed && key == 'a') {
            System.out.println("position " + guoCam.getCamera().getPosition());
            System.out.println("lookAt " + guoCam.getCamera().getLookAt());
            System.out.println("view " + guoCam.getCamera().getFovy());
        }

        if (keyPressed && key == 'w') {
            saveFrame("E:\\INST.AAA\\Term-1\\saveFrame\\outPut\\new\\pic-######.png");
        }



        //视角调整
        //城市平面
        if (keyPressed && key == '1') {
            guoCam.getCamera().setPosition(new Vec_Guo(5154.196385771471, -9036.546300268086, 47589.27891514476));
            guoCam.getCamera().setLookAt(new Vec_Guo(5154.196385771471, -9036.546300268086, 0.0));
            guoCam.getCamera().setPerspective(false);
        }

        //城市轴侧
        if (keyPressed && key == '2') {
            guoCam.getCamera().setPosition(new Vec_Guo(-8791.772617944645, -20417.95198473546, 20808.232047125413));
            guoCam.getCamera().setLookAt(new Vec_Guo(4765.655940529182, -7197.047596612056, 139.34128403086953));
            guoCam.getCamera().setPerspective(false);
        }

        //局部平面
        if (keyPressed && key == '3') {
            guoCam.getCamera().setPosition(new Vec_Guo(-9957.115801377406, 3104.091915149784, 9374.565487164631));
            guoCam.getCamera().setLookAt(new Vec_Guo( -1692.9890207023645, 6316.174684313012, -676.8832383520505));
            guoCam.getCamera().setPerspective(false);
        }

        //局部轴侧
        if (keyPressed && key == '4') {
        guoCam.getCamera().setPosition(new Vec_Guo(654.1434069102174, 10073.001312550077, 14602.378231382067));
        guoCam.getCamera().setLookAt(new Vec_Guo( 1015.7522037062921, 7018.0563993559, -862.5148746319613));
        guoCam.getCamera().setPerspective(false);
        }

        if (keyPressed && key == 'b') {
            saveFrame("E:\\INST.AAA\\paperPic\\pic-######.png");
        }

    }

    public void iniElements() {
        originLand = importer.getOriginLand();
        green = importer.getGreen();
        pod = importer.getPod();
        tower = importer.getTower();
    }

    public void iniBuildingVol() {
        randomSeed(1);

        for (WB_Polygon polygon : pod) {
            podVol1.add(new BuildingNew(polygon, 40, 6-random(0,4), 0, this));
        }

        for (WB_Polygon polygon : tower) {
            towerVol1.add(new BuildingNew(polygon, 40, (int)27-random(1,13), 0, this));
        }

    }

    private List<WB_Polygon> moveLand(List<WB_Polygon> polygons, double dis) {
        List<WB_Polygon> polys = new ArrayList<>();

        for (WB_Polygon polygon : polygons) {
            List<WB_Coord> coords = polygon.getPoints().toList();
            List<WB_Point> pts = new ArrayList<>();

            for (WB_Coord coord : coords) {
                WB_Point p = new WB_Point(coord.xd(), coord.yd(), coord.zd() - dis);
                pts.add(p);
            }
            polys.add(new WB_Polygon(pts));
        }
        return polys;
    }


    private void landDrawing(List<WB_Polygon> polygons) {
        pushStyle();
        fill(252, 227, 138, 30);
        noStroke();
        List<WB_Polygon> lands = moveLand(originLand, 2);
        for (WB_Polygon p : lands) {
            render.drawPolygon(p);
        }

        noFill();
        stroke(217, 83, 79, 80);
        strokeWeight(2);
        for (WB_Polygon p : lands) {
            render.drawPolygonEdges(p);
        }
        popStyle();
    }

    private void greenDrawing(List<WB_Polygon> polygons) {
        pushStyle();
        fill(157, 211, 168);
        stroke(246, 231, 200);
        strokeWeight(3);
        for (WB_Polygon p : green) {
            render.drawPolygonEdges(p);
        }
        popStyle();
    }

}
