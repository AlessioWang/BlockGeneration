package housing;

import processing.core.PApplet;
import wblut.geom.WB_Point;
import wblut.geom.WB_Vector;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Administrator
 */
public class Topunit {
	List<WB_Point> points;
	WB_Point m1, m2;
	WB_Vector direct;
	float roofheight;

	public Topunit(List<WB_Point> pts) {
		double l1 = pts.get(0).getDistance2D(pts.get(1)) + pts.get(3).getDistance2D(pts.get(2));
		double l2 = pts.get(1).getDistance2D(pts.get(2)) + pts.get(0).getDistance2D(pts.get(3));
		/** decide */
		if (l1 > l2)
			points = pts;
		else {
			points=new ArrayList<>();
			for (int i = 0; i < pts.size(); i++)
				points.add(pts.get((i + 1) % pts.size()));
		}
		roofheight=(float) (points.get(1).sub(points.get(2)).getLength2D()*(0.3));
		m1 = points.get(1).add(points.get(2)).div(2);
		m1.setZ(roofheight);
		m2 = points.get(0).add(points.get(3)).div(2);
		m2.setZ(roofheight);
		direct = m1.sub(m2);
		direct.normalizeSelf();
	}

	public void display(PApplet app) {
		app.beginShape();
		app.vertex(points.get(0).xf(),points.get(0).yf(),points.get(0).zf());
		app.vertex(points.get(1).xf(),points.get(1).yf(),points.get(1).zf());
		app.vertex(m1.xf(),m1.yf(),m1.zf());
		app.vertex(m2.xf(),m2.yf(),m2.zf());
		app.vertex(points.get(0).xf(),points.get(0).yf(),points.get(0).zf());
		app.endShape();
		//
		app.beginShape();
		app.vertex(points.get(1).xf(),points.get(1).yf(),points.get(1).zf());
		app.vertex(m1.xf(),m1.yf(),m1.zf());
		app.vertex(points.get(2).xf(),points.get(2).yf(),points.get(2).zf());
		app.vertex(points.get(1).xf(),points.get(1).yf(),points.get(1).zf());
		app.endShape();
		//
		app.beginShape();
		app.vertex(points.get(2).xf(),points.get(2).yf(),points.get(2).zf());
		app.vertex(points.get(3).xf(),points.get(3).yf(),points.get(3).zf());
		app.vertex(m2.xf(),m2.yf(),m2.zf());
		app.vertex(m1.xf(),m1.yf(),m1.zf());
		app.vertex(points.get(2).xf(),points.get(2).yf(),points.get(2).zf());
		app.endShape();
		//
		app.beginShape();
		app.vertex(points.get(3).xf(),points.get(3).yf(),points.get(3).zf());
		app.vertex(m2.xf(),m2.yf(),m2.zf());
		app.vertex(points.get(0).xf(),points.get(0).yf(),points.get(0).zf());
		app.vertex(points.get(3).xf(),points.get(3).yf(),points.get(3).zf());
		app.endShape();
		//
	}
};