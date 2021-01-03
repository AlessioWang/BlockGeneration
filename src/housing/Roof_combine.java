package housing;

import processing.core.PApplet;
import wblut.geom.WB_Coord;
import wblut.geom.WB_Point;
import wblut.geom.WB_Polygon;
import wblut.geom.WB_Vector;
import wblut.processing.WB_Render3D;

import java.util.ArrayList;
import java.util.List;

public class Roof_combine extends Roof {
    List<WB_Coord> Opoints;
    ArrayList<WB_Polygon> unitlines;
    ArrayList<Topunit> unitRoofs;
    ArrayList<WB_Point> outpoints;
    ArrayList<Integer> colors = new ArrayList<>();

    Roof_combine(WB_Polygon _outline, float _buildingheight) {
        super(_outline, _buildingheight);
        init();
    }

    Roof_combine(WB_Point[] ps, float _buildingheight) {
        super(new WB_Polygon(Z_Tool.ArraytoList(ps)), _buildingheight);
        init();
    }

    Roof_combine(ArrayList<WB_Point> ps, float _buildingheight) {
        super(new WB_Polygon(ps), _buildingheight);
        init();
    }

    public void init() {
        unitRoofs = new ArrayList<>();
        Opoints = outline.getPoints().toList();
        num = Opoints.size();
        unitlines = new ArrayList<>();
        findConcaves();
        divide(outline);
        for (WB_Polygon poly : unitlines) {
        	List<WB_Coord> coords = poly.getPoints().toList();
            ArrayList<WB_Point> points = new ArrayList<>();
            for (WB_Coord c : coords) {
                points.add(new WB_Point(c));
            }
            unitRoofs.add(new Topunit(points));
            colors.add(Z_Tool.ranint(0XFF000000, 0XFFFFFFFF));
        }
    }


    private void findConcaves() {
        outpoints = new ArrayList<>();
        int i1, i2, i;
//		List<WB_Point> ps = outline.getPoints();
        List<WB_Coord> coords = outline.getPoints().toList();
        ArrayList<WB_Point> ps = new ArrayList<>();
        for (WB_Coord c : coords) {
            ps.add(new WB_Point(c));
        }

        WB_Vector v1 = ps.get(0).sub(ps.get(ps.size() - 1));
        WB_Vector v2 = null;
        for (i = 0; i < ps.size(); i++) {
            v2 = ps.get((i + 1) % ps.size()).sub(ps.get(i));
            /**
             * System.out.println(v1); System.out.println(v2); System.out.println('s');
             * System.out.println(MLZ.MLZ.getAngle(v1, v2,new WB_Vector(0,0,1)));
             */
            if (Z_Tool.getAngle(v1, v2, new WB_Vector(0, 0, 1)) > Math.PI) {
                outpoints.add(ps.get(i));
            }
            v1 = v2;
        }
    }

    private int findfirstConcave(WB_Polygon poly) {
        int i1, i2, i;
//		List<WB_Point> ps = poly.getPoints();
        List<WB_Coord> coords = outline.getPoints().toList();
        ArrayList<WB_Point> ps = new ArrayList<>();
        for (WB_Coord c : coords) {
            ps.add(new WB_Point(c));
        }
        WB_Vector v1 = ps.get(0).sub(ps.get(ps.size() - 1));
        WB_Vector v2 = null;
        for (i = 0; i < ps.size(); i++) {
            v2 = ps.get((i + 1) % ps.size()).sub(ps.get(i));
            if (Z_Tool.getAngle(v1, v2, new WB_Vector(0, 0, 1)) > Math.PI) {
                break;
            }
            v1 = v2;
        }
        return i;
    }

