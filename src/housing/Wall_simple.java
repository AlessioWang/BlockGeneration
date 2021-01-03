package housing;


import processing.core.PApplet;
import wblut.geom.WB_Segment;
import wblut.geom.WB_Vector;

public class Wall_simple extends Wall {

	public Wall_simple(int _id, WB_Segment _baseline, float _height,WB_Vector _direct) {
		super(_id, _baseline, _height, _direct);
		/** add */
	}

	@Override
	public void display(PApplet app) {
		app.fill(255);
		oneDisplay(app, baseline);
		directdisplay(app);
	}
}
