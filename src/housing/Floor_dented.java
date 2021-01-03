package housing;

import processing.core.PApplet;
import wblut.geom.WB_Polygon;
import wblut.geom.WB_Segment;

import java.util.List;

public class Floor_dented extends Floor {
	public Floor_dented(int _id, WB_Polygon _outline, float _height) {
		//super指引用父类
		super(_id, _outline, _height);
		List<WB_Segment> segments = outline.toSegments();
		int flag = 0;

		for (int i = 1; i < segments.size(); i++) {
			if (segments.get(flag).getLength() < segments.get(i).getLength())
				flag = i;
		}

		//指定墙的样式
		for (int i = 1; i < segments.size(); i++) {
			if (i == flag)
				walls.add(new Wall_dented(i, segments.get(i), height, getWallDirect(1, segments.get(i))));
			else
				walls.add(new Wall_simple(i, segments.get(i), height, getWallDirect(1, segments.get(i))));
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