    private void divide(WB_Polygon poly) {

//		List<WB_Point> pts = poly.getPoints();
        List<WB_Coord> coords = outline.getPoints().toList();
        ArrayList<WB_Point> pts = new ArrayList<>();
        for (WB_Coord c : coords) {
            pts.add(new WB_Point(c));
        }

        int num = poly.getNumberOfPoints();
        if (poly.getNumberOfPoints() <= 4) {
            unitlines.add(poly);
        } else {
            int i1 = findfirstConcave(poly);
            //System.out.println("postion" + i1);
            WB_Point A = pts.get(i1);
            WB_Point B = A.add(A.sub(pts.get((i1 - 1) % num)).mul(10000));
            WB_Point newp = new WB_Point(0, 0, 0);
            int ic = 0, id = 0;
            boolean flag = false;
            for (int i = 1; i < num; i++) {
                ic = (i1 + i) % num;
                id = (ic + 1) % num;
                if (Z_Tool.IsIntersected_Segments(A, B, pts.get(ic), pts.get(id))) {
                    newp = Z_Tool.getIntersectionPoint2Lines(A, B, pts.get(ic), pts.get(id));
                    // System.out.println(newp);
                    flag = true;
                    break;
                }
            }
            if (!flag) {
                unitlines.add(poly);
                return;
            }
            ArrayList<WB_Point> newps1 = new ArrayList<>();
            newps1.add(newp);
            for (int i = id; i != i1; i = (i + 1) % num) {
                newps1.add(pts.get(i));
            }
            divide(new WB_Polygon(newps1));
            /***/
            ArrayList<WB_Point> newps2 = new ArrayList<>();
            for (int i = i1; i != id; i = (i + 1) % num) {
                newps2.add(pts.get(i));
            }
            newps2.add(newp);
            divide(new WB_Polygon(newps2));
            // System.out.println(newps2);
            /***/
        }
    }

    private void divide_simple() {
        int i1 = findfirstConcave(outline);
        WB_Point A = (WB_Point) Opoints.get(i1);
        WB_Point B = A.add(A.sub(Opoints.get((i1 - 1) % num)).mul(10000));
        WB_Point newp = new WB_Point(0, 0, 0);
        int ic = 0, id = 0;
        for (int i = 1; i < num; i++) {
            ic = (i1 + i) % num;
            id = (ic + 1) % num;
            if (Z_Tool.IsIntersected_Segments(A, B, (WB_Point) Opoints.get(ic), (WB_Point) Opoints.get(id))) {
                newp = Z_Tool.getIntersectionPoint2Lines(A, B, (WB_Point) Opoints.get(ic), (WB_Point) Opoints.get(id));
                // System.out.println(newp);
                break;
            }
        }
        ArrayList<WB_Point> newps1 = new ArrayList<>();
        newps1.add(newp);
        for (int i = id; i != i1; i = (i + 1) % num) {
            newps1.add((WB_Point) Opoints.get(i));
        }
        unitlines.add(new WB_Polygon(newps1));
        /***/
        ArrayList<WB_Point> newps2 = new ArrayList<>();
        for (int i = i1; i != id; i = (i + 1) % num) {
            newps2.add((WB_Point) Opoints.get(i));
        }
        newps2.add(newp);
        unitlines.add(new WB_Polygon(newps2));
        // System.out.println(newps2);
        /***/
    }

    @Override
    public void display(PApplet app, WB_Render3D hpp) {
        hpp.drawPolyLine(outline);
        int i = 0;
        for (WB_Polygon poly : unitlines) {

            hpp.drawPolygon(poly);

        }
        // System.out.println(unitlines.size());
        // unitRoofs.get(0).display(app);
        // System.out.println(outpoints.size());
//		for (WB_Point p : outpoints) {
//			hpp.drawPoint(p, 10);
//		}
        // hpp.drawPolyLine(outline);
        app.pushMatrix();
        app.translate(0, 0, buildingheight);
        for (Topunit uroof : unitRoofs) {
            app.fill(colors.get(i));
            uroof.display(app);
            i++;
        }
        app.popMatrix();
    }
}
