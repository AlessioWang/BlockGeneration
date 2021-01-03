package housing;


import processing.core.PApplet;
import wblut.geom.WB_Point;
import wblut.geom.WB_Segment;
import wblut.geom.WB_Vector;

public abstract class Wall {
	int id;
	WB_Point start, end;
	WB_Segment baseline;
	float height;
	WB_Vector direct;

	public Wall(int _id, WB_Segment _baseline, float _height, WB_Vector _direct) {
		id = _id;
		baseline = _baseline;
		height = _height;
		direct = _direct;
		direct.normalizeSelf();
		direct.mulSelf(5);
		start = (WB_Point) baseline.getOrigin();
		end = (WB_Point) baseline.getEndpoint();
	}

	public abstract void display(PApplet app);

	protected void oneDisplay(PApplet app,WB_Point s,WB_Point e) {
		app.beginShape();
		app.vertex(s.xf(), s.yf(),s.zf());
		app.vertex(e.xf(), e.yf(), e.zf());
		app.vertex(e.xf(), e.yf(), e.zf() + height);
		app.vertex(s.xf(),s.yf(),s.zf() + height);
		app.vertex(s.xf(),s.yf(),s.zf());
		app.endShape();
	}
	protected void oneDisplay(PApplet app,WB_Segment baseline) {
		app.beginShape();
		oneDisplay(app,(WB_Point)baseline.getOrigin(),(WB_Point)baseline.getEndpoint());
		app.endShape();
	}
	protected void directdisplay(PApplet app) {
		WB_Point c = start.add(end).mul(0.5);
		// System.out.println(direct);
		app.stroke(0);
		app.line(c.xf(), c.yf(), c.zf(), c.xf() + direct.xf(), c.yf() + direct.yf(), c.zf() + direct.zf());
	}
}
