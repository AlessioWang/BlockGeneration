package Demo;

import CityDesign.BuildingVol;
import gzf.gui.CameraController;
import gzf.gui.Vec_Guo;
import processing.core.PApplet;
import wblut.geom.WB_Coord;
import wblut.geom.WB_Point;
import wblut.geom.WB_Polygon;
import wblut.processing.WB_Render;

import java.util.ArrayList;
import java.util.List;

/**
 * @auther Alessio
 * @date 13/7/2021
 **/
public class PaperDrawing extends PApplet {

    public static void main(String[] args) {
        PApplet.main("Demo.PaperDrawing");
    }

    CameraController guoCam;
    WB_Render render;
    MyImporter importer;

    List<WB_Polygon> originLand;
    List<WB_Polygon> green;
    List<WB_Polygon> pod;
    List<WB_Polygon> tower;

    List<BuildingVol> podVol1 = new ArrayList<>();
    List<BuildingVol> towerVol1 = new ArrayList<>();

    BuildingVol podVol2;
    BuildingVol towerVol2;

    BuildingVol podVol3;
    BuildingVol towerVol3;


    public void settings() {
        size(1500, 1500, P3D);
    }

    public void setup() {
        guoCam = new CameraController(this, 1000);
        guoCam.getCamera().setFovy(1.2);
        render = new WB_Render(this);

        guoCam.getCamera().setPosition(new Vec_Guo(3895.2455115384396, 1746.4724182943353, 1570.693730772855));
        guoCam.getCamera().setLookAt(new Vec_Guo(1834.613749079417, 249.33580830922588, -974.9996705204136));
        guoCam.getCamera().setPerspective(false);


        importer = new MyImporter("C:\\Users\\Alessio\\Desktop\\cadDemo\\paper1.dxf");

        iniElements();
        iniBuildingVol();
    }

    public void draw() {
        background(255);
        ambientLight(180, 180, 180);
        directionalLight(150, 150, 150, -0.3f, -0.6f, -0.6f);


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


    public void iniElements() {
        originLand = importer.getOriginLand();
        green = importer.getGreen();
        pod = importer.getPod();
        tower = importer.getTower();
    }

    public void iniBuildingVol() {
        randomSeed(1);

        for (WB_Polygon polygon : pod) {
            podVol1.add(new BuildingVol(polygon, 40, 6-random(0,3), 0, this));
        }

        for (WB_Polygon polygon : tower) {
            towerVol1.add(new BuildingVol(polygon, 40, (int)25-random(1,20), 0, this));
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

    public void keyPressed(){
        if (keyPressed && key == 'a') {
            System.out.println("position " + guoCam.getCamera().getPosition());
            System.out.println("lookAt " + guoCam.getCamera().getLookAt());
            System.out.println("view " + guoCam.getCamera().getFovy());
        }

        if (keyPressed && key == 's') {
            saveFrame("E:\\INST.AAA\\paperPic\\pic-######.png");
        }
    }


}
