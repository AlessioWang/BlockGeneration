package housing;

import processing.core.PApplet;
import wblut.geom.WB_Polygon;
import wblut.geom.WB_Segment;

import java.util.List;

public class Floor_simple extends Floor {
	public Floor_simple(int _id, WB_Polygon _outline, float _height) {
		super(_id, _outline, _height);
		List<WB_Segment> segments = outline.toSegments();
		int j = 0;
		for (WB_Segment s : segments) {

			walls.add(new Wall_simple(j, s, height,getWallDirect(1, s)));
			j++;
		}
	}

	public void display(PApplet app) {
		app.fill(255);
		app.stroke(0);
		for (Wall w : walls) {
			w.display(app);
		}
	}

}
