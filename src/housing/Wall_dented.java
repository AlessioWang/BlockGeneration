package housing;

import processing.core.PApplet;
import wblut.geom.WB_Point;
import wblut.geom.WB_Segment;
import wblut.geom.WB_Vector;

import java.util.ArrayList;

public class Wall_dented extends Wall {
	ArrayList<WB_Point> points;

	public Wall_dented(int _id, WB_Segment _baseline, float _height, WB_Vector _direct) {
		super(_id, _baseline, _height, _direct);
		/** add */
		points = new ArrayList<WB_Point>();
		WB_Point p2 = start.mul(0.333f).add(end.mul(0.667f));
		WB_Point p1 = start.mul(0.667f).add(end.mul(0.333f));
		WB_Vector offset = direct.div(direct.getLength2D());
		offset.mulSelf(start.getDistance2D(end) / 5);
		points.add(start);
		points.add(p1);
		points.add(p1.add(offset));
		points.add(p2.add(offset));
		points.add(p2);
		points.add(end);

	}

	@Override
	public void display(PApplet app) {
		app.fill(100);
		for(int i=0;i<points.size()-1;i++) {
			oneDisplay(app, points.get(i), points.get(i+1));
		}
		directdisplay(app);
	}
}
