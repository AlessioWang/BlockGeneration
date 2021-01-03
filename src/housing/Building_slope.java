package housing;

import processing.core.PApplet;
import wblut.geom.WB_Coord;
import wblut.geom.WB_Point;
import wblut.geom.WB_Polygon;
import wblut.processing.WB_Render3D;

import java.util.ArrayList;
import java.util.List;

public class Building_slope extends BuildingStyle {
	public Building_slope(WB_Polygon _outline, float _floorheight, int _floornum) {
		super(_outline, _floorheight, _floornum);
//		List<WB_Point> points = outline.getPoints();
		List<WB_Coord> coords =outline.getPoints().toList();
		ArrayList<WB_Point> points = new ArrayList<>();
		for (WB_Coord c:coords){
			points.add(new WB_Point(c));
		}
		// if (points.size() < 5)
		// roof = new Roof(outline, floorheight * floornum);
		// else
		roof = new Roof_combine(outline, floorheight*floornum);
		floors = new ArrayList<>();
		/** initialize floors */
		for (int i = 0; i < floornum; i++) {
			List<WB_Point> pts = new ArrayList<>();
			for (WB_Point p : points) {
				pts.add(new WB_Point(p.xf(), p.yf(), p.zf() + floorheight * i));
			}
			floors.add(new Floor_simple(i, new WB_Polygon(pts), floorheight));
		}
	}

	public void display(PApplet app, WB_Render3D hpp) {
		roof.display(app,hpp);
		for (Floor f : floors)
			f.display(app);
	}
}
