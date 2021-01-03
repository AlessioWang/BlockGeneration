package housing;

import processing.core.PApplet;
import wblut.geom.WB_Point;
import wblut.geom.WB_Polygon;
import wblut.geom.WB_Segment;
import wblut.geom.WB_Vector;

import java.util.ArrayList;

public abstract class Floor {
	WB_Polygon outline;
	ArrayList<Wall> walls;
	int id;
	float height;

	public Floor(int _id, WB_Polygon _outline, float _height) {
		id = _id;
		outline = _outline;
		height = _height;
		walls = new ArrayList<>();
	}

	public abstract void display(PApplet app);

	protected int PolyDirectTest() {
		WB_Segment sg = outline.getSegment(0);
		WB_Point s = (WB_Point) sg.getOrigin();
		WB_Point e = (WB_Point) sg.getEndpoint();
		WB_Point testPoint = e.sub(s).rotateAboutAxis(Math.PI, new WB_Point(0, 0, 0), new WB_Point(0, 0, 1));
		return 0;
	}

	//得到墙的朝向（由墙所在的向量进行垂直旋转操作）
	protected WB_Vector getWallDirect(int flag, WB_Segment sg) {
		WB_Point s = (WB_Point) sg.getOrigin();
		WB_Point e = (WB_Point) sg.getEndpoint();
		return e.sub(s).rotateAboutAxis(Math.PI / 2, new WB_Point(0, 0, 0), new WB_Point(0, 0, 1));
	}
}
